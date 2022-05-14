package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
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
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setLayout(new GridBagLayout());

		JPanel missionCardPanel = new JPanel(new GridBagLayout());
		GridBagConstraints missionCardgbc = new GridBagConstraints();
		missionCardgbc.insets = new Insets(10, 10, 10, 10);
		missionCardgbc.gridx = 0;
		missionCardgbc.gridy = 0;

		for (MissionCard element : missionCardList) {
			MissionPanel missionPanel = new MissionPanel(element);
			this.missionPanels.add(missionPanel);
			missionCardgbc.gridy++;
			missionCardPanel.add(missionPanel, missionCardgbc);
		}

		JButton okButton = new JButton("OK");
		okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "OK");
		okButton.getActionMap().put("OK", new AbstractAction() {
			private static final long serialVersionUID = 6740774083608708284L;

			@Override
			public void actionPerformed(ActionEvent e) {
				MissionCardHelperDialog.this.testMissionCards();
			}
		});
		okButton.addActionListener(e -> this.testMissionCards());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridy = 0;
		this.add(missionCardPanel, gbc);
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.gridy = 1;
		this.add(okButton, gbc);

		((JPanel) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK), "ALL");
		((JPanel) this.getContentPane()).getActionMap().put("ALL", new AbstractAction() {

			private static final long serialVersionUID = 1251851433534082983L;

			@Override
			public void actionPerformed(ActionEvent e) {
				MissionCardHelperDialog.this.missionPanels.forEach(m -> m.selectMission.setSelected(true));
			}
		});

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private void testMissionCards() {
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
