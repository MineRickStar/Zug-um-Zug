package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import connection.Connection;
import connection.ConnectionPath;
import connection.SingleConnectionPath;
import connection.SingleConnection;
import game.board.Location;
import game.board.Location.LocationPair;

public class Algorithm {

	private static Algorithm instance = new Algorithm();

	private AlgorithmChache cache;

	private Algorithm() {
		this.cache = new AlgorithmChache();
	}

	public static synchronized List<SingleConnectionPath> findShortestPath(List<LocationPair> locationPairs, AlgorithmSettings settings) {
		return Algorithm.instance.findShortestPath0(locationPairs, settings);
	}

	private AlgorithmSettings settings;

	private List<SingleConnectionPath> findShortestPath0(List<LocationPair> locationPairs, AlgorithmSettings settings) {
		List<SingleConnectionPath> bestPaths = this.cache.getSingleConnection(locationPairs, settings);
		if (bestPaths != null) { return bestPaths; }
		bestPaths = new ArrayList<>();
		this.settings = settings;
		Map<LocationPair, SortedMap<Integer, List<SingleConnectionPath>>> rankings = new HashMap<>();
		for (LocationPair pair : locationPairs) {
			this.connectionList = new ArrayList<>(10000);
			this.noSingleConnectionList = new ArrayList<>();
			this.allPaths = new ArrayList<>(500000);
			this.index = 0;
			long t = System.currentTimeMillis();
			this.setUpCalculations(pair.start());

			// TODO calculatePathsIterative muss auf multithreading umgeschrieben werden
//			ThreadPoolExecutor exe = (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.min((int) (Runtime.getRuntime().availableProcessors() * .5), 1));
//			for (int i = 0; i < 10; i++) {
//				exe.execute(() -> this.calculatePathsIterataive(pair.end));
//			}
//			exe.shutdown();
//			try {
//				exe.awaitTermination(1, TimeUnit.MINUTES);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			this.calculatePathsIterataive(pair.end());
			System.out.println(System.currentTimeMillis() - t);
			rankings.put(pair, this.getRankings(this.connectionList, SingleConnectionPath::getLength, SingleConnectionPath::getConnections));
		}
		SortedMap<Integer, List<SingleConnectionPath>> locationPairRanking = new TreeMap<>();
		for (LocationPair pair : locationPairs) {
			bestPaths = locationPairRanking.getOrDefault(1, new ArrayList<>());
			SortedMap<Integer, List<SingleConnectionPath>> path = rankings.get(pair);
			bestPaths.add(path.get(path.firstKey()).get(0));
			locationPairRanking.put(1, bestPaths);
		}
		this.cache.addConnection(locationPairs, settings, bestPaths);
		System.out.println(locationPairs);
		return bestPaths;
	}

	List<SingleConnectionPath> connectionList;
	List<ConnectionPath> noSingleConnectionList;
	List<ConnectionPath> allPaths;
	int index;

	private synchronized List<SingleConnectionPath> getConnectionList() {
		return this.connectionList;
	}

	private synchronized List<ConnectionPath> getNoSingleConnectionList() {
		return this.noSingleConnectionList;
	}

	private synchronized List<ConnectionPath> getAllPaths() {
		return this.allPaths;
	}

	private synchronized int getIndex() {
		return this.index;
	}

	private synchronized void increment() {
		this.index++;
	}

	private void setUpCalculations(Location start) {
		// Start fill all Connections from start into List
		for (Connection connection : start.getConnectionsFromHere()) {
			ConnectionPath nextPath = new ConnectionPath(this.settings.connectionAmount, start);
			nextPath.addConnection(connection);
			this.getAllPaths().add(nextPath);
		}
	}

	private void calculatePathsIterataive(Location end) {
		int maxCarriges = this.settings.carrigesLeft.values().stream().reduce((t, u) -> t + u).orElse(0);
		while (this.getIndex() < this.getAllPaths().size()) {
//			System.out.println(this.getIndex());
			ConnectionPath currentPath = this.getAllPaths().get(this.getIndex());
			this.increment();
			Location lastLocation = currentPath.getLastLocation();
			for (Connection connection : lastLocation.getConnectionsFromHere()) {
				Location nextLocation = connection.getNextLocation(lastLocation);
				if (!Location.isPathNode(nextLocation) || currentPath.containsLocation(nextLocation) || currentPath.containsConnection(connection) || currentPath.getLength() > maxCarriges) {
					continue;
				}
				ConnectionPath nextPath = currentPath.clone();
				nextPath.addConnection(connection);
				if (nextPath.isPathPossible(this.settings)) {
					if (nextLocation.equals(end)) {
						this.getNoSingleConnectionList().add(nextPath);
					} else {
						this.getAllPaths().add(nextPath);
					}
				}
			}
		}
		this.addAllSingleConnections();
	}

	private void addAllSingleConnections() {
		List<ConnectionPath> paths = this.getNoSingleConnectionList();
		for (ConnectionPath connectionPath : paths) {
			int pathSize = connectionPath.getConnectionsCount();
			Location start = connectionPath.getLocations().get(0);
			List<SingleConnectionPath> singlePaths = new ArrayList<>(connectionPath.getMultipleCount());
			IntStream.range(0, connectionPath.getMultipleCount()).forEach(i -> singlePaths.add(new SingleConnectionPath(pathSize, start, this.settings)));

			connectionPath.getConnections().forEach(c -> {
				SingleConnection[] singles = c.singleConnections;
				if (c.isGray || c.isSingleConnection()) {
					singlePaths.stream().forEach(p -> p.addConnection(singles[0]));
				} else {
					List<Integer> multiplier = new ArrayList<>();
					multiplier.add((int) c.multiplicity);
					int divider = connectionPath.getMultipleCount() / multiplier.stream().mapToInt(Integer::intValue).sum();
					for (int i = 0; i < singlePaths.size(); i++) {
						int singleConnectionCount = (i / divider) % c.multiplicity;
						singlePaths.get(i).addConnection(singles[singleConnectionCount]);
					}
				}
			});
			this.getConnectionList().addAll(singlePaths.stream().filter(SingleConnectionPath::isPathPossible).toList());
		}
	}

	@SafeVarargs
	private SortedMap<Integer, List<SingleConnectionPath>> getRankings(List<SingleConnectionPath> paths, ToIntFunction<SingleConnectionPath>... functions) {
		List<Integer> mins = this.getExtreme(paths, true, functions);
		List<Integer> max = this.getExtreme(paths, false, functions);
		SortedMap<Integer, List<SingleConnectionPath>> rankings = new TreeMap<>();
		IntStream.rangeClosed(1, this.getDifference(max, mins)).forEach(i -> rankings.put(i, new ArrayList<>()));
		for (SingleConnectionPath path : paths) {
			rankings.get(this.getDifference(this.getList(path, functions), mins)).add(path);
		}
		this.compressMap(rankings);
		return rankings;
	}

	private void compressMap(SortedMap<Integer, List<SingleConnectionPath>> map) {
		ArrayList<Integer> rankings = new ArrayList<>();
		Iterator<Entry<Integer, List<SingleConnectionPath>>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, List<SingleConnectionPath>> next = iterator.next();
			if (next.getValue().isEmpty()) {
				rankings.add(next.getKey());
			} else {
				if (!rankings.isEmpty()) {
					map.get(rankings.remove(0)).addAll(next.getValue());
					next.getValue().clear();
					rankings.add(next.getKey());
				}
			}
		}
		map.values().removeIf(List::isEmpty);
	}

	@SafeVarargs
	private List<Integer> getList(SingleConnectionPath path, ToIntFunction<SingleConnectionPath>... functions) {
		return Stream.of(functions).map(f -> f.applyAsInt(path)).toList();
	}

	@SafeVarargs
	private List<Integer> getExtreme(List<SingleConnectionPath> paths, boolean min, ToIntFunction<SingleConnectionPath>... functions) {
		List<Integer> list = new ArrayList<>();
		for (ToIntFunction<SingleConnectionPath> function : functions) {
			list.add(paths.parallelStream().mapToInt(function).reduce((left, right) -> min ? Integer.min(left, right) : Integer.max(left, right)).orElse(-1));
		}
		return list;
	}

	private int getDifference(List<Integer> list1, List<Integer> list2) {
		int diff = 1;
		for (int i = 0; i < list1.size(); i++) {
			diff += list1.get(i) - list2.get(i);
		}
		return diff;
	}

	private record ConnectionCache(List<LocationPair> locationPairs, AlgorithmSettings settings, List<SingleConnectionPath> connections) {}

	private static final class AlgorithmChache {
		private List<ConnectionCache> cache = new ArrayList<>();
		private final Comparator<LocationPair> comparator = (o1, o2) -> {
			int start = o1.start().compareTo(o2.start());
			if (start == 0) { return o1.end().compareTo(o2.end()); }
			return start;
		};

		private synchronized void addConnection(List<LocationPair> locationPairs, AlgorithmSettings settings, List<SingleConnectionPath> bestPaths) {
			Collections.sort(locationPairs, this.comparator);
			this.cache.add(new ConnectionCache(locationPairs, settings, bestPaths));
		}

		private synchronized List<SingleConnectionPath> getSingleConnection(List<LocationPair> locationPairs, AlgorithmSettings settings) {
			Collections.sort(locationPairs, this.comparator);
			Optional<ConnectionCache> optional = this.cache.parallelStream().filter(c -> (c.locationPairs.equals(locationPairs) && c.settings.equals(settings))).findAny();
			if (optional.isEmpty()) { return null; }
			return optional.get().connections;
		}
	}

}
