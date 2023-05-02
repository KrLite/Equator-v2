package net.krlite.equator.math.logic.base;

@FunctionalInterface
public interface Gate {
	Gate TRUE = value -> true, FALSE = value -> false,
			ZERO = value -> value == 0, POSITIVE = value -> value > 0, NEGATIVE = value -> value < 0,
			NON_ZERO = value -> value != 0, NON_POSITIVE = value -> value <= 0, NON_NEGATIVE = value -> value >= 0;

	boolean pass(double value);

	default Gate not() {
		return value -> !pass(value);
	}

	default Gate and(Gate other) {
		return value -> pass(value) && other.pass(value);
	}

	default Gate or(Gate other) {
		return value -> pass(value) || other.pass(value);
	}

	default Gate nand(Gate other) {
		return value -> !(pass(value) && other.pass(value));
	}

	default Gate xor(Gate other) {
		return value -> pass(value) ^ other.pass(value);
	}

	default Gate nor(Gate other) {
		return value -> !(pass(value) || other.pass(value));
	}

	default Gate xnor(Gate other) {
		return value -> pass(value) == other.pass(value);
	}
}
