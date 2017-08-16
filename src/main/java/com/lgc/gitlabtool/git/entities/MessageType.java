package com.lgc.gitlabtool.git.entities;

import org.apache.commons.lang.StringUtils;

import javafx.scene.paint.Color;

/**
 * Type of messages.
 * It class is needed to get CSS or color for each type.
 *
 * @author Lyudmila Lyska
 */
public enum MessageType {

    ERROR, SUCCESS, SIMPLE;

    /**
     * Gets CSS style for type
     *
     * @param type for getting CSS
     * @return string with code style
     */
    public static String getCSSForStatus(MessageType type) {
        if (type == null || type == MessageType.SIMPLE) {
            return "-fx-text-fill:black";
        }
        return type == MessageType.ERROR ? "-fx-text-fill:red" : "-fx-text-fill:green";
    }

    /**
    * Gets color for type
    *
    * @param type for getting color
    * @return color
    */
    public static Color getColor(MessageType type) {
        if (type == null || type == MessageType.SIMPLE) {
            return Color.BLACK;
        }
        return type == MessageType.ERROR ? Color.RED : Color.GREEN;
    }

    /**
     * Determines MessageType by a text
     *
     * @param text the message
     *
     * @return MessageType.SUCCESS - if the text contains 'success';
     *         MessageType.ERROR   - if the text contains 'error' or 'failed';
     *         MessageType.SIMPLE  - otherwise.
     */
    public static MessageType determineMessageType(String text) {
        if (StringUtils.containsIgnoreCase(text, "success")) {
            return MessageType.SUCCESS;
        }
        if (StringUtils.containsIgnoreCase(text, "error") || StringUtils.containsIgnoreCase(text, "failed")) {
            return MessageType.ERROR;
        }
        return MessageType.SIMPLE;
    }

    /**
     * Determines MessageType
     *
     * @param isSuccess the result operation
     * @return @return MessageType.SUCCESS - if the isSuccess is <true>, otherwise - MessageType.ERROR.
     */
    public static MessageType determineMessageType(boolean isSuccess) {
        return isSuccess ? MessageType.SUCCESS : MessageType.ERROR;
    }
}