package net.krlite.equator.visual.animation.interpolated;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.visual.animation.base.Interpolation;

import java.util.Objects;

public class InterpolatedBox extends Interpolation<Box> {
	public InterpolatedBox(Box initialValue, double ratio) {
		super(initialValue, ratio);
	}

	public InterpolatedBox(double ratio) {
		super(ratio);
	}

	@Override
	public Box interpolate(Box value, Box target) {
		return new Box(
				new InterpolatedVector.Linear(ratio()).interpolate(value.origin(), target.origin()),
				new InterpolatedVector.Linear(ratio()).interpolate(value.size(), target.size())
		);
	}

	@Override
	public boolean isCompleted() {
		return value() != null && target() != null
					   && (Objects.requireNonNull(value()).origin().distanceTo(Objects.requireNonNull(target()).origin()) <= Theory.EPSILON)
					   && (Objects.requireNonNull(value()).size().distanceTo(Objects.requireNonNull(target()).size()) <= Theory.EPSILON);
	}
}
