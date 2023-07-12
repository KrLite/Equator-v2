package net.krlite.equator.visual.animation.animated;

import net.krlite.equator.math.geometry.volume.Pos;
import net.krlite.equator.visual.animation.Slice;
import net.krlite.equator.visual.animation.base.Animation;

import java.util.concurrent.TimeUnit;

public class AnimatedPos {
	public static class Linear extends Animation<Pos> {
		public Linear(Pos start, Pos end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice) {
			super(start, end, speed, duration, timeUnit, sensitive, slice);
		}

		public Linear(Pos start, Pos end, long duration, Slice slice) {
			super(start, end, duration, slice);
		}

		@Override
		public Pos value(double progress) {
			return start().interpolate(end(), slice().apply(0, 1, progress));
		}

		@Override
		public Pos valueClamped(double progress) {
			return start().interpolate(end(), slice().applyClamped(0, 1, progress));
		}
	}

	public static class Spherical extends Animation<Pos> {
		public Spherical(Pos start, Pos end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice) {
			super(start, end, speed, duration, timeUnit, sensitive, slice);
		}

		public Spherical(Pos start, Pos end, long duration, Slice slice) {
			super(start, end, duration, slice);
		}

		@Override
		public Pos value(double progress) {
			return start().sphericalInterpolate(end(), slice().apply(0, 1, progress));
		}

		@Override
		public Pos valueClamped(double progress) {
			return start().sphericalInterpolate(end(), slice().applyClamped(0, 1, progress));
		}
	}
}
