package net.krlite.equator.math.algebra;

/**
 * <h1>Theory</h1>
 * A collection of mathematical functions that are used in the library.
 */
public class Theory {
	/**
	 * A small margin of error for comparing doubles. The value is {@code 1e-6}, which equals to {@code 0.000001}.
	 */
	public static final double EPSILON = 1e-6;

	/**
	 * <h1>{@code a ≈ b}</h1>
	 * Checks if the two doubles are equal within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	{@code true} if the two doubles are equal within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseEquals(double a, double b) {
		return Math.abs(a - b) < EPSILON;
	}

	/**
	 * <h1>{@code a ≠ b}</h1>
	 * Checks if the two doubles are not equal within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	{@code true} if the two doubles are not equal within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseUnequals(double a, double b) {
		return !looseEquals(a, b);
	}

	/**
	 * <h1>{@code a > b}</h1>
	 * Checks if the first double is greater than the second double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	{@code true} if the first double is greater than the second double within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseGreater(double a, double b) {
		return a - b > EPSILON;
	}

	/**
	 * <h1>{@code a ≥ b}</h1>
	 * Checks if the first double is greater than or equal to the second double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	{@code true} if the first double is greater than or equal to the second double within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseGreaterEquals(double a, double b) {
		return looseGreater(a, b) || looseEquals(a, b);
	}

	/**
	 * <h1>{@code a < b}</h1>
	 * Checks if the first double is less than the second double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	{@code true} if the first double is less than the second double within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseLess(double a, double b) {
		return b - a > EPSILON;
	}

	/**
	 * <h1>{@code a ≤ b}</h1>
	 * Checks if the first double is less than or equal to the second double within a small margin of error.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @return	{@code true} if the first double is less than or equal to the second double within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseLessEquals(double a, double b) {
		return looseLess(a, b) || looseEquals(a, b);
	}

	/**
	 * <h1>{@code x ∈ (a, b)}</h1>
	 * Checks if a double is between the left and right bound within a small margin of error.
	 * @param x	The value to check.
	 * @param a	The left bound.
	 * @param b	The right bound.
	 * @return	{@code true} if the value is between the left and right bound within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseBetween(double x, double a, double b) {
		return looseLessEquals(x, a) && looseLessEquals(a, b);
	}

	/**
	 * <h1>{@code x ∈ [a, b]}</h1>
	 * Checks if a double is between or equal to the left and right bound within a small margin of error.
	 * @param x	The double to check.
	 * @param a	The left bound.
	 * @param b	The right bound.
	 * @return	{@code true} if the double is between or equal to the left and right bound within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean looseBetweenEquals(double x, double a, double b) {
		return looseLessEquals(x, a) && looseLessEquals(a, b) || looseEquals(x, a) || looseEquals(a, b);
	}

	/**
	 * <h1>{@code value ≈ 0}</h1>
	 * Checks if a double is zero within a small margin of error.
	 * @param value	The double to check.
	 * @return	{@code true} if the double is zero within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean isZero(double value) {
		return looseEquals(value, 0);
	}

	/**
	 * Clamps a double between a minimum and maximum value.
	 * @param value	The double to clamp.
	 * @param min	The minimum value.
	 * @param max	The maximum value.
	 * @return	The clamped double.
	 */
	public static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * Modulos a double.
	 * @param value	The double to mod.
	 * @param mod	The modulus.
	 * @return	The modulated double.
	 */
	public static double mod(double value, double mod) {
		return value - Math.floor(value / mod) * mod;
	}

	/**
	 * Linearly interpolates between two values.
	 * @param a	The first double.
	 * @param b	The second double.
	 * @param t	The interpolation value.
	 * @return	The interpolated double.
	 */
	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}
}
