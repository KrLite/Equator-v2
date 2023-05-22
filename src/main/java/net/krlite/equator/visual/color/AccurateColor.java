package net.krlite.equator.visual.color;

import net.krlite.equator.math.algebra.Theory;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static net.krlite.equator.visual.color.Colorspace.*;

public class AccurateColor {
	// Constants

	public static final AccurateColor
			BLACK = fromColor(Color.BLACK),
			WHITE = fromColor(Color.WHITE),
			TRANSPARENT = new AccurateColor(RGB, new double[] { 0, 0, 0 }, 0, true);

	public static final AccurateColor
			GRAY = fromColor(Color.GRAY),
			LIGHT_GRAY = fromColor(Color.LIGHT_GRAY),
			DARK_GRAY = fromColor(Color.DARK_GRAY);

	public static final AccurateColor
			RED = fromColor(Color.RED),
			GREEN = fromColor(Color.GREEN),
			BLUE = fromColor(Color.BLUE),
			CYAN = fromColor(Color.CYAN),
			MAGENTA = fromColor(Color.MAGENTA),
			YELLOW = fromColor(Color.YELLOW),
			ORANGE = fromColor(Color.ORANGE),
			PINK = fromColor(Color.PINK);

	// Static Constructors

	public static AccurateColor fromARGB(long argb) {
		return new AccurateColor(RGB.fromInt((int) argb), argb > 0xFFFFFF ? ((argb >> 24) & 0xFF) / 255.0 : 1);
	}

	public static AccurateColor fromRGB(int red, int green, int blue, int alpha) {
		return new AccurateColor(red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0);
	}

	public static AccurateColor fromRGB(int red, int green, int blue) {
		return fromRGB(red, green, blue, 255);
	}

	public static AccurateColor fromColor(Color color) {
		return new AccurateColor(RGB.fromColor(color), color.getAlpha() / 255.0);
	}

	public static AccurateColor fromHexString(String hexString) {
		long hex = Long.decode(hexString);
		return fromARGB(hex | (hex > 0xFFFFFF ? 0x0 : 0xFF000000L));
	}

	// Constructors

	protected AccurateColor(Colorspace colorspace, double[] color, double opacity, boolean transparent) {
		this.colorspace = colorspace;
		this.color = color;
		this.opacity = opacity;
		this.transparent = transparent;
	}

	public AccurateColor(Colorspace colorspace, double[] color, double opacity) {
		this(colorspace, color, opacity, false);
	}

	public AccurateColor(double[] rgb, double opacity) {
		this(RGB, rgb, opacity);
	}

	public AccurateColor(double[] rgba) {
		this(rgba, rgba[3]);
	}

	public AccurateColor(double red, double green, double blue, double opacity) {
		this(new double[] { red, green, blue }, opacity);
	}

	public AccurateColor(double red, double green, double blue) {
		this(red, green, blue, 1);
	}

	public AccurateColor(double gray, double opacity) {
		this(gray, gray, gray, opacity);
	}

	public AccurateColor(double gray) {
		this(gray, 1);
	}

	public AccurateColor(AccurateColor another, Colorspace colorspace) {
		this(colorspace, colorspace.from(another.color(), another.colorspace()), another.opacity(), !another.hasColor());
	}

	// Fields

	private final Colorspace colorspace;
	private final double[] color;
	private final double opacity;
	private final boolean transparent;

	// Accessors

	public Colorspace colorspace() {
		return colorspace;
	}

	public double[] color() {
		return color;
	}

	public double opacity() {
		return opacity;
	}

	public float opacityAsFloat() {
		return (float) opacity;
	}

	public int opacityAsInt() {
		return (int) (opacity * 255);
	}

	// Accessors: RGB Components

	public double red() {
		return new AccurateColor(this, RGB).color()[0];
	}

	public float redAsFloat() {
		return (float) red();
	}

	public int redAsInt() {
		return (int) (red() * 255);
	}

	public double green() {
		return new AccurateColor(this, RGB).color()[1];
	}

	public float greenAsFloat() {
		return (float) green();
	}

	public int greenAsInt() {
		return (int) (green() * 255);
	}

	public double blue() {
		return new AccurateColor(this, RGB).color()[2];
	}

	public float blueAsFloat() {
		return (float) blue();
	}

	public int blueAsInt() {
		return (int) (blue() * 255);
	}

	// Accessors: HSV/HSL Components

	public double hue() {
		return new AccurateColor(this, HSV).color()[0];
	}

	public double saturation() {
		return new AccurateColor(this, HSV).color()[1];
	}

	public double value() {
		return new AccurateColor(this, HSV).color()[2];
	}

	public double lightness() {
		return new AccurateColor(this, HSL).color()[2];
	}

	// Accessors: CMYK Components

	public double cyan() {
		return new AccurateColor(this, CMYK).color()[0];
	}

	public double magenta() {
		return new AccurateColor(this, CMYK).color()[1];
	}

	public double yellow() {
		return new AccurateColor(this, CMYK).color()[2];
	}

	public double black() {
		return new AccurateColor(this, CMYK).color()[3];
	}

	// Accessors: XYZ Components

	public double x() {
		return new AccurateColor(this, XYZ).color()[0];
	}

	public double y() {
		return new AccurateColor(this, XYZ).color()[1];
	}

	public double z() {
		return new AccurateColor(this, XYZ).color()[2];
	}

	// Accessors: LAB/LCH Components

	public double L() {
		return new AccurateColor(this, LAB).color()[0];
	}

	public double a() {
		return new AccurateColor(this, LAB).color()[1];
	}

	public double b() {
		return new AccurateColor(this, LAB).color()[2];
	}

	public double C() {
		return new AccurateColor(this, LCH).color()[1];
	}

	public double h() {
		return new AccurateColor(this, LCH).color()[2];
	}

	// Mutators

	public AccurateColor colorspace(Colorspace colorspace) {
		return colorspace() == colorspace ? this : new AccurateColor(this, colorspace);
	}

	public AccurateColor color(double[] color) {
		return color() == color ? this : new AccurateColor(colorspace(), color, opacity());
	}

	public AccurateColor color(double[] color, Colorspace colorspace) {
		return colorspace() == colorspace ? color(color) : colorspace(colorspace).color(color).colorspace(colorspace());
	}

	public AccurateColor opacity(double opacity) {
		return opacity() == opacity ? this : new AccurateColor(colorspace(), color(), opacity);
	}

	// Mutators: RGB Components

	public AccurateColor red(double red) {
		return color(new double[] { red, green(), blue() }, RGB);
	}

	public AccurateColor green(double green) {
		return color(new double[] { red(), green, blue() }, RGB);
	}

	public AccurateColor blue(double blue) {
		return color(new double[] { red(), green(), blue }, RGB);
	}

	// Mutators: HSV/HSL Components

	public AccurateColor hue(double hue) {
		return color(new double[] { hue, saturation(), value() }, HSV);
	}

	public AccurateColor saturation(double saturation) {
		return color(new double[] { hue(), saturation, value() }, HSV);
	}

	public AccurateColor value(double value) {
		return color(new double[] { hue(), saturation(), value }, HSV);
	}

	public AccurateColor lightness(double lightness) {
		return color(new double[] { hue(), saturation(), lightness }, HSL);
	}

	// Mutators: CMYK Components

	public AccurateColor cyan(double cyan) {
		return color(new double[] { cyan, magenta(), yellow(), black() }, CMYK);
	}

	public AccurateColor magenta(double magenta) {
		return color(new double[] { cyan(), magenta, yellow(), black() }, CMYK);
	}

	public AccurateColor yellow(double yellow) {
		return color(new double[] { cyan(), magenta(), yellow, black() }, CMYK);
	}

	public AccurateColor black(double black) {
		return color(new double[] { cyan(), magenta(), yellow(), black }, CMYK);
	}

	// Mutators: XYZ Components

	public AccurateColor x(double x) {
		return color(new double[] { x, y(), z() }, XYZ);
	}

	public AccurateColor y(double y) {
		return color(new double[] { x(), y, z() }, XYZ);
	}

	public AccurateColor z(double z) {
		return color(new double[] { x(), y(), z }, XYZ);
	}

	// Mutators: LAB/LCH Components

	public AccurateColor L(double L) {
		return color(new double[] { L, a(), b() }, LAB);
	}

	public AccurateColor a(double a) {
		return color(new double[] { L(), a, b() }, LAB);
	}

	public AccurateColor b(double b) {
		return color(new double[] { L(), a(), b }, LAB);
	}

	public AccurateColor C(double C) {
		return color(new double[] { L(), C, h() }, LCH);
	}

	public AccurateColor h(double h) {
		return color(new double[] { L(), C(), h }, LCH);
	}

	// Properties

	public boolean hasColor() {
		return !transparent;
	}

	public boolean hasOpacity() {
		return Theory.looseGreater(opacity(), 0);
	}

	public boolean approximates(@Nullable AccurateColor another, boolean ignoreOpacity) {
		if (another == null) return false;
		double[] rgb = colorspace(Colorspace.RGB).color(), anotherRGB = another.colorspace(RGB).color();
		return Theory.looseEquals(rgb[0], anotherRGB[0]) && Theory.looseEquals(rgb[1], anotherRGB[1]) && Theory.looseEquals(rgb[2], anotherRGB[2]) && (ignoreOpacity || Theory.looseEquals(opacity(), another.opacity()));
	}

	public boolean approximates(@Nullable AccurateColor another) {
		return approximates(another, false);
	}

	// Operations

	public AccurateColor mix(AccurateColor another, double ratio, MixMode mixMode) {
		if (!hasColor() && !another.hasColor()) return TRANSPARENT;
		if (!hasColor()) return another.mix(this, 1 - ratio, MixMode.OPACITY_ONLY);

		if (!another.hasColor()) mixMode = MixMode.OPACITY_ONLY;
		if (mixMode == MixMode.OPACITY_ONLY) return opacity(Theory.lerp(opacity(), another.opacity(), ratio));

		return new AccurateColor(colorspace(), colorspace().mix(color(), another.color(), ratio, another.colorspace(), mixMode), Theory.lerp(opacity(), another.opacity(), ratio));
	}

	public AccurateColor mix(AccurateColor another, MixMode mixMode) {
		return mix(another, 0.5, mixMode);
	}

	public AccurateColor mix(AccurateColor another, double ratio) {
		return mix(another, ratio, MixMode.BLEND);
	}

	public AccurateColor mix(AccurateColor another) {
		return mix(another, 0.5);
	}

	public AccurateColor orElse(AccurateColor color) {
		return hasColor() ? this : color;
	}

	public AccurateColor invert() {
		return color(colorspace().invert(color()));
	}

	public AccurateColor transparent() {
		return opacity(0);
	}

	public AccurateColor opaque() {
		return opacity(1);
	}

	public AccurateColor lighten(double ratio) {
		return color(colorspace().lighten(color(), ratio));
	}

	public AccurateColor darken(double ratio) {
		return color(colorspace().darken(color(), ratio));
	}

	public AccurateColor moreTranslucent(double ratio) {
		return opacity(Theory.lerp(opacity(), 0, ratio));
	}

	public AccurateColor lessTranslucent(double ratio) {
		return opacity(Theory.lerp(opacity(), 1, ratio));
	}

	public int toInt() {
		return colorspace().toInt(color()) + ((int) (opacity() * 255) << 24);
	}

	public Color toColor() {
		return new Color(toInt(), true);
	}

	public String toHexString() {
		return String.format("0x%02x%02x%02x%02x", redAsInt(), greenAsInt(), blueAsInt(), opacityAsInt());
	}

	// Object Methods

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean precisely) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < color().length; i++) {
			builder.append(precisely ? color()[i] : String.format("%.2f", color()[i]));

			if (i < color().length - 1) {
				builder.append(", ");
			}
		}

		return !hasColor() ? (getClass().getSimpleName() + "(transparent)")
					   : (colorspace().getName() + "(" + builder + ")" + "-(opacity=" + (precisely ? opacity() : String.format("%.2f", opacity())) + ")");
	}
}
