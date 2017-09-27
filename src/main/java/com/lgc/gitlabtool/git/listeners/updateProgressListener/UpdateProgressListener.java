package com.lgc.gitlabtool.git.listeners.updateProgressListener;

/**
 * Listener for tracking event of updating progress components.
 *
 * @author Lyudmila Lyska
 */
public interface UpdateProgressListener {

    /**
     * Gets event to update UI component which shows progress current operation.
     *
     * @param progressMessage the progress message for a component
     */
    void updateProgress(String progressMessage);
}
