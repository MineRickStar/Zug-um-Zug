package gui.gameStart;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import application.Application;
import game.Game;
import game.GameMap;
import game.Rules;
import game.cards.ColorCard.TransportMode;
import language.MyResourceBundle.LanguageKey;

public class MapPanel extends AbstractTabbedPanel {

	private static final long serialVersionUID = 5877627924696089813L;

	private JPanel infoPanel;
	private JLabel mapLabel;
	private JComboBox<GameMap> mapComboBox;

	private JPanel mapRulesPanelWrapper;
	private MapRulesPanel mapRulesPanel;

	private JButton reset;

	public MapPanel(GameStartDialog parent) {
		super(parent);
		this.infoPanel = new JPanel();
		this.mapLabel = new JLabel(Application.resources.getString(LanguageKey.MAP) + ": ");
		GameMap[] gameMaps = GameMap.getMaps().toArray(GameMap[]::new);
		this.mapRulesPanelWrapper = new JPanel(new GridLayout(1, 1));
		this.mapComboBox = new JComboBox<>(new DefaultComboBoxModel<>(gameMaps));
		this.mapComboBox.addActionListener(e -> {
			this.mapRulesPanel = new MapRulesPanel((GameMap) this.mapComboBox.getSelectedItem());
			this.mapRulesPanelWrapper.removeAll();
			this.mapRulesPanelWrapper.add(this.mapRulesPanel);
			this.revalidate();
			this.repaint();
			parent.pack();
		});
		this.mapComboBox.setSelectedItem(gameMaps[0]);
		this.reset = new JButton(Application.resources.getString(LanguageKey.RESET));
		this.reset.addActionListener(e -> this.mapRulesPanel.reset());
		this.layoutComponents();
	}

	@Override
	protected void layoutComponents() {
		this.infoPanel.add(this.mapLabel);
		this.infoPanel.add(this.mapComboBox);
		this.gbc.insets = new Insets(10, 10, 10, 10);

		this.gbc.anchor = GridBagConstraints.LINE_START;
		this.gbc.gridx = 0;
		this.gbc.gridy = 0;
		this.gbc.gridwidth = 2;
		this.add(this.infoPanel, this.gbc);

		this.gbc.fill = GridBagConstraints.VERTICAL;
		this.gbc.anchor = GridBagConstraints.LAST_LINE_START;
		this.gbc.gridwidth = 1;
		this.gbc.weightx = 1;
		this.gbc.gridy = 1;
		this.gbc.gridx = 0;
		this.add(this.mapRulesPanelWrapper, this.gbc);

		this.gbc.fill = GridBagConstraints.NONE;
		this.gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.gbc.gridx = 1;
		this.add(this.reset, this.gbc);
		super.addTabbing(3);
	}

	private class MapRulesPanel extends JPanel {
		private static final long serialVersionUID = 8544524552185533634L;

		private TransportMode[] transportModes;

		private TransportModePanel[] transportModePanels;

		private MapRulesPanel(GameMap gameMap) {
			super(new GridBagLayout());
			this.transportModes = gameMap.getTransportModes();
			this.transportModePanels = new TransportModePanel[this.transportModes.length];

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(10, 10, 10, 10);
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.gridy = 0;

			for (int i = 0; i < this.transportModes.length; i++) {
				TransportModePanel transportModePanel = new TransportModePanel(gameMap, this.transportModes[i]);
				this.transportModePanels[i] = transportModePanel;
				gbc.gridx = i;
				this.add(transportModePanel, gbc);
			}
		}

		private void reset() {
			for (TransportModePanel panel : this.transportModePanels) {
				panel.reset();
			}
		}

	}

	private class TransportModePanel extends JPanel {
		private static final long serialVersionUID = -2870320371101278555L;

		private final TransportMode transportMode;

		private JLabel carrigeCountLabel;
		private JLabel colorCardCountLabel;
		private JLabel locomotiveCardCountLabel;
		private List<JLabel> pointsLabelList;

		private MySpinner carrigeCount;
		private MySpinner colorCardCount;
		private MySpinner locomotiveCardCount;
		private List<MySpinner> pointsList;

		private TransportModePanel(GameMap gameMap, TransportMode transportMode) {
			super(new GridBagLayout());
			this.transportMode = transportMode;
			this.setBorder(MapPanel.this.getTitleBorder(transportMode.getDisplayNameSingular()));
			int[] allLengths = gameMap.getLengths(null);
			int[] lengths = gameMap.getLengths(transportMode);

			this.carrigeCountLabel = new JLabel(Application.resources.getString(LanguageKey.CARRIGECOUNT));
			this.colorCardCountLabel = new JLabel(Application.resources.getString(LanguageKey.COLORCARDCOUNT));
			this.locomotiveCardCountLabel = new JLabel(Application.resources.getString(LanguageKey.LOCOMOTIVCARDCOUNT));
			this.pointsLabelList = IntStream.of(allLengths).mapToObj(length -> {
				JLabel label = new JLabel(String.format(Application.resources.getString(LanguageKey.POINTSFORLENGTH), length));
				label.setForeground(Color.LIGHT_GRAY);
				for (int i = 0; i < lengths.length; i++) {
					if (lengths[i] == length) {
						label.setForeground(Color.BLACK);
						break;
					}
				}
				return label;

			}).toList();

			Rules rules = Rules.getInstance();
			this.carrigeCount = new MySpinner(rules.getCarrigeCount(transportMode), 3, 100);
			this.colorCardCount = new MySpinner(rules.getColorCardCount(transportMode), 2, 100);
			this.locomotiveCardCount = new MySpinner(rules.getLocomotiveCardCount(transportMode), 2, 100);
			this.pointsList = IntStream.of(allLengths).mapToObj(length -> {
				MySpinner spinner = new MySpinner(rules.getPointsConnection(transportMode)[length - 1], 0, 50);
				for (int i = 0; i < lengths.length; i++) {
					if (lengths[i] == length) { return spinner; }
				}
				spinner.setEnabled(false);
				spinner.setValue(0);
				return spinner;
			}).collect(Collectors.toList());

			this.layoutComponents();
		}

		private void layoutComponents() {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(2, 5, 2, 5);
			gbc.anchor = GridBagConstraints.LINE_START;

			JLabel[] labels = new JLabel[this.pointsLabelList.size() + 3];
			labels[0] = this.carrigeCountLabel;
			labels[1] = this.colorCardCountLabel;
			labels[2] = this.locomotiveCardCountLabel;
			for (int i = 0; i < this.pointsLabelList.size(); i++) {
				labels[i + 3] = this.pointsLabelList.get(i);
			}

			MySpinner[] spinner = new MySpinner[this.pointsList.size() + 3];
			spinner[0] = this.carrigeCount;
			spinner[1] = this.colorCardCount;
			spinner[2] = this.locomotiveCardCount;
			for (int i = 0; i < this.pointsList.size(); i++) {
				spinner[i + 3] = this.pointsList.get(i);
			}

			MapPanel.this.nextColumn(this, gbc, labels);
			MapPanel.this.nextColumn(this, gbc, spinner);
		}

		private void reset() {
			this.carrigeCount.reset();
			this.colorCardCount.reset();
			this.locomotiveCardCount.reset();
			this.pointsList.forEach(MySpinner::reset);
		}

		private void save() {
			Rules rules = Rules.getInstance();
			rules.setCarrigeCount(this.transportMode, this.carrigeCount.getIntValue());
			rules.setColorCardCount(this.transportMode, this.colorCardCount.getIntValue());
			rules.setLocomotiveCardCount(this.transportMode, this.locomotiveCardCount.getIntValue());
			rules.setPointsConnection(this.transportMode, this.pointsList.stream().mapToInt(MySpinner::getIntValue).toArray());
		}

	}

	@Override
	public String getDisplayName() {
		return Application.resources.getString(LanguageKey.MAP);
	}

	@Override
	public boolean isAllCorrect() {
		return true;
	}

	@Override
	public boolean save() {
		TransportMode[] transportModes = this.mapRulesPanel.transportModes;
		for (TransportModePanel panel : this.mapRulesPanel.transportModePanels) {
			panel.save();
		}
		List<TransportMode> modes = new ArrayList<>(List.of(TransportMode.values()));
		modes.removeAll(List.of(transportModes));
		Rules rules = Rules.getInstance();
		modes.forEach(t -> {
			rules.setCarrigeCount(t, 0);
			rules.setColorCardCount(t, 0);
			rules.setLocomotiveCardCount(t, 0);
			rules.setPointsConnection(t, new int[0]);
		});
		Game.getInstance().setMap((GameMap) this.mapComboBox.getSelectedItem());
		return true;
	}

}
