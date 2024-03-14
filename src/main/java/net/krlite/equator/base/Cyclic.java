package net.krlite.equator.base;

public interface Cyclic<T> {
	/**
	 * Cycle to the next element.
	 * @return	the next element.
	 */
	T next();

	/**
	 * Cycle to the previous element.
	 * @return	the previous element.
	 */
	T previous();

	/**
	 * Cycle to the next element if {@code reverse} is {@code false}, or the previous
	 * element if {@code reverse} is {@code true}.
	 * @param reverse	{@code true} to cycle to the previous element, {@code false} to cycle to the next element.
	 * @return	the next element if {@code reverse} is {@code false}, or the previous
	 * 			element if {@code reverse} is {@code true}.
	 */
	default T fromBoolean(boolean reverse) {
		return reverse ? previous() : next();
	}

	interface Enum<T extends java.lang.Enum<T>> extends Cyclic<T> {
		/**
		 * Cycle to the next enum constant.
		 * @return	the next enum constant.
		 */
		@Override
		default T next() {
			T[] values = enumValues();
			return values[(ordinal() + 1) % values.length];
		}

		/**
		 * Cycle to the previous enum constant.
		 * @return	the previous enum constant.
		 */
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
