package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import application.PropertyEvent;

public class InfoPanel extends JPanel implements IUpdatePanel {

	private static final long serialVersionUID = -6143438348516086903L;

	private PublicPanel publicPanel;
	private PlayerPanel playerPanel;

	public InfoPanel() {
		super(new GridBagLayout());

		this.publicPanel = new PublicPanel();
		this.playerPanel = new PlayerPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;

		this.add(this.publicPanel, gbc);

		gbc.weighty = 1;
		gbc.gridy = 1;
		this.add(this.playerPanel, gbc);

	}

	@Override
	public void update(PropertyEvent propertyEvent) {
		this.publicPanel.update(propertyEvent);
		this.playerPanel.update(propertyEvent);
	}

}
