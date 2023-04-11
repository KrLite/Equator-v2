package net.krlite.equator.visual.animation;

import com.google.common.util.concurrent.AtomicDouble;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.math.algebra.Theory;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@net.krlite.equator.base.Animation("2.2.1")
public class Interpolation implements Runnable {
	public interface Callbacks {
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

		interface StartFrame {
			Event<StartFrame> EVENT = EventFactory.createArrayBacked(StartFrame.class, (listeners) -> (interpolation) -> {
				for (StartFrame listener : listeners) {
					listener.onFrameStart(interpolation);
				}
			});

			void onFrameStart(Interpolation interpolation);
		}

		interface EndFrame {
			Event<EndFrame> EVENT = EventFactory.createArrayBacked(EndFrame.class, (listeners) -> (interpolation) -> {
				for (EndFrame listener : listeners) {
					listener.onFrameEnd(interpolation);
				}
			});

			void onFrameEnd(Interpolation interpolation);
		}
	}

	public Interpolation(double value, double originValue, double targetValue, double approximatedDuration, boolean pauseAtStart) {
		this.value = new AtomicDouble(value);
		this.originValue = new AtomicDouble(originValue);
		this.targetValue = new AtomicDouble(targetValue);
		this.speed = new AtomicDouble(Theory.clamp(1 / approximatedDuration, 0, 1));

		start(pauseAtStart);
	}

	public Interpolation(double value, double originValue, double targetValue, double approximatedDuration) {
		this(value, originValue, targetValue, approximatedDuration, false);
	}

	public Interpolation(double originValue, double targetValue, double approximatedDuration, boolean pauseAtStart) {
		this(originValue, originValue, targetValue, approximatedDuration, pauseAtStart);
	}

	public Interpolation(double originValue, double targetValue, double approximatedDuration) {
		this(originValue, targetValue, approximatedDuration, false);
	}

	public Interpolation(double originValue, double targetValue, boolean pauseAtStart) {
		this(originValue, targetValue, 35, pauseAtStart);
	}

	public Interpolation(double originValue, double targetValue) {
		this(originValue, targetValue, false);
	}
	
	protected Interpolation(Interpolation parent) {
		this.value = new AtomicDouble(parent.value.get());
		this.originValue = new AtomicDouble(parent.originValue.get());
		this.targetValue = new AtomicDouble(parent.targetValue.get());
		this.speed = new AtomicDouble(parent.speed.get());
		this.started.set(parent.started.get());
		this.completed.set(parent.completed.get());
		this.future.set(parent.future.get());
	}

	private final AtomicDouble value, originValue, targetValue, speed;
	private final AtomicBoolean started = new AtomicBoolean(false), completed = new AtomicBoolean(false);
	private final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>(null);

	public Interpolation copy() {
		return new Interpolation(this);
	}

	public double value() {
		return value.get();
	}

	public double percentage() {
		return Theory.clamp((value() - originValue()) / (targetValue() - originValue()), 0, 1);
	}

	public double originValue() {
		return originValue.get();
	}

	public double targetValue() {
		return targetValue.get();
	}

	public double speed() {
		return speed.get();
	}

	public double approximatedTimeSteps() {
		return 1 / speed();
	}

	private ScheduledFuture<?> future() {
		return future.get();
	}

	public void originValue(double originValue) {
		this.originValue.set(originValue);
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

	private void future(@Nullable ScheduledFuture<?> future) {
		this.future.set(future);
	}

	public void reverse() {
		double originValue = originValue();
		originValue(targetValue());
		targetValue(originValue);
	}

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		Callbacks.StartFrame.EVENT.invoker().onFrameStart(this);

		if (!isCompleted() && !started.getAndSet(true)) {
			Callbacks.Start.EVENT.invoker().onStart(this);
			completed.set(false);
		}
		else if (isCompleted() && !completed.getAndSet(true)) {
			Callbacks.Complete.EVENT.invoker().onCompletion(this);
			started.set(false);
		}
		else {
			value.accumulateAndGet(targetValue(), (current, target) -> Theory.lerp(current, target, speed()));
		}

		Callbacks.EndFrame.EVENT.invoker().onFrameEnd(this);
	}

	private void start(boolean pauseAtStart) {
		Callbacks.Start.EVENT.invoker().onStart(this);
		future(AnimationThreadPoolExecutor.join(this, 0));
		if (pauseAtStart) {
			future().cancel(true);
		}
	}

	public void pause() {
		if (isRunning()) {
			assert future() != null;
			future().cancel(true);
		}
	}

	public void resume() {
		if (isPaused()) {
			future(AnimationThreadPoolExecutor.join(this, 0));
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

	public void reset() {
		value.set(originValue());
	}

	public boolean isRunning() {
		return future() != null && !future().isCancelled();
	}

	public boolean isPaused() {
		return future() != null && future().isCancelled();
	}

	public boolean isCompleted() {
		return Theory.looseEquals(value(), targetValue());
	}
}
