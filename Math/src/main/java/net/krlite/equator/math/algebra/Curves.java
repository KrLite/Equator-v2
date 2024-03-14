package net.krlite.equator.math.algebra;

import net.krlite.equator.visual.animation.Slice;

/**
 * <h1>Curves</h1>
 * Contains <b>curves</b> that can be used to <b>animate</b> values.
 * @see Slice
 */
public class Curves {
	/**
	 * <b>Zero</b>
	 * <h2><code>f(x) = 0</code></h2>
	 * A line that <b>represents the start value.</b>
	 */
	public static final Slice ZERO = Slice.map(progress -> 0.0);

 	/**
	 * <b>One</b>
	 * <h2><code>f(x) = 1</code></h2>
	 * A line that <b>represents the end value.</b>
	 */
	public static final Slice ONE = Slice.map(progress -> 1.0);

	/**
	 * <b>Linear</b>
	 * <h2><code>f(x) = x</code></h2>
	 * A <b>linear</b> curve.
	 */
	public static final Slice LINEAR = Slice.map(progress -> progress);

	/**
	 * <h2>Stairs</h2>
	 * Curves that are <b>stair-like.</b>
	 */
	public static class Stairs {
		/**
		 * <b>Stair (up) Generator</b>
		 * @param steps	the number of steps.
		 * @return	a {@code stair} curve that goes <b>up.</b>
		 */
		public static Slice up(int steps) {
			return (start, end, progress) -> start + (end - start) * (progress * steps);
		}

		/**
		 * <b>Stair (down) Generator</b>
		 * @param steps	the number of steps.
		 * @return	a {@code stair} curve that goes <b>down.</b>
		 */
		public static Slice down(int steps) {
			return (start, end, progress) -> start + (end - start) * (1 - (1 - progress) * steps);
		}

		/**
		 * <b>Stair (both) Generator</b>
		 * @param steps	the number of steps.
		 * @return	a {@code stair} curve that goes <b>up then down.</b>
		 */
		public static Slice both(int steps) {
			return (start, end, progress) -> {
				if (progress < 0.5) {
					return start + (end - start) * (progress * steps * 2);
				} else {
					return start + (end - start) * (1 - (1 - progress) * steps * 2);
				}
			};
		}
	}

	/**
	 * <h2>Switch</h2>
	 * Curves that work as <b>switches.</b>
	 */
	public static class Switch {
		/**
		 * <b>Switch (on) Generator</b>
		 * @param on		the slice to use when the progress is <b>above</b> the threshold.
		 * @param off		the slice to use when the progress is <b>below or equivalent</b> the threshold.
		 * @param threshold	the threshold.
		 * @return	a {@code switch} curve that <b>turns on.</b>
		 */
		public static Slice on(Slice on, Slice off, double threshold) {
			return (start, end, progress) -> {
				if (progress < threshold) {
					return off.apply(start, end, progress);
				}
				else {
					return on.apply(start, end, progress);
				}
			};
		}

		/**
		 * <b>Switch (on) Generator</b>
		 * <p>
		 *     <code>On: <b>{@link #ONE}</b></code>
		 *     <br />
		 *     <code>Off: <b>{@link #ZERO}</b></code>
		 * </p>
		 * @param threshold	the threshold.
		 * @return	a {@code switch} curve that <b>turns on.</b>
		 */
		public static Slice on(double threshold) {
			return on(ONE, ZERO, threshold);
		}

		/**
		 * <b>Switch (off) Generator</b>
		 * @param on		the slice to use when the progress is <b>above</b> the threshold.
		 * @param off		the slice to use when the progress is <b>below or equivalent</b> the threshold.
		 * @param threshold	the threshold.
		 * @return	a {@code switch} curve that <b>turns off.</b>
		 */
		public static Slice off(Slice on, Slice off, double threshold) {
			return (start, end, progress) -> {
				if (progress > threshold) {
					return off.apply(start, end, progress);
				}
				else {
					return on.apply(start, end, progress);
				}
			};
		}

		/**
		 * <b>Switch (off) Generator</b>
		 * <p>
		 *     {@code On: <b>{@link #ONE}</b>}
		 *     <br />
		 *     {@code Off: <b>{@link #ZERO}</b>}
		 * </p>
		 * @param threshold	the threshold.
		 * @return	a {@code switch} curve that <b>turns off.</b>
		 */
		public static Slice off(double threshold) {
			return off(ONE, ZERO, threshold);
		}

		/**
		 * <b>Switch (on)</b>
		 * <p>
		 *     {@code On: <b>{@link #ONE}</b>}
		 *     <br />
		 *     {@code Off: <b>{@link #ZERO}</b>}
		 * </p>
		 * A {@code switch} curve that <b>turns on</b> at the threshold of <b>0.5.</b>
		 */
		public static final Slice ON = on(0.5);

		/**
		 * <b>Switch (on)</b>
		 * <p>
		 *     {@code On: <b>{@link #ONE}</b>}
		 *     <br />
		 *     {@code Off: <b>{@link #ZERO}</b>}
		 * </p>
		 * A {@code switch} curve that <b>turns off</b> at the <b>end.</b>
		 */
		public static final Slice TAIL_ON = on(1);

		/**
		 * <b>Switch (off)</b>
		 * <p>
		 *     {@code On: <b>{@link #ONE}</b>}
		 *     <br />
		 *     {@code Off: <b>{@link #ZERO}</b>}
		 * </p>
		 * A {@code switch} curve that <b>turns off</b> at the threshold of <b>0.5.</b>
		 */
		public static final Slice OFF = off(0.5);

		/**
		 * <b>Switch (off)</b>
		 * <p>
		 *     {@code On: <b>{@link #ONE}</b>}
		 *     <br />
		 *     {@code Off: <b>{@link #ZERO}</b>}
		 * </p>
		 * A {@code switch} curve that <b>turns off</b> at the <b>start.</b>
		 */
		public static final Slice TAIL_OFF = off(0);
	}

	/**
	 * <h2>Exponential</h2>
	 * Curves that are <b>exponential.</b>
	 */
	public static class Exponential {
		/**
		 * <b>Exponential (fade-in) Generator</b>
		 * <h2><code>f(x) = x<sup>n</sup></code></h2>
		 * @param n	the exponent.
		 * @return	a <b>fade-in</b> {@code exponential} curve.
		 */
		public static Slice in(double n) {
			return Slice.map(progress -> Math.pow(progress, n));
		}

		/**
		 * <b>Exponential (fade-out) Generator</b>
		 * <h2><code>f(x) = 1 - (1 - x)<sup>n</sup></code></h2>
		 * @param n	the exponent.
		 * @return	a <b>fade-out</b> {@code exponential} curve.
		 */
		public static Slice out(double n) {
			return Slice.map(progress -> 1 - Math.pow(1 - progress, n));
		}

		/**
		 * <b>Exponential Generator</b>
		 * <h2><code>f(x) = x<sup>n</sup> / 2</code></h2>
		 * @param n	the exponent.
		 * @return	an {@code exponential} curve.
		 */
		public static Slice ease(double n) {
			return Slice.map(progress -> {
				if (progress < 0.5) {
					return Math.pow(progress * 2, n) / 2;
				} else {
					return 1 - Math.pow((1 - progress) * 2, n) / 2;
				}
			});
		}

		/**
		 * <h2>Quartic</h2>
		 * Curves that are <b>gentle and smooth.</b>
		 */
		public static class Quadratic {
			/**
			 * <b>Quadratic (fade-in)</b>
			 * <h2><code>f(x) = x<sup>2</sup></code></h2>
			 * A <b>fade-in</b> {@code quadratic} curve.
			 */
			public static final Slice IN = in(2);
			
			/**
			 * <b>Quadratic (fade-out)</b>
			 * <h2><code>f(x) = 1 - (1 - x)<sup>2</sup></code></h2>
			 * A <b>fade-out</b> {@code quadratic} curve.
			 */
			public static final Slice OUT = out(2);
			
			/**
			 * <b>Quadratic</b>
			 * <h2><code>f(x) = x<sup>2</sup> / 2</code></h2>
			 * A {@code quadratic} curve.
			 */
			public static final Slice EASE = ease(2);
		}

		/**
		 * <h2>Cubic</h2>
		 * Curves that are <b>not too steep.</b>
		 */
		public static class Cubic {
			/**
			 * <b>Cubic (fade-in)</b>
			 * <h2><code>f(x) = x<sup>3</sup></code></h2>
			 * A <b>fade-in</b> {@code cubic} curve.
			 */
			public static final Slice IN = in(3);
			
			/**
			 * <b>Cubic (fade-out)</b>
			 * <h2><code>f(x) = 1 - (1 - x)<sup>3</sup></code></h2>
			 * A <b>fade-out</b> {@code cubic} curve.
			 */
			public static final Slice OUT = out(3);
			
			/**
			 * <b>Cubic</b>
			 * <h2><code>f(x) = x<sup>3</sup> / 2</code></h2>
			 * A {@code cubic} curve.
			 */
			public static final Slice EASE = ease(3);
		}

		/**
		 * <h2>Quartic</h2>
		 * Curves that are <b>quite steep.</b>
		 */
		public static class Quartic {
			/**
			 * <b>Quartic (fade-in)</b>
			 * <h2><code>f(x) = x<sup>4</sup></code></h2>
			 * A <b>fade-in</b> {@code quartic} curve.
			 */
			public static final Slice IN = in(4);
			
			/**
			 * <b>Quartic (fade-out)</b>
			 * <h2><code>f(x) = 1 - (1 - x)<sup>4</sup></code></h2>
			 * A <b>fade-out</b> {@code quartic} curve.
			 */
			public static final Slice OUT = out(4);
			
			/**
			 * <b>Quartic</b>
			 * <h2><code>f(x) = x<sup>4</sup> / 2</code></h2>
			 * A {@code quartic} curve.
			 */
			public static final Slice EASE = ease(4);
		}

		/**
		 * <h2>Quintic</h2>
		 * Curves that are <b>very steep.</b>
		 */
		public static class Quintic {
			/**
			 * <b>Quintic (fade-in)</b>
			 * <h2><code>f(x) = x<sup>5</sup></code></h2>
			 * A <b>fade-in</b> {@code quintic} curve.
			 */
			public static final Slice IN = in(5);
			
			/**
			 * <b>Quintic (fade-out)</b>
			 * <h2><code>f(x) = 1 - (1 - x)<sup>5</sup></code></h2>
			 * A <b>fade-out</b> {@code quintic} curve.
			 */
			public static final Slice OUT = out(5);
			
			/**
			 * <b>Quintic</b>
			 * <h2><code>f(x) = x<sup>5</sup> / 2</code></h2>
			 * A {@code quintic} curve.
			 */
			public static final Slice EASE = ease(5);
		}
	}

	/**
	 * <h2>Back</h2>
	 * Curves that are <b>overshot.</b>
	 */
	public static class Back {
		/**
		 * <b>Back (fade-in) Generator</b>
		 * @param overshoot	the amount to overshoot.
		 *                  <p><code>Default: <b>1.70158</b></code></p>
		 * @return a <b>fade-in</b> {@code back} curve.
		 */
		public static Slice in(double overshoot) {
			return Slice.map(progress -> progress * progress * ((overshoot + 1) * progress - overshoot));
		}

		/**
		 * <b>Back (fade-out) Generator</b>
		 * @param overshoot	the amount to overshoot.
		 *                  <p><code>Default: <b>1.70158</b></code></p>
		 * @return a <b>fade-out</b> {@code back} curve.
		 */
		public static Slice out(double overshoot) {
			return Slice.map(progress -> --progress * progress * ((overshoot + 1) * progress + overshoot) + 1);
		}

		/**
		 * <b>Back Generator</b>
		 * @param overshoot	the amount to overshoot.
		 *                  <p><code>Default: <b>1.70158</b></code></p>
		 * @return a {@code back} curve.
		 */
		public static Slice ease(double overshoot) {
			return Slice.map(progress -> {
				progress *= 2;
				if (progress < 1) {
					return 0.5 * (progress * progress * ((overshoot + 1) * progress - overshoot));
				} else {
					progress -= 2;
					return 0.5 * (progress * progress * ((overshoot + 1) * progress + overshoot) + 2);
				}
			});
		}

		private static final double DEFAULT_OVERSHOOT = 1.70158;

		/**
		 * <b>Back (fade-in)</b>
		 * <br />
		 * A <b>fade-in</b> {@code back} curve.
		 */
		public static final Slice IN = in(DEFAULT_OVERSHOOT);

		/**
		 * <b>Back (fade-out)</b>
		 * <br />
		 * A <b>fade-out</b> {@code back} curve.
		 */
		public static final Slice OUT = out(DEFAULT_OVERSHOOT);

		/**
		 * <b>Back</b>
		 * <br />
		 * A {@code back} curve.
		 */
		public static final Slice EASE = ease(DEFAULT_OVERSHOOT);
	}

	/**
	 * <h2>Elastic</h2>
	 * Curves that are <b>resilient and springy.</b>
	 */
	public static class Elastic {
		/**
		 * <b>Elastic (fade-in) Generator</b>
		 * @param amplitude	the amplitude of the oscillation.
		 *                  <p><code>Default: <b>1</b></code></p>
		 * @param period	the period of the oscillation.
		 *                  <p><code>Default: <b>0.3</b></code></p>
		 * @return a <b>fade-in</b> {@code elastic} curve.
		 */
		public static Slice in(double amplitude, double period) {
			return Slice.map(progress -> {
				if (progress == 0) {
					return 0.0;
				} else if (progress == 1) {
					return 1.0;
				} else {
					double s = period / (2 * Math.PI) * Math.asin(1 / amplitude);
					return -(amplitude * Math.pow(2, 10 * --progress) * Math.sin((progress - s) * (2 * Math.PI) / period));
				}
			});
		}

		/**
		 * <b>Elastic (fade-out) Generator</b>
		 * @param amplitude	the amplitude of the oscillation.
		 *                  <p><code>Default: <b>1</b></code></p>
		 * @param period	the period of the oscillation.
		 *                  <p><code>Default: <b>0.3</b></code></p>
		 * @return a <b>fade-out</b> {@code elastic} curve.
		 */
		public static Slice out(double amplitude, double period) {
			return Slice.map(progress -> {
				if (progress == 0) {
					return 0.0;
				} else if (progress == 1) {
					return 1.0;
				} else {
					double s = period / (2 * Math.PI) * Math.asin(1 / amplitude);
					return amplitude * Math.pow(2, -10 * progress) * Math.sin((progress - s) * (2 * Math.PI) / period) + 1;
				}
			});
		}

		/**
		 * <b>Elastic Generator</b>
		 * @param amplitude	the amplitude of the oscillation.
		 *                  <p><code>Default: <b>1</b></code></p>
		 * @param period	the period of the oscillation.
		 *                  <p><code>Default: <b>0.3</b></code></p>
		 * @return an {@code elastic} curve.
		 */
		public static Slice ease(double amplitude, double period) {
			return Slice.map(progress -> {
				if (progress == 0) {
					return 0.0;
				} else if (progress == 1) {
					return 1.0;
				} else {
					double s = period / (2 * Math.PI) * Math.asin(1 / amplitude);
					progress *= 2;
					if (progress < 1) {
						return -0.5 * (amplitude * Math.pow(2, 10 * --progress) * Math.sin((progress - s) * (2 * Math.PI) / period));
					} else {
						return amplitude * Math.pow(2, -10 * --progress) * Math.sin((progress - s) * (2 * Math.PI) / period) * 0.5 + 1;
					}
				}
			});
		}

		private static final double DEFAULT_AMPLITUDE = 1.0, DEFAULT_PERIOD = 0.3;

		/**
		 * <b>Elastic (fade-in)</b>
		 * <br />
		 * A <b>fade-in</b> {@code elastic} curve.
		 */
		public static final Slice IN = in(DEFAULT_AMPLITUDE, DEFAULT_PERIOD);

		/**
		 * <b>Elastic (fade-out)</b>
		 * <br />
		 * A <b>fade-out</b> {@code elastic} curve.
		 */
		public static final Slice OUT = out(DEFAULT_AMPLITUDE, DEFAULT_PERIOD);

		/**
		 * <b>Elastic</b>
		 * <br />
		 * An {@code elastic} curve.
		 */
		public static final Slice EASE = ease(DEFAULT_AMPLITUDE, DEFAULT_PERIOD);
	}

	/**
	 * <h2>Bounce</h2>
	 * Curves that are <b>bouncy,</b> and are made up of multiple <b>parabolic curves.</b>
	 */
	public static class Bounce {
		/**
		 * <b>Bounce (fade-out)</b>
		 * <br />
		 * A <b>fade-out</b> {@code bounce} curve.
		 */
		public static final Slice OUT = Slice.map(progress -> {
			if (progress < 1 / 2.75) {
				return 7.5625 * progress * progress;
			} else if (progress < 2 / 2.75) {
				progress -= 1.5 / 2.75;
				return 7.5625 * progress * progress + 0.75;
			} else if (progress < 2.5 / 2.75) {
				progress -= 2.25 / 2.75;
				return 7.5625 * progress * progress + 0.9375;
			} else {
				progress -= 2.625 / 2.75;
				return 7.5625 * progress * progress + 0.984375;
			}
		});

		/**
		 * <b>Bounce (fade-in)</b>
		 * <br />
		 * A <b>fade-in</b> {@code bounce} curve.
		 */
		public static final Slice IN = OUT.opposite().reverse();

		/**
		 * <b>Bounce (fade-in and fade-out)</b>
		 * <br />
		 * A {@code bounce} curve.
		 */
		public static final Slice EASE = IN.mapRange(0, 0.5).append(OUT.mapRange(0.5, 1));
	}

	/**
	 * <h2>Sinusoidal</h2>
	 * <b>Gently curved,</b> like a sine wave.
	 */
	public static class Sinusoidal {
		/**
		 * <b>Sinusoidal (fade-in)</b>
		 * <h2><code>f(x) = 1 - cos(x * π / 2)</code></h2>
		 * A <b>fade-in</b> {@code sinusoidal} curve.
		 */
		public static final Slice IN = Slice.map(progress -> 1 - Math.cos(progress * Math.PI / 2));

		/**
		 * <b>Sinusoidal (fade-out)</b>
		 * <h2><code>f(x) = sin(x * π / 2)</code></h2>
		 * A <b>fade-out</b> {@code sinusoidal} curve.
		 */
		public static final Slice OUT = Slice.map(progress -> Math.sin(progress * Math.PI / 2));

		/**
		 * <b>Sinusoidal</b>
		 * <h2><code>f(x) = (1 - cos(x * π)) / 2</code></h2>
		 * A {@code sinusoidal} curve.
		 */
		public static final Slice EASE = Slice.map(progress -> (1 - Math.cos(progress * Math.PI)) / 2);
	}

	/**
	 * <h2>2-Based Exponential</h2>
	 * Curves that are <b>extremely curved and steep.</b>
	 */
	public static class TwoBasedExponential {
		/**
		 * <b>2-Based Exponential (fade-in)</b>
		 * <h2><code>f(x) = 2<sup>10 * (x - 1)</sup></code></h2>
		 * A <b>fade-in</b> {@code exponential} curve.
		 */
		public static final Slice IN = Slice.map(progress -> Math.pow(2, 10 * (progress - 1)));

		/**
		 * <b>2-Based Exponential (fade-out)</b>
		 * <h2><code>f(x) = -2<sup>-10 * x</sup> + 1</code></h2>
		 * A <b>fade-out</b> {@code exponential} curve.
		 */
		public static final Slice OUT = Slice.map(progress -> -Math.pow(2, -10 * progress) + 1);

		/**
		 * <b>2-Based Exponential</b>
		 * <h2><code>
		 *     f(x) = 2<sup>10 * (x - 1)</sup> if x < 0.5
		 *     <br />
		 *     -2<sup>-10 * (x - 1)</sup> + 1 if x >= 0.5
		 * </code></h2>
		 * An {@code exponential} curve.
		 */
		public static final Slice EASE = Slice.map(progress -> {
			if (progress < 0.5) {
				return Math.pow(2, 10 * (progress * 2 - 1)) / 2;
			} else {
				return (-Math.pow(2, -10 * (progress * 2 - 1)) + 2) / 2;
			}
		});
	}

	/**
	 * <h2>Circular</h2>
	 * Curves that are <b>unbelievably curved,</b> about to become a circle.
	 */
	public static class Circular {
		/**
		 * <b>Circular (fade-in)</b>
		 * <h2><code>f(x) = 1 - sqrt(1 - x<sup>2</sup>)</code></h2>
		 * A <b>fade-in</b> {@code circular} curve.
		 */
		public static final Slice IN = Slice.map(progress -> 1 - Math.sqrt(1 - Math.pow(progress, 2)));

		/**
		 * <b>Circular (fade-out)</b>
		 * <h2><code>f(x) = sqrt(1 - (x - 1)<sup>2</sup>)</code></h2>
		 * A <b>fade-out</b> {@code circular} curve.
		 */
		public static final Slice OUT = Slice.map(progress -> Math.sqrt(1 - Math.pow(progress - 1, 2)));

		/**
		 * <b>Circular</b>
		 * <h2><code>
		 *     f(x) = (1 - sqrt(1 - x<sup>2</sup>)) / 2 if x < 0.5
		 *     <br />
		 *     f(x) = (sqrt(1 - (x - 1)<sup>2</sup>) + 1) / 2 if x >= 0.5
		 * </code></h2>
		 * A {@code circular} curve.
		 */
		public static final Slice EASE = Slice.map(progress -> {
			if (progress < 0.5) {
				return (1 - Math.sqrt(1 - Math.pow(progress * 2, 2))) / 2;
			} else {
				return (Math.sqrt(1 - Math.pow(progress * 2 - 2, 2)) + 1) / 2;
			}
		});
	}
}
