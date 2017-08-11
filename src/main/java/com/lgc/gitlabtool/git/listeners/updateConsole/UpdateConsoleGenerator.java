package com.lgc.gitlabtool.git.listeners.updateConsole;

/**
* Allows to generate event for updating the UI console.
* Also add and remove listeners for this event.
*
* @author Lyudmila Lyska
*/
public interface UpdateConsoleGenerator {

    /**
     * Adds listener
     *
     * @param listener the listener
     */
    void addListener(UpdateConsoleListener listener);

    /**
     * Remove listener
     *
     * @param listener the listener
     */
    void removeStateListener(UpdateConsoleListener listener);
}