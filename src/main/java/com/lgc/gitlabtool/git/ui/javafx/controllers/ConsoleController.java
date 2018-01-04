package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;

import com.lgc.gitlabtool.git.entities.ConsoleMessage;
import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.listeners.updateConsole.UpdateConsoleListener;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.ServiceProvider;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class ConsoleController implements UpdateConsoleListener {
    private final String ID = "ConsoleController.class";

    private TextFlow _console;
    private ScrollPane _scrollPane;

    private static ConsoleController _consoleController;
    private static ConsoleService _consoleService;

    static {
        _consoleService = (ConsoleService) ServiceProvider.getInstance().getService(ConsoleService.class.getName());
    }

    private ConsoleController() {
        _consoleController = this;
        _consoleService.addListener(this);
    }

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static ConsoleController getInstance() {
        if (_consoleController == null) {
            _consoleController = new ConsoleController();
        }
        return _consoleController;
    }

    @Override
    public void addNewMessage(ConsoleMessage message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (message != null) {
                    _console.getChildren().add(getText(message));
                    moveScrollToBottom();
                }
            }
        });
    }

    @Override
    public void updateConsole() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _console.getChildren().clear();
                _console.getChildren().addAll(convertConsoleMessageToText(_consoleService.getMessages()));
                moveScrollToBottom();
            }
        });
    }

    private List<Text> convertConsoleMessageToText(List<ConsoleMessage> consoleMessages) {
        List<Text> messages = new ArrayList<>();
        consoleMessages.forEach(message -> messages.add(getText(message)));
        return messages;
    }

    private Text getText(ConsoleMessage message) {
        Text text = new Text(message.getMessage());
        text.setFill(MessageType.getColor(message.getType()));
        return text;
    }

    private void moveScrollToBottom() {
        // move scroll bar. Fix for bug with set value 1.0 to scrollPane.setVValue
        final Timeline timeline = new Timeline();
        final KeyValue kv = new KeyValue(_scrollPane.vvalueProperty(), 1.0);
        final KeyFrame kf = new KeyFrame(Duration.millis(100), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    public void setComponents(TextFlow console, ScrollPane scrollPane) {
        _console = console;
        _scrollPane = scrollPane;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ID == null) ? 0 : ID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ConsoleController other = (ConsoleController) obj;
        if (ID == null) {
            if (other.ID != null) {
                return false;
            }
        } else if (!ID.equals(other.ID)) {
            return false;
        }
        return true;
    }
}
