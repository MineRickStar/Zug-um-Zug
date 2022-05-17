package game;

import java.util.EnumMap;
import java.util.List;

import game.cards.ColorCard.MyColor;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;

public class Computer extends Player {

	private final int difficulty;

	public Computer(String name, MyColor color, int difficulty) {
		super(name, color);
		this.difficulty = difficulty;
	}

	public void nextMove() {
		System.out.println("Computer Spielt");
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Game.getInstance().nextPlayer();
//		if (Game.getInstance().getRemainingCards() > 1) {
//			Game.getInstance().drawColorCardFromDeck(this);
//			Game.getInstance().drawColorCardFromDeck(this);
//		} else {
//			System.out.println("Keine Karte mehr da");
//		}
	}

	public void drawMissionCards() {
		EnumMap<Distance, Integer> MissionCardDistribution = new EnumMap<>(Distance.class);
		MissionCardDistribution.put(Distance.SHORT, 2);
		MissionCardDistribution.put(Distance.MIDDLE, 2);
		Game.getInstance().drawMissionCards(MissionCardDistribution);
	}

	public void decideForMissionCards(List<MissionCard> missionCards) {
		this.missionCards.addAll(missionCards);
		System.out.println("Computer Mission Cards");
	}
}
