package com.lgc.gitlabtool.git.entities;

public class ConsoleMessage {

    private final String _message;
    private final MessageType _type;

    public ConsoleMessage(String message, MessageType type) {
        _message = message;
        _type = type;
    }

    public String getMessage() {
        return _message;
    }

    public MessageType getType() {
        return _type;
    }
}
