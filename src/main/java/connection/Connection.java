package connection;

import java.awt.Color;
import java.util.UUID;
import java.util.stream.Stream;

import game.board.Location;
import game.cards.MyColor;
import game.cards.TransportMode;

public class Connection {

	public final UUID ID;
	public final Location fromLocation;
	public final Location toLocation;
	public final byte multiplicity;
//	public final byte[] length;
//	public final byte[] points;
	public final SingleConnection[] singleConnections;
	public final boolean isGray;

	public Connection(Location fromLocation, Location toLocation, byte multiplicity, byte[] length, MyColor[] colors, TransportMode[] transportMode) {
		this.ID = UUID.randomUUID();
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
//		this.length = length;
		this.multiplicity = multiplicity;
//		this.points = new byte[multiplicity];
		this.singleConnections = new SingleConnection[multiplicity];
		for (int i = 0; i < multiplicity; i++) {
//			this.points[i] = Rules.getInstance().getPointsConnection(transportMode[i])[length[i] - 1];
			this.singleConnections[i] = new SingleConnection(this, colors[Math.min(colors.length - 1, i)], transportMode[i], length[i]);
		}
		this.isGray = colors[0] == MyColor.GRAY;
	}

	public SingleConnection getSingleConnectionAt(int count) {
		return this.singleConnections[count];
	}

	public SingleConnection getSingleConnectionWithColor(MyColor color) {
		for (SingleConnection singleConnection : this.singleConnections) {
			if (singleConnection.color == color) { return singleConnection; }
		}
		return null;
	}

	public boolean isSingleConnection() {
		return this.singleConnections.length == 1;
	}

	public Color getColor(int count) {
		return this.singleConnections[count].color.realColor;
	}

	public Location getFromLocation() {
		return this.fromLocation;
	}

	public Location getToLocation() {
		return this.toLocation;
	}

	public Location getNextLocation(Location fromLocation) {
		return this.fromLocation.equals(fromLocation) ? this.toLocation : this.fromLocation;
	}

	public boolean IsNextLocationAPathNode(Location fromLocation) {
		return Location.isPathNode(this.getNextLocation(fromLocation));
	}

	public String getCompressedString() {
		return this.fromLocation.name.substring(0, 2) + " -> " + this.toLocation.name.substring(0, 2);
	}

	public int getMinLength() {
		return Stream.of(this.singleConnections).mapToInt(s -> s.length).min().orElse(0);
	}

	@Override
	public int hashCode() {
		return this.ID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (obj instanceof Connection other) { return this.ID.equals(other.ID); }
		return false;
	}

	@Override
	public String toString() {
		return System.lineSeparator() + "Connection [From=" + this.fromLocation + ", To=" + this.toLocation + ", multipliticity=" + this.multiplicity + " , isGray: " + this.isGray + "]";
	}

}
