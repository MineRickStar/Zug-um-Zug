package game;

import java.util.EnumMap;

import game.cards.ColorCard.TransportMode;

public class Rules {

	private static Rules instance;

	public static Rules getInstance() {
		if (Rules.instance == null) {
			Rules.instance = new Rules();
		}
		return Rules.instance;
	}

	///// PlayerRules /////

	private int cardsLimit = 0;
	private int locomotivCardsLimit = 0;

	private int colorCardsDrawing = 2;
	private int firstColorCards = 4;
	private int missionCardsDrawing = 4;
	private int firstMissionCardsKeeping = 2;
	private int defaultMissionCardsKeeping = 1;

	///// CardRules /////

	private int openColorCards = 5;

	private int locomotiveWorth = 2;

	private boolean shuffleWithMaxOpenLocomotives = true;
	private int maxOpenLocomotives = 3;

	///// MapRules (TransportMode Dependent) /////

	// Train //

	private int trainColorCardCount = 12;
	private int trainLocomotiveColorCardCount = 14;

	private int trainCarrigeCount = 45;

	private int[] pointsTrainConnection = { 1, 2, 4, 7, 10, 15, 18 };

	// Ship //

	private int shipColorCardCount = 6;
	private int shipLocomotiveColorCardCount = 6;

	private int shipCarrigeCount = 20;

	private int[] pointsShipConnection = { 1, 2, 4, 7, 10, 15, 18 };

	// Airplane //

	private int airplaneColorCardCount = 2;
	private int airplaneLocomotiveColorCardCount = 2;

	private int airplaneCarrigeCount = 10;

	private int[] pointsAirplaneConnection = { 1, 2, 4, 7, 10, 15, 18 };

	///// END /////

	private Rules() {}

	///// PlayerRules /////

	public int getCardsLimit() {
		return this.cardsLimit;
	}

	public void setCardsLimit(int cardsLimit) {
		this.cardsLimit = cardsLimit;
	}

	public int getLocomotivCardsLimit() {
		return this.locomotivCardsLimit;
	}

	public void setLocomotiveCardsLimit(int locomotiveCardsLimit) {
		this.locomotivCardsLimit = locomotiveCardsLimit;
	}

	public int getColorCardsDrawing() {
		return this.colorCardsDrawing;
	}

	public void setColorCardsDrawing(int colorCardsDrawing) {
		this.colorCardsDrawing = colorCardsDrawing;
	}

	public int getFirstColorCards() {
		return this.firstColorCards;
	}

	public void setFirstColorCards(int firstColorCards) {
		this.firstColorCards = firstColorCards;
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

	///// CardRules /////

	public int getOpenColorCards() {
		return this.openColorCards;
	}

	public void setOpenColorCards(int openColorCards) {
		this.openColorCards = openColorCards;
	}

	public int getLocomotiveWorth() {
		return this.locomotiveWorth;
	}

	public void setLocomotiveWorth(int locomotiveWorth) {
		this.locomotiveWorth = locomotiveWorth;
	}

	public boolean isShuffleWithMaxOpenLocomotives() {
		return this.shuffleWithMaxOpenLocomotives;
	}

	public void setShuffleWithMaxOpenLocomotives(boolean shuffleWithMaxOpenLocomotives) {
		this.shuffleWithMaxOpenLocomotives = shuffleWithMaxOpenLocomotives;
	}

	public int getMaxOpenLocomotives() {
		return this.maxOpenLocomotives;
	}

	public void setMaxOpenLocomotives(int maxOpenLocomotives) {
		this.maxOpenLocomotives = maxOpenLocomotives;
	}

	///// MapRules (TransportMode Dependent) /////

	public EnumMap<TransportMode, Integer> getTransportMap() {
		EnumMap<TransportMode, Integer> map = new EnumMap<>(TransportMode.class);
		map.put(TransportMode.TRAIN, this.trainCarrigeCount);
		map.put(TransportMode.SHIP, this.shipCarrigeCount);
		map.put(TransportMode.AIRPLANE, this.airplaneCarrigeCount);
		return map;
	}

	public int getLocomotiveCardCount(TransportMode transportMode) {
		switch (transportMode) {
		case AIRPLANE:
			return this.airplaneLocomotiveColorCardCount;
		case SHIP:
			return this.shipLocomotiveColorCardCount;
		case TRAIN:
			return this.trainLocomotiveColorCardCount;
		}
		return 0;
	}

	public void setLocomotiveCardCount(TransportMode transportMode, int locomotiveCardCount) {
		switch (transportMode) {
		case AIRPLANE:
			this.airplaneLocomotiveColorCardCount = locomotiveCardCount;
			break;
		case SHIP:
			this.shipLocomotiveColorCardCount = locomotiveCardCount;
			break;
		case TRAIN:
			this.trainLocomotiveColorCardCount = locomotiveCardCount;
			break;
		}
	}

	public int getColorCardCount(TransportMode transportMode) {
		switch (transportMode) {
		case AIRPLANE:
			return this.airplaneColorCardCount;
		case SHIP:
			return this.shipColorCardCount;
		case TRAIN:
			return this.trainColorCardCount;
		}
		return 0;
	}

	public void setColorCardCount(TransportMode transportMode, int colorCardCount) {
		switch (transportMode) {
		case AIRPLANE:
			this.airplaneColorCardCount = colorCardCount;
			break;
		case SHIP:
			this.shipColorCardCount = colorCardCount;
			break;
		case TRAIN:
			this.shipColorCardCount = colorCardCount;
			break;
		}
	}

	public int getCarrigeCount(TransportMode transportMode) {
		switch (transportMode) {
		case AIRPLANE:
			return this.airplaneCarrigeCount;
		case SHIP:
			return this.shipCarrigeCount;
		case TRAIN:
			return this.trainCarrigeCount;
		}
		return 0;
	}

	public void setCarrigeCount(TransportMode transportMode, int carrigeCount) {
		switch (transportMode) {
		case AIRPLANE:
			this.airplaneCarrigeCount = carrigeCount;
			break;
		case SHIP:
			this.shipCarrigeCount = carrigeCount;
			break;
		case TRAIN:
			this.trainCarrigeCount = carrigeCount;
			break;
		}
	}

	public int[] getPointsConnection(TransportMode transportMode) {
		switch (transportMode) {
		case AIRPLANE:
			return this.pointsAirplaneConnection;
		case SHIP:
			return this.pointsShipConnection;
		case TRAIN:
			return this.pointsTrainConnection;
		}
		return new int[0];
	}

	public void setPointsConnection(TransportMode transportMode, int[] pointsConnection) {
		switch (transportMode) {
		case AIRPLANE:
			this.pointsAirplaneConnection = pointsConnection;
			break;
		case SHIP:
			this.pointsShipConnection = pointsConnection;
			break;
		case TRAIN:
			this.pointsTrainConnection = pointsConnection;
			break;
		}
	}

}
