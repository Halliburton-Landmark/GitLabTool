package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This type of dialog should be used in Gitlab Tool
 * instead of pure <code>Alert</code> dialog
 * because it contains application icon and basic settings
 * 
 * @author Igor Khlaponin
 */
public class StatusDialog extends Alert {

    /**
     * If the the dialog message contains more than {@link #MAX_ROW_COUNT_IN_STATUS_DIALOG}
     * we should show the message with count of rows, e.g.
     * <p>
     *     <code>9 of 12 project have been cloned</code>
     * <p>
     */
    public static final int MAX_ROW_COUNT_IN_STATUS_DIALOG = 10;

    /**
     * Creates the instance of this class with Gitlab Tool icon
     * 
     * @param title - the title of the window
     * @param headerText - header of the message
     * @param content - message that should be shown
     */
    public StatusDialog(String title, String headerText, String content) {
        super(Alert.AlertType.INFORMATION);
        setTitle(title);
        setHeaderText(headerText);
        setContentText(content);

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);
    }

}
