package game.cards;

import java.awt.Color;
import java.util.stream.Stream;

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
		return Stream.of(MyColor.values()).filter(c -> c != GRAY && c != RAINBOW).toArray(MyColor[]::new);
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
