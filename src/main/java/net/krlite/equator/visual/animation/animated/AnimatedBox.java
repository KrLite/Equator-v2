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
		return new Box(
				new AnimatedVector.Linear(start().origin(), end().origin(), 0, slice()).value(progress),
				new AnimatedVector.Linear(start().size(), end().size(), 0, slice()).value(progress)
		);
	}

	@Override
	public Box valueClamped(double progress) {
		return new Box(
				new AnimatedVector.Linear(start().origin(), end().origin(), 0, slice()).valueClamped(progress),
				new AnimatedVector.Linear(start().size(), end().size(), 0, slice()).valueClamped(progress)
		);
	}
}
