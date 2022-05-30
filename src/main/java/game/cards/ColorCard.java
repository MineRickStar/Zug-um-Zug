package game.cards;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import application.Application;
import language.MyResourceBundle.LanguageKey;

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
		return "<html><body>" + this.transportMode().getDisplayNameSingular() + "<br>" + this.color().getColorNameSingular() + "</body></html>";
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

		BLACK(LanguageKey.BLACK, LanguageKey.BLACKPLURAL, Color.BLACK),
		BLUE(LanguageKey.BLUE, LanguageKey.BLUEPLURAL, Color.BLUE),
		RED(LanguageKey.RED, LanguageKey.REDPLURAL, Color.RED),
		GREEN(LanguageKey.GREEN, LanguageKey.GREENPLURAL, Color.GREEN),
		YELLOW(LanguageKey.YELLOW, LanguageKey.YELLOWPLURAL, Color.YELLOW),
		PURPLE(LanguageKey.PURPLE, LanguageKey.PURPLEPLURAL, Color.MAGENTA),
		WHITE(LanguageKey.WHITE, LanguageKey.WHITEPLURAL, Color.WHITE),
		ORANGE(LanguageKey.ORANGE, LanguageKey.ORANGEPLURAL, Color.ORANGE),
		GRAY(LanguageKey.GRAY, LanguageKey.GRAY, Color.GRAY),
		RAINBOW(LanguageKey.RAINBOW, LanguageKey.RAINBOW, Color.MAGENTA);

		private final LanguageKey colorNameSingular;
		private final LanguageKey colorNamePlural;
		public final Color realColor;

		MyColor(LanguageKey colorNameSingular, LanguageKey colorNamePlural, Color realColor) {
			this.colorNameSingular = colorNameSingular;
			this.colorNamePlural = colorNamePlural;
			this.realColor = realColor;
		}

		public String getColorNameSingular() {
			return Application.resources.getString(this.colorNameSingular);
		}

		public String getColorNamePlural() {
			return Application.resources.getString(this.colorNamePlural);
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
			return this.getColorNameSingular();
		}
	}

	public enum TransportMode implements Serializable {

		TRAIN(LanguageKey.TRAIN, LanguageKey.TRAINS, "t"),
		SHIP(LanguageKey.SHIP, LanguageKey.SHIPS, "s"),
		AIRPLANE(LanguageKey.AIRPLANE, LanguageKey.AIRPLANES, "a");

		private final LanguageKey displayNameSingular;
		private final LanguageKey displayNamePlural;
		public final String abbreviation;

		TransportMode(LanguageKey displayNameSingular, LanguageKey displayNamePlural, String abbreviation) {
			this.displayNameSingular = displayNameSingular;
			this.displayNamePlural = displayNamePlural;
			this.abbreviation = abbreviation;
		}

		public String getDisplayNameSingular() {
			return Application.resources.getString(this.displayNameSingular);
		}

		public String getDisplayNamePlural() {
			return Application.resources.getString(this.displayNamePlural);
		}

		public static TransportMode getTransportMode(String abbreviation) {
			return Stream.of(TransportMode.values()).filter(t -> t.abbreviation.equalsIgnoreCase(abbreviation)).findAny().get();
		}
	}
}
