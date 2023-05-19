package net.krlite.equator.math.logic.base;

@FunctionalInterface
@net.krlite.equator.base.Math("2.4.0")
public interface Gate extends Gated<Gate> {
	Gate TRUE = value -> true, FALSE = value -> false,
			ZERO = value -> value == 0, POSITIVE = value -> value > 0, NEGATIVE = value -> value < 0,
			NON_ZERO = value -> value != 0, NON_POSITIVE = value -> value <= 0, NON_NEGATIVE = value -> value >= 0;

	boolean pass(double value);

	default Gate not() {
		return value -> !pass(value);
	}

	default Gate and(Gate another) {
		return value -> pass(value) && another.pass(value);
	}

	default Gate or(Gate another) {
		return value -> pass(value) || another.pass(value);
	}

	default Gate nand(Gate another) {
		return value -> !(pass(value) && another.pass(value));
	}

	default Gate xor(Gate another) {
		return value -> pass(value) ^ another.pass(value);
	}

	default Gate nor(Gate another) {
		return value -> !(pass(value) || another.pass(value));
	}

	default Gate xnor(Gate another) {
		return value -> pass(value) == another.pass(value);
	}
}
