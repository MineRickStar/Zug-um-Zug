package game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import algorithm.AlgorithmSettings;
import connection.Connection;
import connection.SingleConnection;
import game.board.Location;
import game.cards.TransportMode;

public class Path implements Iterable<SingleConnection>, Cloneable {

	private List<SingleConnection> connectionPath;
	private int length;
	private int connections;
	private EnumMap<TransportMode, Integer> modes;
	private Location lastLocation;
	private AlgorithmSettings settings;

	public Path(int maxConnections, Location start, AlgorithmSettings settings) {
		this.connectionPath = new ArrayList<>(maxConnections);
		this.length = 0;
		this.connections = 0;
		this.modes = this.calculateModes();
		this.lastLocation = start;
		this.settings = settings;
	}

	private Path(List<SingleConnection> connectionPath, int length, int connections, EnumMap<TransportMode, Integer> modes, Location lastLocation, AlgorithmSettings settings) {
		this.connectionPath = new ArrayList<>(connectionPath);
		this.length = length;
		this.connections = connections;
		this.modes = new EnumMap<>(modes);
		this.lastLocation = lastLocation;
		this.settings = settings;
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
		this.lastLocation = connection.parentConnection.getNextLocation(this.lastLocation);
	}

	public SingleConnection get(int index) {
		return this.connectionPath.get(index);
	}

	public EnumMap<TransportMode, Integer> getModes() {
		return this.modes;
	}

	public int getLength() {
		if (this.length == 0) {
			this.calculate();
		}
		return this.length;
	}

	public int getConnections() {
		if (this.connections == 0) {
			this.calculate();
		}
		return this.connections;
	}

	public boolean isPathPossible() {
		if (this.modes.isEmpty()) {
			this.calculate();
		}
		return !this.modes.entrySet().stream().anyMatch(entry -> entry.getValue() > this.settings.carrigesLeft.get(entry.getKey()));
	}

	private void calculate() {
		this.connectionPath.forEach(connection -> {
			if (!this.settings.availableConnections.contains(connection)) {
				this.length += connection.parentConnection.length;
				this.connections++;
				this.modes.put(connection.transportMode, this.modes.get(connection.transportMode) + connection.parentConnection.length);
			}
		});
	}

	public List<SingleConnection> getConnectionPath() {
		return this.connectionPath;
	}

	public boolean containsLocation(Location location) {
		return this.connectionPath.stream().anyMatch(s -> s.parentConnection.fromLocation.equals(location) || s.parentConnection.toLocation.equals(location));
	}

	public boolean contains(SingleConnection connection) {
		return this.connectionPath.contains(connection);
	}

	public boolean containsConnection(Connection connection) {
		return Stream.of(connection.singleConnections).anyMatch(this::contains);
	}

	public Location getLastLocation() {
		return this.lastLocation;
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
		return "Path [length=" + this.length + ", connections=" + this.connections + ", Path="
				+ this.connectionPath.stream().map(s -> s.parentConnection.getCompressedString()).collect(Collectors.joining(", ")) + "]";
	}

	@Override
	public Path clone() {
		return new Path(this.connectionPath, this.length, this.connections, this.modes, this.lastLocation, this.settings);
	}

}
