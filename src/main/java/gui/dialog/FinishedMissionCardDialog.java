package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import application.Application;
import game.Game;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;
import gui.AllJMissionCardsPanel;
import gui.JMissionCardPanel;

public class FinishedMissionCardDialog extends JDialog {

	private static final long serialVersionUID = -693411129322131362L;

	private static FinishedMissionCardDialog instance;

	public static void create() {
		if (FinishedMissionCardDialog.instance == null) {
			FinishedMissionCardDialog.instance = new FinishedMissionCardDialog();
		}
		FinishedMissionCardDialog.instance.update();
		FinishedMissionCardDialog.instance.setVisible(true);
	}

	private JScrollPane missioCardScrollPane;

	private AllJMissionCardsPanel allMissionCardsPanel;

	private FinishedMissionCardDialog() {
		super(Application.frame, "Finished Missioncards", false);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.allMissionCardsPanel = new AllJMissionCardsPanel(8, "");
		this.missioCardScrollPane = new JScrollPane(this.allMissionCardsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.missioCardScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.add(this.missioCardScrollPane);
		this.setResizable(false);
	}

	private void update() {
		Game.getInstance().getInstancePlayer().getFinishedMissionCards().stream().map(FinishedJMissionCardPanel::new).forEach(j -> this.allMissionCardsPanel.addMissionCard(j, false));
		this.allMissionCardsPanel
				.setMapper(Game.getInstance().getInstancePlayer().getFinishedMissionCards().stream().map(m -> m.distance).distinct().collect(Collectors.toMap(Distance::ordinal, i -> i.cardLength)));
		this.allMissionCardsPanel.update();
		this.missioCardScrollPane.revalidate();
		this.pack();
		this.setLocationRelativeTo(Application.frame);
	}

	private class FinishedJMissionCardPanel extends JMissionCardPanel {

		private static final long serialVersionUID = -5902959026679675859L;
		private JCheckBox box;

		public FinishedJMissionCardPanel(MissionCard missionCard) {
			super(missionCard);
			this.box = new JCheckBox("Show Mission");
			this.box.setBackground(Color.WHITE);
			this.box.setFocusable(false);
			this.box.addActionListener(e -> {
				if (this.box.isSelected()) {
					// TODO Show selected Finished Mission
				}
			});
			this.add(this.box, BorderLayout.SOUTH);
		}

		@Override
		public int getIndex() {
			return this.missionCard.distance.ordinal();
		}

		@Override
		public boolean isPanelDisplayable() {
			return true;
		}

	}

}
