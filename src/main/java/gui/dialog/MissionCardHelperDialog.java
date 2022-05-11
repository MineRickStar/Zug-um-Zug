package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import game.Rules;
import game.cards.MissionCard;
import gui.JMissionCardPanel;

public class MissionCardHelperDialog extends JDialog {

	private static final long serialVersionUID = 1028232963445078845L;

	private List<MissionPanel> missionPanels = new ArrayList<>();

	public List<SingleConnection[]> paths;

	public MissionCardHelperDialog(List<MissionCard> missionCardList) {
		super(Application.frame, "Missioncard selection");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLayout(new GridBagLayout());

		JPanel missionCardPanel = new JPanel(new GridLayout(missionCardList.size(), 2, 10, 10));

		for (MissionCard element : missionCardList) {
			MissionPanel missionPanel = new MissionPanel(element);
			this.missionPanels.add(missionPanel);
			missionCardPanel.add(missionPanel);
		}

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			int count = Game.getInstance().getInstancePlayer().getMissionCards().size() == 0 ? Rules.getInstance().getFirstMissionCardsKeeping() : Rules.getInstance().getDefaultMissionCardsKeeping();
			if (this.missionPanels.stream().filter(MissionPanel::isMissionSelected).count() < count) {
				JOptionPane.showMessageDialog(this, String.format("Please select at least %d Missions", count));
				return;
			}
			MissionCard[] missionCards = this.missionPanels.stream().filter(MissionPanel::isMissionSelected).map(p -> p.missionCardPanel.missionCard).toArray(MissionCard[]::new);
			Game.getInstance().getInstancePlayer().addMissionCards(missionCards);
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

	public class MissionPanel extends JPanel {
		private static final long serialVersionUID = 3577682247722769563L;

		public JMissionCardPanel missionCardPanel;
		public JCheckBox selectMission;

		public MissionPanel(MissionCard mission) {
			super(new GridBagLayout());
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
			this.add(this.selectMission, gbc);
		}

		public boolean isMissionSelected() {
			return this.selectMission.isSelected();
		}
	}

}
