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

	private List<ColorCard> cards;
	private List<ColorCard> usedCards;

	public GameBoard() {
		this.cards = new ArrayList<>();
		this.usedCards = new ArrayList<>();
	}

	public void setMap(GameMap map) {
		this.map = map;
	}

	public void startGame() {
		this.map.startGame();
		this.fillCards();
		this.shuffleCards(this.cards);
	}

	private void fillCards() {
		TransportMode[] transportModes = this.map.getTransportModes();
		for (MyColor color : MyColor.getNormalMyColors()) {
			for (TransportMode mode : transportModes) {
				this.fillColor(Rules.getInstance().getColorCardCount(mode), color, mode);
			}
		}
		for (TransportMode mode : transportModes) {
			this.fillColor(Rules.getInstance().getLocomotiveCardCount(mode), MyColor.RAINBOW, mode);
		}
	}

	private void fillColor(int max, MyColor color, TransportMode transportMode) {
		for (int i = 0; i < max; i++) {
			this.cards.add(new ColorCard(color, transportMode));
		}
	}

	public ColorCard drawColorCardFromDeck() {
		return this.drawColorCard(Rules.getInstance().getOpenColorCards());
	}

	public ColorCard drawCardFromOpenCards(int index) {
		ColorCard colorCard = this.drawColorCard(index);
		if (Rules.getInstance().isShuffleWithMaxOpenLocomotives()
				&& (this.getOpenCards().stream().filter(c -> c != null).filter(c -> c.color() == MyColor.RAINBOW).count() == Rules.getInstance().getMaxOpenLocomotives())) {
			List<ColorCard> removeCards = this.getOpenCards();
			this.usedCards.addAll(removeCards);
			removeCards.clear();
		}
		return colorCard;
	}

	private ColorCard drawColorCard(int index) {
		if (this.cards.size() <= (Rules.getInstance().getOpenColorCards() + 5)) {
			this.shuffleUsedCardsAndAdd();
		}
		return this.cards.remove(index);
	}

	public ColorCard peekAtIndex(int index) {
		return this.cards.get(index);
	}

	public List<ColorCard> getOpenCards() {
		return this.cards.subList(0, Rules.getInstance().getOpenColorCards());
	}

	public int getCardCount() {
		return this.cards.size();
	}

	public void addUsedCards(List<ColorCard> usedCards) {
		this.usedCards.addAll(usedCards);
	}

	private void shuffleUsedCardsAndAdd() {
		this.shuffleCards(this.usedCards);
		this.cards.addAll(this.usedCards);
		this.usedCards.clear();
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

	public void shuffleCards(List<ColorCard> cards) {
		Collections.shuffle(cards, Game.getInstance().getRandomGenerator());
	}

}
