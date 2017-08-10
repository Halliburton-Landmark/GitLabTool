package com.lgc.gitlabtool.git.listeners.updateConsole;

/**
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