package net.krlite.equator.visual.animation;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.animation.base.Animation;

public class ValueAnimation extends Animation<Double> {
	public ValueAnimation(double startValue, double endValue, long duration, Slice slice) {
		super(startValue, endValue, duration, slice);
	}

	@Override
	public Double value() {
		return slice().apply(start(), end(), progress());
	}

	@Override
	public Double valueClamped() {
		return slice().applyClamped(start(), end(), progress());
	}

	public boolean isPassingValue(double value) {
		return Theory.looseBetween(value, slice().apply(start(), end(), progress() - accumulation()), value());
	}
}
