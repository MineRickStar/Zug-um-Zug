package game;

import java.util.List;

import game.cards.MissionCard;
import game.cards.MyColor;

public class Computer extends Player {

	public Computer(String name, MyColor color) {
		super(name, color);
	}

	public void nextMove() {
		System.out.println("Computer Spielt");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Game.getInstance().colorCardDrawn(false, 0);
		Game.getInstance().colorCardDrawn(false, 0);
	}

	@Override
	public void addNewMissionCards(List<MissionCard> missionCards) {
		this.missionCards.addAll(missionCards);
		// TODO in new Thread
		System.out.println("Computer Mission Cards");
	}

}
