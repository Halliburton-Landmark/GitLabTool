package com.lgc.gitlabtool.git.util;

import java.util.Optional;

import com.lgc.gitlabtool.git.ui.javafx.GLTAlert;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Utility class for working with {@link GLTAlert}
 *
 * @author Lyudmila Lyska
 */
public class GLTAlertUtils {

    /**
     * Request confirmation for operation.
     *
     * @param title - the title of the window
     * @param headerText - header of the message
     * @param content - message that should be shown
     *
     * @return <code>true</code> if user confirmed operation (a user pressed "Yes" button), otherwise <code>false</code>
     */
    public static boolean requesConfirmationOperation(String title, String header, String context) {
        GLTAlert alert = new GLTAlert(AlertType.CONFIRMATION, title, header, context);
        alert.clearDefaultButtons();
        alert.addButtons(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return !(result.orElse(ButtonType.NO) == ButtonType.NO);
    }

}
