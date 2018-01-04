package com.lgc.gitlabtool.git.entities;

import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
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

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
            .getService(ThemeService.class.getName());

    /**
     * Gets CSS id for type
     *
     * @param type for getting CSS id
     * @return string with id
     */
    public static String getCssIdForStatus(MessageType type) {
        if (type == null || type == MessageType.SIMPLE) {
            return "message-type-simple";
        }
        return type == MessageType.ERROR ? "message-type-error" : "message-type-success";
    }

    /**
     * Gets CSS style for type
     *
     * @param type for getting CSS
     * @return string with code style
     */
    public static String getCSSForStatus(MessageType type) {
        String prefix = "-fx-text-fill:";
        if (type == null || type == MessageType.SIMPLE) {
            return prefix + toHexString(_themeService.getCurrentTheme().getMainFontColorCss());
        }
        return type == MessageType.ERROR ? prefix + toHexString(_themeService.getCurrentTheme().getErrorFontColorCss())
                : prefix + toHexString(_themeService.getCurrentTheme().getSuccessFontColorCss());
    }

    /**
    * Gets color for type
    *
    * @param type for getting color
    * @return color
    */
    public static Color getColor(MessageType type) {
        if (type == null || type == MessageType.SIMPLE) {
            return _themeService.getCurrentTheme().getMainFontColorCss();
        }
        return type == MessageType.ERROR ? _themeService.getCurrentTheme().getErrorFontColorCss() :
                _themeService.getCurrentTheme().getSuccessFontColorCss();
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

    private static String toHexString(Color color) throws NullPointerException {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }
}