package game;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;

import application.Application;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.MissionCard;
import game.cards.MissionCard.Distance;
import language.MyResourceBundle.LanguageKey;

public class Computer extends Player {

	private final Difficulty difficulty;

	public Computer(String name, MyColor color, Difficulty difficulty) {
		super(name, color);
		this.difficulty = difficulty;
	}

	public void nextMove() {
//		this.drawCards();
		Game.getInstance().nextPlayer();
	}

	@SuppressWarnings("unused")
	private void drawCards() {
		List<ColorCard> openCards = Game.getInstance().getOpenCards();
		int[] indexes = openCards.stream().filter(c -> c.color() != MyColor.RAINBOW).mapToInt(c -> openCards.indexOf(c)).toArray();
		if (this.difficulty == Difficulty.MEDIUM) {
			Game.getInstance().drawColorCardsFromOpenDeck(this, indexes[0]);
		} else {
			Game.getInstance().drawColorCardsFromOpenDeck(this, indexes[1]);
		}
		List<ColorCard> openCards2 = Game.getInstance().getOpenCards();
		indexes = openCards2.stream().filter(c -> c.color() != MyColor.RAINBOW).mapToInt(c -> openCards2.indexOf(c)).toArray();
		if (this.difficulty == Difficulty.MEDIUM) {
			Game.getInstance().drawColorCardsFromOpenDeck(this, indexes[0]);
		} else {
			Game.getInstance().drawColorCardsFromOpenDeck(this, indexes[1]);
		}
	}

	public void drawMissionCards() {
		EnumMap<Distance, Integer> MissionCardDistribution = new EnumMap<>(Distance.class);
		MissionCardDistribution.put(Distance.SHORT, 2);
		MissionCardDistribution.put(Distance.MIDDLE, 2);
		Game.getInstance().drawMissionCards(this, MissionCardDistribution);
	}

	public void decideForMissionCards(List<MissionCard> missionCards) {
		this.missionCards.addAll(missionCards);
	}

	public enum Difficulty {

		EASY(LanguageKey.EASY, 1),
		MEDIUM(LanguageKey.MEDIUM, 2),
		HARD(LanguageKey.HARD, 3),
		EXTREME(LanguageKey.EXTREME, 4);

		private final LanguageKey displayName;
		public final int index;

		Difficulty(LanguageKey displayName, int index) {
			this.displayName = displayName;
			this.index = index;
		}

		public String getDisplayName() {
			return Application.resources.getString(this.displayName);
		}

		public static Difficulty getDifficultyWithIndex(int index) {
			return Stream.of(Difficulty.values()).filter(d -> d.index == index).findAny().orElse(null);
		}
	}

}
