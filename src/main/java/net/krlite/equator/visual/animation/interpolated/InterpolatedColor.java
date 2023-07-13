package net.krlite.equator.visual.animation.interpolated;

import net.krlite.equator.visual.animation.base.Interpolation;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.base.ColorStandard;

public class InterpolatedColor extends Interpolation<AccurateColor> {
	public InterpolatedColor(AccurateColor initial, double ratio, ColorStandard.MixMode mixMode) {
		super(initial, ratio);
		this.mixMode = mixMode;
	}

	public InterpolatedColor(AccurateColor initial, double ratio) {
		this(initial, ratio, ColorStandard.MixMode.BLEND);
	}

	private ColorStandard.MixMode mixMode;

	public ColorStandard.MixMode mixMode() {
		return mixMode;
	}

	public void mixMode(ColorStandard.MixMode mixMode) {
		this.mixMode = mixMode;
	}

	@Override
	public boolean isCompleted() {
		return value().approximates(target());
	}

	@Override
	public AccurateColor interpolate(AccurateColor value, AccurateColor target) {
		return value.mix(target, ratio(), mixMode());
	}
}
