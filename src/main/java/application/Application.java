package application;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

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

	@SuppressWarnings("unused")
	private static void printEncodings() {
		System.out.println("Default Charset=" + Charset.defaultCharset());
		System.out.println("file.encoding=" + Charset.defaultCharset().displayName());
		System.out.println("Default Charset in Use=" + new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding());
	}

}
