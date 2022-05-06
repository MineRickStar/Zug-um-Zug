package game.cards;

public class ColorCard {

	private MyColor color;
	private TransportMode transportMode;

	public ColorCard(MyColor color, TransportMode transportMode) {
		this.color = color;
		this.transportMode = transportMode;
	}

	public MyColor color() {
		return this.color;
	}

	public TransportMode transportMode() {
		return this.transportMode;
	}
}
