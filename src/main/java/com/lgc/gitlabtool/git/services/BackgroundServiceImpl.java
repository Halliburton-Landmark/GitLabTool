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
        ThreadFactory namingThreadFactory = buildFactory("background-service-%d", false, Thread.NORM_PRIORITY);
        _executorService = Executors.newCachedThreadPool(namingThreadFactory);
    }

    /**
     * Builds new instance of the {@link ThreadFactory} based on set parameters
     * @param namingPattern - thread naming pattern
     * @param isDaemon - {@code true} if all thread created by this {@code TreadFactory} should be daemons,
     *                   {@code false} otherwise
     * @param priority - the priority of created threads
     * @return new instance of the ThreadFactory based on set parameters
     */
    private ThreadFactory buildFactory(String namingPattern, boolean isDaemon, int priority) {
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
                if (validPriority(priority)) {
                    thread.setPriority(priority);
                }
                return thread;
            }
        };
    }

    private boolean validPriority(int priority) {
        String errorMessage = "Wrong priority! It should be >= " + Thread.MIN_PRIORITY + " and <= " + Thread.MAX_PRIORITY;
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(errorMessage);
        }
        return true;
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
