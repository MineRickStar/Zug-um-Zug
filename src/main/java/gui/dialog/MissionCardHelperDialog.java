package gui.dialog;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import application.Application;
import connection.SingleConnection;
import game.Game;
import game.Rules;
import game.cards.MissionCard;
import gui.DefaultAllJMissionCardsPanel;
import gui.JMissionCardPanel;
import language.MyResourceBundle.LanguageKey;

public class MissionCardHelperDialog extends JDialog {

	private static final long serialVersionUID = 1028232963445078845L;

	private DefaultAllJMissionCardsPanel missionCardPanel;

	private List<MissionPanel> selectedCards;

	public List<SingleConnection[]> paths;

	public MissionCardHelperDialog(List<MissionCard> missionCardList) {
		super(Application.frame, Application.resources.getString(LanguageKey.MISSIONCARDSELECTION));
		this.setLayout(new GridBagLayout());
		this.setUndecorated(true);
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		Application.addCTRLShortcut(contentPane, KeyEvent.VK_A, e -> {
			MissionCardHelperDialog.this.selectedCards = new ArrayList<>(MissionCardHelperDialog.this.missionCardPanel.getMissionCardPanelList().stream().map(j -> (MissionPanel) j).toList());
			MissionCardHelperDialog.this.selectedCards.forEach(m -> m.selectMission.setSelected(true));
		});
		this.missionCardPanel = new DefaultAllJMissionCardsPanel(-1, 1);
		this.selectedCards = new ArrayList<>(Rules.getInstance().getMissionCardsDrawing());

		this.missionCardPanel.addMissionCards(missionCardList.stream().map(MissionPanel::new).toList());

		JButton okButton = new JButton(Application.resources.getString(LanguageKey.OK));
		Application.addCTRLEnterShortcut(okButton, e -> MissionCardHelperDialog.this.testMissionCards());
		okButton.addActionListener(e -> this.testMissionCards());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(this.missionCardPanel, gbc);
		gbc.insets = new Insets(10, 0, 10, 0);
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
			JOptionPane.showMessageDialog(this, String.format(Application.resources.getString(LanguageKey.SELECTATLEASTMISSION), count));
			return;
		}
		MissionCard[] missionCards = this.selectedCards.stream().map(m -> m.missionCard).toArray(MissionCard[]::new);
		Game.getInstance().getInstancePlayer().addMissionCards(missionCards);
		this.dispose();
	}

	private class MissionPanel extends JMissionCardPanel {
		private static final long serialVersionUID = 3577682247722769563L;

		private JCheckBox selectMission;

		public MissionPanel(MissionCard missionCard) {
			super(missionCard, true);
			this.selectMission = new JCheckBox(Application.resources.getString(LanguageKey.SELECTMISSION));
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
