package net.krlite.equator.visual.color;

import com.scrtwpns.Mixbox;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.color.base.ColorStandard;

import java.awt.*;

import static net.krlite.equator.visual.color.ColorConvertor.*;

/**
 * Colorspaces:
 * <ul>
 *     <li>{@link Colorspace#RGB}</li>
 *     <li>{@link Colorspace#HSV}</li>
 *     <li>{@link Colorspace#HSL}</li>
 *     <li>{@link Colorspace#CMYK}</li>
 *     <li>{@link Colorspace#XYZ}</li>
 *     <li>{@link Colorspace#LAB}</li>
 *     <li>{@link Colorspace#LCH}</li>
 * </ul>
 */
public enum Colorspace implements ColorStandard {
	RGB("RGB") {
		public static final double[] WHITE = {1, 1, 1};
		public static final double[] BLACK = {0, 0, 0};

		@Override
		public double[] from(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> color;
				case HSV -> ToRGB.fromHSV(color);
				case HSL -> ToRGB.fromHSL(color);
				case CMYK -> ToRGB.fromCMYK(color);
				case XYZ -> ToRGB.fromXYZ(color);
				case LAB -> ToRGB.fromLAB(color);
				case LCH -> ToRGB.fromLCH(color);
			};
		}

		@Override
		public double[] fromInt(int color) {
			return ToRGB.fromInt(color);
		}

		@Override
		public double[] fromColor(Color color) {
			return fromInt(color.getRGB());
		}

		@Override
		public double[] fromHexString(String color) {
			return fromInt(hexStringToIntRGB(color));
		}

		@Override
		public double[] to(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> color;
				case HSV -> FromRGB.toHSV(color);
				case HSL -> FromRGB.toHSL(color);
				case CMYK -> FromRGB.toCMYK(color);
				case XYZ -> FromRGB.toXYZ(color);
				case LAB -> FromRGB.toLAB(color);
				case LCH -> FromRGB.toLCH(color);
			};
		}

		@Override
		public int toInt(double[] color) {
			return FromRGB.toInt(color);
		}

		@Override
		public Color toColor(double[] color) {
			return new Color(toInt(color));
		}

		@Override
		public String toHexString(double[] color) {
			return intToHexStringRGB(toInt(color));
		}

		@Override
		public double[] interpolate(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherRGB = from(another, colorspace);
			return new double[] {
					Theory.lerp(self[0], anotherRGB[0], ratio),
					Theory.lerp(self[1], anotherRGB[1], ratio),
					Theory.lerp(self[2], anotherRGB[2], ratio)
			};
		}

		@Override
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherRGB = from(another, colorspace);
			return floatToDouble(Mixbox.lerpFloat(doubleToFloat(self), doubleToFloat(anotherRGB), (float) ratio));
		}

		@Override
		public double[] invert(double[] color) {
			return new double[] {
					1 - color[0],
					1 - color[1],
					1 - color[2]
			};
		}

		@Override
		public double[] lighten(double[] color, double ratio) {
			return interpolate(color, WHITE, ratio, RGB);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return interpolate(color, BLACK, ratio, RGB);
		}
	},
	HSV("HSV") {
		public static final double[] WHITE = {0, 0, 1};
		public static final double[] BLACK = {0, 0, 0};

		@Override
		public double[] from(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.to(color, HSV);
				case HSV -> color;
				case HSL -> RGB.to(RGB.from(color, HSL), HSV);
				case CMYK -> RGB.to(RGB.from(color, CMYK), HSV);
				case XYZ -> RGB.to(RGB.from(color, XYZ), HSV);
				case LAB -> RGB.to(RGB.from(color, LAB), HSV);
				case LCH -> RGB.to(RGB.from(color, LCH), HSV);
			};
		}

		@Override
		public double[] fromInt(int color) {
			return RGB.to(RGB.fromInt(color), HSV);
		}

		@Override
		public double[] fromColor(Color color) {
			return RGB.to(RGB.fromColor(color), HSV);
		}

		@Override
		public double[] fromHexString(String color) {
			return RGB.to(RGB.fromHexString(color), HSV);
		}

		@Override
		public double[] to(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.from(color, HSV);
				case HSV -> color;
				case HSL -> RGB.to(RGB.from(color, HSV), HSL);
				case CMYK -> RGB.to(RGB.from(color, HSV), CMYK);
				case XYZ -> RGB.to(RGB.from(color, HSV), XYZ);
				case LAB -> RGB.to(RGB.from(color, HSV), LAB);
				case LCH -> RGB.to(RGB.from(color, HSV), LCH);
			};
		}

		@Override
		public int toInt(double[] color) {
			return RGB.toInt(RGB.from(color, HSV));
		}

		@Override
		public Color toColor(double[] color) {
			return RGB.toColor(RGB.from(color, HSV));
		}

		@Override
		public String toHexString(double[] color) {
			return RGB.toHexString(RGB.from(color, HSV));
		}

		@Override
		public double[] interpolate(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherHSV = from(another, colorspace);
			return new double[] {
					Theory.lerp(self[0], anotherHSV[0], ratio),
					Theory.lerp(self[1], anotherHSV[1], ratio),
					Theory.lerp(self[2], anotherHSV[2], ratio)
			};
		}

		@Override
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherRGB = RGB.from(another, colorspace);
			return from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
		}

		@Override
		public double[] invert(double[] color) {
			return new double[] {
					(color[0] + 180) % 360,
					1 - color[1],
					1 - color[2]
			};
		}

		@Override
		public double[] lighten(double[] color, double ratio) {
			return interpolate(color, WHITE, ratio, HSV);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return interpolate(color, BLACK, ratio, HSV);
		}
	},
	HSL("HSL") {
		public static final double[] WHITE = {0, 0, 1};
		public static final double[] BLACK = {0, 0, 0};

		@Override
		public double[] from(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.to(RGB.from(color, RGB), HSL);
				case HSV -> RGB.to(RGB.from(color, HSV), HSL);
				case HSL -> color;
				case CMYK -> RGB.to(RGB.from(color, CMYK), HSL);
				case XYZ -> RGB.to(RGB.from(color, XYZ), HSL);
				case LAB -> RGB.to(RGB.from(color, LAB), HSL);
				case LCH -> RGB.to(RGB.from(color, LCH), HSL);
			};
		}

		@Override
		public double[] fromInt(int color) {
			return RGB.to(RGB.fromInt(color), HSL);
		}

		@Override
		public double[] fromColor(Color color) {
			return RGB.to(RGB.fromColor(color), HSL);
		}

		@Override
		public double[] fromHexString(String color) {
			return RGB.to(RGB.fromHexString(color), HSL);
		}

		@Override
		public double[] to(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.from(color, HSL);
				case HSV -> RGB.to(RGB.from(color, HSL), HSV);
				case HSL -> color;
				case CMYK -> RGB.to(RGB.from(color, HSL), CMYK);
				case XYZ -> RGB.to(RGB.from(color, HSL), XYZ);
				case LAB -> RGB.to(RGB.from(color, HSL), LAB);
				case LCH -> RGB.to(RGB.from(color, HSL), LCH);
			};
		}

		@Override
		public int toInt(double[] color) {
			return RGB.toInt(RGB.from(color, HSL));
		}

		@Override
		public Color toColor(double[] color) {
			return RGB.toColor(RGB.from(color, HSL));
		}

		@Override
		public String toHexString(double[] color) {
			return RGB.toHexString(RGB.from(color, HSL));
		}

		@Override
		public double[] interpolate(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherHSL = from(another, colorspace);
			return new double[] {
					Theory.lerp(self[0], anotherHSL[0], ratio),
					Theory.lerp(self[1], anotherHSL[1], ratio),
					Theory.lerp(self[2], anotherHSL[2], ratio)
			};
		}

		@Override
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherRGB = RGB.from(another, colorspace);
			return from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
		}

		@Override
		public double[] invert(double[] color) {
			return new double[] {
					(color[0] + 180) % 360,
					1 - color[1],
					1 - color[2]
			};
		}

		@Override
		public double[] lighten(double[] color, double ratio) {
			return interpolate(color, WHITE, ratio, HSL);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return interpolate(color, BLACK, ratio, HSL);
		}
	},
	CMYK("CMYK") {
		public static final double[] WHITE = {0, 0, 0, 0};
		public static final double[] BLACK = {0, 0, 0, 1};

		@Override
		public double[] from(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.to(RGB.from(color, RGB), CMYK);
				case HSV -> RGB.to(RGB.from(color, HSV), CMYK);
				case HSL -> RGB.to(RGB.from(color, HSL), CMYK);
				case CMYK -> color;
				case XYZ -> RGB.to(RGB.from(color, XYZ), CMYK);
				case LAB -> RGB.to(RGB.from(color, LAB), CMYK);
				case LCH -> RGB.to(RGB.from(color, LCH), CMYK);
			};
		}

		@Override
		public double[] fromInt(int color) {
			return RGB.to(RGB.fromInt(color), CMYK);
		}

		@Override
		public double[] fromColor(Color color) {
			return RGB.to(RGB.fromColor(color), CMYK);
		}

		@Override
		public double[] fromHexString(String color) {
			return RGB.to(RGB.fromHexString(color), CMYK);
		}

		@Override
		public double[] to(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.from(color, CMYK);
				case HSV -> RGB.to(RGB.from(color, CMYK), HSV);
				case HSL -> RGB.to(RGB.from(color, CMYK), HSL);
				case CMYK -> color;
				case XYZ -> RGB.to(RGB.from(color, CMYK), XYZ);
				case LAB -> RGB.to(RGB.from(color, CMYK), LAB);
				case LCH -> RGB.to(RGB.from(color, CMYK), LCH);
			};
		}

		@Override
		public int toInt(double[] color) {
			return RGB.toInt(RGB.from(color, CMYK));
		}

		@Override
		public Color toColor(double[] color) {
			return RGB.toColor(RGB.from(color, CMYK));
		}

		@Override
		public String toHexString(double[] color) {
			return RGB.toHexString(RGB.from(color, CMYK));
		}

		@Override
		public double[] interpolate(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherCMYK = from(another, colorspace);
			return new double[] {
					Theory.lerp(self[0], anotherCMYK[0], ratio),
					Theory.lerp(self[1], anotherCMYK[1], ratio),
					Theory.lerp(self[2], anotherCMYK[2], ratio),
					Theory.lerp(self[3], anotherCMYK[3], ratio)
			};
		}

		@Override
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherRGB = RGB.from(another, colorspace);
			return from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
		}

		@Override
		public double[] invert(double[] color) {
			return new double[] {
					1 - color[0],
					1 - color[1],
					1 - color[2],
					color[3]
			};
		}

		@Override
		public double[] lighten(double[] color, double ratio) {
			return interpolate(color, WHITE, ratio, CMYK);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return interpolate(color, BLACK, ratio, CMYK);
		}
	},
	XYZ("XYZ") {
		public static final double[] WHITE = {95.047, 100, 108.883};
		public static final double[] BLACK = {0, 0, 0};

		@Override
		public double[] from(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.to(RGB.from(color, RGB), XYZ);
				case HSV -> RGB.to(RGB.from(color, HSV), XYZ);
				case HSL -> RGB.to(RGB.from(color, HSL), XYZ);
				case CMYK -> RGB.to(RGB.from(color, CMYK), XYZ);
				case XYZ -> color;
				case LAB -> RGB.to(RGB.from(color, LAB), XYZ);
				case LCH -> RGB.to(RGB.from(color, LCH), XYZ);
			};
		}

		@Override
		public double[] fromInt(int color) {
			return RGB.to(RGB.fromInt(color), XYZ);
		}

		@Override
		public double[] fromColor(Color color) {
			return RGB.to(RGB.fromColor(color), XYZ);
		}

		@Override
		public double[] fromHexString(String color) {
			return RGB.to(RGB.fromHexString(color), XYZ);
		}

		@Override
		public double[] to(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.from(color, XYZ);
				case HSV -> RGB.to(RGB.from(color, XYZ), HSV);
				case HSL -> RGB.to(RGB.from(color, XYZ), HSL);
				case CMYK -> RGB.to(RGB.from(color, XYZ), CMYK);
				case XYZ -> color;
				case LAB -> RGB.to(RGB.from(color, XYZ), LAB);
				case LCH -> RGB.to(RGB.from(color, XYZ), LCH);
			};
		}

		@Override
		public int toInt(double[] color) {
			return RGB.toInt(RGB.from(color, XYZ));
		}

		@Override
		public Color toColor(double[] color) {
			return RGB.toColor(RGB.from(color, XYZ));
		}

		@Override
		public String toHexString(double[] color) {
			return RGB.toHexString(RGB.from(color, XYZ));
		}

		@Override
		public double[] interpolate(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherXYZ = from(another, colorspace);
			return new double[] {
					Theory.lerp(self[0], anotherXYZ[0], ratio),
					Theory.lerp(self[1], anotherXYZ[1], ratio),
					Theory.lerp(self[2], anotherXYZ[2], ratio)
			};
		}

		@Override
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherRGB = RGB.from(another, colorspace);
			return from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
		}

		@Override
		public double[] invert(double[] color) {
			return new double[] {
					1 - color[0],
					1 - color[1],
					1 - color[2]
			};
		}

		@Override
		public double[] lighten(double[] color, double ratio) {
			return interpolate(color, WHITE, ratio, XYZ);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return interpolate(color, BLACK, ratio, XYZ);
		}
	},
	LAB("Lab") {
		public static final double[] WHITE = {100, 0, 0};
		public static final double[] BLACK = {0, 0, 0};

		@Override
		public double[] from(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.to(RGB.from(color, RGB), LAB);
				case HSV -> RGB.to(RGB.from(color, HSV), LAB);
				case HSL -> RGB.to(RGB.from(color, HSL), LAB);
				case CMYK -> RGB.to(RGB.from(color, CMYK), LAB);
				case XYZ -> RGB.to(RGB.from(color, XYZ), LAB);
				case LAB -> color;
				case LCH -> RGB.to(RGB.from(color, LCH), LAB);
			};
		}

		@Override
		public double[] fromInt(int color) {
			return RGB.to(RGB.fromInt(color), LAB);
		}

		@Override
		public double[] fromColor(Color color) {
			return RGB.to(RGB.fromColor(color), LAB);
		}

		@Override
		public double[] fromHexString(String color) {
			return RGB.to(RGB.fromHexString(color), LAB);
		}

		@Override
		public double[] to(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.from(color, LAB);
				case HSV -> RGB.to(RGB.from(color, LAB), HSV);
				case HSL -> RGB.to(RGB.from(color, LAB), HSL);
				case CMYK -> RGB.to(RGB.from(color, LAB), CMYK);
				case XYZ -> RGB.to(RGB.from(color, LAB), XYZ);
				case LAB -> color;
				case LCH -> RGB.to(RGB.from(color, LAB), LCH);
			};
		}

		@Override
		public int toInt(double[] color) {
			return RGB.toInt(RGB.from(color, LAB));
		}

		@Override
		public Color toColor(double[] color) {
			return RGB.toColor(RGB.from(color, LAB));
		}

		@Override
		public String toHexString(double[] color) {
			return RGB.toHexString(RGB.from(color, LAB));
		}

		@Override
		public double[] interpolate(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherLAB = from(another, colorspace);
			return new double[] {
					Theory.lerp(self[0], anotherLAB[0], ratio),
					Theory.lerp(self[1], anotherLAB[1], ratio),
					Theory.lerp(self[2], anotherLAB[2], ratio)
			};
		}

		@Override
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherRGB = RGB.from(another, colorspace);
			return from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
		}

		@Override
		public double[] invert(double[] color) {
			return new double[] {
					100 - color[0],
					-color[1],
					-color[2]
			};
		}

		@Override
		public double[] lighten(double[] color, double ratio) {
			return interpolate(color, WHITE, ratio, LAB);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return interpolate(color, BLACK, ratio, LAB);
		}
	},
	LCH("LCH") {
		public static final double[] WHITE = {100, 0, 0};
		public static final double[] BLACK = {0, 0, 0};

		@Override
		public double[] from(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.to(RGB.from(color, RGB), LCH);
				case HSV -> RGB.to(RGB.from(color, HSV), LCH);
				case HSL -> RGB.to(RGB.from(color, HSL), LCH);
				case CMYK -> RGB.to(RGB.from(color, CMYK), LCH);
				case XYZ -> RGB.to(RGB.from(color, XYZ), LCH);
				case LAB -> RGB.to(RGB.from(color, LAB), LCH);
				case LCH -> color;
			};
		}

		@Override
		public double[] fromInt(int color) {
			return RGB.to(RGB.fromInt(color), LCH);
		}

		@Override
		public double[] fromColor(Color color) {
			return RGB.to(RGB.fromColor(color), LCH);
		}

		@Override
		public double[] fromHexString(String color) {
			return RGB.to(RGB.fromHexString(color), LCH);
		}

		@Override
		public double[] to(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.from(color, LCH);
				case HSV -> RGB.to(RGB.from(color, LCH), HSV);
				case HSL -> RGB.to(RGB.from(color, LCH), HSL);
				case CMYK -> RGB.to(RGB.from(color, LCH), CMYK);
				case XYZ -> RGB.to(RGB.from(color, LCH), XYZ);
				case LAB -> RGB.to(RGB.from(color, LCH), LAB);
				case LCH -> color;
			};
		}

		@Override
		public int toInt(double[] color) {
			return RGB.toInt(RGB.from(color, LCH));
		}

		@Override
		public Color toColor(double[] color) {
			return RGB.toColor(RGB.from(color, LCH));
		}

		@Override
		public String toHexString(double[] color) {
			return RGB.toHexString(RGB.from(color, LCH));
		}

		@Override
		public double[] interpolate(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherLCH = from(another, colorspace);
			return new double[] {
					Theory.lerp(self[0], anotherLCH[0], ratio),
					Theory.lerp(self[1], anotherLCH[1], ratio),
					Theory.lerp(self[2], anotherLCH[2], ratio)
			};
		}

		@Override
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace) {
			double[] anotherRGB = RGB.from(another, colorspace);
			return from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
		}

		@Override
		public double[] invert(double[] color) {
			return new double[] {
					100 - color[0],
					-color[1],
					-color[2]
			};
		}

		@Override
		public double[] lighten(double[] color, double ratio) {
			return interpolate(color, WHITE, ratio, LCH);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return interpolate(color, BLACK, ratio, LCH);
		}
	};

	private final String name;

	Colorspace(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
