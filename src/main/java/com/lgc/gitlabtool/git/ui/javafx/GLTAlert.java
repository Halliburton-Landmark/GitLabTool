package com.lgc.gitlabtool.git.ui.javafx;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Extended Alert for use in the GitLab Tool.
 *
 * By default, sets AlertType.INFORMATION, an application icon,
 * size and location definition to the dialog.
 *
 * It is recommended that you use this class instead of Alert
 *
 * @author Lyudmila Lyska
 */
public class GLTAlert extends Alert {

    private final static AlertType DEFAULT_TYPE = AlertType.INFORMATION;

    /**
     * Creates an alert with the given AlertType
     *
     * @param alertType - the type of dialog
     */
    public GLTAlert(AlertType alertType) {
        super(alertType);
        setAdvancedOptions();
    }

    /**
     * Creates an alert with the default AlertType (AlertType.INFORMATION) and sets
     * title, header and empty content messages.
     *
     * @param title - the title of the window
     * @param headerText - header of the message
    */
   public GLTAlert(String title, String headerText) {
       this(DEFAULT_TYPE, title, headerText, StringUtils.EMPTY);
   }

    /**
     * Creates an alert with the default AlertType (AlertType.INFORMATION) and sets
     * title, header and content messages.
     *
     * @param title - the title of the window
     * @param headerText - header of the message
     * @param content - message that should be shown
     */
    public GLTAlert(String title, String headerText, String contentText) {
        this(DEFAULT_TYPE, title, headerText, contentText);
    }

    /**
     * Creates an alert with the given AlertType and sets
     * title, header and content messages.
     *
     * @param alertType - the type of dialog
     * @param title - the title of the window
     * @param headerText - header of the message
     * @param content - message that should be shown
    */
   public GLTAlert(AlertType alertType, String title, String headerText, String contentText) {
       super(DEFAULT_TYPE);
       setTitle(title);
       setHeaderText(headerText);
       setContentText(contentText);
       setAdvancedOptions();
   }

   /**
    * Sets advanced options of the dialog.
    * For example: icon, sizing and position.
    */
    protected void setAdvancedOptions() {
        /* Set icon to the dialog */
        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);

         /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 100);
    }

}
