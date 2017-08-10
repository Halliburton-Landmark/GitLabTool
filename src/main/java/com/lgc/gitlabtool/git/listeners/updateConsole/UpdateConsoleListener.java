package com.lgc.gitlabtool.git.listeners.updateConsole;

import javafx.scene.text.Text;

/**
 *
 * @author Lyudmila Lyska
 */
public interface UpdateConsoleListener {

    void addNewMessage(Text message);

    void updateConsole();
}
