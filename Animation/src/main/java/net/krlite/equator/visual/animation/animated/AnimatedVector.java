package net.krlite.equator.visual.animation.animated;

import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.visual.animation.Slice;
import net.krlite.equator.visual.animation.base.Animation;

import java.util.concurrent.TimeUnit;

public class AnimatedVector {
	public static class Linear extends Animation<Vector> {
		public Linear(Vector start, Vector end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice) {
			super(start, end, speed, duration, timeUnit, sensitive, slice);
		}

		public Linear(Vector start, Vector end, long duration, Slice slice) {
			super(start, end, duration, slice);
		}

		@Override
		public Vector value(double progress) {
			return start().interpolate(end(), slice().apply(0, 1, progress));
		}

		@Override
		public Vector valueClamped(double progress) {
			return start().interpolate(end(), slice().applyClamped(0, 1, progress));
		}
	}

	public static class Spherical extends Animation<Vector> {
		public Spherical(Vector start, Vector end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice) {
			super(start, end, speed, duration, timeUnit, sensitive, slice);
		}

		public Spherical(Vector start, Vector end, long duration, Slice slice) {
			super(start, end, duration, slice);
		}

		@Override
		public Vector value(double progress) {
			return start().sphericalInterpolate(end(), slice().apply(0, 1, progress));
		}

		@Override
		public Vector valueClamped(double progress) {
			return start().sphericalInterpolate(end(), slice().applyClamped(0, 1, progress));
		}
	}
}
