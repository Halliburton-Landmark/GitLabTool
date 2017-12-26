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
     * Launches the task in AWT thread<br>
     * This method should be used only for AWT events, that could not be launched from UI thread.
     * I.e., you could not launch {@code Desktop.getDesktop().open()} method from JavaFX Application thread,
     * thus, you should use this method to run task in AWT thread.
     * <br>
     * In all other cases you should prefer 
     * to use {@link #runInBackgroundThread(Runnable)} method.
     * 
     * @param runnable - the task that should be run
     */
    void runInAWTThread(Runnable runnable);

}
