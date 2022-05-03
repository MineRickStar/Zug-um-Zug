package application;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import application.Algorithm.AlgorithmSettings;
import application.Algorithm.LocationPair;
import game.Game;
import game.Path;
import game.Player;
import game.board.Location;
import game.board.SingleConnection;
import game.cards.MyColor;
import gui.MyFrame;

class AlgorithmTest {

	@Test
	void test() {
		Location l1 = Game.getInstance().getLocation("Frankfurt");
		Location l2 = Game.getInstance().getLocation("Passau");
		Location l3 = Game.getInstance().getLocation("Dänemark");
		Location l4 = Game.getInstance().getLocation("Schweiz");

		LocationPair p = new LocationPair(l1, l2);
		LocationPair p1 = new LocationPair(l3, l4);

		AlgorithmSettings settings = new AlgorithmSettings(new Player("TestPlayer", MyColor.BLACK));
		Application.frame = new MyFrame();

		List<Path> paths = Algorithm.findShortestPath(new ArrayList<>(List.of(p, p1)), settings);
		Assert.assertFalse("No Path found", paths.size() == 0);
		Path first = paths.get(0);
		SingleConnection con = first.get(0);
		Assert.assertEquals("Not equal", con.parentConnection.toLocation, l3);
	}

}
