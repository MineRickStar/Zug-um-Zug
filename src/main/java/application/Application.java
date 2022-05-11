package application;

import javax.swing.SwingUtilities;

import gui.MyFrame;

public class Application {

	public static MyFrame frame;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> Application.frame = new MyFrame());
	}

}
