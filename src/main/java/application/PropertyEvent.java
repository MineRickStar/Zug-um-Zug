package application;

import game.Player;

public class PropertyEvent {

	public final Player player;
	public final Property property;

	public PropertyEvent(Player player, Property property) {
		this.player = player;
		this.property = property;
	}

	public enum Property {

		COLORCARDDRAWN, COLORCARDADDED, COLORCARDREMOVED,

		MISSIONCARDDRAWN, MISSIONCARDADDED, MISSIONCARDFINISHED, MISSIONCARDEDITED,

		PLAYERCHANGE, GAMESTART,

		CONNECTIONBOUGHT,

	}

	@Override
	public String toString() {
		return "Event [player=" + this.player.getName() + ", property=" + this.property + "]";
	}

}
