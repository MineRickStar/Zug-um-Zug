package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import application.Application;
import game.cards.MissionCard;
import gui.JMissionCardPanel;

public class FinishedMissionCardDialog extends JDialog implements ItemListener {

	private static final long serialVersionUID = -693411129322131362L;

	public FinishedMissionCardDialog(List<MissionCard> missionCards) {
		super(Application.frame, "Finished Missioncards", false);
		JPanel panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		missionCards.stream().map(MissionPanel::new).forEach(m -> {
			panel.add(m);
		});
		this.add(scrollPane);
		this.pack();
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	public class MissionPanel extends JPanel {
		private static final long serialVersionUID = 3577682247722769563L;

		public JMissionCardPanel missionCardPanel;
		public JCheckBox showMissionButton;

		public MissionPanel(MissionCard mission) {
			super(new GridBagLayout());
			this.showMissionButton = new JCheckBox("Show Mission");
			this.showMissionButton.addItemListener(FinishedMissionCardDialog.this);
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
		}

		public boolean isShowMissionSelected() {
			return this.showMissionButton.isSelected();
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {

	}

}
