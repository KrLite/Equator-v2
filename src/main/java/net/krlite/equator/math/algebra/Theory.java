package net.krlite.equator.math.algebra;

public class Theory {
	/**
	 * A small margin of error for comparing doubles.
	 */
	public static final double EPSILON = 1e-6;

	/**
	 * Returns <code>true</code> if the two doubles are equal within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	<code>true</code> if the two doubles are equal within a small margin of error,
	 * 			<code>false</code> otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseEquals(double a, double b) {
		return Math.abs(a - b) < EPSILON;
	}

	/**
	 * Returns <code>true</code> if the two doubles are not equal within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	<code>true</code> if the two doubles are not equal within a small margin of error,
	 * 			<code>false</code> otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseUnequals(double a, double b) {
		return !looseEquals(a, b);
	}

	/**
	 * Returns <code>true</code> if the first double is greater than the second double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	<code>true</code> if the first double is greater than the second double within a small margin of error,
	 * 			<code>false</code> otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseGreater(double a, double b) {
		return a - b > EPSILON;
	}

	/**
	 * Returns <code>true</code> if the first double is greater than or equal to the second double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	<code>true</code> if the first double is greater than or equal to the second double within a small margin of error,
	 * 			<code>false</code> otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseGreaterEquals(double a, double b) {
		return looseGreater(a, b) || looseEquals(a, b);
	}

	/**
	 * Returns <code>true</code> if the first double is less than the second double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	<code>true</code> if the first double is less than the second double within a small margin of error,
	 * 			<code>false</code> otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseLess(double a, double b) {
		return b - a > EPSILON;
	}

	/**
	 * Returns <code>true</code> if the first double is less than or equal to the second double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	<code>true</code> if the first double is less than or equal to the second double within a small margin of error,
	 * 			<code>false</code> otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseLessEquals(double a, double b) {
		return looseLess(a, b) || looseEquals(a, b);
	}

	/**
	 * Returns <code>true</code> if the first double is between the second and third double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @param c	The third double.
	 * @return	<code>true</code> if the first double is between the second and third double within a small margin of error,
	 * 			<code>false</code> otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseBetween(double a, double b, double c) {
		return looseLessEquals(a, b) && looseLessEquals(b, c);
	}

	/**
	 * Returns <code>true</code> if the first double is between or equal to the second and third double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @param c	The third double.
	 * @return	<code>true</code> if the first double is between or equal to the second and third double within a small margin of error,
	 * 			<code>false</code> otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseBetweenEquals(double a, double b, double c) {
		return looseLessEquals(a, b) && looseLessEquals(b, c) || looseEquals(a, b) || looseEquals(b, c);
	}

	/**
	 * Clamps a value between a minimum and maximum value.
	 * @param value	The value to clamp.
	 * @param min	The minimum value.
	 * @param max	The maximum value.
	 * @return	The clamped value.
	 */
	public static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * Modulos a value.
	 * @param value	The value to mod.
	 * @param mod	The modulus.
	 * @return	The modulated value.
	 */
	public static double mod(double value, double mod) {
		return value - Math.floor(value / mod) * mod;
	}

	/**
	 * Linearly interpolates between two values.
	 * @param a	The first value.
	 * @param b	The second value.
	 * @param t	The interpolation value.
	 * @return	The interpolated value.
	 */
	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}
}
