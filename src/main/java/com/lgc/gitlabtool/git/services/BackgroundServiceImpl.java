package com.lgc.gitlabtool.git.services;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of {@link BackgroundService}
 *
 * @author Igor Khlaponin
 */
public class BackgroundServiceImpl implements BackgroundService {

    /**
     * Instance of the {@link ExecutorService}
     */
    private final ExecutorService _executorService;

    /**
     * Creates the instance of this class
     */
    public BackgroundServiceImpl() {
        ThreadFactory namingThreadFactory = getThreadFactory(false);
        _executorService = Executors.newCachedThreadPool(namingThreadFactory);
    }

    /**
     * Provides {@link ThreadFactory} for executor service
     * @param isDaemon show if created threads should be daemons
     *              if {@code true}, all created threads will be daemons
     * @return instance of {@link ThreadFactory}
     */
    private ThreadFactory getThreadFactory(boolean isDaemon) {
        return buildFactory("background-service-%d", isDaemon, Thread.MAX_PRIORITY);
    }

    private static ThreadFactory buildFactory(String namingPattern, boolean isDaemon, int priority) {
        final AtomicLong counter = new AtomicLong(0);
        final ThreadFactory factory = Executors.defaultThreadFactory();
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = factory.newThread(runnable);
                if (namingPattern != null) {
                    thread.setName(String.format(namingPattern, counter.getAndIncrement()));
                }
                thread.setDaemon(isDaemon);
                thread.setPriority(priority);
                return thread;
            }
        };
    }

    @Override
    public void runInBackgroundThread(Runnable runnable) {
        _executorService.submit(runnable);
    }

    @Override
    public void dispose() {
        _executorService.shutdown();
    }
}
