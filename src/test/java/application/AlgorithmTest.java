package application;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import algorithm.Algorithm;
import algorithm.AlgorithmSettings;
import connection.SingleConnection;
import connection.SingleConnectionPath;
import game.Game;
import game.Player;
import game.board.Location;
import game.board.Location.LocationList;
import game.cards.MyColor;
import gui.MyFrame;

class AlgorithmTest {

	@Test
	void test() {
		Location l1 = Game.getInstance().getLocation("Frankfurt");
		Location l2 = Game.getInstance().getLocation("Passau");
		Location l3 = Game.getInstance().getLocation("D�nemark");
		Location l4 = Game.getInstance().getLocation("Schweiz");

		LocationList p = new LocationList(List.of(l1, l2));
		LocationList p1 = new LocationList(List.of(l3, l4));

		AlgorithmSettings settings = new AlgorithmSettings(new Player("TestPlayer", MyColor.BLACK));
		Application.frame = new MyFrame();

		List<SingleConnectionPath> paths = Algorithm.findShortestPath(new ArrayList<>(List.of(p, p1)), settings);
		Assert.assertFalse("No Path found", paths.size() == 0);
		SingleConnectionPath first = paths.get(0);
		SingleConnection con = first.getConnectionPath().get(0);
		Assert.assertEquals("Not equal", con.parentConnection.toLocation, l3);
	}

}
