package net.krlite.equator.visual.animation.interpolated;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.animation.base.Interpolation;

public class InterpolatedDouble extends Interpolation<Double> {
	public InterpolatedDouble(double initialValue, double ratio) {
		super(initialValue, ratio);
	}

	public InterpolatedDouble(double ratio) {
		super(ratio);
	}

	@Override
	public Double interpolate(Double value, Double target) {
		return Theory.lerp(value, target, ratio());
	}

	@Override
	public boolean isCompleted() {
		return false;
	}
}
