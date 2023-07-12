package net.krlite.equator.visual.animation.interpolated;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.visual.animation.base.Interpolation;

import java.util.Objects;

public class InterpolatedBox extends Interpolation<Box> {
	public InterpolatedBox(Box initial, double ratio) {
		super(initial, ratio);
	}

	public InterpolatedBox(double ratio) {
		super(Box.ZERO, ratio);
	}

	@Override
	public Box interpolate(Box value, Box target) {
		return value.interpolate(target, ratio());
	}

	@Override
	public boolean isCompleted() {
		return value().origin().distanceTo(target().origin()) <= Theory.EPSILON && value().size().distanceTo(target().size()) <= Theory.EPSILON;
	}
}
