package net.krlite.equator.visual.color.base;

import net.krlite.equator.visual.color.Colorspace;

import java.awt.*;

/**
 * Standard color conversion methods, without opacity.
 */
public interface ColorStandard {
	double[] from(double[] color, Colorspace colorspace);

	double[] fromInt(int color);

	double[] fromColor(Color color);

	double[] fromHexString(String color);

	double[] to(double[] color, Colorspace colorspace);

	int toInt(double[] color);

	Color toColor(double[] color);

	String toHexString(double[] color);

	double[] interpolate(double[] self, double[] another, double ratio, Colorspace colorspace);

	double[] mix(double[] self, double[] another, double ratio, Colorspace colorspace);

	double[] invert(double[] color);

	double[] lighten(double[] color, double ratio);

	double[] darken(double[] color, double ratio);
}
