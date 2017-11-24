package com.lgc.gitlabtool.git.services;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

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
        return new BasicThreadFactory.Builder()
                .namingPattern("background-service-%d")
                .daemon(isDaemon)
                .priority(Thread.MAX_PRIORITY)
                .build();
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
