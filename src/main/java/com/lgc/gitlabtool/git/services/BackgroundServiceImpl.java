package com.lgc.gitlabtool.git.services;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Implementation of {@link BackgroundService}
 *
 * @author Igor Khlaponin
 */
public class BackgroundServiceImpl implements BackgroundService {

    /**
     * Instance of this class
     */
    private static BackgroundService _backgroundService;

    /**
     * Instance of the {@link ExecutorService}
     */
    private final ExecutorService _executorService;

    private BackgroundServiceImpl() {
        _executorService = Executors.newCachedThreadPool();
    }

    /**
     * Provides the instance of this class
     * @return BackgroundServiceImpl instance
     */
    public static BackgroundService get() {
        if (_backgroundService == null) {
            _backgroundService = new BackgroundServiceImpl();
        }
        return _backgroundService;
    }

    @Override
    public <T> void runSyncInBackgroundThread(Callable<T> callable, Consumer<T> consumer) {
        Thread thread = new Thread(() -> {
            Future<T> future = _executorService.submit(callable);

            try {
                T result = future.get();
                if (consumer != null && future.isDone()) {
                    consumer.accept(result);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        },"sync-thread");
        thread.start();
    }

    @Override
    public void runInBackgroundThread(Runnable runnable) {
        _executorService.submit(runnable);
    }

    @Override
    protected void finalize() throws Throwable {
        _executorService.shutdown();
        super.finalize();
    }
}
