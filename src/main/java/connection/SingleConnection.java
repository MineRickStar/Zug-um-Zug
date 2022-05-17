package connection;

import java.util.UUID;

import game.Player;
import game.Rules;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.ColorCard.TransportMode;

public class SingleConnection {

	private final UUID ID;

	public final Connection parentConnection;
	public final MyColor color;
	public final TransportMode transportMode;
	public final byte length;
	public final byte points;

	private Player owner;

	public SingleConnection(Connection parentConnection, MyColor color, TransportMode transportMode, byte length) {
		this.ID = UUID.randomUUID();
		this.parentConnection = parentConnection;
		this.color = color;
		this.transportMode = transportMode;
		this.length = length;
		this.points = Rules.getInstance().getPointsConnection(transportMode)[length - 1];
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
		return this.ID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if ((obj == null) || (this.getClass() != obj.getClass())) { return false; }
		SingleConnection other = (SingleConnection) obj;
		return this.ID.equals(other.ID);
	}

	@Override
	public String toString() {
		return "S-Con [From: " + this.parentConnection.getFromLocation() + ", To: " + this.parentConnection.getToLocation() + ", color=" + this.color + "]";
	}
}
