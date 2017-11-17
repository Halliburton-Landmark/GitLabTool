package com.lgc.gitlabtool.git.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * BackgroundService provides possibility to run tasks in the background thread
 *
 * @author Igor Khlaponin
 */
public interface BackgroundService {

    /**
     * Launches the task in background thread and do not return any result
     *
     * @param runnable - the task that should be run
     */
    void runInBackgroundThread(Runnable runnable);

    /**
     * Waits if necessary for the computation in <code>callable</code> to complete, and then
     * retrieves its result to use in <code>consumer</code>
     *
     * This is a blocking operation
     *
     * @param callable - the task that returns the result
     * @param consumer - the part of code that uses the {@code callable}'s result
     * @param <T> the result type of method {@link Callable#call()}
     */
    <T> void runSyncInBackgroundThread(Callable<T> callable, Consumer<T> consumer);
}
