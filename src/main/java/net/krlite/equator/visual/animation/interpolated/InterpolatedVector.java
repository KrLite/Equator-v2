package net.krlite.equator.visual.animation.interpolated;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.visual.animation.base.Interpolation;

import java.util.Objects;

public class InterpolatedVector {
	public static class Linear extends Interpolation<Vector> {
		public Linear(Vector initial, double ratio) {
			super(initial, ratio);
		}

		public Linear(double ratio) {
			super(Vector.ZERO, ratio);
		}

		@Override
		public boolean isCompleted() {
			return value().distanceTo(target()) <= Theory.EPSILON;
		}

		@Override
		public Vector interpolate(Vector value, Vector target) {
			return value.interpolate(target, ratio());
		}
	}

	public static class Spherical extends Interpolation<Vector> {
		public Spherical(Vector initial, double ratio) {
			super(initial, ratio);
		}

		public Spherical(double ratio) {
			super(Vector.ZERO, ratio);
		}

		@Override
		public boolean isCompleted() {
			return value().distanceTo(target()) <= Theory.EPSILON;
		}

		@Override
		public Vector interpolate(Vector value, Vector target) {
			return value.sphericalInterpolate(target, ratio());
		}
	}
}
