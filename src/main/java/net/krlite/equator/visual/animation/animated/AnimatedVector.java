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
			return Vector.fromCartesian(
					slice().apply(start().x(), end().x(), progress),
					slice().apply(start().y(), end().y(), progress)
			);
		}

		@Override
		public Vector valueClamped(double progress) {
			return Vector.fromCartesian(
					slice().applyClamped(end().x(), start().x(), progress),
					slice().applyClamped(end().y(), start().y(), progress)
			);
		}
	}

	public static class Polar extends Animation<Vector> {
		public Polar(Vector start, Vector end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice) {
			super(start, end, speed, duration, timeUnit, sensitive, slice);
		}

		public Polar(Vector start, Vector end, long duration, Slice slice) {
			super(start, end, duration, slice);
		}

		@Override
		public Vector value(double progress) {
			return new Vector(
					slice().apply(start().angle(), end().angle(), progress),
					slice().apply(start().magnitude(), end().magnitude(), progress)
			);
		}

		@Override
		public Vector valueClamped(double progress) {
			return new Vector(
					slice().applyClamped(end().angle(), start().angle(), progress),
					slice().applyClamped(end().magnitude(), start().magnitude(), progress)
			);
		}
	}
}
