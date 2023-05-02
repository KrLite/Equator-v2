package net.krlite.equator.math.geometry.base;

@FunctionalInterface
public interface Limiter {
	Limiter TRUE = value -> true, FALSE = value -> false,
			ZERO = value -> value == 0, POSITIVE = value -> value > 0, NEGATIVE = value -> value < 0,
			NON_ZERO = value -> value != 0, NON_POSITIVE = value -> value <= 0, NON_NEGATIVE = value -> value >= 0;

	boolean pass(double value);
}
