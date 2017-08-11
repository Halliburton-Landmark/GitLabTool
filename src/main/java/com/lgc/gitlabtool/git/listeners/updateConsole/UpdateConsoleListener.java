package com.lgc.gitlabtool.git.listeners.updateConsole;

import com.lgc.gitlabtool.git.entities.ConsoleMessage;

/**
 * Listener for tracking event of updating console.
 *
 * @author Lyudmila Lyska
 */
public interface UpdateConsoleListener {

    /**
     * Adds new message to the UI console.
     *
     * @param message the concole message
     */
    void addNewMessage(ConsoleMessage message);

    /**
     * Updates UI console
     */
    void updateConsole();
}
