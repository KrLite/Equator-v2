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
	
	public static final double TAU = 2 * Math.PI;

	/**
	 * <h1>{@code a ≈ b}</h1>
	 * Checks if the two doubles are equal within a small margin of error.
	 * @param a	the first double.
	 * @param b	the second double.
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
	 * @param a	the first double.
	 * @param b	the second double.
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
	 * @param a	the first double.
	 * @param b	the second double.
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
	 * @param a	the first double.
	 * @param b	the second double.
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
	 * @param a	the first double.
	 * @param b	the second double.
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
	 * @param a	the first double.
	 * @param b	the second double.
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
	 * @param x	the value to check.
	 * @param a	the left bound.
	 * @param b	the right bound.
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
	 * @param x	the double to check.
	 * @param a	the left bound.
	 * @param b	the right bound.
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
	 * @param value	the double to check.
	 * @return	{@code true} if the double is zero within a small margin of error,
	 * 			{@code false} otherwise.
	 * @see #EPSILON
	 */
	public static boolean isZero(double value) {
		return looseEquals(value, 0);
	}

	/**
	 * Clamps a double to the range {@code [min, max]}.
	 * @param value	the double to clamp.
	 * @param min	the minimum value.
	 * @param max	the maximum value.
	 * @return	The clamped double.
	 */
	public static double clamp(double value, double min, double max) {
		if (min > max) {
			return clamp(value, max, min);
		} else if (min == max) {
			return min;
		}

		return Math.max(min, Math.min(max, value));
	}

	/**
	 * Clamps a double to the range {@code (min, max]}.
	 * @param value	the double to clamp.
	 * @param min	the minimum value.
	 * @param max	the maximum value.
	 * @return	The clamped double.
	 */
	public static double clampGreater(double value, double min, double max) {
		if (min > max) {
			return clampGreater(value, max, min);
		} else if (min == max) {
			return min;
		}

		double clamped = clamp(value, min, max);
		return clamped <= min ? max : clamped;
	}

	/**
	 * Clamps a double to the range {@code [min, max)}.
	 * @param value	the double to clamp.
	 * @param min	the minimum value.
	 * @param max	the maximum value.
	 * @return	The clamped double.
	 */
	public static double clampLess(double value, double min, double max) {
		if (min > max) {
			return clampLess(value, max, min);
		} else if (min == max) {
			return min;
		}

		double clamped = clamp(value, min, max);
		return clamped >= max ? min : clamped;
	}

	/**
	 * Modulos a double to the range {@code [0, mod)}.
	 * @param value	the double to mod.
	 * @param mod	the modulus.
	 * @return	The modulated double.
	 */
	public static double mod(double value, double mod) {
		return value - Math.floor(value / mod) * mod;
	}

	/**
	 * Linearly interpolates between two values.
	 * @param a	the first double.
	 * @param b	the second double.
	 * @param t	the interpolation value.
	 * @return	the interpolated double.
	 */
	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	/**
	 * Approximates a value to the maximum value using function:
	 * <table>
	 *     <tr>
	 *         <th>Function</th>
	 *         <th>Domain</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code y = yMax * (1 - e^(-k * x))}</td>
	 *         <td>{@code x ∈ (0, +∞)}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code y = 0}</td>
	 *         <td>{@code x = 0}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@code y = -yMax * (1 - e^(k * x))}</td>
	 *         <td>{@code x ∈ (-∞, 0)}</td>
	 *     </tr>
	 * </table>
	 * @return	the approximated value.
	 */
	public static double approximation(double x, double yMax, double k) {
		if (Theory.looseEquals(x, 0)) return 0;

		return x > 0 ? (yMax * (1 - Math.exp(-k * x))) : (-yMax * (1 - Math.exp(k * x)));
	}
}
