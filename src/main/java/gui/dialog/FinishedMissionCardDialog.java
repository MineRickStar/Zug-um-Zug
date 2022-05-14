package gui.dialog;

import java.awt.Color;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

import application.Application;
import game.cards.MissionCard;
import gui.AllJMissionCardsPanel;
import gui.IMissionCardPanel;
import gui.JMissionCardPanel;

public class FinishedMissionCardDialog extends JDialog {

	private static final long serialVersionUID = -693411129322131362L;

	private AllJMissionCardsPanel allMissionCardsPanel;

	public FinishedMissionCardDialog(List<MissionCard> missionCards) {
		super(Application.frame, "Finished Missioncards", false);
		this.allMissionCardsPanel = new AllJMissionCardsPanel();
		this.allMissionCardsPanel.addAllMissionPanel(missionCards.stream().map(JMissionCardPanelCheckBox::new).map(j -> (IMissionCardPanel) j).toList());
		this.add(this.allMissionCardsPanel);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private class JMissionCardPanelCheckBox extends JMissionCardPanel {

		private static final long serialVersionUID = -5902959026679675859L;
		private JCheckBox box;

		public JMissionCardPanelCheckBox(MissionCard missionCard) {
			super(missionCard);
			this.box = new JCheckBox("Show Mission");
			this.box.setBackground(Color.WHITE);
			this.box.setFocusable(false);
			this.box.addActionListener(e -> {
				if (this.box.isSelected()) {
					// TODO Show Mission
				}
			});
			this.gbc.gridy++;
			this.add(this.box, this.gbc);
		}

	}

}
