package com.lgc.gitlabtool.git.services;

import java.util.List;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.listeners.updateConsole.UpdateConsoleGenerator;

import javafx.scene.text.Text;

public interface ConsoleService extends UpdateConsoleGenerator{

    void addMessage(String message, MessageType type);

    List<Text> getMessages();

    void removeAll();
}
