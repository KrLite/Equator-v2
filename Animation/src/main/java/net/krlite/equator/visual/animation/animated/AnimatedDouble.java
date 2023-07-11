package net.krlite.equator.visual.animation.animated;

import net.krlite.equator.visual.animation.Slice;
import net.krlite.equator.visual.animation.base.Animation;

import java.util.concurrent.TimeUnit;

public class AnimatedDouble extends Animation<Double> {
	public AnimatedDouble(double start, double end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice) {
		super(start, end, speed, duration, timeUnit, sensitive, slice);
	}

	public AnimatedDouble(double start, double end, long duration, Slice slice) {
		super(start, end, duration, slice);
	}

	@Override
	public Double value(double progress) {
		return slice().apply(start(), end(), progress);
	}

	@Override
	public Double valueClamped(double progress) {
		return slice().applyClamped(end(), start(), progress);
	}
}
