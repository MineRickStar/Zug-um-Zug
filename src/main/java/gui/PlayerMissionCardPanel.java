package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import application.Application;
import application.PropertyEvent;
import application.PropertyEvent.Property;
import game.Game;
import game.cards.ColorCard.TransportMode;
import game.cards.MissionCard;
import gui.dialog.EditMissionCardDialog;
import language.MyResourceBundle.LanguageKey;

public class PlayerMissionCardPanel extends JPanel implements IUpdatePanel {

	private static final long serialVersionUID = -5370713738520013370L;

	private final int padding = 10;

	private DefaultAllJMissionCardsScrollPanel missionCardsPanel;

	private JPanel missionCardsSettingsPanel;
	private JButton showFinishedMissionCardsButton;
	private JButton editMissionCardsButton;

	private JPanel playerInfoPanel;
	private JLabel finishedMissionCardsInfo;
	private Map<TransportMode, JLabel> carrigeCountLabelList;

	public PlayerMissionCardPanel() {
		super(new GridBagLayout());
		this.missionCardsPanel = new DefaultAllJMissionCardsScrollPanel(1, -1);
		this.missionCardsPanel.setMaxHeight(0);
		JScrollPane pane = new JScrollPane(this.missionCardsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.missionCardsSettingsPanel = this.setUpMissionCardPanel();
		this.playerInfoPanel = this.setUpPlayerInfoPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(pane, gbc);
		gbc.gridx = 1;
		this.add(this.missionCardsSettingsPanel, gbc);
		gbc.gridx = 2;
		this.add(this.playerInfoPanel, gbc);
		gbc.gridx = 3;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JPanel(), gbc);
	}

	private JPanel setUpMissionCardPanel() {
		JPanel missionCards = new JPanel(new GridBagLayout());
		this.showFinishedMissionCardsButton = new JButton(Application.resources.getString(LanguageKey.SHOWFINISHEDMISSIONCARDS));
		this.showFinishedMissionCardsButton.setEnabled(false);
		this.showFinishedMissionCardsButton.setFocusable(false);
		this.showFinishedMissionCardsButton.addActionListener(e -> Application.createNewFinishedMissionCardDialog());
		this.editMissionCardsButton = new JButton(Application.resources.getString(LanguageKey.EDITMISSIONCARDS));
		this.editMissionCardsButton.setFocusable(false);
		Application.addCTRLShortcut(this.editMissionCardsButton, KeyEvent.VK_E, e -> this.editMissionCards());
		this.editMissionCardsButton.addActionListener(e -> this.editMissionCards());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(this.padding, this.padding, this.padding, this.padding);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		missionCards.add(this.showFinishedMissionCardsButton, gbc);
		gbc.gridy = 1;
		missionCards.add(this.editMissionCardsButton, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.gridy = 2;
		missionCards.add(new JLabel(), gbc);
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
		if (dialog.isEdited()) {
			this.missionCardsPanel.sortMissionCardPanels(dialog.getMissionCardPanelIndexes());
			Application.frame.update(new PropertyEvent(Game.getInstance().getInstancePlayer(), Property.MISSIONCARDEDITED));
		}
	}

	private static class InfoPanelMissionCardPanel extends JMissionCardPanel {

		private static final long serialVersionUID = -3372640083631650600L;

		private boolean visible;
		private boolean finished;

		protected InfoPanelMissionCardPanel(MissionCard missionCard) {
			super(missionCard, true);
			this.finished = false;
			this.visible = true;
		}

		@Override
		public boolean isPanelDisplayable() {
			return !this.finished && this.visible;
		}

	}

	private JPanel setUpPlayerInfoPanel() {
		JPanel infoPanel = new JPanel(new GridBagLayout());
		this.finishedMissionCardsInfo = new JLabel();
		this.carrigeCountLabelList = new TreeMap<>((o1, o2) -> Integer.compare(Game.getInstance().getInstancePlayer().getPieceCount(o2), Game.getInstance().getInstancePlayer().getPieceCount(o1)));
		this.carrigeCountLabelList.putAll(List.of(Game.getInstance().getMap().getTransportModes()).stream().collect(Collectors.toMap(t -> t, t -> new JLabel())));
		this.updatePlayerInfoPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(this.padding, this.padding, 0, this.padding);
		gbc.gridx = 0;
		gbc.gridy = 0;
		infoPanel.add(this.finishedMissionCardsInfo, gbc);
		Iterator<Entry<TransportMode, JLabel>> it = this.carrigeCountLabelList.entrySet().iterator();
		while (it.hasNext()) {
			gbc.gridy++;
			infoPanel.add(it.next().getValue(), gbc);
		}
		return infoPanel;
	}

	private void updateMissionCards() {
		this.missionCardsPanel.setMaxWidth(this.getWidth() - 500);
		List<MissionCard> finishedMissionCards = Game.getInstance().getInstancePlayer().getFinishedMissionCards();
		List<MissionCard> missionCards = Game.getInstance().getInstancePlayer().getMissionCards();
		this.showFinishedMissionCardsButton.setEnabled(finishedMissionCards.size() > 0);
		this.editMissionCardsButton.setEnabled(missionCards.size() > 0);
		this.missionCardsPanel.addMissionCards(missionCards.stream().map(InfoPanelMissionCardPanel::new).toList());
		this.missionCardsPanel.getMissionCardPanelList()
				.stream()
				.filter(j -> finishedMissionCards.stream().anyMatch(m -> j.missionCard.equals(m)))
				.map(j -> (InfoPanelMissionCardPanel) j)
				.forEach(i -> i.finished = true);
		this.missionCardsPanel.update();
	}

	private void updatePlayerInfoPanel() {
		List<MissionCard> finishedMissionCards = Game.getInstance().getInstancePlayer().getFinishedMissionCards();
		this.finishedMissionCardsInfo.setText(String.format("%s: %d, %s: %d", Application.resources.getString(LanguageKey.FINISHEDMISSIONS), finishedMissionCards.size(),
				Application.resources.getString(LanguageKey.POINTS), finishedMissionCards.stream().mapToInt(m -> m.points).sum()));
		this.carrigeCountLabelList.forEach((t, u) -> {
			int pieceCount = Game.getInstance().getInstancePlayer().getPieceCount(t);
			u.setText(pieceCount == 1 ? t.getDisplayNameSingular() : t.getDisplayNamePlural() + " " + pieceCount);
		});
	}

	@Override
	public void update(PropertyEvent propertyEvent) {
		switch (propertyEvent.property) {
		case MISSIONCARDADDED:
		case MISSIONCARDEDITED:
		case MISSIONCARDFINISHED:
			if (Game.getInstance().getInstancePlayer().equals(propertyEvent.player)) {
				this.updateMissionCards();
				this.updatePlayerInfoPanel();
			}
			break;
		case CONNECTIONBOUGHT:
			if (Game.getInstance().getInstancePlayer().equals(propertyEvent.player)) {
				this.updatePlayerInfoPanel();
			}
			break;
		case FRAMESIZECHANGED:
			this.updateMissionCards();
			this.updatePlayerInfoPanel();
			break;
		case GAMESTART:
		case COLORCARDADDED:
		case COLORCARDREMOVED:
		case COLORCARDDRAWN:
		case MISSIONCARDDRAWN:
		case PLAYERCHANGE:
		default:
			break;
		}
	}

}
