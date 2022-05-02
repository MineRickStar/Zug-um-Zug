package game.cards;

import java.util.stream.Stream;

public enum TransportMode {
	TRAIN("t"), SHIP("s"), AIRPLANE("a");

	public final String abbreviation;

	TransportMode(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public static TransportMode getTransportMode(String abbreviation) {
		return Stream.of(TransportMode.values()).filter(t -> t.abbreviation.equalsIgnoreCase(abbreviation)).findAny()
				.get();
	}
}