package com.lgc.gitlabtool.git.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.ConsoleMessage;
import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.updateConsole.UpdateConsoleListener;

public class ConsoleServiceImpl implements ConsoleService {

    private static final Logger _log = LogManager.getLogger(ConsoleServiceImpl.class);

    private final List<ConsoleMessage> _messages;
    private final Set<UpdateConsoleListener> _listeners;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator") ;

    public ConsoleServiceImpl() {
        _messages = new ArrayList<>();
        _listeners = new HashSet<UpdateConsoleListener>();
    }

    @Override
    public void addMessage(String message, MessageType type) {
        addMessageToLog(message, type);

        ConsoleMessage consoleMessage = new ConsoleMessage(formMessage(message), type);
        _messages.add(consoleMessage);
        addNewLineToConsole(consoleMessage);
    }

    @Override
    public List<ConsoleMessage> getMessages() {
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

    @Override
    public void addMessagesForStatuses(Map<Project, JGitStatus> statuses, String nameOperation) {
        if (statuses == null) {
            return;
        }
        for (Entry<Project, JGitStatus> status : statuses.entrySet()) {
            Project project = status.getKey();
            JGitStatus gitStatus = status.getValue();

            String prefix = nameOperation == null ? "" : nameOperation + " of ";
            String message = prefix + project.getName() + " project is " + gitStatus;

            addMessage(message, MessageType.getTypeForStatus(gitStatus));
        }
    }

    private void addNewLineToConsole(ConsoleMessage message) {
        if (_listeners != null) {
            _listeners.forEach(listener -> listener.addNewMessage(message));
        }
    }

    private void updateConsole() {
        if (_listeners != null) {
            _listeners.forEach(listener -> listener.updateConsole());
        }
    }

    private String currentTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        Date date = new Date();
        return "[" + dateFormat.format(date) + "] ";
    }

    private String formMessage(String message) {
        StringBuilder newMessage = new StringBuilder();
        if (!_messages.isEmpty()) {
            newMessage.append(LINE_SEPARATOR);
        }
        newMessage.append(currentTime());
        newMessage.append("" + message);
        return newMessage.toString();
    }

    private void addMessageToLog(String message, MessageType type) {
        if (message == null) {
            _log.error("Error adding message to the UI console. Message is null.");
            return;
        }
        if (type == MessageType.ERROR) {
            _log.error(message);
        } else {
            _log.info(message);
        }
    }
}
