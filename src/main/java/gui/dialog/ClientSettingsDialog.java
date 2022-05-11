package gui.dialog;

import javax.swing.JDialog;

import application.Application;

public class ClientSettingsDialog extends JDialog {

	private static final long serialVersionUID = 6023109233528807648L;

	public ClientSettingsDialog() {
		super(Application.frame, "Client Settings");
		this.pack();
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

}
