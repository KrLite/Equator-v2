package net.krlite.equator.visual.animation;

import net.krlite.label.For;
import net.krlite.label.Module;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Module("Animation")
@For("2.1.1")
public class AnimationThreadPoolExecutor {
	public static final ScheduledThreadPoolExecutor INSTANCE = new ScheduledThreadPoolExecutor(
			1, new ThreadPoolExecutor.DiscardPolicy()
	);

	public static ScheduledFuture<?> join(Animation animation, long delay) {
		return INSTANCE.scheduleAtFixedRate(animation, delay, animation.period(), animation.timeUnit());
	}

	public static ScheduledFuture<?> join(Interpolation interpolation, long delay) {
		return INSTANCE.scheduleAtFixedRate(interpolation, delay, TimeUnit.MILLISECONDS.toMillis(1), TimeUnit.MILLISECONDS);
	}
}
