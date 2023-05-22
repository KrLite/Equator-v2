package net.krlite.equator.visual.animation;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <h1>AnimationThreadPoolExecutor</h1>
 * A {@link ScheduledThreadPoolExecutor} which is used to schedule {@link Animation} and {@link Interpolation}
 * tasks. Basically, this executor handles the timing of the animations and interpolations.
 */
@net.krlite.equator.base.Animation("2.4.2")
public class AnimationThreadPoolExecutor {
	/**
	 * The {@link ScheduledThreadPoolExecutor} instance which is used to schedule {@link Animation} and
	 * {@link Interpolation} tasks.
	 */
	public static final ScheduledThreadPoolExecutor INSTANCE = new ScheduledThreadPoolExecutor(
			1, new ThreadPoolExecutor.DiscardPolicy()
	);

	/**
	 * Joins an {@link Animation} to the {@link #INSTANCE} and returns the {@link ScheduledFuture} of the task.
	 * @param animation	The {@link Animation} to join.
	 * @param delay		The delay before the task starts to execute in milliseconds.
	 * @return	The {@link ScheduledFuture} of the task.
	 */
	public static ScheduledFuture<?> join(Animation animation, long delay) {
		return INSTANCE.scheduleAtFixedRate(animation, delay, animation.period(), animation.timeUnit());
	}

	/**
	 * Joins an {@link Interpolation} to the {@link #INSTANCE} and returns the {@link ScheduledFuture} of the task.
	 * Note well that the {@link Interpolation} will be executed at a rate of 1 millisecond.
	 * @param interpolation	The {@link Interpolation} to join.
	 * @param delay			The delay before the task starts to execute in milliseconds.
	 * @return	The {@link ScheduledFuture} of the task.
	 */
	public static ScheduledFuture<?> join(Interpolation interpolation, long delay) {
		return INSTANCE.scheduleAtFixedRate(interpolation, delay, TimeUnit.MILLISECONDS.toMillis(1), TimeUnit.MILLISECONDS);
	}
}
