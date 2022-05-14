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
import java.util.List;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
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
import gui.dialog.DrawMissionCardDialog;
import gui.dialog.EditMissionCardDialog;
import gui.dialog.FinishedMissionCardDialog;

public class InfoPanel extends JSplitPane implements PropertyChangeListener {

	private static final long serialVersionUID = -6143438348516086903L;

	public InfoPanel() {
		super(JSplitPane.VERTICAL_SPLIT);
		super.setContinuousLayout(false);
		super.setDividerSize(0);
	}

	public void startGame() {
		this.setupPublicPanel();
		this.setupPlayerPanel();

		this.setTopComponent(this.publicPanel);
		this.setBottomComponent(this.playerPanel);
		super.setDividerLocation(.2);
		this.revalidate();
		this.repaint();
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDFINISHED, this);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDADDED, this);
		Game.getInstance().addPropertyChangeListener(Property.COLORCARDCHANGE, this);
		Game.getInstance().addPropertyChangeListener(Property.PLAYERCHANGE, this);
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
		this.drawMissionCardsButton.addActionListener(e -> new DrawMissionCardDialog(false));

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

	private JPanel missionCardsSettingsPanel;
	private JLabel finishedMissionCardLabel;
	private JButton showFinishedMissionCardsButton;
	private JButton editMissionCards;

	private AllJMissionCardsPanel allJMissionCardsPanel;

	private JColorCardPanel colorCardPanel;

	private void setupPlayerPanel() {
		this.playerPanel = new JPanel(new GridBagLayout());

		this.missionCardsSettingsPanel = this.setUpFinishedMissionCardPanel();
		this.allJMissionCardsPanel = new AllJMissionCardsPanel("Active Mission Cards");
		this.colorCardPanel = new JColorCardPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;
		this.playerPanel.add(this.missionCardsSettingsPanel, gbc);

		gbc.weightx = 1;
		gbc.gridy = 1;
		this.playerPanel.add(this.allJMissionCardsPanel, gbc);

		gbc.weighty = 1;
		gbc.gridy = 2;
		this.playerPanel.add(this.colorCardPanel, gbc);
	}

	private JPanel setUpFinishedMissionCardPanel() {
		JPanel missionCards = new JPanel(new GridBagLayout());
		missionCards.setBackground(Color.LIGHT_GRAY);
		this.finishedMissionCardLabel = new JLabel("Finished Missioncards: 0, Points: 0");
		this.showFinishedMissionCardsButton = new JButton("Show Finished Mission Cards");
		this.showFinishedMissionCardsButton.setEnabled(false);
		this.showFinishedMissionCardsButton.addActionListener(e -> new FinishedMissionCardDialog(Game.getInstance().getInstancePlayer().getFinishedMissionCards()));
		this.editMissionCards = new JButton("Edit Missioncards");
		this.editMissionCards.addActionListener(e -> new EditMissionCardDialog(this.allJMissionCardsPanel));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		missionCards.add(this.finishedMissionCardLabel, gbc);
		gbc.gridx = 1;
		missionCards.add(this.showFinishedMissionCardsButton, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.weightx = 1;
		missionCards.add(new JLabel(), gbc);
		gbc.weightx = 0;
		gbc.gridx = 3;
		missionCards.add(this.editMissionCards, gbc);
		return missionCards;
	}

	private void updateFinishedMissionCardPanel(MissionCard finishedMissionCard) {
		List<MissionCard> finishedMissionCards = Game.getInstance().getInstancePlayer().getFinishedMissionCards();
		this.finishedMissionCardLabel.setText("Finished Missioncards: " + finishedMissionCards.size() + ", Points: " + finishedMissionCards.stream().mapToInt(m -> m.points).sum());
		this.showFinishedMissionCardsButton.setEnabled(true);
		this.allJMissionCardsPanel.setFinished(finishedMissionCard);
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

	public static String getColorCardString(ColorCard colorCard) {
		return "<html><body>" + colorCard.transportMode().displayName + "<br>" + colorCard.color().colorName + "</body></html>";
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Property.MISSIONCARDADDED.name().equals(evt.getPropertyName())) {
			try {
				SwingUtilities.invokeAndWait(() -> {
					Stream.of((MissionCard[]) evt.getNewValue()).map(JMissionCardPanel::new).forEach(this.allJMissionCardsPanel::addMissionPanel);
					this.colorCardPanel.updateColorCardPanel();
					this.revalidate();
					this.repaint();
				});
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
			SwingUtilities.invokeLater(() -> this.colorCardPanel.updateColorCardPanel());
		} else if (Property.COLORCARDCHANGE.name().equals(evt.getPropertyName())) {
			if (evt.getSource() instanceof Computer) {
				this.updateColorCardPanelTitle();
			} else {
				ColorCard[] card = evt.getNewValue() == null ? (ColorCard[]) evt.getOldValue() : (ColorCard[]) evt.getNewValue();
				SwingUtilities.invokeLater(() -> this.colorCardPanel.editCardAndUpdatePanel(card, evt.getNewValue() == null));
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
