package net.krlite.equator.visual.color;

import net.krlite.equator.math.algebra.Theory;

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

	public static AccurateColor fromRGBA(long rgba) {
		return new AccurateColor(RGB.fromInt((int) (rgba >> 8)), ((rgba >> 24) & 0xFF) / 255.0);
	}

	public static AccurateColor fromRGBA(int red, int green, int blue, int alpha) {
		return new AccurateColor(red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0);
	}

	public static AccurateColor fromRGBA(int red, int green, int blue) {
		return fromRGBA(red, green, blue, 255);
	}

	public static AccurateColor fromColor(Color color) {
		return new AccurateColor(RGB.fromColor(color), color.getAlpha() / 255.0);
	}

	public static AccurateColor fromHexString(String hexString) {
		long hex = Long.decode(hexString);
		return fromRGBA(hex | (hex > 0xFFFFFF ? 0x0 : 0xFF000000L));
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

	public double H() {
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
		return color(new double[] { L(), C, H() }, LCH);
	}

	public AccurateColor H(double H) {
		return color(new double[] { L(), C(), H }, LCH);
	}

	// Properties

	public boolean hasColor() {
		return !transparent;
	}

	// Operations

	public AccurateColor interpolate(AccurateColor another, double ratio) {
		return new AccurateColor(colorspace(), colorspace().interpolate(color(), another.color(), ratio, another.colorspace()), Theory.lerp(opacity(), another.opacity(), ratio));
	}

	public AccurateColor interpolate(AccurateColor another) {
		return interpolate(another, 0.5);
	}

	public AccurateColor mix(AccurateColor another, double ratio) {
		return new AccurateColor(colorspace(), colorspace().mix(color(), another.color(), ratio, another.colorspace()), Theory.lerp(opacity(), another.opacity(), ratio));
	}

	public AccurateColor mix(AccurateColor another) {
		return mix(another, 0.5);
	}

	public AccurateColor blendOpacity(AccurateColor another, double ratio) {
		return opacity(Theory.lerp(opacity(), another.opacity(), ratio));
	}

	public AccurateColor blendOpacity(AccurateColor another) {
		return blendOpacity(another, 0.5);
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
		return colorspace().toInt(color()) + (int) (opacity() * 255) << 24;
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
					   : (colorspace().getName() + "(" + builder + ")" + "-opacity(" + (precisely ? opacity() : String.format("%.2f", opacity())) + ")");
	}
}

/*
public class AccurateColor {
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
		if (color.length < 3 || color.length > 4) {
			throw new RuntimeException(Exceptions.Visual.colorArrayLength(color.length, null));
		}
		return new AccurateColor(color[0], color[1], color[2], color.length > 3 ? color[3] : 1);
	}

	public static AccurateColor fromArray(float[] color) {
		if (color.length < 3 || color.length > 4) {
			throw new RuntimeException(Exceptions.Visual.colorArrayLength(color.length, null));
		}
		return new AccurateColor(color[0], color[1], color[2], color.length > 3 ? color[3] : 1);
	}

	public static AccurateColor fromArray(int[] color) {
		if (color.length < 3 || color.length > 4) {
			throw new RuntimeException(Exceptions.Visual.colorArrayLength(color.length, null));
		}
		return new AccurateColor(color[0] / 255.0, color[1] / 255.0, color[2] / 255.0, color.length > 3 ? color[3] / 255.0 : 1);
	}

	public static AccurateColor fromHSV(double hue, double saturation, double value, double opacity) {
		hue = Theory.clamp(hue, 0, 360);
		value = Theory.clamp(value, 0, 1);
		saturation = Theory.clamp(saturation, 0, 1);

		double chroma = value * saturation;
		double secondary = chroma * (1 - Math.abs((hue / 60.0) % 2 - 1));
		double offset = value - chroma;
		double red, green, blue;

		if (hue >= 0 && hue < 60) {
			red = chroma;
			green = secondary;
			blue = 0;
		} else if (hue >= 60 && hue < 120) {
			red = secondary;
			green = chroma;
			blue = 0;
		} else if (hue >= 120 && hue < 180) {
			red = 0;
			green = chroma;
			blue = secondary;
		} else if (hue >= 180 && hue < 240) {
			red = 0;
			green = secondary;
			blue = chroma;
		} else if (hue >= 240 && hue < 300) {
			red = secondary;
			green = 0;
			blue = chroma;
		} else {
			red = chroma;
			green = 0;
			blue = secondary;
		}

		return new AccurateColor(red + offset, green + offset, blue + offset, opacity);
	}

	public static AccurateColor fromHSB(double hue, double saturation, double brightness, double opacity) {
		return fromHSV(hue, saturation, brightness, opacity);
	}

	public static AccurateColor fromHSL(double hue, double saturation, double lightness, double opacity) {
		hue = Theory.clamp(hue, 0, 360);
		saturation = Theory.clamp(saturation, 0, 1);
		lightness = Theory.clamp(lightness, 0, 1);

		double chroma = (1 - Math.abs(2 * lightness - 1)) * saturation;
		double huePrime = hue / 60.0;
		double secondary = chroma * (1.0 - Math.abs(huePrime % 2.0 - 1.0));
		double offset = lightness - chroma / 2.0;

		double red, green, blue;

		if (huePrime < 1.0) {
			red = chroma;
			green = secondary;
			blue = 0.0;
		} else if (huePrime < 2.0) {
			red = secondary;
			green = chroma;
			blue = 0.0;
		} else if (huePrime < 3.0) {
			red = 0.0;
			green = chroma;
			blue = secondary;
		} else if (huePrime < 4.0) {
			red = 0.0;
			green = secondary;
			blue = chroma;
		} else if (huePrime < 5.0) {
			red = secondary;
			green = 0.0;
			blue = chroma;
		} else {
			red = chroma;
			green = 0.0;
			blue = secondary;
		}

		return new AccurateColor(red + offset, green + offset, blue + offset, opacity);
	}

	public static AccurateColor fromCMYK(double cyan, double magenta, double yellow, double black, double opacity) {
		cyan = Theory.clamp(cyan, 0, 1);
		magenta = Theory.clamp(magenta, 0, 1);
		yellow = Theory.clamp(yellow, 0, 1);
		black = Theory.clamp(black, 0, 1);

		double red = 1 - Math.min(1, cyan * (1 - black) + black);
		double green = 1 - Math.min(1, magenta * (1 - black) + black);
		double blue = 1 - Math.min(1, yellow * (1 - black) + black);

		return new AccurateColor(red, green, blue, opacity);
	}

	public static AccurateColor fromLab(double lightness, double a, double b, double opacity) {
		lightness = Theory.clamp(lightness, 0, 100);
		a = Theory.clamp(a, 0, 255);
		b = Theory.clamp(b, 0, 255);

		double y = (lightness + 16) / 116;
		double x = a / 500 + y;
		double z = y - b / 200;

		double red = x * x * x > 0.008856 ? x * x * x : (x - (double) 16 / 116) / 7.787;
		double green = y * y * y > 0.008856 ? y * y * y : (y - (double) 16 / 116) / 7.787;
		double blue = z * z * z > 0.008856 ? z * z * z : (z - (double) 16 / 116) / 7.787;

		red = red * 0.95047 > 0.0031308 ? 1.055 * Math.pow(red * 0.95047, 1 / 2.4) - 0.055 : 12.92 * red * 0.95047;
		green = green > 0.0031308 ? 1.055 * Math.pow(green, 1 / 2.4) - 0.055 : 12.92 * green;
		blue = blue * 1.08883 > 0.0031308 ? 1.055 * Math.pow(blue * 1.08883, 1 / 2.4) - 0.055 : 12.92 * blue * 1.08883;

		return new AccurateColor(red, green, blue, opacity);
	}

	public static AccurateColor fromXYZ(double x, double y, double z, double opacity) {
		x = Theory.clamp(x, 0, 95.047);
		y = Theory.clamp(y, 0, 100);
		z = Theory.clamp(z, 0, 108.883);

		double red = x / 100 * 3.2406 + y / 100 * -1.5372 + z / 100 * -0.4986;
		double green = x / 100 * -0.9689 + y / 100 * 1.8758 + z / 100 * 0.0415;
		double blue = x / 100 * 0.0557 + y / 100 * -0.2040 + z / 100 * 1.0570;

		red = red > 0.0031308 ? 1.055 * Math.pow(red, 1 / 2.4) - 0.055 : 12.92 * red;
		green = green > 0.0031308 ? 1.055 * Math.pow(green, 1 / 2.4) - 0.055 : 12.92 * green;
		blue = blue > 0.0031308 ? 1.055 * Math.pow(blue, 1 / 2.4) - 0.055 : 12.92 * blue;

		return new AccurateColor(red, green, blue, opacity);
	}

	public static AccurateColor fromLCH(double lightness, double chroma, double hue, double opacity) {
		double a = chroma * Math.cos(Math.toRadians(hue));
		double b = chroma * Math.sin(Math.toRadians(hue));
		return fromLab(lightness, a, b, opacity);
	}

	public static AccurateColor fromGrayscale(double gray) {
		return new AccurateColor(gray, gray, gray, 1.0, false);
	}

	public static AccurateColor fromGrayscale(int gray) {
		return fromGrayscale(gray / 255.0);
	}

	protected AccurateColor(double red, double green, double blue, double opacity, boolean transparent) {
		this.red = Theory.clamp(red, 0, 1);
		this.green = Theory.clamp(green, 0, 1);
		this.blue = Theory.clamp(blue, 0, 1);
		this.opacity = Theory.clamp(opacity, 0, 1);
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

	public double green() {
		return green;
	}

	public double blue() {
		return blue;
	}

	public double opacity() {
		return opacity;
	}

	public float redAsFloat() {
		return (float) red();
	}

	public float greenAsFloat() {
		return (float) green();
	}

	public float blueAsFloat() {
		return (float) blue();
	}

	public float opacityAsFloat() {
		return (float) opacity();
	}

	public int redAsInt() {
		return (int) (red() * 255);
	}

	public int greenAsInt() {
		return (int) (green() * 255);
	}

	public int blueAsInt() {
		return (int) (blue() * 255);
	}

	public int opacityAsInt() {
		return (int) (opacity() * 255);
	}

	public boolean hasColor() {
		return !transparent;
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

	public AccurateColor blend(AccurateColor another, double factor) {
		return new AccurateColor(
			red() * (1 - factor) + another.red() * factor,
			green() * (1 - factor) + another.green() * factor,
			blue() * (1 - factor) + another.blue() * factor,
			opacity() * (1 - factor) + another.opacity() * factor
		);
	}

	public AccurateColor blend(AccurateColor another) {
		return blend(another, 0.5);
	}

	public AccurateColor blendOpacity(AccurateColor another, double factor) {
		return opacity(opacity() * (1 - factor) + another.opacity() * factor);
	}

	public AccurateColor blendOpacity(AccurateColor another) {
		return blendOpacity(another, 0.5);
	}

	public AccurateColor mix(AccurateColor another, double factor) {
		return AccurateColor.fromArray(Mixbox.lerpFloat(toFloatArray(), another.toFloatArray(), (float) factor));
	}

	public AccurateColor mix(AccurateColor another) {
		return mix(another, 0.5);
	}

	public AccurateColor transparent() {
		return opacity(0);
	}

	public AccurateColor opaque() {
		return opacity(1);
	}

	public AccurateColor lighter(double factor) {
		return new AccurateColor(
			red() * (1 - factor) + 1 * factor,
			green() * (1 - factor) + 1 * factor,
			blue() * (1 - factor) + 1 * factor,
			opacity()
		);
	}

	public AccurateColor lighter() {
		return lighter(0.5);
	}

	public AccurateColor darker(double factor) {
		return new AccurateColor(
			red() * (1 - factor) + 0 * factor,
			green() * (1 - factor) + 0 * factor,
			blue() * (1 - factor) + 0 * factor,
			opacity()
		);
	}

	public AccurateColor darker() {
		return darker(0.5);
	}

	@Override
	public boolean equals(Object another) {
		return another instanceof AccurateColor
					   && red() == ((AccurateColor) another).red()
					   && green() == ((AccurateColor) another).green()
					   && blue() == ((AccurateColor) another).blue()
					   && opacity() == ((AccurateColor) another).opacity()
					   && transparent() == ((AccurateColor) another).transparent();
	}

	public boolean looseEquals(Object another) {
		return another instanceof AccurateColor
					   && Theory.looseEquals(red(), ((AccurateColor) another).red())
					   && Theory.looseEquals(green(), ((AccurateColor) another).green())
					   && Theory.looseEquals(blue(), ((AccurateColor) another).blue())
					   && Theory.looseEquals(opacity(), ((AccurateColor) another).opacity())
					   && transparent() == ((AccurateColor) another).transparent();
	}

	public Color toColor() {
		return new Color(redAsInt(), greenAsInt(), blueAsInt(), opacityAsInt());
	}

	public int toInt() {
		return toColor().getRGB();
	}

	public int[] toIntArray() {
		return new int[] { redAsInt(), greenAsInt(), blueAsInt(), opacityAsInt() };
	}

	public float[] toFloatArray() {
		return new float[] { redAsFloat(), greenAsFloat(), blueAsFloat(), opacityAsFloat() };
	}

	public double[] toDoubleArray() {
		return new double[] { red(), green(), blue(), opacity() };
	}

	public String toHex() {
		return String.format("0x%02x%02x%02x%02x", redAsInt(), greenAsInt(), blueAsInt(), opacityAsInt());
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean precisely) {
		return !hasColor() ? "<transparent>" : precisely
													   ? String.format("RGBA<%f, %f, %f, %f>", red(), green(), blue(), opacity())
													   : String.format("RGBA<%d, %d, %d, %d>", redAsInt(), greenAsInt(), blueAsInt(), opacityAsInt());
	}
}

 */
