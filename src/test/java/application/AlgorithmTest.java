package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import algorithm.Algorithm;
import algorithm.AlgorithmSettings;
import game.Game;
import game.Player;
import game.board.Location;
import game.board.Location.LocationList;
import game.cards.MyColor;

class AlgorithmTest {

	@Test
	void test() {
		List<Location> locations = Game.getInstance().getLocations();

		AlgorithmSettings settings = new AlgorithmSettings(new Player("TestPlayer", MyColor.BLACK));
		settings.pathSegments = 10;

		for (int i = 0; i < 1000; i++) {
			Collections.shuffle(locations);
			List<LocationList> l = new ArrayList<>(List.of(new LocationList(locations.subList(0, 2)), new LocationList(locations.subList(4, 6))));
			Algorithm.findShortestPath(l, settings);
			System.out.println("count: " + i);
		}

	}

}
