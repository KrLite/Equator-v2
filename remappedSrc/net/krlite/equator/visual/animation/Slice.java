package net.krlite.equator.visual.animation;

import java.util.function.UnaryOperator;

/**
 * <h1>Slice</h1>
 * A <b>multi-parameter function</b> that accepts the start, end and progress values and returns a mapped
 * value.
 * <br />
 * <br />
 * For example, {@link net.krlite.equator.math.algebra.Curves#LINEAR Curves.LINEAR} is a slice that maps
 * the progress to itself, which means that the value is interpolated linearly between the start and end
 * values;
 * <br />
 * {@link net.krlite.equator.math.algebra.Curves.Exponential.Quadratic#IN Curves.Exponential.Quadratic.EASE}
 * is a slice that maps the progress to the square of itself, which means that the value is interpolated
 * quadratically between the start and end values, that is, it starts slow and ends fast;
 * <br />
 * <br />
 * Most slices follow the formula {@code start + (end - start) * mapped}, where {@code mapped} is the
 * value of the progress mapped by the slice. Basically, different slices map the progress to different
 * values, thus changing the formula behaviour.
 */
@FunctionalInterface
public interface Slice {
	/**
	 * Accepts the start and end values and the progress of the animation and returns the current value.
	 * @param start		The start value.
	 * @param end		The end value.
	 * @param progress	The progress of the animation.
	 * @return			The current value.
	 */
	double apply(double start, double end, double progress);

	/**
	 * Creates a slice that maps the progress to a new value.
	 * @param progressMapper	The mapper function.
	 * @return	A new slice that maps the progress, calculated by formula {@code start + (end - start) * progress}.
	 * 			Which means, the value can be seen as an interpolation between the start and end values,
	 * 	  		depending on the progress. Changing the mapper function means changing the method to stretch
	 * 	 		or compress the progress, thus changing the interpolation.
	 */
	static Slice map(UnaryOperator<Double> progressMapper) {
		return (start, end, progress) -> start + (end - start) * progressMapper.apply(progress);
	}

	/**
	 * Creates a slice that maps the progress to a new value.
	 * @param progressMapper	The mapper function.
	 * @return	A new slice that maps the current progress to a newer one.
	 */
	default Slice mapProgress(UnaryOperator<Double> progressMapper) {
		return (start, end, progress) -> apply(start, end, progressMapper.apply(progress));
	}

	/**
	 * Creates a slice that maps the start and end values to a new range. For example, {@code mapRange(0.5, 1)}
	 * will map the start and end values to the second half of the range, causing the value to begin at
	 * {@code start + (end - start) * 0.5} and end at {@code start + (end - start) * 1}.
	 * @param rangeStart	The start percentage of the new range.
	 * @param rangeEnd		The end percentage of the new range.
	 * @return	A new slice that maps the start and end values.
	 */
	default Slice mapRange(double rangeStart, double rangeEnd) {
		return (start, end, progress) -> {
			double range = end - start;
			return apply(start + range * rangeStart, start + range * rangeEnd, progress);
		};
	}

	/**
	 * X-flips the slice, which means that the start and end values are swapped.
	 * @return	A new slice that swaps the start and end values.
	 */
	default Slice opposite() {
		return (start, end, progress) -> apply(end, start, progress);
	}

	/**
	 * Y-flips the slice, which means that the progress is inverted.
	 * @return	A new slice that inverts the progress.
	 */
	default Slice reverse() {
		return (start, end, progress) -> apply(start, end, 1 - progress);
	}

	/**
	 * Rewinds the slice after it is finished. For example, if the slice is a linear interpolation from {@code 0} to
	 * {@code 1}, the rewound slice will be a linear interpolation from {@code 0} to {@code 1} then to {@code 0} again.
	 * That is, if the progress is {@code 0.5} or less, the slice will be applied as normal with doubled speed, and if
	 * the progress is more than {@code 0.5}, the slice will be applied in opposite direction with doubled speed. Kind
	 * of like to {@link #append(Slice)} the {@link #opposite()} slice.
	 * @return	A new slice that rewinds.
	 */
	default Slice rewind() {
		return (start, end, progress) -> progress <= 0.5 ? apply(start, end, progress * 2) : apply(end, start, (progress - 0.5) * 2);
	}

	/**
	 * After this slice is applied, the result is passed to the given slice.
	 * @param another	The slice to apply after this slice.
	 * @return	A new slice that applies this slice and then the given one.
	 */
	default Slice andThen(Slice another) {
		return (start, end, progress) -> another.apply(start, end, apply(start, end, progress));
	}

	/**
	 * Before this slice is applied, the result is passed to the given slice.
	 * @param another	The slice to apply before this slice.
	 * @return	A new slice that applies the given slice and then this one.
	 */
	default Slice compose(Slice another) {
		return (start, end, progress) -> apply(start, end, another.apply(start, end, progress));
	}

	/**
	 * Appends the given slice to this one, with the given threshold.
	 * @param another	The slice to append to this slice.
	 * @param threshold		The threshold at which the combination slice is applied. For example, if the threshold is {@code 0.5},
	 *                      the combination slice is applied from beginning to end when the progress is {@code 50%} or more, and this
	 *                      slice is applied from beginning to end when the progress is less than {@code 50%}.
	 * @return	A new slice which appends the given slice to this one.
	 */
	default Slice append(Slice another, double threshold) {
		return (start, end, progress) -> {
			double t = Math.max(0, Math.min(1, threshold));
			return progress < t ? apply(start, end, progress / t) : another.apply(start, end, (progress - t) / (1 - t));
		};
	}

	/**
	 * Appends the given slice to this one, with the default threshold of {@code 0.5}.
	 * @param another	The slice to append to this slice.
	 * @return	A new slice which appends the given slice to this one.
	 * @see #append(Slice, double)
	 */
	default Slice append(Slice another) {
		return append(another, 0.5);
	}

	/**
	 * Prepends the given slice to this one, with the given threshold.
	 * @param another	The slice to prepend to this slice.
	 * @param threshold		The threshold at which the combination slice is applied. For example, if the threshold is {@code 0.5},
	 *                      this slice is applied from beginning to end when the progress is {@code 50%} or more, and the combination
	 *                      slice is applied from beginning to end when the progress is less than {@code 50%}
	 * @return	A new slice which prepends the given slice to this one.
	 */
	default Slice prepend(Slice another, double threshold) {
		return (start, end, progress) -> {
			double t = Math.max(0, Math.min(1, threshold));
			return progress < t ? another.apply(start, end, progress / t) : apply(start, end, (progress - t) / (1 - t));
		};
	}

	/**
	 * Prepends the given slice to this one, with the default threshold of {@code 0.5}.
	 * @param another	The slice to prepend to this slice.
	 * @return	A new slice which prepends the given slice to this one.
	 * @see #prepend(Slice, double)
	 */
	default Slice prepend(Slice another) {
		return prepend(another, 0.5);
	}

	/**
	 * Blends this slice with the given slice, with the given factor. For example, if the factor is {@code 0.7}, the result is
	 * {@code this * 0.3 + another * 0.7}.
	 * @param another	The slice to blend with this slice.
	 * @param factor	The factor of the blend.
	 * @return	A new slice which blends this slice with the given slice.
	 */
	default Slice blend(Slice another, double factor) {
		return (start, end, progress) -> apply(start, end, progress) * (1 - factor) + another.apply(start, end, progress) * factor;
	}

	/**
	 * Blends this slice with the given slice, with the default factor of {@code 0.5}.
	 * @param another	The slice to blend with this slice.
	 * @return	A new slice which blends this slice with the given slice.
	 * @see #blend(Slice, double)
	 */
	default Slice blend(Slice another) {
		return blend(another, 0.5);
	}
}
