package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StatusDialog extends Alert {

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
