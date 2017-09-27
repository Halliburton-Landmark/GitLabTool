package com.lgc.gitlabtool.git.entities;

/**
 * Class keeps message and type for the UI console.
 *
 * @author Lyudmila Lyska
 */
public class ConsoleMessage {

    private String _message;
    private MessageType _type;

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

    /**
     *
     * @param message
     */
    public void setMessage(String message) {
        if (message != null) {
            _message = message;
        }
    }

    /**
    *
    * @param type
    */
   public void setType(MessageType type) {
       if (type != null) {
           _type = type;
       }
   }
}
