package com.lgc.gitlabtool.git.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * BackgroundService provides possibility to run tasks in the background thread
 *
 * @author Igor Khlaponin
 */
public interface BackgroundService extends Service {

    /**
     * Launches the task in background thread and does not return any result
     *
     * @param runnable - the task that should be run
     */
    void runInBackgroundThread(Runnable runnable);

}
