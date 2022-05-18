package gui.dialog;

import javax.swing.JDialog;

import application.Application;

public class GameSettingsDialog extends JDialog {

	private static final long serialVersionUID = -7235310957220651240L;

	public GameSettingsDialog() {
		super(Application.frame, "Game Settings", true);

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

}
