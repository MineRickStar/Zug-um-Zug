package application;

import java.awt.Color;

import javax.swing.SwingUtilities;

import game.Game;
import game.Player;
import game.cards.MyColor;
import gui.MyFrame;

public class Application {

	public static MyFrame frame;

	public static Player player;

	public static void main(String[] args) {
		Application.player = Game.getInstance().addPlayer("Patrick", MyColor.GREEN);
		Game.getInstance().addComputer("Computer", MyColor.RED);
		SwingUtilities.invokeLater(() -> Application.frame = new MyFrame());
	}

	public static Color getComplementaryColor(Color color) {
		if (color == Color.WHITE) { return Color.BLACK; }
		if (color == Color.BLACK) { return Color.WHITE; }
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int maxRGB = Math.max(r, Math.max(g, b));
		int minRGB = Math.min(r, Math.min(g, b));
		int addition = maxRGB + minRGB;
		return new Color(addition - r, addition - g, addition - b);
	}

}
