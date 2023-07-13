package net.krlite.equator.visual.animation.interpolated;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.volume.Pos;
import net.krlite.equator.visual.animation.base.Interpolation;

public class InterpolatedPos {
	public static class Linear extends Interpolation<Pos> {
		public Linear(Pos initial, double ratio) {
			super(initial, ratio);
		}

		public Linear(double ratio) {
			super(Pos.ZERO, ratio);
		}

		@Override
		public boolean isCompleted() {
			return value().distanceTo(target()) <= Theory.EPSILON;
		}

		@Override
		public Pos interpolate(Pos value, Pos target) {
			return value.interpolate(target, ratio());
		}
	}

	public static class Spherical extends Interpolation<Pos> {
		public Spherical(Pos initial, double ratio) {
			super(initial, ratio);
		}

		public Spherical(double ratio) {
			super(Pos.ZERO, ratio);
		}

		@Override
		public boolean isCompleted() {
			return value().distanceTo(target()) <= Theory.EPSILON;
		}

		@Override
		public Pos interpolate(Pos value, Pos target) {
			return value.sphericalInterpolate(target, ratio());
		}
	}
}
