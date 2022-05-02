package game.board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import game.Game;

public class Location implements Comparable<Location> {

	public static final List<Location> nonPathLocations = new ArrayList<>();

	static {
		Game instance = Game.getInstance();
		Location.nonPathLocations.add(instance.getLocation("Österreich"));
		Location.nonPathLocations.add(instance.getLocation("Schweiz"));
		Location.nonPathLocations.add(instance.getLocation("Frankreich"));
		Location.nonPathLocations.add(instance.getLocation("Niederlande"));
		Location.nonPathLocations.add(instance.getLocation("Dänemark"));
	}

	public final String name;
	public final Point point;

	private final List<Connection> connectionsFromHere;

	public Location(String name, Point point) {
		this.name = name;
		this.point = point;
		this.connectionsFromHere = new ArrayList<>();
	}

	public void addConnection(Connection connection) {
		this.connectionsFromHere.add(connection);
	}

	public List<Connection> getConnectionsFromHere() {
		return this.connectionsFromHere;
	}

	public static boolean isPathNode(Location location) {
		return !Location.nonPathLocations.contains(location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.point);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if ((obj == null) || (this.getClass() != obj.getClass())) { return false; }
		Location other = (Location) obj;
		return Objects.equals(this.name, other.name) && Objects.equals(this.point, other.point);
	}

	@Override
	public String toString() {
		return "Location [name=" + this.name + "]";
	}

	@Override
	public int compareTo(Location o) {
		return this.name.compareTo(o.name);
	}
}
