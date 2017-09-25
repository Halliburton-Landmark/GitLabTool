package com.lgc.gitlabtool.git.entities;

/**
 * Class keeps message and type for the UI console.
 *
 * @author Lyudmila Lyska
 */
public class ConsoleMessage {

    private final String _message;
    private final MessageType _type;

    /**
     * Creates console message
     *
     * @param message the text for console
     * @param type the type of message (ERROR, SUCCESS or SIMPLE)
     */
    public ConsoleMessage(String message, MessageType type) {
        _message = message;
        _type = type;
    }

    /**
     * Gets text for the console
     * @return a message
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Gets type of message
     *
     * @return MessageType
     */
    public MessageType getType() {
        return _type;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof ConsoleMessage)) {
            return false;
        }

        ConsoleMessage c = (ConsoleMessage) o;

        return _message.equals(c.getMessage()) && _type == c.getType();
    }
}
