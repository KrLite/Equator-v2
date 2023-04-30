package net.krlite.equator.visual.color;

import net.krlite.equator.base.Exceptions;

public class ColorConvertor {
	public static void checkArrayLength(String colorspaceName, double[] color, int expectedLength) {
		if (color.length != expectedLength) {
			throw Exceptions.Visual.colorArrayLength(colorspaceName, expectedLength, color.length, null);
		}
	}

	public static int hexStringToIntRGB(String color) {
		return Integer.parseInt(color.substring(1), 16);
	}

	public static int hexStringToIntRGBA(String color) {
		return Integer.parseInt(color.substring(1), 16);
	}

	public static String intToHexStringRGB(int color) {
		return String.format("0x%06x", (0xFFFFFF & color));
	}

	public static String intToHexStringRGBA(int color) {
		return String.format("0x%08x", (color));
	}

	public static float[] doubleToFloat(double[] color) {
		float[] result = new float[color.length];
		for (int i = 0; i < color.length; i++) {
			result[i] = (float) color[i];
		}
		return result;
	}

	public static double[] floatToDouble(float[] color) {
		double[] result = new double[color.length];
		for (int i = 0; i < color.length; i++) {
			result[i] = color[i];
		}
		return result;
	}

	public static class FromRGB {
		public static int toInt(double[] rgb) {
			checkArrayLength("RGB", rgb, 3);

			int red = (int) (rgb[0] * 255);
			int green = (int) (rgb[1] * 255);
			int blue = (int) (rgb[2] * 255);
			return (red << 16) | (green << 8) | blue;
		}

		public static double[] toHSV(double[] rgb) {
			checkArrayLength("RGB", rgb, 3);

			double red = rgb[0];
			double green = rgb[1];
			double blue = rgb[2];

			double min = Math.min(Math.min(red, green), blue);
			double max = Math.max(Math.max(red, green), blue);
			double delta = max - min;

			double hue = 0;
			if (delta != 0) {
				if (max == red) {
					hue = ((green - blue) / delta) % 6;
				} else if (max == green) {
					hue = ((blue - red) / delta) + 2;
				} else {
					hue = ((red - green) / delta) + 4;
				}
				hue *= 60;
				if (hue < 0) {
					hue += 360;
				}
			}

			double saturation = max == 0 ? 0 : delta / max;

			return new double[] { hue, saturation, max };
		}

		public static double[] toHSL(double[] rgb) {
			checkArrayLength("RGB", rgb, 3);

			double red = rgb[0];
			double green = rgb[1];
			double blue = rgb[2];

			double min = Math.min(Math.min(red, green), blue);
			double max = Math.max(Math.max(red, green), blue);
			double delta = max - min;

			double hue = 0;
			if (delta != 0) {
				if (max == red) {
					hue = ((green - blue) / delta) % 6;
				} else if (max == green) {
					hue = ((blue - red) / delta) + 2;
				} else {
					hue = ((red - green) / delta) + 4;
				}
				hue *= 60;
				if (hue < 0) {
					hue += 360;
				}
			}

			double lightness = (max + min) / 2;

			double saturation = 0;
			if (delta != 0) {
				saturation = delta / (1 - Math.abs(2 * lightness - 1));
			}

			return new double[] { hue, saturation, lightness };
		}

		public static double[] toCMYK(double[] rgb) {
			checkArrayLength("RGB", rgb, 3);

			double red = rgb[0];
			double green = rgb[1];
			double blue = rgb[2];

			double cyan = 1 - red;
			double magenta = 1 - green;
			double yellow = 1 - blue;

			double black = Math.min(Math.min(cyan, magenta), yellow);

			if (black != 1) {
				cyan = (cyan - black) / (1 - black);
				magenta = (magenta - black) / (1 - black);
				yellow = (yellow - black) / (1 - black);
			} else {
				cyan = 0;
				magenta = 0;
				yellow = 0;
			}

			return new double[] { cyan, magenta, yellow, black };
		}

		public static double[] toXYZ(double[] rgb) {
			checkArrayLength("RGB", rgb, 3);

			double red = rgb[0];
			double green = rgb[1];
			double blue = rgb[2];

			double x = 0.4124 * red + 0.3576 * green + 0.1805 * blue;
			double y = 0.2126 * red + 0.7152 * green + 0.0722 * blue;
			double z = 0.0193 * red + 0.1192 * green + 0.9505 * blue;

			return new double[] { x, y, z };
		}

		public static double[] toLAB(double[] rgb) {
			checkArrayLength("RGB", rgb, 3);

			// Convert to XYZ
			double[] xyz = toXYZ(rgb);
			double x = xyz[0];
			double y = xyz[1];
			double z = xyz[2];

			// Convert to LAB
			double xRef = 0.9642;
			double yRef = 1.0;
			double zRef = 0.8251;

			double fx = x / xRef;
			double fy = y / yRef;
			double fz = z / zRef;

			double epsilon = 0.008856;
			double kappa = 903.3;

			double xr = fx > epsilon ? Math.pow(fx, 1.0 / 3) : (kappa * fx + 16) / 116;
			double yr = fy > epsilon ? Math.pow(fy, 1.0 / 3) : (kappa * fy + 16) / 116;
			double zr = fz > epsilon ? Math.pow(fz, 1.0 / 3) : (kappa * fz + 16) / 116;

			double l = 116 * yr - 16;
			double a = 500 * (xr - yr);
			double b = 200 * (yr - zr);

			return new double[] { l, a, b };
		}

		public static double[] toLCH(double[] rgb) {
			checkArrayLength("RGB", rgb, 3);

			// Convert to LAB
			double[] Lab = toLAB(rgb);
			double L = Lab[0];
			double a = Lab[1];
			double b = Lab[2];

			// Convert to LCH
			double C = Math.sqrt(a * a + b * b);
			double H = Math.atan2(b, a);
			H = H < 0 ? H + 2 * Math.PI : H;
			H = H * 180 / Math.PI;

			return new double[] { L, C, H };
		}
	}

	public static class ToRGB {
		public static double[] fromInt(int color) {
			double red = ((color >> 16) & 0xFF) / 255.0;
			double green = ((color >> 8) & 0xFF) / 255.0;
			double blue = (color & 0xFF) / 255.0;
			return new double[] { red, green, blue };
		}

		public static double[] fromHSV(double[] hsv) {
			checkArrayLength("HSV", hsv, 3);

			double hue = hsv[0];
			double saturation = hsv[1];
			double value = hsv[2];

			double chroma = value * saturation;
			double huePrime = hue / 60.0;
			double x = chroma * (1 - Math.abs(huePrime % 2 - 1));

			double red = 0, green = 0, blue = 0;
			if (huePrime < 1) {
				red = chroma;
				green = x;
			} else if (huePrime < 2) {
				red = x;
				green = chroma;
			} else if (huePrime < 3) {
				green = chroma;
				blue = x;
			} else if (huePrime < 4) {
				green = x;
				blue = chroma;
			} else if (huePrime < 5) {
				red = x;
				blue = chroma;
			} else {
				red = chroma;
				blue = x;
			}

			double m = value - chroma;
			red += m;
			green += m;
			blue += m;

			return new double[] { red, green, blue };
		}

		public static double[] fromHSL(double[] hsl) {
			checkArrayLength("HSL", hsl, 3);

			double hue = hsl[0];
			double saturation = hsl[1];
			double lightness = hsl[2];

			double chroma = (1 - Math.abs(2 * lightness - 1)) * saturation;
			double huePrime = hue / 60.0;
			double x = chroma * (1 - Math.abs(huePrime % 2 - 1));

			double red = 0, green = 0, blue = 0;
			if (huePrime < 1) {
				red = chroma;
				green = x;
			} else if (huePrime < 2) {
				red = x;
				green = chroma;
			} else if (huePrime < 3) {
				green = chroma;
				blue = x;
			} else if (huePrime < 4) {
				green = x;
				blue = chroma;
			} else if (huePrime < 5) {
				red = x;
				blue = chroma;
			} else {
				red = chroma;
				blue = x;
			}

			double m = lightness - chroma / 2;
			red += m;
			green += m;
			blue += m;

			return new double[] { red, green, blue };
		}

		public static double[] fromCMYK(double[] cmyk) {
			checkArrayLength("CMYK", cmyk, 4);

			double cyan = cmyk[0];
			double magenta = cmyk[1];
			double yellow = cmyk[2];
			double black = cmyk[3];

			double red = (1 - cyan) * (1 - black);
			double green = (1 - magenta) * (1 - black);
			double blue = (1 - yellow) * (1 - black);

			return new double[] { red, green, blue };
		}

		public static double[] fromXYZ(double[] xyz) {
			checkArrayLength("XYZ", xyz, 3);

			double x = xyz[0];
			double y = xyz[1];
			double z = xyz[2];

			double red = 3.2406 * x - 1.5372 * y - 0.4986 * z;
			double green = -0.9689 * x + 1.8758 * y + 0.0415 * z;
			double blue = 0.0557 * x - 0.2040 * y + 1.0570 * z;

			// Clip RGB values to the range [0, 1]
			red = Math.min(Math.max(red, 0), 1);
			green = Math.min(Math.max(green, 0), 1);
			blue = Math.min(Math.max(blue, 0), 1);

			return new double[] { red, green, blue };
		}

		public static double[] fromLAB(double[] Lab) {
			checkArrayLength("Lab", Lab, 3);

			// Convert to XYZ
			double l = Lab[0];
			double a = Lab[1];
			double b = Lab[2];

			double yr = (l + 16) / 116;
			double xr = a / 500 + yr;
			double zr = yr - b / 200;

			double xRef = 0.9642;
			double yRef = 1.0;
			double zRef = 0.8251;

			double x = xRef * (xr * xr * xr > 0.008856 ? xr * xr * xr : (116 * xr - 16) / 903.3);
			double y = yRef * (yr * yr * yr > 0.008856 ? yr * yr * yr : (116 * yr - 16) / 903.3);
			double z = zRef * (zr * zr * zr > 0.008856 ? zr * zr * zr : (116 * zr - 16) / 903.3);

			// Convert to RGB
			return fromXYZ(new double[] { x, y, z });
		}

		public static double[] fromLCH(double[] LCH) {
			checkArrayLength("LCH", LCH, 3);

			double L = LCH[0];
			double C = LCH[1];
			double H = LCH[2];

			// Convert to LAB
			double a = C * Math.cos(H * Math.PI / 180);
			double b = C * Math.sin(H * Math.PI / 180);
			double[] Lab = new double[] { L, a, b };

			// Convert to RGB
			return fromLAB(Lab);
		}
	}
}
