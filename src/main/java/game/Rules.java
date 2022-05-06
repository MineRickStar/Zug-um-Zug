package game;

import java.util.EnumMap;

import game.cards.TransportMode;

public class Rules {

	private static Rules instance;

	public static Rules getInstance() {
		if (Rules.instance == null) {
			Rules.instance = new Rules();
		}
		return Rules.instance;
	}

	private int cardsLimit;
	private int rainbowCardslimit;

	private int colorCardsLayingDown = 5;
	private int firstColorCards = 4;
	private int ColorCardsDrawing = 2;
	private int LocomotiveWorth = 2;
	private int missionCardsDrawing = 4;
	private int firstMissionCardsKeeping = 2;
	private int defaultMissionCardsKeeping = 1;

	private boolean shuffleWithThreeLocomotives = true;
	private int MaxOpenLocomotives = 3;

	private int trainColorCardCount = 12;
	private int trainRainbowColorCardCount = 14;

	private int shipColorCardCount = 6;
	private int shipRainbowColorCardCount = 6;

	private int airplaneColorCardCount = 6;
	private int airplaneRainbowColorCardCount = 6;

	private int trainCount = 45;
	private int shipCount = 20;
	private int airplaneCount = 10;

	private byte[] pointsTrainConnection = { 1, 2, 4, 7, 10, 15, 18 };
	private byte[] pointsShipConnection = { 1, 2, 4, 7, 10, 15, 18 };
	private byte[] pointsAirConnection = { 1, 2, 4, 7, 10, 15, 18 };

	private Rules() {}

	public int getCardsLimit() {
		return this.cardsLimit;
	}

	public void setCardsLimit(int cardsLimit) {
		this.cardsLimit = cardsLimit;
	}

	public int getRainbowCardslimit() {
		return this.rainbowCardslimit;
	}

	public void setRainbowCardslimit(int rainbowCardslimit) {
		this.rainbowCardslimit = rainbowCardslimit;
	}

	public int getColorCardsLayingDown() {
		return this.colorCardsLayingDown;
	}

	public void setColorCardsLayingDown(int colorCardsLayingDown) {
		this.colorCardsLayingDown = colorCardsLayingDown;
	}

	public int getFirstColorCards() {
		return this.firstColorCards;
	}

	public void setFirstColorCards(int firstColorCards) {
		this.firstColorCards = firstColorCards;
	}

	public int getColorCardsDrawing() {
		return this.ColorCardsDrawing;
	}

	public void setColorCardsDrawing(int colorCardsDrawing) {
		this.ColorCardsDrawing = colorCardsDrawing;
	}

	public int getLocomotiveWorth() {
		return this.LocomotiveWorth;
	}

	public void setLocomotiveWorth(int locomotiveWorth) {
		this.LocomotiveWorth = locomotiveWorth;
	}

	public int getMissionCardsDrawing() {
		return this.missionCardsDrawing;
	}

	public void setMissionCardsDrawing(int missionCardsDrawing) {
		this.missionCardsDrawing = missionCardsDrawing;
	}

	public int getFirstMissionCardsKeeping() {
		return this.firstMissionCardsKeeping;
	}

	public void setFirstMissionCardsKeeping(int firstMissionCardsKeeping) {
		this.firstMissionCardsKeeping = firstMissionCardsKeeping;
	}

	public int getDefaultMissionCardsKeeping() {
		return this.defaultMissionCardsKeeping;
	}

	public void setDefaultMissionCardsKeeping(int defaultMissionCardsKeeping) {
		this.defaultMissionCardsKeeping = defaultMissionCardsKeeping;
	}

	public boolean isShuffleWithThreeLocomotives() {
		return this.shuffleWithThreeLocomotives;
	}

	public void setShuffleWithThreeLocomotives(boolean shuffleWithThreeLocomotives) {
		this.shuffleWithThreeLocomotives = shuffleWithThreeLocomotives;
	}

	public int getMaxOpenLocomotives() {
		return this.MaxOpenLocomotives;
	}

	public void setMaxOpenLocomotives(int maxOpenLocomotives) {
		this.MaxOpenLocomotives = maxOpenLocomotives;
	}

	public int getTrainColorCardCount() {
		return this.trainColorCardCount;
	}

	public void setTrainColorCardCount(int trainColorCardCount) {
		this.trainColorCardCount = trainColorCardCount;
	}

	public int getTrainRainbowColorCardCount() {
		return this.trainRainbowColorCardCount;
	}

	public void setTrainRainbowColorCardCount(int trainRainbowColorCardCount) {
		this.trainRainbowColorCardCount = trainRainbowColorCardCount;
	}

	public int getShipColorCardCount() {
		return this.shipColorCardCount;
	}

	public void setShipColorCardCount(int shipColorCardCount) {
		this.shipColorCardCount = shipColorCardCount;
	}

	public int getShipRainbowColorCardCount() {
		return this.shipRainbowColorCardCount;
	}

	public void setShipRainbowColorCardCount(int shipRainbowColorCardCount) {
		this.shipRainbowColorCardCount = shipRainbowColorCardCount;
	}

	public int getAirplaneColorCardCount() {
		return this.airplaneColorCardCount;
	}

	public void setAirplaneColorCardCount(int airplaneColorCardCount) {
		this.airplaneColorCardCount = airplaneColorCardCount;
	}

	public int getAirplaneRainbowColorCardCount() {
		return this.airplaneRainbowColorCardCount;
	}

	public void setAirplaneRainbowColorCardCount(int airplaneRainbowColorCardCount) {
		this.airplaneRainbowColorCardCount = airplaneRainbowColorCardCount;
	}

	public int getTrainCount() {
		return this.trainCount;
	}

	public void setTrainCount(int trainCount) {
		this.trainCount = trainCount;
	}

	public int getShipCount() {
		return this.shipCount;
	}

	public void setShipCount(int shipCount) {
		this.shipCount = shipCount;
	}

	public int getAirplaneCount() {
		return this.airplaneCount;
	}

	public void setAirplaneCount(int airplaneCount) {
		this.airplaneCount = airplaneCount;
	}

	public int getCarrigeCount() {
		return this.trainCount + this.shipCount + this.airplaneCount;
	}

	public EnumMap<TransportMode, Integer> getTransportMap() {
		EnumMap<TransportMode, Integer> map = new EnumMap<>(TransportMode.class);
		map.put(TransportMode.TRAIN, this.getTrainCount());
		map.put(TransportMode.SHIP, this.getShipCount());
		map.put(TransportMode.AIRPLANE, this.getAirplaneCount());
		return map;
	}

	public byte[] getPointsTrainConnection() {
		return this.pointsTrainConnection;
	}

	public byte[] getPointsShipConnection() {
		return this.pointsShipConnection;
	}

	public byte[] getPointsAirConnection() {
		return this.pointsAirConnection;
	}

	public byte[] getPointsConnection(TransportMode transportMode) {
		switch (transportMode) {
		case AIRPLANE:
			return this.getPointsAirConnection();
		case SHIP:
			return this.getPointsShipConnection();
		case TRAIN:
			return this.getPointsTrainConnection();
		}
		return new byte[0];
	}

}
