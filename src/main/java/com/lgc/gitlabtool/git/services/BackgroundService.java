package com.lgc.gitlabtool.git.services;

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

    /**
     * Launches the task in AWT thread
     * @param runnable - the task that should be run
     */
    void runInEventThread(Runnable runnable);

}
