package application;

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

}
