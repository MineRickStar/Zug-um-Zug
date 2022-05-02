package application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import game.Game;
import game.Path;
import game.Player;
import game.Rules;
import game.board.Connection;
import game.board.Location;
import game.board.SingleConnection;
import game.cards.ColorCard;
import game.cards.MyColor;
import game.cards.TransportMode;

public class Algorithm {

	private static Algorithm instance = new Algorithm();

	private AlgorithmChache cache;

	private Algorithm() {
		this.cache = new AlgorithmChache();
	}

	public static List<Path> findShortestPath(List<LocationPair> locationPairs, AlgorithmSettings settings) {
		return Algorithm.instance.findShortestPath0(locationPairs, settings);
	}

	private AlgorithmSettings settings;

	private List<Path> connectionList = new ArrayList<>();

	private List<Path> findShortestPath0(List<LocationPair> locationPairs, AlgorithmSettings settings) {
		List<Path> bestPaths = this.cache.getSingleConnection(locationPairs, settings);
		if (bestPaths != null) { return bestPaths; }
		bestPaths = new ArrayList<>();
		this.settings = settings;

		Map<LocationPair, List<Path>> paths = new HashMap<>();
		for (LocationPair pair : locationPairs) {
			this.createPathsList(pair);
			paths.put(pair, this.connectionList);
			System.out.println("next");
		}

		// TODO besserer algorithmus
		Iterator<LocationPair> iterator = locationPairs.iterator();
		while (iterator.hasNext()) {
			bestPaths.add(paths.get(iterator.next()).get(0));
		}
		this.cache.addConnection(locationPairs, settings, bestPaths);
		return bestPaths;
	}

	private void createPathsList(LocationPair pair) {
		this.connectionList = new ArrayList<>();
		Path currentPath = new Path();
		this.calculatePaths(pair.start, pair.end, currentPath);
	}

	private void calculatePaths(Location start, Location end, Path currentPath) {
		this.printWay(currentPath);
		this.highlight(currentPath);
		if (!this.isPathPossible(currentPath, this.settings)) { return; }
		if (start.equals(end)) {
			this.connectionList.add(currentPath);
			return;
		}
		List<Connection> connections = start.getConnectionsFromHere();
		for (Connection connection : connections) {
			// TODO testen ob die connection schon drin ist und vorher entfernen
			SingleConnection[] singleConnections = connection.singleConnections;
			if (connection.singleConnections[0].color == MyColor.GRAY) {
				singleConnections = new SingleConnection[] { connection.singleConnections[0] };
			}
			for (SingleConnection singleConnection : singleConnections) {
				if (currentPath.contains(singleConnection)) {
					break;
				}
				Location next = connection.fromLocation.equals(start) ? connection.toLocation : connection.fromLocation;
				if (currentPath.stream().anyMatch(s -> s.parentConnection.fromLocation.equals(next) || s.parentConnection.toLocation.equals(next))
						|| (!next.equals(end) && !Location.isPathNode(next))) {
					break;
				}
				Path copy = currentPath.clone();
				copy.addConnection(singleConnection);
				this.calculatePaths(next, end, copy);
			}
		}
	}

	// TODO connection length gleich mit speichern
	private Map<LocationPair, SortedMap<Integer, List<Path>>> sortByConnectionLength(Map<LocationPair, SortedMap<Integer, List<Path>>> shortestPathRankings) {
		Map<LocationPair, SortedMap<Integer, List<SingleConnection[]>>> connectionMap = new HashMap<>();
		for (Entry<LocationPair, SortedMap<Integer, List<Path>>> entry : shortestPathRankings.entrySet()) {
			SortedMap<Integer, List<Path>> connectionRankingMap = new TreeMap<>();
			entry.getValue().values().parallelStream().flatMap(Collection::stream).forEach(s -> {
				int length = s.getLength();
				connectionRankingMap.get(length);
			});
		}

		return null;
	}

	private boolean isPathPossible(Path path, AlgorithmSettings settings) {
		if (path.getConnections() > settings.pathSegments) { return false; }
		if (path.getLength() == 0) { return true; }
//		long time = System.nanoTime();
//		EnumMap<TransportMode, Integer> map = new EnumMap<>(TransportMode.class);
//		for (TransportMode mode : TransportMode.values()) {
//			map.put(mode, 0);
//		}
//		path.stream().forEach(s -> map.put(s.transportMode, map.get(s.transportMode) + s.parentConnection.length));
//		map = path.stream()
//			.collect(Collector.of(() -> new EnumMap<TransportMode, Integer>(TransportMode.class), (t, u) -> t.put(u.transportMode, t.getOrDefault(u.transportMode, 0) + u.parentConnection.length),
//					(t, u) -> {
//						Iterator<Entry<TransportMode, Integer>> it = u.entrySet().iterator();
//						while (it.hasNext()) {
//							Entry<TransportMode, Integer> entry = it.next();
//							t.put(entry.getKey(), t.getOrDefault(entry.getKey(), 0) + entry.getValue());
//						}
//						return t;
//					}));
		Iterator<Entry<TransportMode, Integer>> iterator = path.getModes().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<TransportMode, Integer> entry = iterator.next();
			if (entry.getValue() > settings.carrigesLeft.get(entry.getKey())) { return false; }
		}
//		System.out.println(System.nanoTime() - time);
		return true;
	}

	private void highlight(Path path) {
		ArrayList<Path> l = new ArrayList<>();
		l.add(path);
		Game.getInstance().setHighlightConnections(l);
		Application.frame.repaint();
	}

	private void printWay(Path path) {
//		System.out.println(path.stream().map(s -> s.parentConnection.length).reduce((byte) 0, (t, u) -> (byte) (t + u)) + " "
//				+ path.stream().map(s -> s.parentConnection.fromLocation.name.substring(0, 2) + " -> " + s.parentConnection.toLocation.name.substring(0, 2)).collect(Collectors.joining(", ")));
		System.out.println(path);
	}

	public static class AlgorithmSettings {
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

		public AlgorithmSettings() {
			this(10, 1, Rules.getInstance().getTransportMap(), new ArrayList<>(), new HashMap<>());
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
					+ this.carrigesLeft.values().stream().collect(Collectors.reducing((t, u) -> t + u)) + ", availableConnections=" + this.availableConnections.size() + ", colorCards="
					+ this.colorCards + "]";
		}

	}

	public record LocationPair(Location start, Location end) {}

	private record ConnectionCache(List<LocationPair> locationPairs, AlgorithmSettings settings, List<Path> connections) {}

	private static final class AlgorithmChache {
		private List<ConnectionCache> cache = new ArrayList<>();
		private final Comparator<LocationPair> comparator = (o1, o2) -> {
			int start = o1.start.name.compareTo(o2.start.name);
			if (start == 0) { return o1.end.name.compareTo(o2.end.name); }
			return start;
		};

		private synchronized void addConnection(List<LocationPair> locationPairs, AlgorithmSettings settings, List<Path> bestPaths) {
			Collections.sort(locationPairs, this.comparator);
			this.cache.add(new ConnectionCache(locationPairs, settings, bestPaths));
		}

		private synchronized List<Path> getSingleConnection(List<LocationPair> locationPairs, AlgorithmSettings settings) {
			Collections.sort(locationPairs, this.comparator);
			Optional<ConnectionCache> optional = this.cache.parallelStream().filter(c -> (c.locationPairs.equals(locationPairs) && c.settings.equals(settings))).findAny();
			if (optional.isEmpty()) { return null; }
			return optional.get().connections;
		}
	}

}
