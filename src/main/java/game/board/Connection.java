package game.board;

import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;

import game.cards.MyColor;
import game.cards.TransportMode;

public class Connection {

	public final Location fromLocation;
	public final Location toLocation;
	public final byte length;
	public final byte multiplicity;
	public final SingleConnection[] singleConnections;

	public Connection(Location fromLocation, Location toLocation, byte length, byte multiplicity, MyColor[] colors, TransportMode[] transportMode) {
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.length = length;
		this.multiplicity = multiplicity;
		this.singleConnections = new SingleConnection[multiplicity];
		for (int i = 0; i < multiplicity; i++) {
			this.singleConnections[i] = new SingleConnection(this, colors[Math.min(colors.length - 1, i)], transportMode[i]);
		}
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

	public Color getColor(int count) {
		return this.singleConnections[count].color.realColor;
	}

	public Location getFromLocation() {
		return this.fromLocation;
	}

	public Location getToLocation() {
		return this.toLocation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Arrays.hashCode(this.singleConnections);
		return (prime * result) + Objects.hash(this.fromLocation, this.length, this.multiplicity, this.toLocation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if ((obj == null) || (this.getClass() != obj.getClass())) { return false; }
		Connection other = (Connection) obj;
		boolean b = this.singleConnections.length == other.singleConnections.length;
		if (!b) { return false; }
		for (int i = 0; i < this.singleConnections.length; i++) {
			b &= this.singleConnections[i].color == other.singleConnections[i].color;
		}
		return b && Objects.equals(this.fromLocation, other.fromLocation) && (this.length == other.length) && (this.multiplicity == other.multiplicity)
				&& Objects.equals(this.toLocation, other.toLocation);
	}

	@Override
	public String toString() {
		return System.lineSeparator() + "Connection [From=" + this.fromLocation + ", To=" + this.toLocation + ", length=" + this.length + ", multipliticity=" + this.multiplicity + ", colors="
				+ Arrays.toString(this.singleConnections) + "]";
	}

}
