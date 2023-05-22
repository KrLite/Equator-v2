package net.krlite.equator.visual.color.base;

import net.krlite.equator.visual.color.Colorspace;

import java.awt.*;

/**
 * Standard color conversion methods, without opacity.
 */
@net.krlite.equator.base.Visual("2.4.2")
public interface ColorStandard {
	double[] from(double[] color, Colorspace colorspace);

	double[] fromInt(int color);

	double[] fromColor(Color color);

	double[] fromHexString(String color);

	double[] to(double[] color, Colorspace colorspace);

	int toInt(double[] color);

	Color toColor(double[] color);

	String toHexString(double[] color);

	double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace, MixMode mixMode);

	double[] invert(double[] color);

	double[] lighten(double[] color, double ratio);

	double[] darken(double[] color, double ratio);

	enum MixMode {
		/**
		 * Value channels blending.
		 */
		BLEND,
		/**
		 * Real-world pigment mixing.
		 */
		PIGMENT,
		/**
		 * Opacity channel blending.
		 */
		OPACITY_ONLY
	}
}
