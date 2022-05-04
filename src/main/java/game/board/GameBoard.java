package game.board;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import algorithm.Algorithm;
import algorithm.AlgorithmSettings;
import application.Application;
import connection.Connection;
import connection.SingleConnectionPath;
import csvCoder.Decode;
import game.Game;
import game.Player;
import game.Rules;
import game.board.Location.LocationPair;
import game.cards.ColorCard;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;
import game.cards.MissionCard.MissionCardConstraints;
import game.cards.MyColor;
import game.cards.TransportMode;

public class GameBoard {

	private Map<Distance, List<MissionCard>> missionCards;

	private List<ColorCard> allCards;
	private List<ColorCard> openCards;
	private List<ColorCard> usedCards;

	private TreeMap<String, Location> locations;
	private TreeMap<UUID, Connection> connections;

	private List<SingleConnectionPath> highlightedConnections;

	public GameBoard() {
		this.locations = new TreeMap<>();
		this.connections = new TreeMap<>();
		this.loadLocations();
		this.loadConnections();
		this.missionCards = new EnumMap<>(Distance.class);
		for (Distance distance : Distance.values()) {
			this.missionCards.put(distance, new ArrayList<>());
		}
		this.readMissionCards();

		this.allCards = new ArrayList<>();
		this.openCards = new ArrayList<>();
		this.usedCards = new ArrayList<>();
		this.fillCards();
	}

	public void startGame() {
		Collections.shuffle(this.missionCards.get(Distance.SHORT), Game.getInstance().getRandomGenerator());
		Collections.shuffle(this.missionCards.get(Distance.LONG), Game.getInstance().getRandomGenerator());
		this.shuffleCards();
		for (int i = 0, max = Rules.getInstance().getColorCardsLayingDown(); i < max; i++) {
			this.openCards.add(this.drawColorCard());
		}
	}

	private void fillCards() {
		for (MyColor color : MyColor.getNormalMyColors()) {
			this.fillColor(Rules.getInstance().getTrainColorCardCount(), color, TransportMode.TRAIN);
			this.fillColor(Rules.getInstance().getShipColorCardCount(), color, TransportMode.SHIP);
			this.fillColor(Rules.getInstance().getAirplaneColorCardCount(), color, TransportMode.AIRPLANE);
		}
		this.fillColor(Rules.getInstance().getTrainRainbowCardColorCount(), MyColor.RAINBOW, TransportMode.TRAIN);
		this.fillColor(Rules.getInstance().getShipRainbowColorCardCount(), MyColor.RAINBOW, TransportMode.SHIP);
		this.fillColor(Rules.getInstance().getAirplaneRainbowColorCardCount(), MyColor.RAINBOW, TransportMode.AIRPLANE);
	}

	private void fillColor(int max, MyColor color, TransportMode transportMode) {
		for (int i = 0; i < max; i++) {
			this.allCards.add(new ColorCard(color, transportMode));
		}
	}

	public ColorCard drawColorCard() {
		if (this.allCards.size() == 0) {
			if (this.usedCards.isEmpty()) { return null; }
			this.allCards.addAll(this.usedCards);
			this.usedCards.clear();
			this.shuffleCards();
		}
		return this.allCards.remove(0);
	}

	public ColorCard drawCardFromOpenCards(int index) {
		ColorCard colorCard = this.openCards.remove(index);
		this.openCards.add(this.drawColorCard());
		if (Rules.getInstance().isShuffleWithThreeLocomotives()
				&& (this.openCards.stream().filter(c -> c != null).filter(c -> c.color() == MyColor.RAINBOW).count() == Rules.getInstance().getMaxOpenLocomotives())) {
			this.usedCards.addAll(this.openCards);
			this.openCards.clear();
			for (int i = 0; i < Rules.getInstance().getColorCardsLayingDown(); i++) {
				this.openCards.add(this.drawColorCard());
			}
		}
		return colorCard;
	}

	public List<ColorCard> getOpenCards() {
		return this.openCards;
	}

	public int getRemainingCards() {
		return this.allCards.size();
	}

	public void addUsedCards(ColorCard color, int count) {
		for (int i = 0; i <= count; i++) {
			this.usedCards.add(color);
		}
	}

	public void highlightConnection(List<LocationPair> locationPairs, Player player) {
		if (locationPairs == null || locationPairs.isEmpty()) {
			this.highlightedConnections = null;
			Application.frame.repaint();
			return;
		}
		SwingWorker<List<SingleConnectionPath>, Void> worker = new SwingWorker<List<SingleConnectionPath>, Void>() {

			@Override
			public List<SingleConnectionPath> doInBackground() {
				return Algorithm.findShortestPath(locationPairs, new AlgorithmSettings(player));
			}

			@Override
			protected void done() {
				try {
					GameBoard.this.highlightedConnections = this.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		worker.addPropertyChangeListener(evt -> {
			if ("state".equals(evt.getPropertyName())) {
				if ((StateValue) evt.getNewValue() == StateValue.DONE) {
					Application.frame.repaint();
				}
			}
		});
		worker.execute();

	}

	public void setHighlightConnections(List<SingleConnectionPath> paths) {
		this.highlightedConnections = paths;
	}

	public List<SingleConnectionPath> getHighlightedConnections() {
		return this.highlightedConnections;
	}

	public MissionCard drawMissionCard(Distance distance) {
		return this.missionCards.get(distance).remove(0);
	}

	public int getMissionCardCount(Distance distance) {
		return this.missionCards.get(distance).size();
	}

	public void addNotUsedMissionCard(MissionCard missionCard) {
		this.missionCards.get(missionCard.distance).add(missionCard);
	}

	public Map<Distance, List<MissionCard>> getMissionCards() {
		return this.missionCards;
	}

	public int getMissionCardCount() {
		return this.missionCards.size();
	}

	public List<Location> getLocations() {
		return new ArrayList<>(this.locations.values());
	}

	public List<Connection> getConnections() {
		return new ArrayList<>(this.connections.values());
	}

	public Connection getConnectionFromLocations(Location fromLocation, Location toLocation) {
		return this.connections.values().parallelStream().filter(c -> {
			if (c.fromLocation.equals(fromLocation) || c.fromLocation.equals(toLocation)) { return c.toLocation.equals(fromLocation) || c.toLocation.equals(toLocation); }
			return false;
		}).findAny().orElse(null);
	}

	private void loadLocations() {
		try {
			Decode decode = Decode.decode("Locations.txt");
			while (decode.hasNext()) {
				String[] line = decode.next();
				String name = line[0];
				int x = Integer.parseInt(line[1]);
				int y = Integer.parseInt(line[2]);
				Point p = new Point(x, y);
				this.locations.put(name, new Location(name, p));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Location getLocation(String locationName) {
		return this.locations.getOrDefault(locationName, null);
	}

	private void loadConnections() {
		try {
			Decode decode = Decode.decode("Connections.txt");
			while (decode.hasNext()) {
				String[] line = decode.next();

				Location fromLocation = this.getLocation(line[0]);
				if (fromLocation == null) {
					System.err.println("From Location not found " + line[0]);
					continue;
				}
				Location toLocation = this.getLocation(line[1]);
				if (toLocation == null) {
					System.err.println("To Location not found " + line[1]);
					continue;
				}
				byte length = 0;
				byte multiplicity = 0;
				try {
					length = Byte.parseByte(line[2]);
					multiplicity = Byte.parseByte(line[3]);
				} catch (NumberFormatException nfe) {
					System.err.println(Arrays.toString(line));
					continue;
				}
				MyColor[] colors = new MyColor[multiplicity];
				TransportMode[] transportMode = new TransportMode[multiplicity];
				for (int i = 0; i < multiplicity; i++) {
					colors[i] = MyColor.getMyColor(line[i + 4]);
					transportMode[i] = TransportMode.getTransportMode(line[i + 4 + multiplicity]);
				}
				Connection connection = new Connection(fromLocation, toLocation, length, multiplicity, colors, transportMode);
				fromLocation.addConnection(connection);
				toLocation.addConnection(connection);
				this.addConnection(connection);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addConnection(Connection connection) {
		if (!this.connections.containsKey(connection.ID)) {
			this.connections.put(connection.ID, connection);
		} else {
			System.err.println("Connection already saved: " + connection);
		}
	}

	private void readMissionCards() {
		try {
			Decode decode = Decode.decode("Missioncards.txt");
			while (decode.hasNext()) {
				String[] line = decode.next();
				Distance distance = Distance.findByAbbreviation(line[0]);
				byte points = 0;
				try {
					points = Byte.parseByte(line[1]);
				} catch (NumberFormatException nfe) {
					System.err.println(Arrays.toString(line));
					continue;
				}
				List<Location> locations = new ArrayList<>();
				for (int i = 2; i < line.length; i++) {
					locations.add(this.getLocation(line[i]));
				}
				MissionCardConstraints constraints = new MissionCardConstraints();
				MissionCard missionCard = new MissionCard(distance, points, locations, constraints);
				this.addMissionCard(missionCard);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addMissionCard(MissionCard missionCard) {
		List<MissionCard> cards = this.missionCards.get(missionCard.distance);
		if (!cards.contains(missionCard)) {
			cards.add(missionCard);
		} else {
			System.err.println("MissionCard already saved: " + missionCard);
		}
	}

	public void shuffleCards() {
		Collections.shuffle(this.allCards, Game.getInstance().getRandomGenerator());
	}

}
