package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import game.cards.MissionCard;

public abstract class AllJMissionCardsPanel extends JPanel {

	private static final long serialVersionUID = 2579072798714564454L;

	protected final int padding = 5;

	protected List<JMissionCardPanel> missionCardPanelList;

	private Map<Integer, String> mapper;

	public AllJMissionCardsPanel() {
		this.setBackground(Color.LIGHT_GRAY);
		this.missionCardPanelList = new ArrayList<>();
		this.mapper = new HashMap<>();
		this.mapper.put(-1, "");
		this.setLayout(new GridBagLayout());
	}

	public final void setMapper(Map<Integer, String> mapper) {
		this.mapper = mapper;
		this.setLayout(new GridBagLayout());
	}

	private void addJMissionCardPanel(JMissionCardPanel missionCard, boolean update) {
		if (this.missionCardPanelList.contains(missionCard)) { return; }
		this.missionCardPanelList.add(missionCard);
		if (update) {
			this.update();
		}
	}

	public final void addMissionCard(JMissionCardPanel missionCard) {
		this.addJMissionCardPanel(missionCard, true);
	}

	public final void addMissionCards(JMissionCardPanel[] missionCardPanel) {
		for (JMissionCardPanel missionCard : missionCardPanel) {
			this.addJMissionCardPanel(missionCard, false);
		}
		this.update();
	}

	public final void addMissionCards(List<? extends JMissionCardPanel> missionCards) {
		missionCards.forEach(m -> this.addJMissionCardPanel(m, false));
		this.update();
	}

	protected abstract int getRowCount();

	protected abstract int getColumnCount();

	public final void update() {
		this.removeAll();
		Map<Integer, List<JMissionCardPanel>> panels = new HashMap<>();
		Map<Integer, JPanel> indexMap = new HashMap<>();
		for (int i : this.mapper.keySet()) {
			List<JMissionCardPanel> missionCardPanel = this.missionCardPanelList.stream().filter(JMissionCardPanel::isPanelDisplayable).filter(j -> j.getIndex() == i).toList();
			panels.put(i, missionCardPanel);

			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), this.mapper.get(i)));
			indexMap.put(i, panel);
		}
		GridBagConstraints missionPanelGBC = new GridBagConstraints();
		missionPanelGBC.gridx = 0;
		missionPanelGBC.anchor = GridBagConstraints.LINE_START;
		missionPanelGBC.fill = GridBagConstraints.HORIZONTAL;
		missionPanelGBC.weightx = 1;

		Iterator<Entry<Integer, JPanel>> it = indexMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, JPanel> entry = it.next();
			JPanel panel = entry.getValue();
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(this.padding, this.padding, this.padding, this.padding);
			gbc.anchor = GridBagConstraints.LINE_START;
			List<JMissionCardPanel> missionCardPanel = panels.get(entry.getKey());
			int size = missionCardPanel.size();
			for (int i = 0; i < size; i++) {
				int columnCount = this.getColumnCount();
				gbc.gridx = i % columnCount;
				gbc.gridy = i / columnCount;
				JMissionCardPanel p = missionCardPanel.get(i);
				p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				panel.add(p, gbc);
			}
			if (size < this.getColumnCount()) {
				gbc.weightx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				panel.add(new JPanel(), gbc);
			}
			missionPanelGBC.gridy++;
			this.add(panel, missionPanelGBC);
		}
	}

	protected final int getCount() {
		return this.missionCardPanelList.size();
	}

	public List<JMissionCardPanel> getMissionCardPanelList() {
		return this.missionCardPanelList;
	}

	public void setMissionCardPanelList(List<JMissionCardPanel> panel) {
		this.missionCardPanelList = panel;
	}

	public final void sortMissionCardPanels(Map<Integer, MissionCard> map) {
		JMissionCardPanel[] panel = new JMissionCardPanel[this.missionCardPanelList.size()];
		Iterator<Entry<Integer, MissionCard>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, MissionCard> next = it.next();
			panel[next.getKey()] = this.missionCardPanelList.stream().filter(j -> j.missionCard.equals(next.getValue())).findAny().orElse(null);
		}
		this.missionCardPanelList = new ArrayList<>(List.of(panel));
		this.update();
	}

}
