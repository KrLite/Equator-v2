package net.krlite.equator.visual.animation.base;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <h1>AnimationThreadPoolExecutor</h1>
 * A {@link ScheduledThreadPoolExecutor} which is used to schedule {@link Animation} and {@link Interpolation}
 * tasks. Basically, this executor handles the timing of the animations and interpolations.
 */
public class AnimationThreadPoolExecutor {
	/**
	 * The {@link ScheduledThreadPoolExecutor} instance which is used to schedule {@link Animation} and
	 * {@link Interpolation} tasks.
	 */
	public static final ScheduledThreadPoolExecutor INSTANCE = new ScheduledThreadPoolExecutor(
			1, new ThreadPoolExecutor.DiscardPolicy()
	);

	public static ScheduledFuture<?> join(Animation<?> animation, long delay) {
		return INSTANCE.scheduleAtFixedRate(animation, delay, animation.period(), animation.timeUnit());
	}

	public static ScheduledFuture<?> join(Interpolation interpolation, long delay) {
		return INSTANCE.scheduleAtFixedRate(interpolation, delay, TimeUnit.MILLISECONDS.toMillis(1), TimeUnit.MILLISECONDS);
	}
}
