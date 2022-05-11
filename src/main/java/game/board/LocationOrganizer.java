package game.board;

import java.util.ArrayList;
import java.util.List;

import connection.SingleConnection;

public class LocationOrganizer {

	private List<LocationPool> locationPools;

	public LocationOrganizer() {
		this.locationPools = new ArrayList<>();
	}

	public void addSingleConnection(SingleConnection singleConnection) {
		// TODO If connection is over Forbidden Location, what to do?
		List<LocationPool> pools = this.locationPools.stream().filter(l -> l.containsConnection(singleConnection)).toList();
		if (pools.size() == 0) {
			LocationPool pool = new LocationPool(singleConnection);
			this.locationPools.add(pool);
		} else if (pools.size() == 1) {
			pools.get(0).addConnection(singleConnection);
		} else if (pools.size() == 2) {
			LocationPool pool = LocationPool.combine(pools.get(0), pools.get(1));
			this.locationPools.removeAll(pools);
			this.locationPools.add(pool);
		}
	}

	public boolean isInOnePool(List<Location> locations) {
		return this.locationPools.stream().anyMatch(l -> l.containsLocations(locations));
	}

	private static class LocationPool {

		private List<Location> locations;

		private LocationPool() {
			this.locations = new ArrayList<>();
		}

		private LocationPool(SingleConnection single) {
			this();
			this.locations.add(single.parentConnection.fromLocation);
			this.locations.add(single.parentConnection.toLocation);
		}

		private LocationPool(List<Location> locations) {
			this.locations = locations;
		}

		public void addConnection(SingleConnection single) {
			if (!this.locations.contains(single.parentConnection.fromLocation)) {
				this.locations.add(single.parentConnection.fromLocation);
			}
			if (!this.locations.contains(single.parentConnection.toLocation)) {
				this.locations.add(single.parentConnection.toLocation);
			}
		}

		public boolean containsConnection(SingleConnection singleConnection) {
			return this.locations.parallelStream().anyMatch(l -> (singleConnection.parentConnection.fromLocation.equals(l) || singleConnection.parentConnection.toLocation.equals(l)));
		}

		public boolean containsLocations(List<Location> locations) {
			return this.locations.containsAll(locations);
		}

		public static LocationPool combine(LocationPool pool1, LocationPool pool2) {
			return new LocationPool(LocationPool.combine(pool1.locations, pool2.locations));
		}

		public static List<Location> combine(List<Location> locations1, List<Location> locations2) {
			locations2.forEach(l -> {
				if (!locations1.contains(l)) {
					locations1.add(l);
				}
			});
			return locations1;
		}

	}

}
