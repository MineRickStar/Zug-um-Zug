package game.cards;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import game.board.Location;
import game.board.LocationOrganizer;

public class MissionCard implements Comparable<MissionCard> {

	public final Distance distance;
	public final byte points;

	private List<Location> locations;
	public final MissionCardConstraints constraints;

	public MissionCard(Distance distance, byte points, List<Location> locations, MissionCardConstraints constraints) {
		this.locations = locations;
		this.points = points;
		this.distance = distance;
		this.constraints = constraints;
	}

	public Location getFromLocation() {
		return this.locations.get(0);
	}

	public List<Location> getMidLocations() {
		if (this.locations.size() == 2) { return Collections.emptyList(); }
		return this.locations.subList(1, this.locations.size() - 1);
	}

	public Location getToLocation() {
		return this.locations.get(this.locations.size() - 1);
	}

	public List<Location> getLocations() {
		return this.locations;
	}

	public boolean isFinished(LocationOrganizer locationOrganizer) {
		return locationOrganizer.isInOnePool(this.locations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.distance, this.getFromLocation(), this.points, this.getToLocation());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if ((obj == null) || (this.getClass() != obj.getClass())) { return false; }
		MissionCard other = (MissionCard) obj;
		return (this.distance == other.distance) && Objects.equals(this.getFromLocation(), other.getFromLocation()) && (this.points == other.points)
				&& Objects.equals(this.getToLocation(), other.getToLocation());
	}

	@Override
	public String toString() {
		String over = this.getLocations().size() > 2 ? this.locations.subList(1, this.getLocations().size() - 1).stream().map(l -> l.name).collect(Collectors.joining(", ", ", Over: (", ")")) : "";
		return "Card [fromLocation=" + this.getFromLocation() + over + ", toLocation=" + this.getToLocation() + ", points=" + this.points + ", distance=" + this.distance + "]";
	}

	public enum Distance {
		SHORT("Short Card", "s"), MIDDLE("Middle Card", "m"), LONG("Long Card", "l"), EXTRA_LONG("Extra Long Card", "e");

		public final String cardLength;
		public final String abbreviation;

		Distance(String cardLength, String abbreviation) {
			this.cardLength = cardLength;
			this.abbreviation = abbreviation;
		}

		public static Distance findByAbbreviation(String abbreviation) {
			return Stream.of(Distance.values()).filter(d -> d.abbreviation.equalsIgnoreCase(abbreviation)).findAny().get();
		}
	}

	public static class MissionCardConstraints {

	}

	@Override
	public int compareTo(MissionCard o) {
		int pointCompare = -Integer.compare(this.points, o.points);
		if (pointCompare == 0) {
			for (int i = 0, max = Math.min(this.locations.size(), o.locations.size()); i < max; i++) {
				int compare = this.locations.get(i).compareTo(o.locations.get(i));
				if (compare != 0) { return compare; }
			}
			return (int) Math.signum(this.locations.size() - o.locations.size());
		}
		return pointCompare;
	}

}
