package application;

import javax.swing.SwingUtilities;

import gui.MyFrame;
import gui.dialog.EditMissionCardDialog;
import gui.dialog.FinishedMissionCardDialog;

public class Application {

	public static MyFrame frame;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> Application.frame = new MyFrame());
	}

	public static void createNewMssionCardEditor() {
		EditMissionCardDialog.create();
	}

	public static void createNewFinishedMissionCardDialog() {
		FinishedMissionCardDialog.create();
	}

}
