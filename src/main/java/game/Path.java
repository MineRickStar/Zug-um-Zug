package game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import game.board.Location;
import game.board.SingleConnection;
import game.cards.TransportMode;

public class Path implements Iterable<SingleConnection>, Cloneable {

	private List<SingleConnection> connectionPath;
	private int length;
	private EnumMap<TransportMode, Integer> modes;

	public Path() {
		this.connectionPath = new ArrayList<>();
		this.length = 0;
		this.modes = this.calculateModes();
	}

	public Path(List<SingleConnection> connections) {
		this.connectionPath = connections;
		this.length = connections.stream().map(s -> s.parentConnection.length).reduce((byte) 0, (t, u) -> (byte) (t + u));
		this.modes = this.calculateModes();
	}

	private Path(List<SingleConnection> connections, int length, EnumMap<TransportMode, Integer> modes) {
		this.connectionPath = new ArrayList<>(connections);
		this.length = length;
		this.modes = modes;
	}

	private EnumMap<TransportMode, Integer> calculateModes() {
		EnumMap<TransportMode, Integer> map = new EnumMap<>(TransportMode.class);
		for (TransportMode mode : TransportMode.values()) {
			map.put(mode, 0);
		}
		this.connectionPath.stream().forEach(s -> map.put(s.transportMode, map.get(s.transportMode) + s.parentConnection.length));
		return map;
	}

	public void addConnection(SingleConnection connection) {
		this.connectionPath.add(connection);
		this.length += connection.parentConnection.length;
		this.modes.put(connection.transportMode, this.modes.get(connection.transportMode) + connection.parentConnection.length);
	}

	public SingleConnection get(int index) {
		return this.connectionPath.get(index);
	}

	public EnumMap<TransportMode, Integer> getModes() {
		return this.modes;
	}

	public int getLength() {
		return this.length;
	}

	public int getConnections() {
		return this.connectionPath.size();
	}

	public boolean containsLocation(Location location) {
		return this.connectionPath.stream().anyMatch(s -> s.parentConnection.fromLocation.equals(location) || s.parentConnection.toLocation.equals(location));
	}

	public boolean contains(SingleConnection connection) {
		return this.connectionPath.contains(connection);
	}

	public Stream<SingleConnection> stream() {
		return this.connectionPath.stream();
	}

	@Override
	public Iterator<SingleConnection> iterator() {
		return new Iterator<SingleConnection>() {

			int count = 0;

			@Override
			public boolean hasNext() {
				return this.count < Path.this.connectionPath.size();
			}

			@Override
			public SingleConnection next() {
				return Path.this.connectionPath.get(this.count++);
			}
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.connectionPath, this.length);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		return Objects.equals(this.connectionPath, other.connectionPath) && this.length == other.length;
	}

	@Override
	public String toString() {
		return "Path [length=" + this.length + ", connections=" + this.getConnections() + ", Path="
				+ this.connectionPath.stream()
					.map(s -> s.parentConnection.fromLocation.name.substring(0, 2) + " -> " + s.parentConnection.toLocation.name.substring(0, 2))
					.collect(Collectors.joining(", "))
				+ "]";
	}

	@Override
	public Path clone() {
		return new Path(this.connectionPath, this.length, this.modes);
	}

}
