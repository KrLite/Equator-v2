package net.krlite.equator.visual.animation.base;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * <h1>Interpolation</h1>
 * Handles the interpolation between two values.
 * @param <I>	The type of the interpolated value.
 */
public abstract class Interpolation<I> implements Runnable {
	public static class Any<I> {
		@FunctionalInterface
		public interface Protocol<I> {
			I interpolate(I value, I target, double ratio);
		}

		private final Protocol<I> protocol;

		public Protocol<I> protocol() {
			return protocol;
		}

		public Any(Protocol<I> protocol) {
			this.protocol = protocol;
		}

		public Interpolation<I> use(I initial, double ratio) {
			return new Interpolation<>(initial, ratio) {
				@Override
				public I interpolate(I value, I target) {
					return protocol().interpolate(value, target, ratio());
				}

				@Override
				public boolean isCompleted() {
					return Objects.equals(value(), target());
				}
			};
		}
	}

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

	public Interpolation(I initial, double ratio) {
		this.value = this.last = this.target = initial;
		this.states = new States(ratio, false, false, null);
	}

	// Fields

	private I value, last, target;
	private States states;

	// Accessors

	public I value() {
		return value;
	}

	public I last() {
		return last;
	}

	public I target() {
		return target;
	}

	public double ratio() {
		return states.ratio();
	}

	protected @Nullable ScheduledFuture<?> future() {
		return states.future();
	}

	// Mutators

	protected void fetch() {
		last = value;
	}

	protected void value(I value) {
		this.value = value;
	}

	public void reset(I value) {
		value(value);
		fetch();
	}

	public void target(I target) {
		this.target = target;
		if (!isAvailable()) {
			available(true);
			play();
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

		if (value() != null && target() != null) {
			fetch();
			value(interpolate(value(), target()));
		}

		Callbacks.FrameComplete.EVENT.invoker().onFrameComplete(this);
	}

	public abstract I interpolate(I value, I target);

	// Functions

	protected void play() {
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
