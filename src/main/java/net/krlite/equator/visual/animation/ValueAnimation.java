package net.krlite.equator.visual.animation;

import net.krlite.equator.visual.animation.base.Animation;

public class ValueAnimation extends Animation<Double> {
	public ValueAnimation(double startValue, double endValue, long duration, Slice slice) {
		super(startValue, endValue, duration, slice);
	}

	@Override
	public Double value() {
		return slice().apply(startValue(), endValue(), progress());
	}

	@Override
	public Double valueClamped() {
		return slice().applyClamped(startValue(), endValue(), progress());
	}
}
