package com.lgc.gitlabtool.git.listeners.updateConsole;

import com.lgc.gitlabtool.git.entities.ConsoleMessage;

/**
 *
 * @author Lyudmila Lyska
 */
public interface UpdateConsoleListener {

    void addNewMessage(ConsoleMessage message);

    void updateConsole();
}
