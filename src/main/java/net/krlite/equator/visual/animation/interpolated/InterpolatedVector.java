package net.krlite.equator.visual.animation.interpolated;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.visual.animation.base.Interpolation;

import java.util.Objects;

public class InterpolatedVector {
	public static class Linear extends Interpolation<Vector> {
		public Linear(Vector initialValue, double ratio) {
			super(initialValue, ratio);
		}

		public Linear(double ratio) {
			super(ratio);
		}

		@Override
		public Vector interpolate(Vector value, Vector target) {
			return value.add(target.subtract(value).scale(ratio()));
		}

		@Override
		public boolean isCompleted() {
			return value() != null && target() != null && (Objects.requireNonNull(value()).distanceTo(target()) <= Theory.EPSILON);
		}
	}

	public static class Polar extends Interpolation<Vector> {
		public Polar(Vector initialValue, double ratio) {
			super(initialValue, ratio);
		}

		public Polar(double ratio) {
			super(ratio);
		}

		@Override
		public Vector interpolate(Vector value, Vector target) {
			return value.add(target.subtract(value).scale(ratio()));
		}

		@Override
		public boolean isCompleted() {
			return value() != null && target() != null && (Objects.requireNonNull(value()).distanceTo(target()) <= Theory.EPSILON);
		}
	}
}
