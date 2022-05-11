package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import application.Property;
import game.Computer;
import game.Game;
import game.Player;
import game.Rules;
import game.cards.ColorCard;
import game.cards.MissionCard;
import game.cards.MyColor;
import game.cards.TransportMode;
import gui.dialog.FinishedMissionCardDialog;
import gui.dialog.MissionCardDialog;

public class InfoPanel extends JSplitPane implements PropertyChangeListener {

	private static final long serialVersionUID = -6143438348516086903L;

	public InfoPanel() {
		super(JSplitPane.VERTICAL_SPLIT);
		super.setContinuousLayout(false);
		super.setDividerSize(0);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDFINISHED, this);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDADDED, this);
		Game.getInstance().addPropertyChangeListener(Property.COLORCARDCHANGE, this);
		Game.getInstance().addPropertyChangeListener(Property.PLAYERCHANGE, this);
	}

	public void startGame() {
		this.setupPublicPanel();
		this.setupPlayerPanel();

		this.setTopComponent(this.publicPanel);
		this.setBottomComponent(this.playerPanel);
		super.setDividerLocation(.2);
		this.revalidate();
		this.repaint();
	}

	////////// Public Panel //////////
	private JPanel publicPanel;

	private TitledBorder colorCardsBorder;

	private JPanel colorCardButtonPanel;
	private List<JColorCardButton> colorCardButtons;

	private JPanel cardButtonPanel;

	private JButton drawColorCardsButton;
	private JButton drawMissionCardsButton;

	private JLabel currentPlayerLabel;

	private void setupPublicPanel() {
		this.publicPanel = new JPanel(new GridBagLayout());
		this.publicPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		this.publicPanel.setDoubleBuffered(true);
		this.colorCardButtons = new ArrayList<>(Rules.getInstance().getColorCardsLayingDown());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;

		this.createColorCardButtonPanel();
		this.publicPanel.add(this.colorCardButtonPanel, gbc);

		this.createCardButtonPanel();
		gbc.weighty = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 20, 10, 20);
		this.publicPanel.add(this.cardButtonPanel, gbc);

		this.currentPlayerLabel = new JLabel("Current Player: ");
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 10, 0, 0);
		this.publicPanel.add(this.currentPlayerLabel, gbc);
	}

	private void createColorCardButtonPanel() {
		this.colorCardButtonPanel = new JPanel(new GridBagLayout());
		this.colorCardButtonPanel.setBackground(Color.LIGHT_GRAY);
		this.colorCardsBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), this.getTitleString());
		this.colorCardButtonPanel.setBorder(this.colorCardsBorder);
		this.colorCardButtonPanel.setDoubleBuffered(true);

		this.drawColorCardButtonPanel();
	}

	private List<ColorCard> previousColorCards = null;

	private void drawColorCardButtonPanel() {
		if (Game.getInstance().getOpenCards().equals(this.previousColorCards)) {
			boolean enabled = Rules.getInstance().getColorCardsDrawing() - Game.getInstance().getCurrentPlayerColorCardDraws() >= Rules.getInstance().getLocomotiveWorth();
			this.colorCardButtons.forEach(b -> {
				if (b.colorCard.color() == MyColor.RAINBOW) {
					b.setEnabled(enabled);
				}
			});
			return;
		}
		this.colorCardButtonPanel.removeAll();
		GridBagConstraints colorCardConstraints = new GridBagConstraints();
		colorCardConstraints.insets = new Insets(10, 10, 10, 10);
		colorCardConstraints.fill = GridBagConstraints.BOTH;
		colorCardConstraints.weighty = 1;
		colorCardConstraints.weightx = 1;

		this.previousColorCards = new ArrayList<>(Game.getInstance().getOpenCards());
		for (int i = 0; i < Math.min(Rules.getInstance().getColorCardsLayingDown(), Game.getInstance().getRemainingCards()); i++) {
			colorCardConstraints.gridx = i;

			ColorCard colorCard = this.previousColorCards.get(i);
			JColorCardButton colorCardButton;
			if (colorCard.color() == MyColor.RAINBOW) {
				colorCardButton = new JGradientButton(colorCard, i);
			} else {
				colorCardButton = new JColorCardButton(colorCard, i);
			}
			colorCardButton.addActionListener(e -> {
				if (e.getSource() instanceof JColorCardButton button) {
					Game.getInstance().drawColorCardsFromOpenDeck(Game.getInstance().getInstancePlayer(), button.index);
					this.updateColorCardPanelTitle();
				}
			});
			colorCardButton.setEnabled(Game.getInstance().isPlayersTurn());
			if (colorCard.color() == MyColor.RAINBOW) {
				if ((Rules.getInstance().getColorCardsDrawing() - Game.getInstance().getCurrentPlayerColorCardDraws()) < Rules.getInstance().getLocomotiveWorth()) {
					colorCardButton.setEnabled(false);
				}
			}
			this.colorCardButtons.add(colorCardButton);

			this.colorCardButtonPanel.add(colorCardButton, colorCardConstraints);
		}

	}

	private void createCardButtonPanel() {
		this.cardButtonPanel = new JPanel(new GridLayout(1, 2, 20, 20));
		this.drawColorCardsButton = new JButton("Draw Card");

		this.drawColorCardsButton.addActionListener(e -> {
			Game.getInstance().drawColorCardFromDeck(Game.getInstance().getInstancePlayer());
			this.updateColorCardPanelTitle();
			this.drawColorCardsButton.setEnabled(Game.getInstance().getRemainingCards() > 0);
		});
		this.drawMissionCardsButton = new JButton("Draw Mission Cards");
		this.drawMissionCardsButton.addActionListener(e -> new MissionCardDialog(false));

		this.cardButtonPanel.add(this.drawColorCardsButton);
		this.cardButtonPanel.add(this.drawMissionCardsButton);
	}

	private void updateColorCardPanelTitle() {
		this.colorCardsBorder.setTitle(this.getTitleString());
		this.drawColorCardButtonPanel();
		this.revalidate();
		this.repaint();
	}

	private String getTitleString() {
		String drawCards = "Drawcards (" + Game.getInstance().getRemainingCards() + ")";
		if (Game.getInstance().isPlayersTurn() && Game.getInstance().getCurrentPlayerColorCardDraws() > 0) {
			drawCards += " Cards left to draw (" + (Rules.getInstance().getColorCardsDrawing() - Game.getInstance().getCurrentPlayerColorCardDraws()) + ")";
		}
		return drawCards;
	}

	////////// Private Panel //////////

	private JPanel playerPanel;

	private JPanel finishedMissionCards;
	private JLabel finishedMissionCardLabel;
	private JButton showFinishedMissionCardsButton;

	private JPanel missionCardPanel;
	private List<JMissionCardPanel> missionCardPanelList;

	private JPanel colorCardPanel;
	private List<ColorCard> colorCardList;

	private void setupPlayerPanel() {
		this.playerPanel = new JPanel(new GridBagLayout());

		this.finishedMissionCards = new JPanel(new GridBagLayout());
		this.finishedMissionCards.setBackground(Color.LIGHT_GRAY);
		this.finishedMissionCardLabel = new JLabel("Finished Missioncards: 0");
		this.showFinishedMissionCardsButton = new JButton("Show Finished Mission Cards");
		this.showFinishedMissionCardsButton.setEnabled(false);
		this.showFinishedMissionCardsButton.addActionListener(e -> new FinishedMissionCardDialog(Game.getInstance().getInstancePlayer().getFinishedMissionCards()));
		this.setUpFinishedMissionCardPanel();

		this.missionCardPanel = new JPanel(new GridBagLayout());
		this.missionCardPanel.setBackground(Color.LIGHT_GRAY);

		this.missionCardPanelList = new ArrayList<>();

		this.colorCardPanel = new JPanel(new GridBagLayout());
		this.colorCardPanel.setBackground(Color.LIGHT_GRAY);

		this.colorCardList = new ArrayList<>();

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;
		this.playerPanel.add(this.finishedMissionCards, gbc);

		gbc.weightx = 1;
		gbc.gridy = 1;
		this.playerPanel.add(this.missionCardPanel, gbc);

		gbc.weighty = 1;
		gbc.gridy = 2;
		this.playerPanel.add(this.colorCardPanel, gbc);
	}

	private void setUpFinishedMissionCardPanel() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		this.finishedMissionCards.add(this.finishedMissionCardLabel, gbc);
		gbc.gridx = 1;
		this.finishedMissionCards.add(this.showFinishedMissionCardsButton, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.weightx = 1;
		this.finishedMissionCards.add(new JLabel(), gbc);
	}

	private void updateFinishedMissionCardPanel(MissionCard finishedMissionCard) {
		this.finishedMissionCardLabel.setText("Finished Missioncards: " + Game.getInstance().getInstancePlayer().getFinishedMissionCards().size());
		this.showFinishedMissionCardsButton.setEnabled(true);
		this.missionCardPanelList.removeIf(j -> j.missionCard.equals(finishedMissionCard));
		this.updateMissionCardPanel();
	}

	private void updateMissionCardPanel() {
		Collections.sort(this.missionCardPanelList);
		this.missionCardPanel.removeAll();
		int columnCount = 4;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 5, 10, 5);
		for (int i = 0, max = this.missionCardPanelList.size(); i < max; i++) {
			gbc.weighty = (max <= columnCount) || (i >= columnCount) ? 1 : 0;
			gbc.gridx = i % columnCount;
			gbc.gridy = i / columnCount;
			this.missionCardPanel.add(this.missionCardPanelList.get(i), gbc);
		}
	}

	public static class JColorCardButton extends JButton {
		private static final long serialVersionUID = -4035891459387013101L;

		public final ColorCard colorCard;
		public final int index;

		public JColorCardButton(ColorCard colorCard, int index) {
			super(InfoPanel.getColorCardString(colorCard));
			this.colorCard = colorCard;
			this.index = index;
			this.setForeground(MyColor.getComplementaryColor(colorCard.color()));
			this.setPreferredSize(new Dimension(60, 20));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
			this.setBackground(colorCard.color().realColor);
			this.setSelected(false);
			this.setDoubleBuffered(true);
			this.setFocusable(false);
		}
	}

	public static class JGradientButton extends JColorCardButton {
		private static final long serialVersionUID = 5469665614084730926L;

		public JGradientButton(ColorCard colorCard, int index) {
			super(colorCard, index);
			this.setForeground(Color.BLACK);
			this.setContentAreaFilled(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			MyColor[] colors = MyColor.getNormalMyColors();
			int stripHeigth = this.getHeight() / (colors.length - 1);
			for (int i = 0; i < (colors.length - 1); i++) {
				g2.setPaint(new GradientPaint(new Point(0, i * stripHeigth), colors[i].realColor, new Point(0, (i + 1) * stripHeigth), colors[i + 1].realColor));
				g2.fillRect(0, i * stripHeigth, this.getWidth(), (i + 1) * stripHeigth);
			}
			g2.dispose();
			super.paintComponent(g);
		}
	}

	private void addCardAndUpdatePanel(ColorCard[] colorCard, boolean remove) {
		if (this.colorCardList == null) { return; }
		if (remove) {
			List.of(colorCard).forEach(c -> this.colorCardList.remove(c));
		} else {
			this.colorCardList.addAll(List.of(colorCard));
		}
		this.updateColorCardPanel();
	}

	private void updateColorCardPanel() {
		this.colorCardPanel.removeAll();

		Map<TransportMode, SortedMap<Integer, List<ColorCard>>> rankingMap = new EnumMap<>(TransportMode.class);
		Iterator<ColorCard> iterator = this.colorCardList.iterator();
		while (iterator.hasNext()) {
			ColorCard card = iterator.next();
			SortedMap<Integer, List<ColorCard>> rankingTransportMode = rankingMap.getOrDefault(card.transportMode(), new TreeMap<>(Collections.reverseOrder()));
			Integer oldKey = 0;
			List<ColorCard> oldList = new ArrayList<>();
			for (Entry<Integer, List<ColorCard>> entry : rankingTransportMode.entrySet()) {
				if (entry.getValue().contains(card)) {
					oldKey = entry.getKey();
					oldList = entry.getValue();
					break;
				}
			}
			Integer newKey = oldKey + 1;
			oldList.remove(card);
			List<ColorCard> newList = rankingTransportMode.getOrDefault(newKey, new ArrayList<>());
			newList.add(card);
			rankingTransportMode.put(newKey, newList);
			rankingMap.put(card.transportMode(), rankingTransportMode);
		}

		int padding = 10;
		double ratio = 2 / 3.0;
		int maxHeight = 150;
		int maxWidth = (int) (maxHeight * ratio);
		int height = 0;
		int width = 0;

		while (width <= padding * 4) {
			int maxPossibleHeight = ((this.colorCardPanel.getHeight() - 10)
					/ rankingMap.values().stream().flatMap(t -> Stream.of(t.values().stream().flatMap(List::stream).toList().size())).mapToInt(i -> i).max().getAsInt()) - (2 * padding);
			int maxPossibleWidth = ((this.colorCardPanel.getWidth() - 20) / rankingMap.values().stream().flatMap(t -> Stream.of(new ArrayList<>(t.keySet()).get(0))).reduce(0, (t, u) -> t + u))
					- (2 * padding);
			height = maxPossibleHeight > maxHeight ? maxHeight : maxPossibleHeight;
			width = maxPossibleWidth > maxWidth ? maxWidth : maxPossibleWidth;
			double proportion = width / (double) height;
			if (proportion < ratio) {
				height = (int) (width / ratio);
			} else {
				width = (int) (height * ratio);
			}
			padding--;
		}

		Dimension prefederredDimension = new Dimension(width, height);
		GridBagConstraints gbcTransport = new GridBagConstraints();
		gbcTransport.anchor = GridBagConstraints.NORTH;
		JPanel transportPanel;
		GridBagConstraints gbc = new GridBagConstraints();
		Iterator<Entry<TransportMode, SortedMap<Integer, List<ColorCard>>>> iteratorMap = rankingMap.entrySet().iterator();
		while (iteratorMap.hasNext()) {
			transportPanel = new JPanel(new GridBagLayout());
			transportPanel.setBackground(Color.LIGHT_GRAY);
			gbcTransport.gridx++;
			gbc.insets = new Insets(padding, padding, padding, padding);
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.BOTH;

			Entry<TransportMode, SortedMap<Integer, List<ColorCard>>> entryTransportMode = iteratorMap.next();
			Iterator<Entry<Integer, List<ColorCard>>> it = entryTransportMode.getValue().entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, List<ColorCard>> entry = it.next();
				JColorCardLabel label;
				List<ColorCard> cards = entry.getValue();
				for (int i = 0, m = cards.size(); i < m; i++) {
					ColorCard card = cards.get(i);
					gbc.gridx = 0;
					for (int j = 0, n = entry.getKey(); j < n; j++) {
						if (card.color() == MyColor.RAINBOW) {
							label = new JGradientLabel(card);
						} else {
							label = new JColorCardLabel(card);
						}
						label.setPreferredSize(prefederredDimension);
						transportPanel.add(label, gbc);
						gbc.gridx++;
					}
					gbc.gridy++;
				}
			}
			transportPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), entryTransportMode.getKey().displayName));
			this.colorCardPanel.add(transportPanel, gbcTransport);
		}
		gbcTransport.weightx = 1;
		gbcTransport.weighty = 1;
		gbcTransport.gridx++;
		gbcTransport.fill = GridBagConstraints.BOTH;
		this.colorCardPanel.add(new JPanel(), gbcTransport);
	}

	private static class JColorCardLabel extends JLabel {
		private static final long serialVersionUID = -5607808287807778978L;

		public final ColorCard colorCard;

		public JColorCardLabel(ColorCard colorCard) {
			super(InfoPanel.getColorCardString(colorCard));
			this.colorCard = colorCard;
			this.setForeground(MyColor.getComplementaryColor(colorCard.color()));
			this.setPreferredSize(new Dimension(60, 20));
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
			this.setBackground(colorCard.color().realColor);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setDoubleBuffered(true);
			this.setFocusable(false);
			this.setOpaque(true);
		}

	}

	public static class JGradientLabel extends JColorCardLabel {
		private static final long serialVersionUID = 5469665614084730926L;

		public JGradientLabel(ColorCard colorCard) {
			super(colorCard);
			this.setForeground(Color.BLACK);
			this.setBackground(null);
			this.setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();

			MyColor[] colors = MyColor.getNormalMyColors();
			int stripHeigth = this.getHeight() / (colors.length - 1);

			for (int i = 0; i < (colors.length - 1); i++) {
				g2.setPaint(new GradientPaint(new Point(0, i * stripHeigth), colors[i].realColor, new Point(0, (i + 1) * stripHeigth), colors[i + 1].realColor));
				g2.fillRect(0, i * stripHeigth, this.getWidth(), (i + 1) * stripHeigth);
			}
			g2.dispose();
			super.paintComponent(g);
		}
	}

	private static String getColorCardString(ColorCard colorCard) {
		return "<html><body>" + colorCard.transportMode().displayName + "<br>" + colorCard.color().colorName + "</body></html>";
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Property.MISSIONCARDADDED.name().equals(evt.getPropertyName())) {
			try {
				SwingUtilities.invokeAndWait(() -> {
					for (MissionCard card : (MissionCard[]) evt.getNewValue()) {
						JMissionCardPanel missionCardPanel = new JMissionCardPanel(card);
						missionCardPanel.setBackground(Color.WHITE);
						this.missionCardPanelList.add(missionCardPanel);
					}
					this.updateMissionCardPanel();
					this.revalidate();
					this.repaint();
				});
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
			SwingUtilities.invokeLater(this::updateColorCardPanel);
		} else if (Property.COLORCARDCHANGE.name().equals(evt.getPropertyName())) {
			if (evt.getSource() instanceof Computer) {
				this.updateColorCardPanelTitle();
			} else {
				ColorCard[] card = evt.getNewValue() == null ? (ColorCard[]) evt.getOldValue() : (ColorCard[]) evt.getNewValue();
				SwingUtilities.invokeLater(() -> this.addCardAndUpdatePanel(card, evt.getNewValue() == null));
			}
		} else if (Property.PLAYERCHANGE.name().equals(evt.getPropertyName())) {
			Player newPlayer = (Player) evt.getNewValue();
			this.currentPlayerLabel.setText("Current Player: " + newPlayer.getName());
			boolean isPlayer = !(newPlayer instanceof Computer);
			SwingUtilities.invokeLater(() -> {
				this.drawColorCardsButton.setEnabled(isPlayer);
				this.drawMissionCardsButton.setEnabled(isPlayer);
				this.colorCardButtons.forEach(j -> j.setEnabled(isPlayer));
			});
		} else if (Property.MISSIONCARDFINISHED.name().equals(evt.getPropertyName())) {
			if (evt.getSource() instanceof Player player) {
				this.updateFinishedMissionCardPanel((MissionCard) evt.getNewValue());
			}
		}
		this.revalidate();
		this.repaint();
	}

}
