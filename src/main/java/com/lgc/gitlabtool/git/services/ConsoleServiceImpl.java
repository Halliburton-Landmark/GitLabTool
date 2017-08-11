package com.lgc.gitlabtool.git.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.listeners.updateConsole.UpdateConsoleListener;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;

import javafx.scene.text.Text;

public class ConsoleServiceImpl implements ConsoleService {

    private final List<Text> _messages;
    private final Set<UpdateConsoleListener> _listeners;

    public ConsoleServiceImpl() {
        _messages = new ArrayList<>();
        _listeners = new HashSet<UpdateConsoleListener>();
    }

    @Override
    public void addMessage(String message, MessageType type) {
        if (message == null) {
            //todo: logger
            return;
        }
        Text newMessage = new Text(LineSeparator.Windows + message);
        newMessage.setFill(MessageType.getColor(type));
        _messages.add(newMessage);
        addNewLineToConsole(newMessage);
    }

    @Override
    public List<Text> getMessages() {
        return Collections.unmodifiableList(_messages);
    }

    @Override
    public void removeAll() {
        _messages.clear();
        updateConsole();
    }

    @Override
    public void addListener(UpdateConsoleListener listener) {
        if (listener != null) {
            _listeners.add(listener);
        }
    }

    @Override
    public void removeStateListener(UpdateConsoleListener listener) {
        if (listener != null) {
            _listeners.remove(listener);
        }
    }

    private void addNewLineToConsole(Text message) {
        if (_listeners != null) {
            _listeners.forEach(listener -> listener.addNewMessage(message));
        }
    }

    private void updateConsole() {
        if (_listeners != null) {
            _listeners.forEach(listener -> listener.updateConsole());
        }
    }

}
