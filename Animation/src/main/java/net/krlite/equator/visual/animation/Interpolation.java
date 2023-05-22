package net.krlite.equator.visual.animation;

import com.google.common.util.concurrent.AtomicDouble;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.base.SelfMutable;
import net.krlite.equator.math.algebra.Theory;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <h1>Interpolation</h1>
 * Handles the interpolation between two values.
 */
@SelfMutable
@net.krlite.equator.base.Animation("2.4.2")
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

		interface FrameStart {
			Event<FrameStart> EVENT = EventFactory.createArrayBacked(FrameStart.class, (listeners) -> (interpolation) -> {
				for (FrameStart listener : listeners) {
					listener.onFrameStart(interpolation);
				}
			});

			void onFrameStart(Interpolation interpolation);
		}

		interface FrameEnd {
			Event<FrameEnd> EVENT = EventFactory.createArrayBacked(FrameEnd.class, (listeners) -> (interpolation) -> {
				for (FrameEnd listener : listeners) {
					listener.onFrameEnd(interpolation);
				}
			});

			void onFrameEnd(Interpolation interpolation);
		}
	}

	// Constructors

	public Interpolation(double value, double originValue, double targetValue, double approximatedDuration, boolean pauseAtStart) {
		this.value = new AtomicDouble(value);
		this.lastValue = new AtomicDouble(value);
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
		this.lastValue = new AtomicDouble(parent.lastValue.get());
		this.originValue = new AtomicDouble(parent.originValue.get());
		this.targetValue = new AtomicDouble(parent.targetValue.get());
		this.speed = new AtomicDouble(parent.speed.get());
		this.started.set(parent.started.get());
		this.completed.set(parent.completed.get());
		this.future.set(parent.future.get());
	}

	// Fields

	private final AtomicDouble value, lastValue, originValue, targetValue, speed;
	private final AtomicBoolean started = new AtomicBoolean(false), completed = new AtomicBoolean(false);
	private final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>(null);

	// Accessors

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

	// Mutators

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

	// Interface Implementations

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		Callbacks.FrameStart.EVENT.invoker().onFrameStart(this);

		if (!isCompleted() && !started.getAndSet(true)) {
			Callbacks.Start.EVENT.invoker().onStart(this);
			completed.set(false);
		}
		else if (isCompleted() && !completed.getAndSet(true)) {
			Callbacks.Complete.EVENT.invoker().onCompletion(this);
			started.set(false);
		}
		else {
			lastValue.set(value.get());
			value.set(Theory.lerp(value.get(), targetValue(), speed()));
		}

		Callbacks.FrameEnd.EVENT.invoker().onFrameEnd(this);
	}

	// Functions

	public boolean passing(double value) {
		return (lastValue.get() < value && value <= value()) || (lastValue.get() > value && value >= value());
	}

	public Interpolation copy() {
		return new Interpolation(this);
	}

	public void reverse() {
		double originValue = originValue();
		originValue(targetValue());
		targetValue(originValue);
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

	public void onStart(Runnable runnable) {
		Callbacks.Start.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}

	public void onCompletion(Runnable runnable) {
		Callbacks.Complete.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}

	public void onFrameStart(Runnable runnable) {
		Callbacks.FrameStart.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}

	public void onFrameEnd(Runnable runnable) {
		Callbacks.FrameEnd.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}

	public void onFrameStartAt(double at, Runnable runnable) {
		onFrameStart(() -> {
			if (passing(at)) runnable.run();
		});
	}

	public void onFrameEndAt(double at, Runnable runnable) {
		onFrameEnd(() -> {
			if (passing(at)) runnable.run();
		});
	}
}
