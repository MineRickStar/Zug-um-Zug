package game;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.SwingUtilities;

import algorithm.Algorithm;
import algorithm.AlgorithmSettings;
import application.Application;
import application.Property;
import connection.Connection;
import connection.SingleConnection;
import game.board.GameBoard;
import game.board.Location;
import game.board.Location.LocationList;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;
import gui.dialog.DrawMissionCardDialog;
import gui.dialog.MissionCardHelperDialog;

public class Game {

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

	private Player instancePlayer;
	private List<Player> players;
	private int currentPlayerCounter;
	private int currentPlayerColorCardDraws;

	private PropertyChangeSupport propertyChangeSupport;

	public Game() {
		this.randomGenerator = new Random(13323334);
		this.players = new ArrayList<>();
		this.gameBoard = new GameBoard();
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void startGame() {
		this.gameBoard.startGame();
		Application.frame.start();
		this.currentPlayerCounter = this.getRandomGenerator().nextInt(this.players.size());
		this.distributeCards();

		for (int i = 0; i < this.players.size(); i++) {
			if (i == this.players.size() - 1) {
				this.gameStarted = true;
			}
			if (this.isPlayersTurn()) {
				new DrawMissionCardDialog(true);
			} else {
				this.getCurrentPlayerComputer().drawMissionCards();
			}
		}
		Application.frame.revalidate();
		Application.frame.repaint();
	}

	public void distributeCards() {
		this.players.forEach(p -> p.addColorCard(IntStream.range(0, Rules.getInstance().getFirstColorCards()).mapToObj(i -> this.gameBoard.drawColorCard()).toArray(ColorCard[]::new)));
	}

	public int getMissionCardCount(Distance distance) {
		return this.gameBoard.getMissionCardCount(distance);
	}

	public void drawMissionCards(Map<Distance, Integer> missionCardDistribution) {
		List<MissionCard> missionCards = new ArrayList<>();
		Iterator<Entry<Distance, Integer>> it = missionCardDistribution.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Distance, Integer> entry = it.next();
			for (int i = 0; i < entry.getValue(); i++) {
				missionCards.add(this.gameBoard.drawMissionCard(entry.getKey()));
			}
		}
		if (this.isPlayersTurn()) {
			SwingUtilities.invokeLater(() -> new MissionCardHelperDialog(missionCards));
		} else {
			this.getCurrentPlayerComputer().decideForMissionCards(missionCards);
		}
		this.nextPlayer();
	}

	@SuppressWarnings("unused")
	private void calculateMissionCards(List<MissionCard> missionCards, Player currentPlayer) {
		List<List<LocationList>> pairs = Game.subsets(missionCards.stream().map(m -> new LocationList(m.getLocations())).toList());
		AlgorithmSettings settings = new AlgorithmSettings(currentPlayer);
		settings.pathSegments = 12;
		pairs.stream().sorted((o1, o2) -> Integer.compare(o1.size(), o2.size())).forEach(l -> Algorithm.findShortestPath(l, settings));
	}

	private static List<List<LocationList>> subsets(List<LocationList> pairs) {
		return IntStream.rangeClosed(1, (int) (Math.pow(2, pairs.size()) - 1)).mapToObj(value -> {
			List<LocationList> list1 = new ArrayList<>();
			for (int i = 0, j = 1; j <= value; i++, j *= 2) {
				if ((value & j) == j) {
					list1.add(pairs.get(i));
				}
			}
			return list1;
		}).collect(Collectors.toList());
	}

	public void playerBuysConnection(Player player, SingleConnection singelConnection, List<ColorCard> buyingCards) {
		singelConnection.setOwner(player);
		player.buySingleConnection(singelConnection, buyingCards);
		this.nextPlayer();
	}

	public void drawColorCardFromDeck(Player player) {
		this.colorCardDrawn(player, this.gameBoard.drawColorCard(), false);
	}

	public void drawColorCardsFromOpenDeck(Player player, int index) {
		this.colorCardDrawn(player, this.gameBoard.drawCardFromOpenCards(index), true);
	}

	private void colorCardDrawn(Player player, ColorCard colorCard, boolean openCardDrawn) {
		if (openCardDrawn && (colorCard.color() == MyColor.RAINBOW)) {
			this.currentPlayerColorCardDraws += Rules.getInstance().getLocomotiveWorth();
		} else {
			this.currentPlayerColorCardDraws++;
		}
		player.addColorCard(new ColorCard[] { colorCard });
		if (this.currentPlayerColorCardDraws >= Rules.getInstance().getColorCardsDrawing()) {
			this.nextPlayer();
		}
	}

	public List<ColorCard> getOpenCards() {
		return this.gameBoard.getOpenCards();
	}

	public int getRemainingCards() {
		return this.gameBoard.getClosedCardCount();
	}

	public int getCurrentPlayerColorCardDraws() {
		return this.currentPlayerColorCardDraws;
	}

	public boolean isCardAlreadyDrawn() {
		return this.currentPlayerColorCardDraws != 0;
	}

	private Player getCurrentPlayer() {
		return this.players.get(this.currentPlayerCounter);
	}

	public Player getInstancePlayer() {
		return this.instancePlayer;
	}

	public Computer getCurrentPlayerComputer() {
		return (Computer) this.getCurrentPlayer();
	}

	public boolean isPlayersTurn() {
		return this.getCurrentPlayer().equals(this.instancePlayer);
	}

	public boolean isColorAvailable(MyColor color) {
		return this.players.stream().noneMatch(p -> p.playerColor == color);
	}

	public void nextPlayer() {
		Player oldValue = this.getCurrentPlayer();
		this.currentPlayerCounter = (this.currentPlayerCounter + 1) % this.players.size();
		this.currentPlayerColorCardDraws = 0;
		if (this.gameStarted) {
			this.fireAction(this, Property.PLAYERCHANGE, oldValue, this.getCurrentPlayer());
			if (this.getCurrentPlayer() instanceof Computer com) {
				new Thread(() -> com.nextMove()).start();
			}
		}
		System.out.println();
		System.out.println("Nächster spieler: ");
		System.out.println(this.getCurrentPlayer());
	}

	public Player addInstancePlayer(String playerName, MyColor color) {
		if (this.instancePlayer == null) {
			Player player = new Player(playerName, color);
			this.instancePlayer = player;
			this.players.add(player);
			return player;
		}
		return null;
	}

	public void addComputer(String computerName, MyColor color, int difficulty) {
		Computer computer = new Computer(computerName, color, difficulty);
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

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
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

}
