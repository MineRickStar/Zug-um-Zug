package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import application.PropertyEvent;
import game.Game;

public class PlayerPanel extends JPanel implements IUpdatePanel {

	private static final long serialVersionUID = -5734164742630869042L;

	private final int padding = 10;

	private JColorCardPanel colorCardPanel;

	public PlayerPanel() {
		super(new GridBagLayout());

		this.colorCardPanel = new JColorCardPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(this.padding, this.padding, this.padding, this.padding);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(this.colorCardPanel, gbc);
	}

	@Override
	public void update(PropertyEvent propertyEvent) {
		switch (propertyEvent.property) {
		case COLORCARDADDED:
		case COLORCARDREMOVED:
		case MISSIONCARDEDITED:
			if (Game.getInstance().getInstancePlayer().equals(propertyEvent.player)) {
				this.colorCardPanel.updateColorCardPanel(this.getHeight() - (2 * this.padding));
			}
			break;
		case GAMESTART:
		case FRAMESIZECHANGED:
			this.colorCardPanel.updateColorCardPanel(this.getHeight() - (2 * this.padding));
			break;
		case MISSIONCARDDRAWN:
		case MISSIONCARDADDED:
		case MISSIONCARDFINISHED:
		case COLORCARDDRAWN:
		case PLAYERCHANGE:
		case CONNECTIONBOUGHT:
			break;
		}
	}

}
