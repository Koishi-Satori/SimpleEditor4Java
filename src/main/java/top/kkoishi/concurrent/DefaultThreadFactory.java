package top.kkoishi.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultThreadFactory implements ThreadFactory {
    private static final String THREAD_PREFIX = "KKoishi_::thread#";
    private static final AtomicInteger COUNT = new AtomicInteger(0);

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param r a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     * create a thread is rejected
     */
    @Override
    public Thread newThread (Runnable r) {
        return new Thread(r, THREAD_PREFIX + COUNT.getAndIncrement());
    }
}
