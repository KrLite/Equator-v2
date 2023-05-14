package net.krlite.equator.visual.color;

import com.scrtwpns.Mixbox;
import net.krlite.equator.base.Visual;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.color.base.ColorStandard;

import java.awt.*;

import static net.krlite.equator.visual.color.ColorConvertor.*;

/**
 * Supported colorspaces:
 * <ul>
 *     <li>{@link Colorspace#RGB}	&emsp;<code>- red,			green,			blue</code></li>
 *     <li>{@link Colorspace#HSV}	&emsp;<code>- hue,			saturation,		value</code></li>
 *     <li>{@link Colorspace#HSL}	&emsp;<code>- hue,			saturation,		lightness</code></li>
 *     <li>{@link Colorspace#CMYK}	&ensp;<code>- cyan,			magenta,		yellow,			black</code></li>
 *     <li>{@link Colorspace#XYZ}	&emsp;<code>- x-axis,		y-axis,			z-axis			(CIE 1931 XYZ COLORSPACE)</code></li>
 *     <li>{@link Colorspace#LAB}	&emsp;<code>- Lightness*,	green-magenta*,	blue-yellow*	(CIELAB COLORSPACE)</code></li>
 *     <li>{@link Colorspace#LCh}	&emsp;<code>- Luminance*,	Chroma*,		hue*		(CLELCh COLORSPACE)</code></li>
 * </ul>
 */
@Visual("2.3.0")
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
				case LCh -> ToRGB.fromLCH(color);
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
				case LCh -> FromRGB.toLCH(color);
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
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace, MixMode mixMode) {
			double[] anotherRGB = from(another, colorspace);

			return switch (mixMode) {
				case BLEND -> new double[] {
						Theory.lerp(self[0], anotherRGB[0], ratio),
						Theory.lerp(self[1], anotherRGB[1], ratio),
						Theory.lerp(self[2], anotherRGB[2], ratio)
				};
				case PIGMENT -> floatToDouble(Mixbox.lerpFloat(doubleToFloat(self), doubleToFloat(anotherRGB), (float) ratio));
				default -> self;
			};
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
			return mix(color, WHITE, ratio, RGB, MixMode.BLEND);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return mix(color, BLACK, ratio, RGB, MixMode.BLEND);
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
				case LCh -> RGB.to(RGB.from(color, LCh), HSV);
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
				case LCh -> RGB.to(RGB.from(color, HSV), LCh);
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
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace, MixMode mixMode) {
			return switch (mixMode) {
				case BLEND -> {
					double[] anotherHSV = from(another, colorspace);
					yield new double[]{
							Theory.lerp(self[0], anotherHSV[0], ratio),
							Theory.lerp(self[1], anotherHSV[1], ratio),
							Theory.lerp(self[2], anotherHSV[2], ratio)
					};
				}
				case PIGMENT -> {
					double[] anotherRGB = RGB.from(another, colorspace);
					yield from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
				}
				default -> self;
			};
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
			return mix(color, WHITE, ratio, HSV, MixMode.BLEND);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return mix(color, BLACK, ratio, HSV, MixMode.BLEND);
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
				case LCh -> RGB.to(RGB.from(color, LCh), HSL);
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
				case LCh -> RGB.to(RGB.from(color, HSL), LCh);
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
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace, MixMode mixMode) {
			return switch (mixMode) {
				case BLEND -> {
					double[] anotherHSL = from(another, colorspace);
					yield new double[] {
							Theory.lerp(self[0], anotherHSL[0], ratio),
							Theory.lerp(self[1], anotherHSL[1], ratio),
							Theory.lerp(self[2], anotherHSL[2], ratio)
					};
				}
				case PIGMENT -> {
					double[] anotherRGB = RGB.from(another, colorspace);
					yield from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
				}
				default -> self;
			};
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
			return mix(color, WHITE, ratio, HSL, MixMode.BLEND);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return mix(color, BLACK, ratio, HSL, MixMode.BLEND);
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
				case LCh -> RGB.to(RGB.from(color, LCh), CMYK);
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
				case LCh -> RGB.to(RGB.from(color, CMYK), LCh);
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
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace, MixMode mixMode) {
			return switch (mixMode) {
				case BLEND -> {
					double[] anotherCMYK = from(another, colorspace);
					yield new double[] {
							Theory.lerp(self[0], anotherCMYK[0], ratio),
							Theory.lerp(self[1], anotherCMYK[1], ratio),
							Theory.lerp(self[2], anotherCMYK[2], ratio),
							Theory.lerp(self[3], anotherCMYK[3], ratio)
					};
				}
				case PIGMENT -> {
					double[] anotherRGB = RGB.from(another, colorspace);
					yield  from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
				}
				default -> self;
			};
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
			return mix(color, WHITE, ratio, CMYK, MixMode.BLEND);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return mix(color, BLACK, ratio, CMYK, MixMode.BLEND);
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
				case LCh -> RGB.to(RGB.from(color, LCh), XYZ);
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
				case LCh -> RGB.to(RGB.from(color, XYZ), LCh);
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
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace, MixMode mixMode) {
			return switch (mixMode) {
				case BLEND -> {
					double[] anotherXYZ = from(another, colorspace);
					yield new double[] {
							Theory.lerp(self[0], anotherXYZ[0], ratio),
							Theory.lerp(self[1], anotherXYZ[1], ratio),
							Theory.lerp(self[2], anotherXYZ[2], ratio)
					};
				}
				case PIGMENT -> {
					double[] anotherRGB = RGB.from(another, colorspace);
					yield from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
				}
				default -> self;
			};
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
			return mix(color, WHITE, ratio, XYZ, MixMode.BLEND);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return mix(color, BLACK, ratio, XYZ, MixMode.BLEND);
		}
	},
	LAB("L*a*b*") {
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
				case LCh -> RGB.to(RGB.from(color, LCh), LAB);
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
				case LCh -> RGB.to(RGB.from(color, LAB), LCh);
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
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace, MixMode mixMode) {
			return switch (mixMode) {
				case BLEND -> {
					double[] anotherLAB = from(another, colorspace);
					yield new double[] {
							Theory.lerp(self[0], anotherLAB[0], ratio),
							Theory.lerp(self[1], anotherLAB[1], ratio),
							Theory.lerp(self[2], anotherLAB[2], ratio)
					};
				}
				case PIGMENT -> {
					double[] anotherRGB = RGB.from(another, colorspace);
					yield from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
				}
				default -> self;
			};
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
			return mix(color, WHITE, ratio, LAB, MixMode.BLEND);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return mix(color, BLACK, ratio, LAB, MixMode.BLEND);
		}
	},
	LCh("LCh") {
		public static final double[] WHITE = {100, 0, 0};
		public static final double[] BLACK = {0, 0, 0};

		@Override
		public double[] from(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.to(RGB.from(color, RGB), LCh);
				case HSV -> RGB.to(RGB.from(color, HSV), LCh);
				case HSL -> RGB.to(RGB.from(color, HSL), LCh);
				case CMYK -> RGB.to(RGB.from(color, CMYK), LCh);
				case XYZ -> RGB.to(RGB.from(color, XYZ), LCh);
				case LAB -> RGB.to(RGB.from(color, LAB), LCh);
				case LCh -> color;
			};
		}

		@Override
		public double[] fromInt(int color) {
			return RGB.to(RGB.fromInt(color), LCh);
		}

		@Override
		public double[] fromColor(Color color) {
			return RGB.to(RGB.fromColor(color), LCh);
		}

		@Override
		public double[] fromHexString(String color) {
			return RGB.to(RGB.fromHexString(color), LCh);
		}

		@Override
		public double[] to(double[] color, Colorspace colorspace) {
			return switch (colorspace) {
				case RGB -> RGB.from(color, LCh);
				case HSV -> RGB.to(RGB.from(color, LCh), HSV);
				case HSL -> RGB.to(RGB.from(color, LCh), HSL);
				case CMYK -> RGB.to(RGB.from(color, LCh), CMYK);
				case XYZ -> RGB.to(RGB.from(color, LCh), XYZ);
				case LAB -> RGB.to(RGB.from(color, LCh), LAB);
				case LCh -> color;
			};
		}

		@Override
		public int toInt(double[] color) {
			return RGB.toInt(RGB.from(color, LCh));
		}

		@Override
		public Color toColor(double[] color) {
			return RGB.toColor(RGB.from(color, LCh));
		}

		@Override
		public String toHexString(double[] color) {
			return RGB.toHexString(RGB.from(color, LCh));
		}

		@Override
		public double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace, MixMode mixMode) {
			return switch (mixMode) {
				case BLEND -> {
					double[] anotherLCH = from(another, colorspace);
					yield  new double[] {
							Theory.lerp(self[0], anotherLCH[0], ratio),
							Theory.lerp(self[1], anotherLCH[1], ratio),
							Theory.lerp(self[2], anotherLCH[2], ratio)
					};
				}
				case PIGMENT -> {
					double[] anotherRGB = RGB.from(another, colorspace);
					yield  from(floatToDouble(Mixbox.lerpFloat(doubleToFloat(to(self, RGB)), doubleToFloat(anotherRGB), (float) ratio)), RGB);
				}
				default -> self;
			};
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
			return mix(color, WHITE, ratio, LCh, MixMode.BLEND);
		}

		@Override
		public double[] darken(double[] color, double ratio) {
			return mix(color, BLACK, ratio, LCh, MixMode.BLEND);
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
