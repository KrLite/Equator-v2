package net.krlite.equator.visual.animation.animated;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.visual.animation.Slice;
import net.krlite.equator.visual.animation.base.Animation;

import java.util.concurrent.TimeUnit;

public class AnimatedBox extends Animation<Box> {
	public AnimatedBox(Box start, Box end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice) {
		super(start, end, speed, duration, timeUnit, sensitive, slice);
	}

	public AnimatedBox(Box start, Box end, long duration, Slice slice) {
		super(start, end, duration, slice);
	}

	@Override
	public Box value(double progress) {
		return start().interpolate(end(), slice().apply(0, 1, progress));
	}

	@Override
	public Box valueClamped(double progress) {
		return start().interpolate(end(), slice().applyClamped(0, 1, progress));
	}
}
