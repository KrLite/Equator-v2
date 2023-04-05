package net.krlite.equator.visual.animation;

import jdk.jfr.Label;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

@Label("Animation 2.1.3")
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

		interface StartFrame {
			Event<StartFrame> EVENT = EventFactory.createArrayBacked(StartFrame.class, (listeners) -> (animation) -> {
				for (StartFrame listener : listeners) {
					listener.onFrameStart(animation);
				}
			});

			void onFrameStart(Animation animation);
		}

		interface EndFrame {
			Event<EndFrame> EVENT = EventFactory.createArrayBacked(EndFrame.class, (listeners) -> (animation) -> {
				for (EndFrame listener : listeners) {
					listener.onFrameEnd(animation);
				}
			});

			void onFrameEnd(Animation animation);
		}
	}

	public Animation(double startValue, double endValue, long duration, TimeUnit timeUnit, Slice slice, boolean repeat) {
		this.startValue = startValue;
		this.endValue = endValue;
		this.duration = duration;
		this.progress = new AtomicLong(0);
		this.timeUnit = timeUnit;
		this.slice = new AtomicReference<>(slice);
		this.repeat = new AtomicBoolean(repeat);
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
	private final AtomicReference<Slice> slice;
	private final AtomicBoolean repeat;
	private final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>(null);
	private final Executor executor = Executors.newSingleThreadScheduledExecutor();

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
		return slice().apply(startValue(), endValue(), percentage());
	}

	public double valueAsPercentage() {
		return slice().apply(0, 1, percentage());
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

	private ScheduledFuture<?> future() {
		return future.get();
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
		progress.set(duration() - progress.get());
	}

	private void future(@Nullable ScheduledFuture<?> future) {
		this.future.set(future);
	}

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		Callbacks.StartFrame.EVENT.invoker().onFrameStart(this);

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
			progress.addAndGet(period());
		}

		Callbacks.EndFrame.EVENT.invoker().onFrameEnd(this);
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

	public void swichPauseResume() {
		if (isPaused()) resume();
		else pause();
	}

	public void stop() {
		pause();
		future(null);
	}

	public void reset() {
		progress.set(0);
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

	public boolean isCompleted() {
		return progress.get() >= duration();
	}
}
