package net.krlite.equator.visual.animation;

import com.google.common.util.concurrent.AtomicDouble;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.math.algebra.Theory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Interpolation implements Runnable {
	public interface InterpolationCallbacks {
		interface Start {
			Event<Start> EVENT = EventFactory.createArrayBacked(Start.class, (listeners) -> (interpolation) -> {
				for (Start listener : listeners) {
					listener.onStart(interpolation);
				}
			});

			void onStart(Interpolation interpolation);
		}

		interface Complete {
			Event<Complete> EVENT = EventFactory.createArrayBacked(Complete.class, (listeners) -> (interpolation) -> {
				for (Complete listener : listeners) {
					listener.onCompletion(interpolation);
				}
			});

			void onCompletion(Interpolation interpolation);
		}
	}

	public Interpolation(double originValue, double targetValue, double approximatedTimeSteps, boolean pauseAtStart) {
		this.targetValue = new AtomicDouble(targetValue);
		this.value = new AtomicDouble(originValue);
		this.speed = new AtomicDouble(Theory.clamp(1 / approximatedTimeSteps, 0, 1));
		start();
		if (pauseAtStart) {
			pause();
			reset(originValue);
		}
	}

	public Interpolation(double originValue, double targetValue, double approximatedTimeSteps) {
		this(originValue, targetValue, approximatedTimeSteps, false);
	}

	public Interpolation(double originValue, double targetValue, boolean pauseAtStart) {
		this(originValue, targetValue, 35, pauseAtStart);
	}

	public Interpolation(double originValue, double targetValue) {
		this(originValue, targetValue, false);
	}

	private final AtomicDouble value, targetValue, speed;
	private final AtomicBoolean started = new AtomicBoolean(false), completed = new AtomicBoolean(false);
	private ScheduledFuture<?> future;

	public double targetValue() {
		return targetValue.get();
	}

	public double value() {
		return value.get();
	}

	public double speed() {
		return speed.get();
	}

	public double approximatedTimeSteps() {
		return 1 / speed();
	}

	public void targetValue(double targetValue) {
		this.targetValue.set(targetValue);
	}

	public void speed(double speed) {
		this.speed.set(Theory.clamp(speed, 0, 1));
	}

	public void approximatedTimeSteps(double approximatedTimeSteps) {
		speed(1 / approximatedTimeSteps);
	}

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		if (!isCompleted() && !started.getAndSet(true)) {
			InterpolationCallbacks.Start.EVENT.invoker().onStart(this);
			completed.set(false);
		}
		else if (isCompleted() && !completed.getAndSet(true)) {
			InterpolationCallbacks.Complete.EVENT.invoker().onCompletion(this);
			started.set(false);
		}
		else value.accumulateAndGet(targetValue(), (current, target) -> Theory.lerp(current, target, speed()));
	}

	private void start() {
		InterpolationCallbacks.Start.EVENT.invoker().onStart(this);
		future = AnimationThreadPoolExecutor.join(this, 0);
	}

	public void pause() {
		if (isRunning()) {
			assert future != null;
			future.cancel(true);
		}
	}

	public void resume() {
		if (isPaused()) {
			future = AnimationThreadPoolExecutor.join(this, 0);
		}
	}

	public void switchPauseResume() {
		if (isPaused()) {
			resume();
		}
		else {
			pause();
		}
	}

	public void reset(double originValue) {
		value.set(originValue);
	}

	public boolean isRunning() {
		return future != null && !future.isCancelled();
	}

	public boolean isPaused() {
		return future != null && future.isCancelled();
	}

	public boolean isCompleted() {
		return Theory.looseEquals(value(), targetValue());
	}
}
