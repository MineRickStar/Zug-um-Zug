package algorithm;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.stream.Collectors;

import connection.SingleConnection;
import game.Player;
import game.cards.ColorCard;
import game.cards.ColorCard.TransportMode;

public class AlgorithmSettings implements Cloneable {
	/**
	 * Amount of Segments that the Path has at most.
	 */
	public int pathSegments;
	/**
	 * How many different best possibilities are accounted for.
	 */
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
	public AlgorithmSettings clone() {
		return new AlgorithmSettings(this.pathSegments, this.connectionAmount, this.carrigesLeft.clone(), new ArrayList<>(this.availableConnections), new HashMap<>(this.colorCards));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.availableConnections, this.carrigesLeft, this.colorCards, this.connectionAmount, this.pathSegments);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (obj instanceof AlgorithmSettings other) { return this.hashCode() == other.hashCode(); }
		return false;
	}

	@Override
	public String toString() {
		return "AlgorithmSettings [pathSegments=" + this.pathSegments + ", connectionAmount=" + this.connectionAmount + ", carrigesLeft="
				+ this.carrigesLeft.values().stream().collect(Collectors.reducing((t, u) -> t + u)) + ", availableConnections=" + this.availableConnections.size() + ", colorCards=" + this.colorCards
				+ "]";
	}

}
