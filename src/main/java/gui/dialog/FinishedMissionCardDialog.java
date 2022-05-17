package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import application.Application;
import application.Property;
import game.Game;
import game.cards.MissionCard;
import gui.AllJMissionCardsPanel;
import gui.JMissionCardPanel;

public class FinishedMissionCardDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = -693411129322131362L;

	private static FinishedMissionCardDialog instance;

	public static void create() {
		if (FinishedMissionCardDialog.instance == null) {
			FinishedMissionCardDialog.instance = new FinishedMissionCardDialog(Game.getInstance().getInstancePlayer().getFinishedMissionCards());
		}
		FinishedMissionCardDialog.instance.setVisible(true);
	}

	private AllJMissionCardsPanel allMissionCardsPanel;

	private FinishedMissionCardDialog(List<MissionCard> missionCards) {
		super(Application.frame, "Finished Missioncards", false);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDFINISHED, this);
		this.allMissionCardsPanel = new AllJMissionCardsPanel();
		missionCards.stream().map(JMissionCardPanelCheckBox::new).forEach(this.allMissionCardsPanel::addMissionCard);
		JScrollPane pane = new JScrollPane(this.allMissionCardsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setMinimumSize(new Dimension(0, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * .5)));
		this.add(pane);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(Application.frame);
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
					// TODO Show selected Finished Mission
				}
			});
			this.add(this.box, BorderLayout.SOUTH);
		}

		@Override
		public boolean isPanelDisplayable() {
			return true;
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Property.MISSIONCARDFINISHED.name().equals(evt.getPropertyName())) {
			this.allMissionCardsPanel.addMissionCard(new JMissionCardPanelCheckBox((MissionCard) evt.getNewValue()));
			this.allMissionCardsPanel.update();
			this.pack();
			this.setLocationRelativeTo(Application.frame);
		}
	}

}
