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
        ThreadFactory namingThreadFactory = getNamingThreadFactory();
        _executorService = Executors.newCachedThreadPool(namingThreadFactory);
    }

    private ThreadFactory getNamingThreadFactory() {
        final AtomicLong counter = new AtomicLong(0);
        final ThreadFactory factory = Executors.defaultThreadFactory();
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = factory.newThread(runnable);
                thread.setName(String.format("background-service-%d", counter.getAndIncrement()));
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
