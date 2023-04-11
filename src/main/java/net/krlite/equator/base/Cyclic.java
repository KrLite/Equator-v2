package net.krlite.equator.base;

public interface Cyclic<T> {
	/**
	 * Cycle to the next element.
	 * @return	The next element.
	 */
	T next();

	/**
	 * Cycle to the previous element.
	 * @return	The previous element.
	 */
	T previous();

	/**
	 * Cycle to the next element if <code>reverse</code> is <code>false</code>, or the previous
	 * element if <code>reverse</code> is <code>true</code>.
	 * @param reverse	<code>true</code> to cycle to the previous element, <code>false</code> to cycle to the next element.
	 * @return	The next element if <code>reverse</code> is <code>false</code>, or the previous
	 * 			element if <code>reverse</code> is <code>true</code>.
	 */
	default T fromBoolean(boolean reverse) {
		return reverse ? previous() : next();
	}

	interface Enum<T extends java.lang.Enum<T>> extends Cyclic<T> {
		/**
		 * Cycle to the next enum constant.
		 * @return	The next enum constant.
		 */
		@Override
		default T next() {
			T[] values = enumValues();
			return values[(ordinal() + 1) % values.length];
		}

		/**
		 * Cycle to the previous enum constant.
		 * @return	The previous enum constant.
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
