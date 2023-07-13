package net.krlite.equator.visual.animation.animated;

import net.krlite.equator.visual.animation.Slice;
import net.krlite.equator.visual.animation.base.Animation;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.base.ColorStandard;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class AnimatedColor extends Animation<AccurateColor> {
	public AnimatedColor(@Nullable AccurateColor start, @Nullable AccurateColor end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice, ColorStandard.MixMode mixMode) {
		super(AccurateColor.notnull(start), AccurateColor.notnull(end), speed, duration, timeUnit, sensitive, slice);
		this.mixMode = mixMode;
	}

	public AnimatedColor(@Nullable AccurateColor start, @Nullable AccurateColor end, double speed, long duration, TimeUnit timeUnit, boolean sensitive, Slice slice) {
		this(AccurateColor.notnull(start), AccurateColor.notnull(end), speed, duration, timeUnit, sensitive, slice, ColorStandard.MixMode.BLEND);
	}

	public AnimatedColor(@Nullable AccurateColor start, @Nullable AccurateColor end, long duration, Slice slice, ColorStandard.MixMode mixMode) {
		super(AccurateColor.notnull(start), AccurateColor.notnull(end), duration, slice);
		this.mixMode = mixMode;
	}

	public AnimatedColor(@Nullable AccurateColor start, @Nullable AccurateColor end, long duration, Slice slice) {
		this(AccurateColor.notnull(start), AccurateColor.notnull(end), duration, slice, ColorStandard.MixMode.BLEND);
	}

	private ColorStandard.MixMode mixMode;

	public ColorStandard.MixMode mixMode() {
		return mixMode;
	}

	public void mixMode(ColorStandard.MixMode mixMode) {
		this.mixMode = mixMode;
	}

	@Override
	public AccurateColor value(double progress) {
		return valueClamped(progress);
	}

	@Override
	public AccurateColor valueClamped(double progress) {
		return start().mix(end(), slice().applyClamped(0, 1, progress), mixMode());
	}
}
