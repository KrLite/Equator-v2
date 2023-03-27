package net.krlite.equator.visual.animation;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

public class Animation implements Runnable {
	public interface AnimationCallbacks {
		interface Start {
			Event<AnimationCallbacks.Start> EVENT = EventFactory.createArrayBacked(AnimationCallbacks.Start.class, (listeners) -> (animation) -> {
				for (AnimationCallbacks.Start listener : listeners) {
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
	}

	private Animation(double startValue, double endValue, long duration, AtomicLong progress, TimeUnit timeUnit, Slice slice, boolean repeat, @Nullable ScheduledFuture<?> future) {
		this.startValue = startValue;
		this.endValue = endValue;
		this.duration = duration;
		this.progress = progress;
		this.timeUnit = timeUnit;
		this.slice = slice;
		this.repeat = repeat;
		this.future = future;
	}

	public Animation(double startValue, double endValue, long duration, TimeUnit timeUnit, Slice slice, boolean repeat) {
		this.startValue = startValue;
		this.endValue = endValue;
		this.duration = duration;
		this.progress = new AtomicLong(0);
		this.timeUnit = timeUnit;
		this.slice = slice;
		this.repeat = repeat;
	}

	public Animation(double startValue, double endValue, long duration, TimeUnit timeUnit, Slice slice) {
		this(startValue, endValue, duration, timeUnit, slice, false);
	}

	public Animation(double startValue, double endValue, long duration, Slice slice) {
		this(startValue, endValue, duration, TimeUnit.MILLISECONDS, slice);
	}

	private final double startValue, endValue;
	private final long duration;
	private final AtomicLong progress;
	private final TimeUnit timeUnit;
	private final Slice slice;
	private final boolean repeat;
	@Nullable
	private ScheduledFuture<?> future;

	public double startValue() {
		return startValue;
	}

	public double endValue() {
		return endValue;
	}

	public long duration() {
		return duration;
	}

	public TimeUnit timeUnit() {
		return timeUnit;
	}

	public double percentage() {
		return progress.get() / (double) duration;
	}

	public double value() {
		return slice.apply(startValue(), endValue(), percentage());
	}

	public double valueAsPercentage() {
		return slice.apply(0, 1, percentage());
	}

	public Slice slice() {
		return slice;
	}

	public boolean repeat() {
		return repeat;
	}

	public long period() {
		return timeUnit.toMillis(1);
	}

	public Animation slice(Slice slice) {
		return new Animation(startValue, endValue, duration, progress, timeUnit, slice, repeat, future);
	}

	public Animation slice(UnaryOperator<Slice> slice) {
		return new Animation(startValue, endValue, duration, progress, timeUnit, slice.apply(this.slice), repeat, future);
	}

	public Animation enableRepeat() {
		return new Animation(startValue, endValue, duration, progress, timeUnit, slice, true, future);
	}

	public Animation disableRepeat() {
		return new Animation(startValue, endValue, duration, progress, timeUnit, slice, false, future);
	}

	public Animation toggleRepeat() {
		return new Animation(startValue, endValue, duration, progress, timeUnit, slice, !repeat, future);
	}

	public Animation reverseProgress() {
		return new Animation(startValue, endValue, duration, new AtomicLong(duration - progress.get()), timeUnit, slice, repeat, future);
	}

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		if (isCompleted()) {
			if (repeat()) {
				reset();
				AnimationCallbacks.Repeat.EVENT.invoker().onRepetition(this);
			}
			else {
				stop();
				AnimationCallbacks.Complete.EVENT.invoker().onCompletion(this);
			}
		} else
			progress.addAndGet(period());
	}

	public void start() {
		if (isStopped()) {
			reset();
			AnimationCallbacks.Start.EVENT.invoker().onStart(this);
			future = AnimationThreadPoolExecutor.join(this, 0);
		}
	}

	public void pause() {
		if (isRunning()) {
			assert future != null;
			future.cancel(true);
		}
	}

	public void resume() {
		if (isRunning())
			future = AnimationThreadPoolExecutor.join(this, 0);
	}

	public void stop() {
		pause();
		future = null;
	}

	public void reset() {
		progress.set(0);
	}

	public void restart() {
		if (isRunning())
			AnimationCallbacks.Start.EVENT.invoker().onStart(this);
		stop();
		start();
	}

	public boolean isRunning() {
		return future != null && !future.isCancelled();
	}

	public boolean isPaused() {
		return future != null && future.isCancelled();
	}

	public boolean isStopped() {
		return future == null;
	}

	public boolean isCompleted() {
		return progress.get() >= duration();
	}
}
