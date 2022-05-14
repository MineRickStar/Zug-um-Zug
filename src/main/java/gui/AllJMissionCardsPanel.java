package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import game.cards.MissionCard;

public class AllJMissionCardsPanel extends JPanel {

	private static final long serialVersionUID = 2579072798714564454L;

	private int rowCount;
	private int columnCount;

	private boolean keepRowCount;
	private boolean keepColumnCount;

	private List<IMissionCardPanel> missionCardPanelList;

	private boolean ignoreMissionCardVisibility;

	public AllJMissionCardsPanel() {
		this("");
	}

	public AllJMissionCardsPanel(String title) {
		this(-1, 4, title);
	}

	public AllJMissionCardsPanel(int rowCount, int columnCount, String title) {
		super(new GridLayout(rowCount, columnCount, 10, 10));
		this.setBackground(Color.LIGHT_GRAY);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), title));
		this.rowCount = rowCount;
		this.keepRowCount = rowCount != -1;
		this.columnCount = columnCount;
		this.keepColumnCount = columnCount != -1;
		this.missionCardPanelList = new ArrayList<>();
	}

	public void addMissionPanel(IMissionCardPanel panel) {
		this.missionCardPanelList.add(panel);
		this.update();
	}

	public void addAllMissionPanel(List<IMissionCardPanel> panels) {
		panels.forEach(j -> this.missionCardPanelList.add(j));
		this.update();
	}

	public void removeMissionPanel(IMissionCardPanel panel) {
		this.missionCardPanelList.remove(panel);
		this.update();
	}

	public void setFinished(MissionCard missionCard) {
		this.missionCardPanelList.forEach(j -> {
			if (j.getMissionCard().equals(missionCard)) {
				j.setCardFinished(true);
			}
		});
		this.update();
	}

	public void update() {
		this.removeAll();
		if (!this.keepColumnCount || !this.keepRowCount) {
			// If one is set to -1 means resizable, otherwise no change on Layout
			int count;
			if (this.ignoreMissionCardVisibility) {
				count = this.missionCardPanelList.size();
			} else {
				count = (int) this.missionCardPanelList.stream().filter(i -> i.isCardVisible() || !i.isCardFinished()).count();
			}
			if (this.keepColumnCount) {
				this.rowCount = (int) Math.ceil(count / (double) this.columnCount);
			}
			if (this.keepRowCount) {
				this.columnCount = (int) Math.ceil(count / (double) this.rowCount);
			}
			this.setLayout(new GridLayout(this.rowCount, this.columnCount, 10, 10));
		}
		int max = this.rowCount * this.columnCount;

		for (int i = 0, panelSize = this.missionCardPanelList.size(); i < max; i++) {
			if (i < panelSize) {
				IMissionCardPanel missionPanel = this.missionCardPanelList.get(i);
				if ((!this.ignoreMissionCardVisibility && !missionPanel.isCardVisible()) || missionPanel.isCardFinished()) {
					max++;
					continue;
				}
				this.add(missionPanel.getPanel());
			} else {
				JPanel panel = new JPanel();
				panel.setBackground(Color.LIGHT_GRAY);
				this.add(panel);
			}
		}
	}

	public int getRowCount() {
		return this.rowCount;
	}

	public int getColumnCount() {
		return this.columnCount;
	}

	public void setIgnoreMissionCardVisibility(boolean ignoreMissionCardVisibility) {
		this.ignoreMissionCardVisibility = ignoreMissionCardVisibility;
	}

	public int getMissionCardCount() {
		return this.missionCardPanelList.size();
	}

	public List<IMissionCardPanel> getMissionCardPanelList() {
		return this.missionCardPanelList;
	}

	public void setMissionCardPanelList(List<IMissionCardPanel> missionCardPanelList) {
		this.missionCardPanelList = new ArrayList<>(missionCardPanelList);
	}

}
