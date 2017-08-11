package com.lgc.gitlabtool.git.entities;

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
}