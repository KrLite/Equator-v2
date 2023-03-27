package net.krlite.equator.visual.color;

import com.scrtwpns.Mixbox;

import java.awt.*;

public class AccurateColor {
	private static final IllegalArgumentException COLOR_ARRAY_LENGTH_EXCEPTION = new IllegalArgumentException("Color array must be of length 3 or 4");

	public static final AccurateColor BLACK = fromColor(Color.BLACK), WHITE = fromColor(Color.WHITE),
			GRAY = fromColor(Color.GRAY), LIGHT_GRAY = fromColor(Color.LIGHT_GRAY), DARK_GRAY = fromColor(Color.DARK_GRAY),
			RED = fromColor(Color.RED), GREEN = fromColor(Color.GREEN), BLUE = fromColor(Color.BLUE),
			CYAN = fromColor(Color.CYAN), MAGENTA = fromColor(Color.MAGENTA), YELLOW = fromColor(Color.YELLOW),
			ORANGE = fromColor(Color.ORANGE), PINK = fromColor(Color.PINK), TRANSPARENT = new AccurateColor(0, 0, 0, 0, true);

	public static AccurateColor fromColor(Color color) {
		return fromInt(color.getRGB());
	}

	public static AccurateColor fromInt(int color) {
		return new AccurateColor(
			((color >> 16) & 0xFF) / 255.0,
			((color >> 8) & 0xFF) / 255.0,
			(color & 0xFF) / 255.0,
			color > 0xFFFFFF ? ((color >> 24) & 0xFF) / 255.0 : 1
		);
	}

	public static AccurateColor fromHexString(String hex) {
		return fromInt(Long.decode(hex).intValue());
	}

	public static AccurateColor fromArray(double[] color) {
		if (color.length < 3 || color.length > 4)
			throw COLOR_ARRAY_LENGTH_EXCEPTION;
		return new AccurateColor(color[0], color[1], color[2], color.length > 3 ? color[3] : 1);
	}

	public static AccurateColor fromArray(float[] color) {
		if (color.length < 3 || color.length > 4)
			throw COLOR_ARRAY_LENGTH_EXCEPTION;
		return new AccurateColor(color[0], color[1], color[2], color.length > 3 ? color[3] : 1);
	}

	public static AccurateColor fromArray(int[] color) {
		if (color.length < 3 || color.length > 4)
			throw COLOR_ARRAY_LENGTH_EXCEPTION;
		return new AccurateColor(color[0] / 255.0, color[1] / 255.0, color[2] / 255.0, color.length > 3 ? color[3] / 255.0 : 1);
	}

	public static AccurateColor fromGrayscale(double gray) {
		return new AccurateColor(gray, gray, gray, 1.0, false);
	}

	public static AccurateColor fromGrayscale(int gray) {
		return fromGrayscale(gray / 255.0);
	}

	protected AccurateColor(double red, double green, double blue, double opacity, boolean transparent) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.opacity = opacity;
		this.transparent = transparent;
	}

	public AccurateColor(double red, double green, double blue, double opacity) {
		this(red, green, blue, opacity, false);
	}

	public AccurateColor(double red, double green, double blue) {
		this(red, green, blue, 1.0, false);
	}

	public AccurateColor(int red, int green, int blue, int opacity) {
		this(red / 255.0, green / 255.0, blue / 255.0, opacity / 255.0, false);
	}

	public AccurateColor(int red, int green, int blue) {
		this(red, green, blue, 255);
	}

	private final double red, green, blue, opacity;
	private final boolean transparent;

	public double red() {
		return red;
	}

	public float redAsFloat() {
		return (float) red();
	}

	public int redAsInt() {
		return (int) (red() * 255);
	}

	public double green() {
		return green;
	}

	public float greenAsFloat() {
		return (float) green();
	}

	public int greenAsInt() {
		return (int) (green() * 255);
	}

	public double blue() {
		return blue;
	}

	public float blueAsFloat() {
		return (float) blue();
	}

	public int blueAsInt() {
		return (int) (blue() * 255);
	}

	public double opacity() {
		return opacity;
	}

	public float opacityAsFloat() {
		return (float) opacity();
	}

	public int opacityAsInt() {
		return (int) (opacity() * 255);
	}

	public boolean hasColor() {
		return !transparent;
	}

	public Color toColor() {
		return new Color(redAsInt(), greenAsInt(), blueAsInt(), opacityAsInt());
	}

	public int toInt() {
		return toColor().getRGB();
	}
	public Double[] toDoubleArray() {
		return new Double[] { red(), green(), blue(), opacity() };
	}

	public float[] toFloatArray() {
		return new float[] { redAsFloat(), greenAsFloat(), blueAsFloat(), opacityAsFloat() };
	}

	public int[] toIntArray() {
		return new int[] { redAsInt(), greenAsInt(), blueAsInt(), opacityAsInt() };
	}

	public AccurateColor red(double red) {
		return new AccurateColor(red, green(), blue(), opacity(), transparent);
	}

	public AccurateColor green(double green) {
		return new AccurateColor(red(), green, blue(), opacity(), transparent);
	}

	public AccurateColor blue(double blue) {
		return new AccurateColor(red(), green(), blue, opacity(), transparent);
	}

	public AccurateColor opacity(double opacity) {
		return new AccurateColor(red(), green(), blue(), opacity, transparent);
	}

	public AccurateColor orElse(AccurateColor another) {
		return hasColor() ? this : another;
	}

	public AccurateColor mix(AccurateColor another, double lambda) {
		return new AccurateColor(
			red() * (1 - lambda) + another.red() * lambda,
			green() * (1 - lambda) + another.green() * lambda,
			blue() * (1 - lambda) + another.blue() * lambda,
			opacity() * (1 - lambda) + another.opacity() * lambda
		);
	}

	public AccurateColor mix(AccurateColor another) {
		return mix(another, 0.5);
	}

	public AccurateColor opacityMix(AccurateColor another, double lambda) {
		return opacity(opacity() * (1 - lambda) + another.opacity() * lambda);
	}

	public AccurateColor opacityMix(AccurateColor another) {
		return opacityMix(another, 0.5);
	}

	public AccurateColor pigmentMix(AccurateColor another, double lambda) {
		return AccurateColor.fromArray(Mixbox.lerpFloat(toFloatArray(), another.toFloatArray(), (float) lambda));
	}

	public AccurateColor pigmentMix(AccurateColor another) {
		return pigmentMix(another, 0.5);
	}

	public AccurateColor transparent() {
		return opacity(0);
	}

	public AccurateColor opaque() {
		return opacity(1);
	}
}
