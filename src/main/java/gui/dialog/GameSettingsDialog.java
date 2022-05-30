package gui.dialog;

import javax.swing.JDialog;

import application.Application;
import language.MyResourceBundle.LanguageKey;

public class GameSettingsDialog extends JDialog {

	private static final long serialVersionUID = -7235310957220651240L;

	public GameSettingsDialog() {
		super(Application.frame, Application.resources.getString(LanguageKey.GAMESETTINGS), true);

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

}
