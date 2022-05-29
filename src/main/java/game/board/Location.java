package game.board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import connection.Connection;
import game.Game;

public class Location implements Comparable<Location> {

	public record LocationList(List<Location> locations) {
		public Location start() {
			return this.locations.get(0);
		}

		public Location end() {
			return this.locations.get(this.locations.size() - 1);
		}
	}

	public final UUID ID;
	public final String name;
	public final String abbreviation;
	public final Point point;

	private final List<Connection> connectionsFromHere;

	public Location(String name, Point point, String abbreviation) {
		this.ID = UUID.randomUUID();
		this.name = name;
		this.point = point;
		this.abbreviation = abbreviation;
		this.connectionsFromHere = new ArrayList<>();
	}

	public void addConnection(Connection connection) {
		this.connectionsFromHere.add(connection);
	}

	public List<Connection> getConnectionsFromHere() {
		return this.connectionsFromHere;
	}

	public static final List<Location> nonPathLocations = new ArrayList<>();

	public static boolean isPathNode(Location location) {
		if (Location.nonPathLocations.isEmpty()) {
			Location.loadNonPathLocations();
		}
		return !Location.nonPathLocations.contains(location);
	}

	private static void loadNonPathLocations() {
		Game instance = Game.getInstance();
		Location.nonPathLocations.add(instance.getLocation("Österreich"));
		Location.nonPathLocations.add(instance.getLocation("Schweiz"));
		Location.nonPathLocations.add(instance.getLocation("Frankreich"));
		Location.nonPathLocations.add(instance.getLocation("Niederlande"));
		Location.nonPathLocations.add(instance.getLocation("Dänemark"));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.point);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (obj instanceof Location other) { return this.ID.equals(other.ID); }
		return false;
	}

	@Override
	public String toString() {
		return "Location [name=" + this.name + "]";
	}

	@Override
	public int compareTo(Location o) {
		return this.ID.compareTo(o.ID);
	}
}
