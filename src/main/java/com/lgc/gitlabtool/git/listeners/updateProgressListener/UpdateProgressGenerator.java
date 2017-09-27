package com.lgc.gitlabtool.git.listeners.updateProgressListener;

/**
 * Allows to generate event for updating the UI components to show progress operation.
 * Also add and remove listeners for this event.
 *
 * @author Lyudmila Lyska
 */
public interface UpdateProgressGenerator {

    /**
     * Adds new update progress listener
     *
     * @param listener the listener
     */
    void addUpdateProgressListener(UpdateProgressListener listener);

    /**
     * Remove the update progress listener
     *
     * @param listener the listener
     */
    void removeUpdateProgressListener(UpdateProgressListener listener);

}
