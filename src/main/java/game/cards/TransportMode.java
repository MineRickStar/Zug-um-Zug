package game.cards;

import java.util.stream.Stream;

public enum TransportMode {
	TRAIN("Train", "t"), SHIP("Ship", "s"), AIRPLANE("Airplane", "a");

	public final String displayName;
	public final String abbreviation;

	TransportMode(String displayName, String abbreviation) {
		this.displayName = displayName;
		this.abbreviation = abbreviation;
	}

	public static TransportMode getTransportMode(String abbreviation) {
		return Stream.of(TransportMode.values()).filter(t -> t.abbreviation.equalsIgnoreCase(abbreviation)).findAny().get();
	}
}