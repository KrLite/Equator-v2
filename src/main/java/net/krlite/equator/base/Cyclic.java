package net.krlite.equator.base;

public interface Cyclic<T> {
	T next();
	T previous();
	default T fromBoolean(boolean reverse) {
		return reverse ? previous() : next();
	}

	interface Enum<T extends java.lang.Enum<T>> extends Cyclic<T> {
		@Override
		default T next() {
			T[] values = enumValues();
			return values[(ordinal() + 1) % values.length];
		}

		@Override
		default T previous() {
			T[] values = enumValues();
			return values[(ordinal() - 1 + values.length) % values.length];
		}

		default T[] enumValues() {
			return (T[]) getClass().getEnumConstants();
		}

		int ordinal();
	}
}
