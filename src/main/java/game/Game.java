package game;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.SwingWorker;

import application.Algorithm;
import application.Algorithm.AlgorithmSettings;
import application.Algorithm.LocationPair;
import application.Application;
import application.Property;
import game.board.Connection;
import game.board.GameBoard;
import game.board.Location;
import game.board.SingleConnection;
import game.cards.ColorCard;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;
import game.cards.MyColor;
import gui.MissionCardDialog;
import gui.MissionCardHelperFrame;

public class Game implements PropertyChangeListener {

	private static Game instance;

	public static Game getInstance() {
		if (Game.instance == null) {
			Game.instance = new Game();
		}
		return Game.instance;
	}

	private boolean gameStarted = false;
	private Random randomGenerator;

	private GameBoard gameBoard;

	private List<Player> players;
	private int currentPlayerCounter;
	private int currentPlayerColorCardDraws;

	private PropertyChangeSupport propertyChangeSupport;

	public Game() {
		this.randomGenerator = new Random(13323334);
		this.players = new ArrayList<>();
		this.gameBoard = new GameBoard();
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.addPropertyChangeListener(Property.DRAWMISSIONCARDS, this);
	}

	public void startComputerGame() {
		this.startGame();

		if (this.getCurrentPlayer() instanceof Computer) {
			EnumMap<Distance, Integer> cards = new EnumMap<>(Distance.class);
			cards.put(Distance.SHORT, 2);
			cards.put(Distance.MIDDLE, 2);
			this.drawMissionCards(cards, false);
		} else {
			new MissionCardDialog(true);
		}

		if (this.getCurrentPlayer() instanceof Computer) {
			EnumMap<Distance, Integer> cards = new EnumMap<>(Distance.class);
			cards.put(Distance.SHORT, 2);
			cards.put(Distance.MIDDLE, 2);
			this.drawMissionCards(cards, false);
		} else {
			new MissionCardDialog(true);
		}
	}

	public void startGame() {
		this.gameStarted = true;
		this.gameBoard.startGame();
		Application.frame.start();
		this.currentPlayerCounter = this.getRandomGenerator().nextInt(this.players.size());
		this.distributeCards();
	}

	public void distributeCards() {
		this.players.forEach(p -> {
			for (int i = 0; i < Rules.getInstance().getFirstColorCards(); i++) {
				p.addColorCard(this.gameBoard.drawColorCard());
			}
		});
	}

	public int getMissionCardCount(Distance distance) {
		return this.gameBoard.getMissionCardCount(distance);
	}

	public void drawMissionCards(Map<Distance, Integer> missionCardDistribution, boolean fireProperty) {
		List<MissionCard> missionCards = new ArrayList<>();
		Iterator<Entry<Distance, Integer>> it = missionCardDistribution.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Distance, Integer> entry = it.next();
			for (int i = 0; i < entry.getValue(); i++) {
				missionCards.add(this.gameBoard.drawMissionCard(entry.getKey()));
			}
		}
		Player currentPlayer = this.getCurrentPlayer();
		currentPlayer.addNewMissionCards(missionCards);
		if (fireProperty) {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					Game.this.calculateMissionCards(missionCards, currentPlayer);
					return null;
				}
			};
			worker.execute();
			new MissionCardHelperFrame(currentPlayer);
		}
		this.nextPlayer();
	}

	private void calculateMissionCards(List<MissionCard> missionCards, Player currentPlayer) {
		List<List<LocationPair>> pairs = Game.subsets(missionCards.stream().map(m -> new LocationPair(m.getFromLocation(), m.getToLocation())).toList());
		AlgorithmSettings settings = new AlgorithmSettings(currentPlayer);
		pairs.stream().sorted((o1, o2) -> Integer.compare(o1.size(), o2.size())).forEach(l -> Algorithm.findShortestPath(l, settings));
	}

	private static List<List<LocationPair>> subsets(List<LocationPair> pairs) {
		return IntStream.rangeClosed(1, (int) (Math.pow(2, pairs.size()) - 1)).mapToObj(value -> {
			List<LocationPair> list1 = new ArrayList<>();
			for (int i = 0, j = 1; j <= value; i++, j *= 2) {
				if ((value & j) == j) {
					list1.add(pairs.get(i));
				}
			}
			return list1;
		}).collect(Collectors.toList());
	}

	public boolean canPlayerBuySingleConnection(SingleConnection singleConnection) {
		Player currentPlayer = this.getCurrentPlayer();
		boolean enoughCarriges = currentPlayer.getPieceCount(singleConnection.transportMode) >= singleConnection.parentConnection.length;
		boolean enoughColorCards = currentPlayer.hasPlayerEnoughColorCards(singleConnection.getColorCardRepresentation(), singleConnection.parentConnection.length);
		return enoughCarriges && enoughColorCards;
	}

	public void playerBuysConnection(SingleConnection singelConnection, List<ColorCard> buyingCards) {
		Player currentPlayer = this.getCurrentPlayer();
		currentPlayer.buySingleConnection(singelConnection, buyingCards);
		singelConnection.setOwner(this.getCurrentPlayer());
		this.nextPlayer();
	}

	public List<ColorCard[]> getBuyingOptions(ColorCard colorCard, int count) {
		return this.getCurrentPlayer().getBuyingOptions(colorCard, count);
	}

	public void colorCardDrawn(boolean openCardDrawn, int index) {
		Player currentPlayer = this.getCurrentPlayer();
		ColorCard colorCard;
		if (openCardDrawn) {
			colorCard = this.gameBoard.drawCardFromOpenCards(index);
		} else {
			colorCard = this.gameBoard.drawColorCard();
		}
		currentPlayer.addColorCard(colorCard);
		if (openCardDrawn && (colorCard.color() == MyColor.RAINBOW)) {
			this.currentPlayerColorCardDraws += Rules.getInstance().getLocomotiveWorth();
		} else {
			this.currentPlayerColorCardDraws++;
		}
		if (this.currentPlayerColorCardDraws >= Rules.getInstance().getColorCardsDrawing()) {
			this.nextPlayer();
		}
	}

	public void highlightConnection(List<LocationPair> locationPairs, Player player) {
		this.gameBoard.highlightConnection(locationPairs, player);
	}

	public void setHighlightConnections(List<Path> paths) {
		if (this.gameBoard == null) { return; }
		this.gameBoard.setHighlightConnections(paths);
	}

	public List<Path> getHighlightConnections() {
		if (this.gameBoard == null) { return null; }
		return this.gameBoard.getHighlightedConnections();
	}

	public List<ColorCard> getOpenCards() {
		return this.gameBoard.getOpenCards();
	}

	public int getRemainingCards() {
		return this.gameBoard.getRemainingCards();
	}

	public int getCurrentPlayerColorCardDraws() {
		return this.currentPlayerColorCardDraws;
	}

	public boolean isCardAlreadyDrawn() {
		return this.currentPlayerColorCardDraws != 0;
	}

	public Player getCurrentPlayer() {
		return this.players.get(this.currentPlayerCounter);
	}

	public boolean isPlayersTurn() {
		return this.getCurrentPlayer().equals(Application.player);
	}

	public void nextPlayer() {
		System.out.println();
		System.out.println("Spieler: ");
		System.out.println(this.getCurrentPlayer());
		Player oldValue = this.getCurrentPlayer();
		this.currentPlayerCounter = (this.currentPlayerCounter + 1) % this.players.size();
		this.currentPlayerColorCardDraws = 0;
		this.fireAction(this, Property.PLAYERCHANGE, oldValue, this.getCurrentPlayer());
		if (this.getCurrentPlayer() instanceof Computer com) {
			new Thread(() -> com.nextMove()).start();
		}
	}

	public Player addPlayer(String playerName, MyColor color) {
		Player player = new Player(playerName, color);
		this.players.add(player);
		return player;
	}

	public void addComputer(String computerName, MyColor color) {
		Computer computer = new Computer(computerName, color);
		this.players.add(computer);
	}

	public Location getLocation(String locationName) {
		return this.gameBoard.getLocation(locationName);
	}

	public List<Location> getLocations() {
		return this.gameBoard.getLocations();
	}

	public Connection getConnectionFromLocations(String fromLocation, String toLocation) {
		return this.gameBoard.getConnectionFromLocations(this.getLocation(fromLocation), this.getLocation(toLocation));
	}

	public SingleConnection getConnectionFromLocations(String fromLocation, String toLocation, MyColor color) {
		return this.gameBoard.getConnectionFromLocations(this.getLocation(fromLocation), this.getLocation(toLocation)).getSingleConnectionWithColor(color);
	}

	public Connection getConnectionFromLocations(Location fromLocation, Location toLocation) {
		return this.gameBoard.getConnectionFromLocations(fromLocation, toLocation);
	}

	public List<Connection> getConnections() {
		return this.gameBoard.getConnections();
	}

	public Random getRandomGenerator() {
		return this.randomGenerator;
	}

	public boolean isGameStarted() {
		return this.gameStarted;
	}

	public void addPropertyChangeListener(Property property, PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(property.name(), listener);
	}

	public void fireAction(Object source, Property property, Object oldValue, Object newValue) {
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(source == null ? this : source, property.name(), oldValue, newValue));
	}

	@SuppressWarnings("unused")
	private List<MissionCard> getMissionsToLocation(String locationName) {
		Location location = this.getLocation(locationName);
		if (location == null) { return Collections.emptyList(); }
		return this.gameBoard.getMissionCards().values().stream().reduce((t, u) -> {
			ArrayList<MissionCard> l = new ArrayList<>(t);
			l.addAll(u);
			return l;
		}).get().parallelStream().filter(mission -> mission.getFromLocation().equals(location) || mission.getToLocation().equals(location)).toList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Property.DRAWMISSIONCARDS.name().equals(evt.getPropertyName())) {
			this.drawMissionCards((Map<Distance, Integer>) evt.getNewValue(), true);
		}
	}

}
