package net.krlite.equator.visual.animation.base;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * <h1>Interpolation</h1>
 * Handles the interpolation between two values.
 */
public abstract class Interpolation<I> implements Runnable {
	public interface Callbacks {
		interface Complete {
			Event<Complete> EVENT = EventFactory.createArrayBacked(Complete.class, (listeners) -> (interpolation) -> {
				for (Complete listener : listeners) {
					listener.onCompletion(interpolation);
				}
			});

			void onCompletion(Interpolation<?> interpolation);
		}

		interface Pause {
			Event<Pause> EVENT = EventFactory.createArrayBacked(Pause.class, (listeners) -> (interpolation) -> {
				for (Pause listener : listeners) {
					listener.onPause(interpolation);
				}
			});

			void onPause(Interpolation<?> interpolation);
		}

		interface Resume {
			Event<Resume> EVENT = EventFactory.createArrayBacked(Resume.class, (listeners) -> (interpolation) -> {
				for (Resume listener : listeners) {
					listener.onResume(interpolation);
				}
			});

			void onResume(Interpolation<?> interpolation);
		}

		interface FrameStart {
			Event<FrameStart> EVENT = EventFactory.createArrayBacked(FrameStart.class, (listeners) -> (interpolation) -> {
				for (FrameStart listener : listeners) {
					listener.onFrameStart(interpolation);
				}
			});

			void onFrameStart(Interpolation<?> interpolation);
		}

		interface FrameComplete {
			Event<FrameComplete> EVENT = EventFactory.createArrayBacked(FrameComplete.class, (listeners) -> (interpolation) -> {
				for (FrameComplete listener : listeners) {
					listener.onFrameComplete(interpolation);
				}
			});

			void onFrameComplete(Interpolation<?> interpolation);
		}
	}

	protected record States(
			double ratio,
			boolean available, boolean completed,
			@Nullable ScheduledFuture<?> future
	) {}

	// Constructors

	public Interpolation(@Nullable I initialValue, double ratio) {
		this.value = this.last = this.target = initialValue;
		this.states = new States(ratio, false, false, null);
	}

	public Interpolation(double ratio) {
		this(null, 0);
	}

	// Fields

	@Nullable
	private I value, last, target;
	private States states;

	// Accessors

	@Nullable
	public I value() {
		return value;
	}

	@Nullable
	public I last() {
		return last;
	}

	@Nullable
	public I target() {
		return target;
	}

	public double ratio() {
		return states.ratio();
	}

	@Nullable
	protected ScheduledFuture<?> future() {
		return states.future();
	}

	// Mutators

	protected void updateLast() {
		last = value;
	}

	public void reset(I value) {
		this.value = value;
		updateLast();
	}

	public void target(I target) {
		this.target = target;
		if (!isAvailable()) {
			available(true);
			start();
		}
	}

	protected void states(double ratio, boolean started, boolean completed, @Nullable ScheduledFuture<?> future) {
		states = new States(ratio, started, completed, future);
	}

	public void ratio(double ratio) {
		states(ratio, states.available(), states.completed(), states.future());
	}

	protected void available(boolean available) {
		states(states.ratio(), available, states.completed(), states.future());
	}

	protected void completed(boolean completed) {
		states(states.ratio(), states.available(), completed, states.future());
	}

	private void future(@Nullable ScheduledFuture<?> future) {
		states(states.ratio(), states.available(), states.completed(), future);
	}

	// Properties

	public boolean isPlaying() {
		return future() != null && !Objects.requireNonNull(future()).isCancelled();
	}

	public boolean isPaused() {
		return future() != null && Objects.requireNonNull(future()).isCancelled();
	}

	public boolean isAvailable() {
		return states.available();
	}

	public abstract boolean isCompleted();

	// Interface Implementations

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		if (!isAvailable()) return;

		Callbacks.FrameStart.EVENT.invoker().onFrameStart(this);

		if (isCompleted() && !states.completed()) {
			Callbacks.Complete.EVENT.invoker().onCompletion(this);
			completed(true);
		} else completed(false);

		updateLast();
		update();

		Callbacks.FrameComplete.EVENT.invoker().onFrameComplete(this);
	}

	protected abstract void update();

	// Functions

	protected void start() {
		future(AnimationThreadPoolExecutor.join(this, 0));
	}

	public void pause() {
		if (isPlaying()) {
			Callbacks.Pause.EVENT.invoker().onPause(this);
			Objects.requireNonNull(future()).cancel(true);
		}
	}

	public void resume() {
		if (isPaused()) {
			Callbacks.Resume.EVENT.invoker().onResume(this);
			future(AnimationThreadPoolExecutor.join(this, 0));
		}
	}

	public void onCompletion(Runnable runnable) {
		Callbacks.Complete.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}

	public void onPause(Runnable runnable) {
		Callbacks.Pause.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}

	public void onResume(Runnable runnable) {
		Callbacks.Resume.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}

	public void onFrameStart(Runnable runnable) {
		Callbacks.FrameStart.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}

	public void onFrameComplete(Runnable runnable) {
		Callbacks.FrameComplete.EVENT.register((interpolation) -> {
			if (interpolation == this) runnable.run();
		});
	}
}
