package com.lgc.gitlabtool.git.services;

import java.util.List;

import com.lgc.gitlabtool.git.entities.ConsoleMessage;
import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.listeners.updateConsole.UpdateConsoleGenerator;

public interface ConsoleService extends UpdateConsoleGenerator{

    void addMessage(String message, MessageType type);

    List<ConsoleMessage> getMessages();

    void removeAll();
}
