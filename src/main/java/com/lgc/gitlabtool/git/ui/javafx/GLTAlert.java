package com.lgc.gitlabtool.git.ui.javafx;

import java.util.Optional;

import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
import org.apache.commons.lang3.StringUtils;

import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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

    private static final ThemeService _themeService = ServiceProvider.getInstance()
            .getService(ThemeService.class);

    /**
     * Creates an alert with the given AlertType
     *
     * @param alertType - the type of dialog
     */
    public GLTAlert(AlertType alertType) {
        super(alertType);
        setAdvancedOptions();
        _themeService.styleScene(getDialogPane().getScene());
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
     * @param contentText - message that should be shown
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
     * @param contentText - message that should be shown
    */
   public GLTAlert(AlertType alertType, String title, String headerText, String contentText) {
       super(alertType);
       setTitle(title);
       setHeaderText(headerText);
       setContentText(contentText);
       setAdvancedOptions();
       _themeService.styleScene(getDialogPane().getScene());
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

    /**
     * Sets new text for button by ButtonType
     * @param type the button type
     * @param newText the text
     */
    public void setTextButton(ButtonType type, String newText) {
        Button button = (Button) getDialogPane().lookupButton(type);
        button.setText(newText);
    }

    /**
     * Checks buttons types are equal
     *
     * @param pressedButton the button type
     * @return <true> is buttons are equal, otherwise <false>.
     */
    public boolean isOKButtonPressed(Optional<ButtonType> pressedButton) {
        return ButtonType.OK.equals(pressedButton.orElse(ButtonType.CANCEL));
    }

    /**
     * Adds buttons to dialog
     *
     * @param buttonTypes the need button types.
     */
    public void addButtons(ButtonType... buttonTypes) {
        getDialogPane().getButtonTypes().addAll(buttonTypes);
    }

    /**
     * Removes default buttons on dialog.
     * You can add own {@link ButtonType}s using com.lgc.gitlabtool.git.ui.javafx.GLTAlert.addButtons(ButtonType...).
     */
    public void clearDefaultButtons() {
        getDialogPane().getButtonTypes().clear();
    }
}
