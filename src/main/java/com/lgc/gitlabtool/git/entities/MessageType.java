package com.lgc.gitlabtool.git.entities;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.jgit.JGitStatus;

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
            return "-fx-text-fill:silver";
        }
        return type == MessageType.ERROR ? "-fx-text-fill:red" : "-fx-text-fill:lightGreen";
    }

    /**
    * Gets color for type
    *
    * @param type for getting color
    * @return color
    */
    public static Color getColor(MessageType type) {
        if (type == null || type == MessageType.SIMPLE) {
            return Color.SILVER;
        }
        return type == MessageType.ERROR ? Color.RED : Color.LIGHTGREEN;
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

    /**
     * Gets MessageType which corresponds this JGitStatus.
     *
     * @param  status the jgit status
     * @return a MessageType
     */
    public static MessageType getTypeForStatus(JGitStatus status) {
        if (JGitStatus.SUCCESSFUL == status) {
            return MessageType.SUCCESS;
        } else if (JGitStatus.FAILED == status) {
            return MessageType.ERROR;
        } else {
            return MessageType.SIMPLE;
        }
    }
}