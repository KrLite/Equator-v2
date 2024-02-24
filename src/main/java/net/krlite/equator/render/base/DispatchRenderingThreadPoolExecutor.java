package net.krlite.equator.render.base;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DispatchRenderingThreadPoolExecutor {
    public static final ScheduledThreadPoolExecutor INSTANCE = new ScheduledThreadPoolExecutor(
            1, new ThreadPoolExecutor.DiscardPolicy()
    );

    public static ScheduledFuture<?> join(Runnable command) {
        return INSTANCE.schedule(command, 0, TimeUnit.MILLISECONDS);
    }
}
