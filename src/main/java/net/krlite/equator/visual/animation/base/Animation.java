package net.krlite.equator.visual.animation.base;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.krlite.equator.math.algebra.Curves;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.animation.Slice;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

/**
 * <h1>Animation</h1>
 * Handles the animation between two values.
 * @param <A>	The type of the animated value.
 */
public abstract class Animation<A> implements Runnable {
	public static class Any<A> {
		@FunctionalInterface
		public interface Protocol<A> {
			A animate(A start, A end, double progress, Slice slice);
		}

		public Any(Protocol<A> protocol, Protocol<A> protocolClamped) {
			this.protocol = protocol;
			this.protocolClamped = protocolClamped;
		}

		public Any(Protocol<A> protocol) {
			this(protocol, protocol);
		}

		protected final Protocol<A> protocol, protocolClamped;

		public Protocol<A> protocol() {
			return protocol;
		}

		public Protocol<A> protocolClamped() {
			return protocolClamped;
		}

		public Animation<A> use(
				A start, A end,
				double speed, long duration, TimeUnit timeUnit,
				boolean sensitive, Slice slice
		) {
			return new Animation<>(start, end, speed, duration, timeUnit, sensitive, slice) {
				@Override
				public A value(double progress) {
					return Any.this.protocol.animate(start(), end(), progress, slice());
				}

				@Override
				public A valueClamped(double progress) {
					return Any.this.protocolClamped.animate(start(), end(), progress, slice());
				}
			};
		}

		public Animation<A> use(A start, A end, long duration, Slice slice) {
			Animation<A> animation = use(start, end, 0, duration, TimeUnit.MILLISECONDS, false, slice);
			animation.defaultSpeedPositive();

			return animation;
		}
	}

	public interface Callbacks {
		interface Start {
			Event<Start> EVENT = EventFactory.createArrayBacked(Start.class, (listeners) -> (animation) -> {
				for (Start listener : listeners) {
					listener.onStart(animation);
				}
			});

			void onStart(Animation<?> animation);
		}

		interface Complete {
			Event<Complete> EVENT = EventFactory.createArrayBacked(Complete.class, (listeners) -> (animation) -> {
				for (Complete listener : listeners) {
					listener.onCompletion(animation);
				}
			});

			void onCompletion(Animation<?> animation);
		}

		interface Pause {
			Event<Pause> EVENT = EventFactory.createArrayBacked(Pause.class, (listeners) -> (animation) -> {
				for (Pause listener : listeners) {
					listener.onPause(animation);
				}
			});

			void onPause(Animation<?> animation);
		}

		interface Resume {
			Event<Resume> EVENT = EventFactory.createArrayBacked(Resume.class, (listeners) -> (animation) -> {
				for (Resume listener : listeners) {
					listener.onResume(animation);
				}
			});

			void onResume(Animation<?> animation);
		}

		interface Loop {
			Event<Loop> EVENT = EventFactory.createArrayBacked(Loop.class, (listeners) -> (animation) -> {
				for (Loop listener : listeners) {
					listener.onLooping(animation);
				}
			});

			void onLooping(Animation<?> animation);
		}

		interface FrameStart {
			Event<FrameStart> EVENT = EventFactory.createArrayBacked(FrameStart.class, (listeners) -> (animation) -> {
				for (FrameStart listener : listeners) {
					listener.onFrameStart(animation);
				}
			});

			void onFrameStart(Animation<?> animation);
		}

		interface FrameComplete {
			Event<FrameComplete> EVENT = EventFactory.createArrayBacked(FrameComplete.class, (listeners) -> (animation) -> {
				for (FrameComplete listener : listeners) {
					listener.onFrameComplete(animation);
				}
			});

			void onFrameComplete(Animation<?> animation);
		}
	}

	protected record Values<A>(A start, A end, double progress) {
		public Values(A start, A end, double progress) {
			this.start = start;
			this.end = end;
			this.progress = Theory.clamp(progress, 0, 1);
		}
	}

	protected record Frequency(double speed, long duration, TimeUnit timeUnit) {
		public Frequency(double speed, long duration, TimeUnit timeUnit) {
			this.speed = speed;
			this.duration = Math.abs(duration);
			this.timeUnit = timeUnit;
		}

		public long period() {
			return timeUnit.toMillis(1);
		}

		public double accumulation() {
			return period() * speed;
		}
	}

	protected record States(
			Slice slice, boolean sensitive, boolean looping,
			@Nullable ScheduledFuture<?> future
	) {}

	// Constructors

	protected Animation(
			A start, A end,
			double speed, long duration, TimeUnit timeUnit,
			boolean sensitive, Slice slice
	) {
		this.values = new Values<>(start, end, 0);
		this.frequency = new Frequency(speed, duration, timeUnit);
		this.states = new States(slice, sensitive, false, null);
	}

	protected Animation(A start, A end, long duration, Slice slice) {
		this(start, end, 0, duration, TimeUnit.MILLISECONDS, false, slice);
		defaultSpeedPositive();
	}

	// Fields
	private Values<A> values;
	private Frequency frequency;
	private States states;

	// Accessors

	public abstract A value(double progress);

	public abstract A valueClamped(double progress);

	public A value() {
		return value(progress());
	}

	public A valueClamped() {
		return valueClamped(progress());
	}

	public double valuePercent() {
		return Curves.LINEAR.apply(0, 1, values.progress());
	}

	public double valuePercentClamped() {
		return Curves.LINEAR.applyClamped(0, 1, values.progress());
	}

	public A start() {
		return values.start();
	}

	public A end() {
		return values.end();
	}

	public double progress() {
		return values.progress();
	}

	public double speed() {
		return frequency.speed();
	}

	public long duration() {
		return frequency.duration();
	}

	public TimeUnit timeUnit() {
		return frequency.timeUnit();
	}

	public long period() {
		return frequency.period();
	}

	public double accumulation() {
		return frequency.accumulation();
	}

	public Slice slice() {
		return states.slice();
	}

	public boolean sensitive() {
		return states.sensitive();
	}

	public boolean looping() {
		return states.looping();
	}

	protected @Nullable ScheduledFuture<?> future() {
		return states.future();
	}

	// Mutators

	protected void values(A start, A end, double progress) {
		values = new Values<>(start, end, progress);
	}

	public void start(A start) {
		values(start, end(), progress());
	}

	public void end(A end) {
		values(start(), end, progress());
	}

	protected void progress(double progress) {
		values(start(), end(), progress);
	}

	protected void frequency(double speed, long duration, TimeUnit timeUnit) {
		frequency = new Frequency(speed, duration, timeUnit);
	}

	public void speed(double speed) {
		frequency(speed, duration(), timeUnit());
		if (sensitive()) play();
	}

	public void speedNegate() {
		speed(-speed());
	}

	public void speedDirection(boolean positive) {
		speed(positive ? Math.abs(speed()) : -Math.abs(speed()));
	}

	public void defaultSpeedPositive() {
		speed(1);
	}

	public void defaultSpeedNegative() {
		speed(-1);
	}

	public void duration(long duration) {
		frequency(speed(), duration, timeUnit());
	}

	public void timeUnit(TimeUnit timeUnit) {
		frequency(speed(), duration(), timeUnit);
	}

	protected void states(Slice slice, boolean sensitive, boolean looping, @Nullable ScheduledFuture<?> future) {
		states = new States(slice, sensitive, looping, future);
	}

	public void slice(Slice slice) {
		states(slice, sensitive(), looping(), future());
	}

	public void slice(UnaryOperator<Slice> operator) {
		slice(operator.apply(slice()));
	}

	public void sensitive(boolean sensitive) {
		states(slice(), sensitive, looping(), future());
	}

	public void looping(boolean looping) {
		states(slice(), sensitive(), looping, future());
		if (sensitive()) play();
	}

	public void switchLooping() {
		looping(!looping());
	}

	protected void future(@Nullable ScheduledFuture<?> future) {
		states(slice(), sensitive(), looping(), future);
	}

	// Properties

	public boolean isPositive() {
		return speed() > 0;
	}

	public boolean isNegative() {
		return speed() < 0;
	}

	public boolean isPlaying() {
		return future() != null && !Objects.requireNonNull(future()).isCancelled();
	}

	public boolean isPaused() {
		return future() != null && Objects.requireNonNull(future()).isCancelled();
	}

	public boolean isCompleted() {
		return isPositive() && progress() >= 1 || isNegative() && progress() <= 0;
	}

	public boolean isPassing(double atProgress) {
		if (atProgress < 0 || atProgress > 1) return false;

		return Theory.looseBetween(atProgress, progress() - accumulation(), progress());
	}

	// Interface Implementations

	@Override
	public void run() {
		Callbacks.FrameStart.EVENT.invoker().onFrameStart(this);

		if (isCompleted()) {
			if (looping()) {
				Callbacks.Loop.EVENT.invoker().onLooping(this);
				reset();
				progress(animate(progress()));
			} else {
				Callbacks.Complete.EVENT.invoker().onCompletion(this);
				terminate();
			}
		} else {
			progress(animate(progress()));
		}

		Callbacks.FrameComplete.EVENT.invoker().onFrameComplete(this);
	}

	protected double animate(double progress) {
		return Theory.clamp(progress + accumulation() / duration(), 0, 1);
	}

	// Functions

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

	public void pauseOrResume() {
		if (isPaused()) resume();
		else pause();
	}

	public void play() {
		if (!isPlaying()) {
			reset();
			Callbacks.Start.EVENT.invoker().onStart(this);
			future(AnimationThreadPoolExecutor.join(this, 0));
		}
	}

	public void terminate() {
		pause();
		future(null);
	}

	public void reset() {
		progress(isPositive() ? 0 : 1);
	}

	public void replay() {
		terminate();
		play();
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

	public void onPause(Runnable runnable) {
		Callbacks.Pause.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onResume(Runnable runnable) {
		Callbacks.Resume.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onLooping(Runnable runnable) {
		Callbacks.Loop.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onFrameStart(Runnable runnable) {
		Callbacks.FrameStart.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onFrameComplete(Runnable runnable) {
		Callbacks.FrameComplete.EVENT.register((animation) -> {
			if (animation == this) runnable.run();
		});
	}

	public void onFrameStart(double atProgress, Runnable runnable) {
		onFrameStart(() -> {
			if (isPassing(atProgress)) runnable.run();
		});
	}

	public void onFrameComplete(double atProgress, Runnable runnable) {
		onFrameComplete(() -> {
			if (isPassing(atProgress)) runnable.run();
		});
	}
}
