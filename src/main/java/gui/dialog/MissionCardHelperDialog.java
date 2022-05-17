package gui.dialog;

import java.awt.BorderLayout;
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
import gui.AllJMissionCardsPanel;
import gui.JMissionCardPanel;

public class MissionCardHelperDialog extends JDialog {

	private static final long serialVersionUID = 1028232963445078845L;

	private AllJMissionCardsPanel missionCardPanel;

	private List<MissionPanel> selectedCards;

	public List<SingleConnection[]> paths;

	public MissionCardHelperDialog(List<MissionCard> missionCardList) {
		super(Application.frame, "Missioncard selection");
		this.setLayout(new GridBagLayout());
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		((JPanel) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK), "ALL");
		((JPanel) this.getContentPane()).getActionMap().put("ALL", new AbstractAction() {

			private static final long serialVersionUID = 1251851433534082983L;

			@Override
			public void actionPerformed(ActionEvent e) {
				MissionCardHelperDialog.this.selectedCards = new ArrayList<>(MissionCardHelperDialog.this.missionCardPanel.getMissionCardPanelList().stream().map(j -> (MissionPanel) j).toList());
				MissionCardHelperDialog.this.selectedCards.forEach(m -> m.selectMission.setSelected(true));
			}
		});
		this.missionCardPanel = new AllJMissionCardsPanel(-1, 1, "");
		this.selectedCards = new ArrayList<>(Rules.getInstance().getMissionCardsDrawing());

		for (MissionCard element : missionCardList) {
			MissionPanel missionPanel = new MissionPanel(element);
			this.missionCardPanel.addMissionCard(missionPanel);
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

		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(this.missionCardPanel, gbc);
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.gridy = 1;
		this.add(okButton, gbc);

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private void testMissionCards() {
		int count = Game.getInstance().getInstancePlayer().getMissionCards().size() == 0 ? Rules.getInstance().getFirstMissionCardsKeeping() : Rules.getInstance().getDefaultMissionCardsKeeping();
		if (this.selectedCards.size() < count) {
			JOptionPane.showMessageDialog(this, String.format("Please select at least %d Missions", count));
			return;
		}
		MissionCard[] missionCards = this.selectedCards.stream().map(m -> m.missionCard).toArray(MissionCard[]::new);
		Game.getInstance().getInstancePlayer().addMissionCards(missionCards);
		Application.frame.revalidate();
		Application.frame.repaint();
		this.dispose();
	}

	private class MissionPanel extends JMissionCardPanel {
		private static final long serialVersionUID = 3577682247722769563L;

		private JCheckBox selectMission;

		public MissionPanel(MissionCard missionCard) {
			super(missionCard, true);
			this.selectMission = new JCheckBox("Select Mission");
			this.selectMission.setFocusable(false);
			this.selectMission.addActionListener(e -> {
				if (this.selectMission.isSelected()) {
					MissionCardHelperDialog.this.selectedCards.add(this);
				} else {
					MissionCardHelperDialog.this.selectedCards.remove(this);
				}
			});
			this.add(this.selectMission, BorderLayout.EAST);
		}

		@Override
		public boolean isPanelDisplayable() {
			return true;
		}
	}

}
