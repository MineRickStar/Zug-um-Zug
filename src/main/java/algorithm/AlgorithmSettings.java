package algorithm;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.stream.Collectors;

import connection.SingleConnection;
import game.Player;
import game.cards.ColorCard;
import game.cards.TransportMode;

public class AlgorithmSettings {
	public int pathSegments;
	public int connectionAmount;
	public EnumMap<TransportMode, Integer> carrigesLeft;
	public List<SingleConnection> availableConnections;
	public Map<TransportMode, SortedMap<Integer, List<ColorCard>>> colorCards;

	public AlgorithmSettings(int pathSegments, int connectionAmount, EnumMap<TransportMode, Integer> carrigesLeft, List<SingleConnection> availableConnections,
			Map<TransportMode, SortedMap<Integer, List<ColorCard>>> colorCards) {
		this.pathSegments = pathSegments;
		this.connectionAmount = connectionAmount;
		this.carrigesLeft = carrigesLeft;
		this.availableConnections = availableConnections;
		this.colorCards = colorCards;
	}

	public AlgorithmSettings(Player player) {
		this.pathSegments = 10;
		this.connectionAmount = 1;
		this.carrigesLeft = player.getPieceCount();
		this.availableConnections = player.getSingleConnections();
		this.colorCards = player.getColorCards();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.availableConnections, this.carrigesLeft, this.colorCards, this.connectionAmount, this.pathSegments);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if ((obj == null) || (this.getClass() != obj.getClass())) { return false; }
		AlgorithmSettings other = (AlgorithmSettings) obj;
		return Objects.equals(this.availableConnections, other.availableConnections) && Objects.equals(this.carrigesLeft, other.carrigesLeft) && Objects.equals(this.colorCards, other.colorCards)
				&& (this.connectionAmount == other.connectionAmount) && (this.pathSegments == other.pathSegments);
	}

	@Override
	public String toString() {
		return "AlgorithmSettings [pathSegments=" + this.pathSegments + ", connectionAmount=" + this.connectionAmount + ", carrigesLeft="
				+ this.carrigesLeft.values().stream().collect(Collectors.reducing((t, u) -> t + u)) + ", availableConnections=" + this.availableConnections.size() + ", colorCards=" + this.colorCards
				+ "]";
	}

}
