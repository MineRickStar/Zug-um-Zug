package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class AllJMissionCardsPanel extends JPanel {

	private static final long serialVersionUID = 2579072798714564454L;

	private int rowCount;
	private int columnCount;

	private boolean keepRowCount;
	private boolean keepColumnCount;

	private List<JMissionCardPanel> missionCardPanelList;

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

	private void addJMissionCardPanel(JMissionCardPanel missionCard, boolean update) {
		this.missionCardPanelList.add(missionCard);
		if (update) {
			this.update();
		}
	}

	public void addMissionCard(JMissionCardPanel missionCard) {
		this.addJMissionCardPanel(missionCard, true);
	}

	public void addMissionCards(JMissionCardPanel[] missionCardPanel) {
		for (JMissionCardPanel missionCard : missionCardPanel) {
			this.addJMissionCardPanel(missionCard, false);
		}
		this.update();
	}

	public void addMissionCards(List<JMissionCardPanel> missionCards) {
		missionCards.forEach(m -> this.addJMissionCardPanel(m, false));
		this.update();
	}

	public void update() {
		this.removeAll();
		if (!this.keepColumnCount || !this.keepRowCount) {
			int count = (int) this.missionCardPanelList.stream().filter(JMissionCardPanel::isPanelDisplayable).count();
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
				JMissionCardPanel missionPanel = this.missionCardPanelList.get(i);
				if (missionPanel.isPanelDisplayable()) {
					this.add(missionPanel);
					continue;
				}
				max++;
			} else {
				JPanel panel = new JPanel();
				panel.setBackground(Color.LIGHT_GRAY);
				this.add(panel);
			}
		}
	}

	public int indexOf(JMissionCardPanel panel) {
		return this.missionCardPanelList.indexOf(panel);
	}

	public void movePanel(int currentIndex, int direction) {
		if (currentIndex != -1 && this.testAction(currentIndex, direction)) {
			int newIndex = this.newIndex(currentIndex, direction);
			JMissionCardPanel missionCards = this.missionCardPanelList.remove(currentIndex);
			this.missionCardPanelList.add(newIndex, missionCards);
			this.update();
			this.revalidate();
			this.repaint();
		}
	}

	public int newIndex(int currentIndex, int direction) {
		if (!this.testAction(currentIndex, direction)) { return currentIndex; }
		switch (direction) {
		case GridBagConstraints.FIRST_LINE_START:
			return 0;
		case GridBagConstraints.NORTH:
			return currentIndex - this.getColumnCount();
		case GridBagConstraints.EAST:
			return currentIndex + 1;
		case GridBagConstraints.SOUTH:
			return Math.min(currentIndex + this.getColumnCount(), this.missionCardPanelList.size() - 1);
		case GridBagConstraints.WEST:
			return currentIndex - 1;
		case GridBagConstraints.LAST_LINE_END:
			return this.missionCardPanelList.size() - 1;
		default:
			return currentIndex;
		}
	}

	public boolean testAction(int index, int direction) {
		if (index == -1) { return false; }
		switch (direction) {
		case GridBagConstraints.NORTH:
			return index >= this.getColumnCount();
		case GridBagConstraints.LAST_LINE_END:
		case GridBagConstraints.EAST:
			return index < this.missionCardPanelList.size() - 1;
		case GridBagConstraints.SOUTH:
			return index < this.getColumnCount() * (this.getRowCount() - 1);
		case GridBagConstraints.FIRST_LINE_START:
		case GridBagConstraints.WEST:
			return index > 0;
		default:
			return false;
		}
	}

	public void sortMissionCardPanels(int[] newLocations, int[] oldLocations) {
		JMissionCardPanel[] newList = new JMissionCardPanel[newLocations.length];
		for (int i = 0; i < newLocations.length; i++) {
			newList[newLocations[i]] = this.missionCardPanelList.get(oldLocations[i]);
		}
		this.missionCardPanelList = new ArrayList<>(List.of(newList));
		this.update();
	}

	public List<JMissionCardPanel> getMissionCardPanelList() {
		return this.missionCardPanelList;
	}

	public int getRowCount() {
		return this.rowCount;
	}

	public int getColumnCount() {
		return this.columnCount;
	}

}
