package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import application.Application;

public class AllJMissionCardsPanel extends JPanel implements Scrollable {

	private static final long serialVersionUID = 2579072798714564454L;

	private final int padding = 10;

	private final int viewPortRowCount;

	private boolean viewPortRowCountSet;

	private int rowCount;
	private int columnCount;

	private boolean keepRowCount;
	private boolean keepColumnCount;

	private List<JMissionCardPanel> missionCardPanelList;

	private Map<Integer, String> mapper;

	public AllJMissionCardsPanel() {
		this("");
		this.viewPortRowCountSet = false;
	}

	public AllJMissionCardsPanel(String title) {
		this(3, title);
		this.viewPortRowCountSet = false;
	}

	public AllJMissionCardsPanel(int viewPortRowCount, String title) {
		this(-1, 4, viewPortRowCount, title);
	}

	public AllJMissionCardsPanel(int rowCount, int columnCount, String title) {
		this(rowCount, columnCount, 3, title);
		this.viewPortRowCountSet = false;
	}

	public AllJMissionCardsPanel(int rowCount, int columnCount, int viewPortRowCount, String title) {
		this.setBackground(Color.LIGHT_GRAY);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(this.padding, this.padding, this.padding, this.padding), title));
		this.viewPortRowCount = viewPortRowCount;
		this.viewPortRowCountSet = true;
		this.rowCount = rowCount;
		this.keepRowCount = rowCount != -1;
		this.columnCount = columnCount;
		this.keepColumnCount = columnCount != -1;
		this.missionCardPanelList = new ArrayList<>();
		this.mapper = new HashMap<>();
		this.mapper.put(-1, "");
		this.setLayout(new GridLayout(1, 1, this.padding, this.padding));
	}

	public void setMapper(Map<Integer, String> mapper) {
		this.mapper = mapper;
		this.setLayout(new GridLayout(mapper.size(), 1, this.padding, this.padding));
	}

	private void addJMissionCardPanel(JMissionCardPanel missionCard, boolean duplicate, boolean update) {
		if (!duplicate && this.missionCardPanelList.contains(missionCard)) { return; }
		this.missionCardPanelList.add(missionCard);
		if (update) {
			this.update();
		}
	}

	public void addMissionCard(JMissionCardPanel missionCard, boolean duplicate) {
		this.addJMissionCardPanel(missionCard, duplicate, true);
	}

	public void addMissionCards(JMissionCardPanel[] missionCardPanel, boolean duplicate) {
		for (JMissionCardPanel missionCard : missionCardPanel) {
			this.addJMissionCardPanel(missionCard, duplicate, false);
		}
		this.update();
	}

	public void addMissionCards(List<JMissionCardPanel> missionCards, boolean duplicate) {
		missionCards.forEach(m -> this.addJMissionCardPanel(m, duplicate, false));
		this.update();
	}

	public void update() {
		this.removeAll();
		Map<Integer, List<JMissionCardPanel>> panels = new HashMap<>();
		Map<Integer, JPanel> indexMap = new HashMap<>();
		for (int i : this.mapper.keySet()) {
			List<JMissionCardPanel> missionCardPanel = this.missionCardPanelList.stream().filter(JMissionCardPanel::isPanelDisplayable).filter(j -> j.getIndex() == i).toList();
			panels.put(i, missionCardPanel);

			if (this.keepColumnCount) {
				this.rowCount = (int) Math.ceil(missionCardPanel.size() / (double) this.columnCount);
			}
			if (this.keepRowCount) {
				this.columnCount = (int) Math.ceil(missionCardPanel.size() / (double) this.rowCount);
			}
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(this.rowCount, this.columnCount, this.padding, this.padding));
			panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), this.mapper.get(i)));
			indexMap.put(i, panel);
		}

		Iterator<Entry<Integer, JPanel>> it = indexMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, JPanel> entry = it.next();
			JPanel panel = entry.getValue();
			List<JMissionCardPanel> missionCardPanel = panels.get(entry.getKey());
			int max = Math.max(missionCardPanel.size() % this.columnCount, this.columnCount) * this.rowCount;
			for (int i = 0; i < max; i++) {
				if (missionCardPanel.size() > i) {
					JMissionCardPanel p = missionCardPanel.get(i);
					p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
					panel.add(p);
				} else {
					panel.add(new JPanel());
				}
				this.add(panel);
			}
		}
	}

	public int indexOf(JMissionCardPanel panel) {
		return this.missionCardPanelList.indexOf(panel);
	}

	public void movePanel(int currentIndex, int direction) {
		if ((currentIndex != -1) && this.testAction(currentIndex, direction)) {
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
			return index < (this.missionCardPanelList.size() - 1);
		case GridBagConstraints.SOUTH:
			return index < (this.getColumnCount() * (this.getRowCount() - 1));
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
			newList[oldLocations[i]] = this.missionCardPanelList.get(newLocations[i]);
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

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		int height = Integer.MAX_VALUE;
		if (!this.missionCardPanelList.isEmpty()) {
			if (this.viewPortRowCountSet) {
				if (this.missionCardPanelList.get(0).getHeight() != 0) {
					height = (this.missionCardPanelList.get(0).getHeight() + (2 * this.padding)) * this.viewPortRowCount;
				}
			} else {
				height = (int) Math.min(Application.frame.getHeight() * .25, (this.missionCardPanelList.get(0).getHeight() + (2 * this.padding)) * this.viewPortRowCount);
			}
		}
		Dimension supPref = super.getPreferredSize();
		return new Dimension(supPref.width, Math.min(supPref.height, height));
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
