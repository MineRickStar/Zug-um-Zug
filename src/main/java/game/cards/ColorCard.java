package game.cards;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

public class ColorCard {

	private final MyColor color;
	private final TransportMode transportMode;

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

	public String getColorCardString() {
		return "<html><body>" + this.transportMode().displayName + "<br>" + this.color().colorName + "</body></html>";
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.color, this.transportMode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (this.getClass() != obj.getClass()) { return false; }
		ColorCard other = (ColorCard) obj;
		return (this.color == other.color) && (this.transportMode == other.transportMode);
	}

	@Override
	public String toString() {
		return "[" + this.color + ", " + this.transportMode + "]";
	}

	public enum MyColor {

		BLACK("Black", Color.BLACK), BLUE("Blue", Color.BLUE), RED("Red", Color.RED), GREEN("Green", Color.GREEN), YELLOW("Yellow", Color.YELLOW), PURPLE("Purple", Color.MAGENTA),
		WHITE("White", Color.WHITE), ORANGE("Orange", Color.ORANGE), GRAY("Gray", Color.GRAY), RAINBOW("Rainbow", Color.MAGENTA);

		public final String colorName;
		public final Color realColor;

		MyColor(String colorName, Color realColor) {
			this.colorName = colorName;
			this.realColor = realColor;
		}

		public Color getRealColor() {
			return this.realColor;
		}

		public static MyColor getMyColor(String colorName) {
			return MyColor.valueOf(colorName.toUpperCase());
		}

		public static MyColor[] getNormalMyColors() {
			return Stream.of(MyColor.values()).filter(c -> (c != GRAY) && (c != RAINBOW)).toArray(MyColor[]::new);
		}

		public Color getComplementaryColor() {
			return MyColor.getComplementaryColor(this);
		}

		public static Color getComplementaryColor(MyColor myColor) {
			Color color = myColor.realColor;
			if (color == Color.WHITE) { return Color.BLACK; }
			if (color == Color.BLACK) { return Color.WHITE; }
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			int maxRGB = Math.max(r, Math.max(g, b));
			int minRGB = Math.min(r, Math.min(g, b));
			int addition = maxRGB + minRGB;
			return new Color(addition - r, addition - g, addition - b);
		}

		@Override
		public String toString() {
			return this.colorName;
		}
	}

	public enum TransportMode implements Serializable {
		TRAIN("Train", "t"), SHIP("Ship", "s"), AIRPLANE("Airplane", "a");

		public final String displayName;
		public final String abbreviation;

		TransportMode(String displayName, String abbreviation) {
			this.displayName = displayName;
			this.abbreviation = abbreviation;
		}

		public static TransportMode getTransportMode(String abbreviation) {
			return Stream.of(TransportMode.values()).filter(t -> t.abbreviation.equalsIgnoreCase(abbreviation)).findAny().get();
		}
	}
}
