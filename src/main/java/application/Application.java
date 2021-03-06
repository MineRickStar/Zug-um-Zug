package application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import gui.MyFrame;
import gui.dialog.EditMissionCardDialog;
import gui.dialog.FinishedMissionCardDialog;
import language.MyResourceBundle;

public class Application {

	public static final String NAME = "Zug um Zug";

	public static MyFrame frame;

	public static MyResourceBundle resources = (MyResourceBundle) ResourceBundle.getBundle("language.MyResources");

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> Application.frame = new MyFrame());
	}

	public static EditMissionCardDialog createNewMssionCardEditor() {
		return EditMissionCardDialog.create();
	}

	public static void createNewFinishedMissionCardDialog() {
		FinishedMissionCardDialog.create();
	}

	public static void addCTRLEnterShortcut(JComponent component, ActionListener listener) {
		Application.addCTRLShortcut(component, KeyEvent.VK_ENTER, listener);
	}

	public static void addCTRLShortcut(JComponent component, int keyCode, ActionListener listener) {
		Application.addShortcut(component, KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_DOWN_MASK), listener);
	}

	public static void addShortcut(JComponent component, int keyCode, ActionListener listener) {
		Application.addShortcut(component, KeyStroke.getKeyStroke(keyCode, 0), listener);
	}

	public static void addShortcut(JComponent component, KeyStroke stroke, ActionListener listener) {
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, listener.toString());
		component.getActionMap().put(listener.toString(), new AbstractAction() {
			private static final long serialVersionUID = 5838774419550988688L;

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.actionPerformed(e);
			}
		});
	}

	public static Dimension scaleDimension(Dimension dim, double scale) {
		dim.width *= scale;
		dim.height *= scale;
		return dim;
	}

	@SuppressWarnings("unused")
	private static void printEncodings() {
		System.out.println("Default Charset=" + Charset.defaultCharset());
		System.out.println("file.encoding=" + Charset.defaultCharset().displayName());
		System.out.println("Default Charset in Use=" + new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding());
	}

}
