package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import application.Application;
import application.PropertyEvent;
import application.PropertyEvent.Property;
import game.Game;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.MissionCard;
import gui.dialog.EditMissionCardDialog;

public class PlayerPanel extends JPanel implements IUpdatePanel {

	private static final long serialVersionUID = -5734164742630869042L;

	private final int padding = 10;

	private JPanel missionCardsSettingsPanel;
	private JLabel finishedMissionCardLabel;
	private JButton showFinishedMissionCardsButton;
	private JButton editMissionCards;

	private JScrollPane allJMissionCardsScrollPane;
	private AllJMissionCardsPanel missionCardsPanel;

	private JColorCardPanel colorCardPanel;

	public PlayerPanel() {
		super(new GridBagLayout());

		this.missionCardsSettingsPanel = this.setUpMissionCardPanel();
		this.missionCardsPanel = new AllJMissionCardsPanel("Active Mission Cards");
		this.allJMissionCardsScrollPane = new JScrollPane(this.missionCardsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		this.allJMissionCardsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.colorCardPanel = new JColorCardPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(this.padding, this.padding, this.padding, this.padding);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(this.missionCardsSettingsPanel, gbc);

		gbc.weightx = 1;
		gbc.gridy = 1;
		this.add(this.allJMissionCardsScrollPane, gbc);

		gbc.weighty = 1;
		gbc.gridy = 2;
		this.add(this.colorCardPanel, gbc);
	}

	private JPanel setUpMissionCardPanel() {
		JPanel missionCards = new JPanel(new GridBagLayout());
		missionCards.setBackground(Color.LIGHT_GRAY);
		this.finishedMissionCardLabel = new JLabel("Finished Missioncards: 0, Points: 0");
		this.showFinishedMissionCardsButton = new JButton("Show Finished Mission Cards");
		this.showFinishedMissionCardsButton.setEnabled(false);
		this.showFinishedMissionCardsButton.setFocusable(false);
		this.showFinishedMissionCardsButton.addActionListener(e -> Application.createNewFinishedMissionCardDialog());
		this.editMissionCards = new JButton("Edit Missioncards");
		this.editMissionCards.setFocusable(false);
		Application.addCTRLShortcut(this.editMissionCards, KeyEvent.VK_E, e -> this.editMissionCards());
		this.editMissionCards.addActionListener(e -> this.editMissionCards());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(this.padding, this.padding, this.padding, this.padding);
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

	private void editMissionCards() {
		EditMissionCardDialog dialog = Application.createNewMssionCardEditor();
		Map<JMissionCardPanel, Boolean> missionCardPanelVisibility = dialog.getMissionCardPanelVisibility();
		if (missionCardPanelVisibility != null) {
			Iterator<Entry<JMissionCardPanel, Boolean>> iterator = missionCardPanelVisibility.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<JMissionCardPanel, Boolean> entry = iterator.next();
				this.missionCardsPanel.getMissionCardPanelList()
						.stream()
						.filter(j -> j.missionCard.equals(entry.getKey().missionCard))
						.map(j -> (InfoPanelMissionCardPanel) j)
						.forEach(i -> i.visible = !entry.getValue());
			}
		}
		Map<int[], int[]> indexes = dialog.getMissionCardPanelIndexes();
		if (indexes != null) {
			Entry<int[], int[]> entry = indexes.entrySet().iterator().next();
			this.missionCardsPanel.sortMissionCardPanels(entry.getKey(), entry.getValue());
		}
		Application.frame.update(new PropertyEvent(Game.getInstance().getInstancePlayer(), Property.MISSIONCARDEDITED));
	}

	private class InfoPanelMissionCardPanel extends JMissionCardPanel {

		private static final long serialVersionUID = -3372640083631650600L;

		private boolean visible;
		private boolean finished;

		protected InfoPanelMissionCardPanel(MissionCard missionCard) {
			super(missionCard);
			this.finished = false;
			this.visible = true;
		}

		@Override
		public boolean isPanelDisplayable() {
			return !this.finished && this.visible;
		}

	}

	public static class JColorCardButton extends JButton {
		private static final long serialVersionUID = -4035891459387013101L;

		public final ColorCard colorCard;
		public final int index;

		public JColorCardButton(ColorCard colorCard, int index) {
			super(colorCard.getColorCardString());
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

	private void updateFinishedMissionCardPanel() {
		List<MissionCard> finishedMissionCards = Game.getInstance().getInstancePlayer().getFinishedMissionCards();
		this.finishedMissionCardLabel.setText("Finished Missioncards: " + finishedMissionCards.size() + ", Points: " + finishedMissionCards.stream().mapToInt(m -> m.points).sum());
		this.showFinishedMissionCardsButton.setEnabled(finishedMissionCards.size() > 0);
		this.missionCardsPanel.getMissionCardPanelList()
				.stream()
				.filter(j -> finishedMissionCards.stream().anyMatch(m -> j.missionCard.equals(m)))
				.map(j -> (InfoPanelMissionCardPanel) j)
				.forEach(i -> i.finished = true);
	}

	private void updateMissionCards() {
		List<MissionCard> missionCards = Game.getInstance().getInstancePlayer().getMissionCards();
		missionCards.stream().map(InfoPanelMissionCardPanel::new).forEach(i -> this.missionCardsPanel.addMissionCard(i, false));
		this.missionCardsPanel.update();
		int maxHeight = this.getHeight() - this.allJMissionCardsScrollPane.getPreferredSize().height - this.missionCardsSettingsPanel.getHeight() - (5 * this.padding);
		this.colorCardPanel.updateColorCardPanel(maxHeight);
	}

	@Override
	public void update(PropertyEvent propertyEvent) {
		switch (propertyEvent.property) {
		case COLORCARDADDED:
		case COLORCARDREMOVED:
		case MISSIONCARDADDED:
		case MISSIONCARDEDITED:
		case MISSIONCARDFINISHED:
			if (Game.getInstance().getInstancePlayer().equals(propertyEvent.player)) {
				this.updateMissionCards();
				this.updateFinishedMissionCardPanel();
			}
			break;
		case MISSIONCARDDRAWN:
		case COLORCARDDRAWN:
		case GAMESTART:
		case PLAYERCHANGE:
		case CONNECTIONBOUGHT:
			break;
		}
	}

}
