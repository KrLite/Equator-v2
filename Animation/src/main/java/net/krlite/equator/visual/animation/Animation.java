package net.krlite.equator.visual.animation;

import com.google.common.util.concurrent.AtomicDouble;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.math.algebra.Curves;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * <h1>Animation</h1>
 * Handles the animation between two values.
 */
@net.krlite.equator.base.Animation("2.3.0")
public class Animation implements Runnable {
	public interface Callbacks {
		interface Start {
			Event<Start> EVENT = EventFactory.createArrayBacked(Start.class, (listeners) -> (animation) -> {
				for (Start listener : listeners) {
					listener.onStart(animation);
				}
			});

			void onStart(Animation animation);
		}

		interface Complete {
			Event<Complete> EVENT = EventFactory.createArrayBacked(Complete.class, (listeners) -> (animation) -> {
				for (Complete listener : listeners) {
					listener.onCompletion(animation);
				}
			});

			void onCompletion(Animation animation);
		}

		interface Repeat {
			Event<Repeat> EVENT = EventFactory.createArrayBacked(Repeat.class, (listeners) -> (animation) -> {
				for (Repeat listener : listeners) {
					listener.onRepetition(animation);
				}
			});

			void onRepetition(Animation animation);
		}

		interface FrameStart {
			Event<FrameStart> EVENT = EventFactory.createArrayBacked(FrameStart.class, (listeners) -> (animation) -> {
				for (FrameStart listener : listeners) {
					listener.onFrameStart(animation);
				}
			});

			void onFrameStart(Animation animation);
		}

		interface FrameEnd {
			Event<FrameEnd> EVENT = EventFactory.createArrayBacked(FrameEnd.class, (listeners) -> (animation) -> {
				for (FrameEnd listener : listeners) {
					listener.onFrameEnd(animation);
				}
			});

			void onFrameEnd(Animation animation);
		}
	}

	// Constructors

	public Animation(double startValue, double endValue, double speed, long duration, TimeUnit timeUnit, Slice slice, boolean repeat) {
		this.startValue = new AtomicDouble(startValue);
		this.endValue = new AtomicDouble(endValue);
		this.current = new AtomicDouble(0);
		this.speed = new AtomicDouble(Math.max(0, speed));
		this.duration = duration;
		this.timeUnit = timeUnit;
		this.slice = new AtomicReference<>(slice);
		this.repeat = new AtomicBoolean(repeat);
	}

	public Animation(double startValue, double endValue, long duration, TimeUnit timeUnit, Slice slice) {
		this(startValue, endValue, 1, duration, timeUnit, slice, false);
	}

	public Animation(double startValue, double endValue, long duration, Slice slice) {
		this(startValue, endValue, duration, TimeUnit.MILLISECONDS, slice);
	}

	protected Animation(Animation parent) {
		this.startValue = new AtomicDouble(parent.startValue.get());
		this.endValue = new AtomicDouble(parent.endValue.get());
		this.current = new AtomicDouble(parent.current.get());
		this.speed = new AtomicDouble(parent.speed.get());
		this.duration = parent.duration();
		this.timeUnit = parent.timeUnit();
		this.slice = new AtomicReference<>(parent.slice.get());
		this.repeat = new AtomicBoolean(parent.repeat.get());
		this.future.set(parent.future.get());
	}

	// Fields

	private final AtomicDouble startValue, endValue, current, speed;
	private final long duration;
	private final TimeUnit timeUnit;
	private final AtomicReference<Slice> slice;
	private final AtomicBoolean repeat;
	private final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>(null);

	// Accessors

	public double startValue() {
		return startValue.get();
	}

	public double endValue() {
		return endValue.get();
	}

	public double progress() {
		return current.get() / duration();
	}

	public double speed() {
		return speed.get();
	}

	public long duration() {
		return duration;
	}

	public TimeUnit timeUnit() {
		return timeUnit;
	}

	public Slice slice() {
		return slice.get();
	}

	public boolean repeat() {
		return repeat.get();
	}

	public long period() {
		return timeUnit().toMillis(1);
	}

	public double value() {
		return slice().apply(startValue(), endValue(), progress());
	}

	public double percentage() {
		return Curves.LINEAR.apply(0, 1, progress());
	}

	private double accumulation() {
		return period() * speed();
	}

	private ScheduledFuture<?> future() {
		return future.get();
	}

	// Mutators

	public void startValue(double startValue) {
		this.startValue.set(startValue);
	}

	public void endValue(double endValue) {
		this.endValue.set(endValue);
	}

	public void speed(double speed) {
		this.speed.set(speed);
	}

	public void defaultSpeed() {
		this.speed.set(1);
	}

	public void slice(Slice slice) {
		this.slice.set(slice);
	}

	public void slice(UnaryOperator<Slice> slice) {
		this.slice.set(slice.apply(slice()));
	}

	public void enableRepeat() {
		this.repeat.set(true);
	}

	public void disableRepeat() {
		this.repeat.set(false);
	}

	public void toggleRepeat() {
		this.repeat.set(!repeat());
	}

	public void reverseProgress() {
		current.set(duration() - current.get());
	}

	private void future(@Nullable ScheduledFuture<?> future) {
		this.future.set(future);
	}

	// Interface Implementations

	@Override
	public void run() {
		Callbacks.FrameStart.EVENT.invoker().onFrameStart(this);

		if (isCompleted()) {
			if (repeat()) {
				reset();
				Callbacks.Repeat.EVENT.invoker().onRepetition(this);
			}
			else {
				stop();
				Callbacks.Complete.EVENT.invoker().onCompletion(this);
			}
		} else {
			current.set(Math.min(duration(), current.addAndGet(accumulation())));
		}

		Callbacks.FrameEnd.EVENT.invoker().onFrameEnd(this);
	}

	// Functions

	public boolean passingRatio(double at) {
		if (at < 0 || at > 1) return false;

		double value = at * duration();
		return current.get() - accumulation() < value && current.get() >= value;
	}

	public boolean passingValue(double at) {
		double last = slice().apply(startValue(), endValue(), (current.get() - accumulation()) / duration());
		return (last < at && at <= value()) || (last > at && at >= value());
	}

	public Animation copy() {
		return new Animation(this);
	}

	public void start() {
		if (isStopped()) {
			reset();
			Callbacks.Start.EVENT.invoker().onStart(this);
			future(AnimationThreadPoolExecutor.join(this, 0));
		}
	}

	public void pause() {
		if (isRunning()) {
			assert future() != null;
			future().cancel(true);
		}
	}

	public void resume() {
		if (isPaused()) future(AnimationThreadPoolExecutor.join(this, 0));
	}

	public void switchPauseResume() {
		if (isPaused()) resume();
		else pause();
	}

	public void stop() {
		pause();
		future(null);
	}

	public void reset() {
		current.set(0);
	}

	public void restart() {
		if (isRunning()) {
			Callbacks.Start.EVENT.invoker().onStart(this);
		}
		stop();
		start();
	}

	public boolean isRunning() {
		return future() != null && !future().isCancelled();
	}

	public boolean isPaused() {
		return future() != null && future().isCancelled();
	}

	public boolean isStopped() {
		return future() == null;
	}

	public boolean isNotStarted() {
		return current.get() <= 0;
	}

	public boolean isCompleted() {
		return current.get() >= duration();
	}

	public void onStart(Runnable runnable) {
		Callbacks.Start.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onCompletion(Runnable runnable) {
		Callbacks.Complete.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onRepetition(Runnable runnable) {
		Callbacks.Repeat.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onFrameStart(Runnable runnable) {
		Callbacks.FrameStart.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onFrameEnd(Runnable runnable) {
		Callbacks.FrameEnd.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onFrameStartAtRatio(double at, Runnable runnable) {
		onFrameStart(() -> {
			if (passingRatio(at)) runnable.run();
		});
	}

	public void onFrameEndAtRatio(double at, Runnable runnable) {
		onFrameEnd(() -> {
			if (passingRatio(at)) runnable.run();
		});
	}

	public void onFrameStartAtValue(double at, Runnable runnable) {
		onFrameStart(() -> {
			if (passingValue(at)) runnable.run();
		});
	}

	public void onFrameEndAtValue(double at, Runnable runnable) {
		onFrameEnd(() -> {
			if (passingValue(at)) runnable.run();
		});
	}
}
