package simple.impl;

import java.util.concurrent.*;

/**
 * Background daemon thread that will read from any amount of caches and check fore expired elements.
 */
public class CacheExpireThread implements Runnable, Expirer {

    // A single threaded expire thread for all caches
    public static final CacheExpireThread DEFAULT = new CacheExpireThread(1, 500, TimeUnit.MILLISECONDS);

    final protected ConcurrentHashMap<Integer, Expireable> expireListeners = new ConcurrentHashMap<>();

    final ScheduledExecutorService exec;

    public CacheExpireThread(int threads, long delay, TimeUnit unit) {

        // we create a daemon thread that will not block application shutdown.
        exec = Executors.newScheduledThreadPool(threads, r -> {
            Thread th = new Thread(r);
            th.setUncaughtExceptionHandler(
                    (thread, throwable) -> {
                        // at a bare minimum we want to print a stack trace.
                        // we could support better logging or error report listeners.
                        throwable.printStackTrace();
                    }
            );
            th.setDaemon(true);
            return th;
        });

        exec.scheduleWithFixedDelay(this, delay, delay, unit);
    }

    @Override
    public void run() {
        for (Expireable expireable : expireListeners.values()) {
            expireable.checkExpire();
        }
    }

    @Override
    public void put(int id, Expireable expireable) {
        expireListeners.put(id, expireable);
    }

    @Override
    public void remove(int id) {
        expireListeners.remove(Integer.valueOf(id));
    }
}
