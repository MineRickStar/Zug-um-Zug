package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import connection.SingleConnectionPath;
import game.board.Location.LocationList;

public class Algorithm {

	private static Algorithm instance = new Algorithm();

	private AlgorithmChache cache;

	private Algorithm() {
		this.cache = new AlgorithmChache();
	}

	public static synchronized List<SingleConnectionPath> findShortestPath(List<LocationList> locationLists, AlgorithmSettings settings) {
		return Algorithm.instance.findShortestPath0(locationLists, settings);
	}

	private List<SingleConnectionPath> findShortestPath0(List<LocationList> locationLists, AlgorithmSettings settings) {
		List<SingleConnectionPath> bestPaths = this.cache.getSingleConnection(locationLists, settings);
		if (bestPaths != null) { return bestPaths; }
		bestPaths = new ArrayList<>();
		Map<LocationList, SortedMap<Integer, List<SingleConnectionPath>>> rankings = new HashMap<>();
		for (LocationList locationList : locationLists) {
			long t = System.currentTimeMillis();
			Ranking ranking = new Ranking(settings);
			ranking.start(locationList);
			System.out.println("Time: " + (System.currentTimeMillis() - t));
			rankings.put(locationList, ranking.getMap());
		}
		SortedMap<Integer, List<SingleConnectionPath>> locationPairRanking = new TreeMap<>();
		for (LocationList locationList : locationLists) {
			bestPaths = locationPairRanking.getOrDefault(1, new ArrayList<>());
			SortedMap<Integer, List<SingleConnectionPath>> path = rankings.get(locationList);
			if (!path.isEmpty()) {
				bestPaths.add(path.get(path.firstKey()).get(0));
				locationPairRanking.put(1, bestPaths);
			}
		}
		this.cache.addConnection(locationLists, settings, bestPaths);
		System.out.println(locationLists);
		return bestPaths;
	}

	private record ConnectionCache(List<LocationList> locationPairs, AlgorithmSettings settings, List<SingleConnectionPath> connections) {}

	private static final class AlgorithmChache {
		private List<ConnectionCache> cache = new ArrayList<>();
		private final Comparator<LocationList> comparator = (o1, o2) -> {
			int start = o1.start().compareTo(o2.start());
			if (start == 0) { return o1.end().compareTo(o2.end()); }
			return start;
		};

		private synchronized void addConnection(List<LocationList> locationPairs, AlgorithmSettings settings, List<SingleConnectionPath> bestPaths) {
			Collections.sort(locationPairs, this.comparator);
			ArrayList<LocationList> list = new ArrayList<>();
			locationPairs.forEach(l -> list.add(new LocationList(new ArrayList<>(l.locations()))));
			this.cache.add(new ConnectionCache(list, settings.clone(), new ArrayList<>(bestPaths)));
		}

		private synchronized List<SingleConnectionPath> getSingleConnection(List<LocationList> locationPairs, AlgorithmSettings settings) {
			Collections.sort(locationPairs, this.comparator);
			Optional<ConnectionCache> optional = this.cache.parallelStream().filter(c -> (c.locationPairs.equals(locationPairs) && c.settings.equals(settings))).findAny();
			if (optional.isEmpty()) { return null; }
			return optional.get().connections;
		}
	}

}
