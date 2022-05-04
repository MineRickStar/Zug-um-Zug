package connection;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import algorithm.AlgorithmSettings;
import game.board.Location;
import game.cards.TransportMode;

public class SingleConnectionPath {

	private List<SingleConnection> connectionPath;
	private int length;
	private int connections;
	private EnumMap<TransportMode, Integer> modes;
	private Location lastLocation;
	private AlgorithmSettings settings;

	public SingleConnectionPath(int maxConnections, Location start, AlgorithmSettings settings) {
		this.connectionPath = new ArrayList<>(maxConnections);
		this.length = 0;
		this.connections = 0;
		this.modes = new EnumMap<>(TransportMode.class);
		this.lastLocation = start;
		this.settings = settings;
	}

	public void addConnection(SingleConnection connection) {
		this.connectionPath.add(connection);
		this.lastLocation = connection.parentConnection.getNextLocation(this.lastLocation);
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

	public int getPoints() {
		return 0;
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
				this.modes.put(connection.transportMode, this.modes.getOrDefault(connection.transportMode, 0) + connection.parentConnection.length);
			}
		});
	}

	public List<SingleConnection> getConnectionPath() {
		return this.connectionPath;
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
		SingleConnectionPath other = (SingleConnectionPath) obj;
		return Objects.equals(this.connectionPath, other.connectionPath) && this.length == other.length;
	}

	@Override
	public String toString() {
		return "Path [length=" + this.length + ", connections=" + this.connections + ", Path="
				+ this.connectionPath.stream().map(s -> s.parentConnection.getCompressedString()).collect(Collectors.joining(", ")) + "]";
	}

}
