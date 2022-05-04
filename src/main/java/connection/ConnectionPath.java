package connection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import algorithm.AlgorithmSettings;
import game.board.Location;

public class ConnectionPath implements Cloneable {

	private List<Connection> connections;
	private List<Location> locations;
	private int length;
	private int connectionsCount;
	private int multipleCount;

	public ConnectionPath(int maxCount, Location start) {
		this.connections = new ArrayList<>(maxCount);
		this.locations = new ArrayList<>();
		this.locations.add(start);
		this.connectionsCount = 0;
		this.multipleCount = 1;
	}

	private ConnectionPath(List<Connection> connections, List<Location> locations, int length, int connectionsCount, int multipleCount) {
		this.connections = new ArrayList<>(connections);
		this.locations = new ArrayList<>(locations);
		this.length = length;
		this.connectionsCount = connectionsCount;
		this.multipleCount = multipleCount;
	}

	public void addConnection(Connection connection) {
		this.connections.add(connection);
		this.length += connection.length;
		this.connectionsCount++;
		this.multipleCount *= connection.isGray ? 1 : connection.multiplicity;
		this.locations.add(connection.getNextLocation(this.getLastLocation()));
	}

	public boolean isPathPossible(AlgorithmSettings settings) {
		if (this.connectionsCount <= settings.pathSegments || this.length == 0) { return true; }
		return false;
	}

	public boolean containsConnection(Connection connection) {
		return this.connections.contains(connection);
	}

	public boolean containsLocation(Location location) {
		return this.locations.contains(location);
	}

	public Location getLastLocation() {
		return this.locations.get(this.locations.size() - 1);
	}

	public int getLength() {
		return this.length;
	}

	public List<Connection> getConnections() {
		return this.connections;
	}

	public int getConnectionsCount() {
		return this.connectionsCount;
	}

	public int getMultipleCount() {
		return this.multipleCount;
	}

	public List<Location> getLocations() {
		return this.locations;
	}

	@Override
	public ConnectionPath clone() {
		return new ConnectionPath(this.connections, this.locations, this.length, this.connectionsCount, this.multipleCount);
	}

	@Override
	public String toString() {
		return "Path [length=" + this.length + ", connections=" + this.connectionsCount + ", Path=" + this.connections.stream().map(Connection::getCompressedString).collect(Collectors.joining(", "))
				+ "]";
	}
}
