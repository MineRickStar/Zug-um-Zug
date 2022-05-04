package algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import connection.Connection;
import connection.ConnectionPath;
import connection.SingleConnection;
import connection.SingleConnectionPath;
import game.board.Location;
import game.board.Location.LocationList;

public class Ranking {

	private AlgorithmSettings settings;
	ArrayBlockingQueue<ConnectionPath> queue;
	ArrayBlockingQueue<ConnectionPath> queueReady;

	List<SingleConnectionPath> connectionList;

	SortedMap<Integer, List<SingleConnectionPath>> rankings;

	private List<ToIntFunction<SingleConnectionPath>> functions;

	private int maxCarriges;

	public Ranking(AlgorithmSettings settings) {
		this.settings = settings;
		this.queue = new ArrayBlockingQueue<>(settings.pathSegments * 50000);
		this.queueReady = new ArrayBlockingQueue<>(settings.pathSegments * 1000);
		this.connectionList = new ArrayList<>(settings.pathSegments * 1000);
		this.maxCarriges = settings.carrigesLeft.values().stream().reduce((t, u) -> t + u).orElse(0);
		this.functions = List.of(SingleConnectionPath::getLength, SingleConnectionPath::getConnections, SingleConnectionPath::getPoints);
	}

	public void start(LocationList list) {
		List<List<SingleConnectionPath>> connectedPaths = new ArrayList<>(this.settings.pathSegments * 1000);

		int processors = (int) Math.max(Runtime.getRuntime().availableProcessors() * .5, 1);
		double three_quarters = 3 / 4.0;

		Iterator<Location> iterator = list.locations().iterator();
		Location start = null;
		Location end = iterator.next();
		while (iterator.hasNext()) {
			start = end;
			end = iterator.next();
			final Location tmpEnd = end;

			this.setUpCalculations(start);
			ThreadPoolExecutor exe = (ThreadPoolExecutor) Executors.newFixedThreadPool(processors);
			for (int i = 0, max = (int) Math.max(1, processors * three_quarters), maxSmaller = Math.max(processors - max, 1); i < max; i++) {
				// 75% for calculating Paths
				exe.execute(() -> this.calculatePathsIterataive(tmpEnd));
				if (i < maxSmaller) {
					// 25% for calculation singlePaths
					exe.execute(this::createAllSingleConnections);
				}
			}
			exe.shutdown();
			try {
				exe.awaitTermination(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			connectedPaths.add(new ArrayList<>(this.connectionList));
			this.connectionList.clear();
		}

		this.connectionList = this.connectPaths(connectedPaths);

		this.rankings = this.getRankings();
	}

	private void setUpCalculations(Location start) {
		// Start fill all Connections from start into List
		for (Connection connection : start.getConnectionsFromHere()) {
			ConnectionPath nextPath = new ConnectionPath(this.settings.connectionAmount, start);
			nextPath.addConnection(connection);
			this.queue.add(nextPath);
		}
	}

	private void calculatePathsIterataive(Location end) {
		while (!this.queue.isEmpty()) {
			ConnectionPath currentPath = this.queue.poll();
			if (currentPath == null) {
				continue;
			}
			Location lastLocation = currentPath.getLastLocation();
			ArrayList<ConnectionPath> collectedPaths = new ArrayList<>(10);
			for (Connection connection : lastLocation.getConnectionsFromHere()) {
				Location nextLocation = connection.getNextLocation(lastLocation);
				if (!Location.isPathNode(nextLocation) || currentPath.containsLocation(nextLocation) || currentPath.containsConnection(connection) || currentPath.getLength() > this.maxCarriges) {
					continue;
				}
				ConnectionPath nextPath = currentPath.clone();
				nextPath.addConnection(connection);
				if (nextPath.isPathPossible(this.settings)) {
					if (nextLocation.equals(end)) {
						try {
							this.queueReady.put(nextPath);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						collectedPaths.add(nextPath);
					}
				}
			}
			collectedPaths.forEach(c -> {
				try {
					this.queue.put(c);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
	}

	private void createAllSingleConnections() {
		while (!this.queue.isEmpty() || !this.queueReady.isEmpty()) {
			ConnectionPath connectionPath = this.queueReady.poll();
			if (connectionPath == null) {
				continue;
			}
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
//			Game.getInstance().setHighlightConnections(singlePaths);
//			Application.frame.repaint();
			this.getConnectionList().addAll(singlePaths.stream().filter(SingleConnectionPath::isPathPossible).toList());
		}
	}

	private synchronized List<SingleConnectionPath> getConnectionList() {
		return this.connectionList;
	}

	private SortedMap<Integer, List<SingleConnectionPath>> getRankings() {
		List<Integer> mins = this.getExtreme(true);
		List<Integer> max = this.getExtreme(false);
		SortedMap<Integer, List<SingleConnectionPath>> rankings = new TreeMap<>();
		IntStream.rangeClosed(1, this.getDifference(max, mins)).forEach(i -> rankings.put(i, new ArrayList<>()));
		for (SingleConnectionPath path : this.connectionList) {
			rankings.get(this.getDifference(this.getList(path), mins)).add(path);
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

	private List<Integer> getList(SingleConnectionPath path) {
		return this.functions.stream().map(f -> f.applyAsInt(path)).toList();
	}

	private List<Integer> getExtreme(boolean min) {
		List<Integer> list = new ArrayList<>();
		for (ToIntFunction<SingleConnectionPath> function : this.functions) {
			try {
				// TODO Nullpointer Exception
				list.add(this.connectionList.stream().mapToInt(function).reduce((left, right) -> (min ? Integer.min(left, right) : Integer.max(left, right))).orElse(-1));
			} catch (NullPointerException e) {
				System.out.println(list);
				System.out.println(this.connectionList.size());
				System.out.println(function);
				e.printStackTrace();
			}
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

	private List<SingleConnectionPath> connectPaths(List<List<SingleConnectionPath>> connectedPaths) {
		if (connectedPaths.size() == 1) { return connectedPaths.get(0); }
		return null;
	}

	public SortedMap<Integer, List<SingleConnectionPath>> getMap() {
		return this.rankings;
	}

}
