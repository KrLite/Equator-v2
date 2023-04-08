package net.krlite.equator.visual.animation;

import jdk.jfr.Label;

import java.util.function.UnaryOperator;

/**
 * A multi-parameter function that accepts the start, end and progress and returns a value.
 * Mostly, slices should follow the formula {@code start + (end - start) * processedProgress},
 * where {@code processedProgress} is the progress that has been processed by the slice. In
 * other words, the slice should map the progress to a new value, which is then used to
 * interpolate between the start and end values.
 */
@Label("Animation 2.2.0")
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
	 * @return	A slice that maps the progress, calculated by formula {@code start + (end - start) * progress}.
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
	 * @return	A slice that maps the start and end values.
	 */
	default Slice mapRange(double rangeStart, double rangeEnd) {
		return (start, end, progress) -> {
			double range = end - start;
			return apply(start + range * rangeStart, start + range * rangeEnd, progress);
		};
	}

	/**
	 * X-flips the slice, which means that the
	 * start and end values are swapped.
	 * @return	A slice that swaps the start and end values.
	 */
	default Slice opposite() {
		return (start, end, progress) -> apply(end, start, progress);
	}

	/**
	 * Y-flips the slice, which means that the
	 * progress is inverted.
	 * @return	A slice that inverts the progress.
	 */
	default Slice reverse() {
		return (start, end, progress) -> apply(start, end, 1 - progress);
	}

	/**
	 * After this slice is applied, the result is passed to the given slice.
	 * @param after	The slice to apply after this one.
	 * @return	A slice that applies this slice and then the given one.
	 */
	default Slice andThen(Slice after) {
		return (start, end, progress) -> after.apply(start, end, apply(start, end, progress));
	}

	/**
	 * Before this slice is applied, the result is passed to the given slice.
	 * @param before	The slice to apply before this one.
	 * @return	A slice that applies the given slice and then this one.
	 */
	default Slice compose(Slice before) {
		return (start, end, progress) -> apply(start, end, before.apply(start, end, progress));
	}

	/**
	 * Appends the given slice to this one, with the given threshold.
	 * @param combination	The slice to append.
	 * @param threshold		The threshold at which the combination slice is applied. For example, if the threshold is <code>0.5</code>,
	 *                      the combination slice is applied from beginning to end when the progress is <code>50%</code> or more, and this
	 *                      slice is applied from beginning to end when the progress is less than <code>50%</code>.
	 * @return	A slice which appends the given slice to this one.
	 */
	default Slice append(Slice combination, double threshold) {
		return (start, end, progress) -> {
			double t = Math.max(0, Math.min(1, threshold));
			return progress < t ? apply(start, end, progress / t) : combination.apply(start, end, (progress - t) / (1 - t));
		};
	}

	/**
	 * Appends the given slice to this one, with the default threshold of <code>0.5</code>.
	 * @param combination	The slice to append.
	 * @return	A slice which appends the given slice to this one.
	 * @see #append(Slice, double)
	 */
	default Slice append(Slice combination) {
		return append(combination, 0.5);
	}

	/**
	 * Prepends the given slice to this one, with the given threshold.
	 * @param combination	The slice to prepend.
	 * @param threshold		The threshold at which the combination slice is applied. For example, if the threshold is <code>0.5</code>,
	 *                      this slice is applied from beginning to end when the progress is <code>50%</code> or more, and the combination
	 *                      slice is applied from beginning to end when the progress is less than <code>50%</code>
	 * @return	A slice which prepends the given slice to this one.
	 */
	default Slice prepend(Slice combination, double threshold) {
		return (start, end, progress) -> {
			double t = Math.max(0, Math.min(1, threshold));
			return progress < t ? combination.apply(start, end, progress / t) : apply(start, end, (progress - t) / (1 - t));
		};
	}

	/**
	 * Prepends the given slice to this one, with the default threshold of <code>0.5</code>.
	 * @param combination	The slice to prepend.
	 * @return	A slice which prepends the given slice to this one.
	 * @see #prepend(Slice, double)
	 */
	default Slice prepend(Slice combination) {
		return prepend(combination, 0.5);
	}
}
