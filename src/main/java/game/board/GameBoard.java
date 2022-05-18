package game.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import connection.Connection;
import game.Game;
import game.GameMap;
import game.Rules;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.ColorCard.TransportMode;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;

public class GameBoard {

	private GameMap map;

	private List<ColorCard> closedCards;
	private List<ColorCard> openCards;
	private List<ColorCard> usedCards;

	public GameBoard() {
		this.closedCards = new ArrayList<>();
		this.openCards = new ArrayList<>();
		this.usedCards = new ArrayList<>();
		this.fillCards();
	}

	public void setMap(GameMap map) {
		this.map = map;
		this.map.loadMap();

	}

	public void startGame() {
		this.map.startGame();
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
		this.fillColor(Rules.getInstance().getTrainRainbowColorCardCount(), MyColor.RAINBOW, TransportMode.TRAIN);
		this.fillColor(Rules.getInstance().getShipRainbowColorCardCount(), MyColor.RAINBOW, TransportMode.SHIP);
		this.fillColor(Rules.getInstance().getAirplaneRainbowColorCardCount(), MyColor.RAINBOW, TransportMode.AIRPLANE);
	}

	private void fillColor(int max, MyColor color, TransportMode transportMode) {
		for (int i = 0; i < max; i++) {
			this.closedCards.add(new ColorCard(color, transportMode));
		}
	}

	public ColorCard drawColorCard() {
		if (this.closedCards.size() == 0) {
			if (this.usedCards.isEmpty()) { return null; }
			this.closedCards.addAll(this.usedCards);
			this.usedCards.clear();
			this.shuffleCards();
		}
		return this.closedCards.remove(0);
	}

	public ColorCard drawCardFromOpenCards(int index) {
		ColorCard colorCard = this.openCards.remove(index);
		this.openCards.add(this.drawColorCard());
		if (Rules.getInstance().isShuffleWithMaxOpenLocomotives()
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

	public int getClosedCardCount() {
		return this.closedCards.size();
	}

	public void addUsedCards(ColorCard color, int count) {
		for (int i = 0; i <= count; i++) {
			this.usedCards.add(color);
		}
	}

	public GameMap getMap() {
		return this.map;
	}

	public MissionCard drawMissionCard(Distance distance) {
		return this.map.drawMissionCard(distance);
	}

	public int getMissionCardCount(Distance distance) {
		return this.map.getMissionCardCount(distance);
	}

	public List<Location> getLocations() {
		return this.map.getLocations();
	}

	public List<Connection> getConnections() {
		return this.map.getConnections();
	}

	public Connection getConnectionFromLocations(Location fromLocation, Location toLocation) {
		return this.map.getConnectionFromLocations(fromLocation, toLocation);
	}

	public Location getLocation(String locationName) {
		return this.map.getLocation(locationName);
	}

	public void shuffleCards() {
		Collections.shuffle(this.closedCards, Game.getInstance().getRandomGenerator());
	}

}
