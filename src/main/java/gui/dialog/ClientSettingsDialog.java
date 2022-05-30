package gui.dialog;

import javax.swing.JDialog;

import application.Application;
import language.MyResourceBundle.LanguageKey;

public class ClientSettingsDialog extends JDialog {

	private static final long serialVersionUID = 6023109233528807648L;

	public ClientSettingsDialog() {
		super(Application.frame, Application.resources.getString(LanguageKey.CLIENTSETTINGS));
		this.pack();
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

}
