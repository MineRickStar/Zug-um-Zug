package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import application.Application;
import connection.SingleConnection;
import game.Game;
import game.Player;
import game.Rules;
import game.board.Location.LocationPair;
import game.cards.MissionCard;

public class MissionCardHelperFrame extends JDialog implements ItemListener {

	private static final long serialVersionUID = 1028232963445078845L;

	private List<MissionPanel> missionPanels = new ArrayList<>();

	public List<SingleConnection[]> paths;

	private final Player player;

	public MissionCardHelperFrame(Player player) {
		super(Application.frame, "Missioncard selection");
		this.player = player;
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				Game.getInstance().highlightConnection(null, player);
			}
		});
		this.setLayout(new GridBagLayout());

		List<MissionCard> array = player.getNewMissionCards();

		JPanel missionCardPanel = new JPanel(new GridLayout(array.size(), 2, 10, 10));

		for (MissionCard element : array) {
			MissionPanel missionPanel = new MissionPanel(element);
			this.missionPanels.add(missionPanel);
			missionCardPanel.add(missionPanel);
		}

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			int count = player.getMissionCards().size() == 0 ? Rules.getInstance().getFirstMissionCardsKeeping() : Rules.getInstance().getDefaultMissionCardsKeeping();
			if (this.missionPanels.stream().filter(MissionPanel::isSelected).count() < count) {
				JOptionPane.showMessageDialog(this, String.format("Please select at least %d Missions", count));
				return;
			}
			MissionCard[] missionCards = this.missionPanels.stream().filter(MissionPanel::isSelected).map(p -> p.missionCardPanel.missionCard).toArray(MissionCard[]::new);
			player.addMissionCards(missionCards);
			Application.frame.revalidate();
			Application.frame.repaint();
			this.dispose();
		});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);

		gbc.gridy = 0;
		this.add(missionCardPanel, gbc);
		gbc.gridy = 1;
		this.add(okButton, gbc);

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private class MissionPanel extends JPanel {

		private static final long serialVersionUID = 3577682247722769563L;

		public JMissionCardPanel missionCardPanel;
		public JCheckBox showMissionButton;
		public JCheckBox selectMission;

		public MissionPanel(MissionCard mission) {
			super(new GridBagLayout());
			this.showMissionButton = new JCheckBox("Show Mission");
			this.showMissionButton.addItemListener(MissionCardHelperFrame.this);
			this.selectMission = new JCheckBox("Select Mission");
			this.missionCardPanel = new JMissionCardPanel(mission);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(10, 10, 10, 10);
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			this.add(this.missionCardPanel, gbc);

			gbc.gridx++;
			this.add(this.showMissionButton, gbc);

			gbc.gridx++;
			this.add(this.selectMission, gbc);
		}

		public boolean isShowMissionSelected() {
			return this.showMissionButton.isSelected();
		}

		public boolean isSelected() {
			return this.selectMission.isSelected();
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		List<LocationPair> pairs = this.missionPanels.stream()
			.filter(MissionPanel::isShowMissionSelected)
			.map(p -> new LocationPair(p.missionCardPanel.missionCard.getFromLocation(), p.missionCardPanel.missionCard.getToLocation()))
			.toList();
		Game.getInstance().highlightConnection(new ArrayList<>(pairs), this.player);
	}

}
