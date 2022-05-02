package game.board;

import java.util.Objects;

import game.Player;
import game.cards.ColorCard;
import game.cards.MyColor;
import game.cards.TransportMode;

public class SingleConnection {

	public final Connection parentConnection;
	public final MyColor color;
	public final TransportMode transportMode;
	private final int id;

	private Player owner;

	public SingleConnection(Connection parentConnection, MyColor color, TransportMode transportMode, int id) {
		this.parentConnection = parentConnection;
		this.color = color;
		this.transportMode = transportMode;
		this.id = id;
	}

	public Player getOwner() {
		return this.owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public ColorCard getColorCardRepresentation() {
		return new ColorCard(this.color, this.transportMode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.color, this.transportMode, this.id, this.owner, this.parentConnection);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if ((obj == null) || (this.getClass() != obj.getClass())) { return false; }
		SingleConnection other = (SingleConnection) obj;
		return (this.color == other.color) && (this.id == other.id) && (this.transportMode == other.transportMode)
				&& Objects.equals(this.parentConnection.fromLocation, other.parentConnection.fromLocation) && Objects.equals(this.parentConnection.toLocation, other.parentConnection.toLocation);
	}

	@Override
	public String toString() {
		return "S-Con [From: " + this.parentConnection.getFromLocation() + ", To: " + this.parentConnection.getToLocation() + ", color=" + this.color + "]";
	}
}
