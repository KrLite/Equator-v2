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

	public Interpolation(double originValue, double targetValue, double speed) {
		this.originValue = originValue;
		this.targetValue = new AtomicDouble(targetValue);
		this.value = new AtomicDouble(originValue);
		this.speed = Theory.clamp(speed, 0, 1);
	}

	private final double originValue, speed;
	private final AtomicDouble value, targetValue;
	private final AtomicBoolean started = new AtomicBoolean(false), completed = new AtomicBoolean(false);
	private ScheduledFuture<?> future;

	public double originValue() {
		return originValue;
	}

	public double targetValue() {
		return targetValue.get();
	}

	public double value() {
		return value.get();
	}

	public double speed() {
		return speed;
	}

	public void targetValue(double targetValue) {
		this.targetValue.set(targetValue);
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
		else
			value.set(Theory.clamp(value() + speed() * (targetValue() - originValue()), originValue(), targetValue()));
	}

	public void start() {
		if (isStopped()) {
			reset();
			InterpolationCallbacks.Start.EVENT.invoker().onStart(this);
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
		value.set(originValue());
	}

	public void restart() {
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
		return Theory.looseEquals(value(), targetValue());
	}
}
