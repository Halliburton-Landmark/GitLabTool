package com.lgc.gitlabtool.git.ui.javafx.controllers;

import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.GLTScene;
import com.lgc.gitlabtool.git.util.ScreenUtil;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller for SettingsWindow.fxml
 *
 * @author Igor Khlaponin
 */
public class SettingsWindowController {

    /**
     * Opens settings window stage
     *
     * @param parent parent window
     */
    public void loadSettingsWindow(Parent parent) {
        Stage stage = new Stage();
        stage.setScene(new GLTScene(parent));
        stage.setTitle("Application settings");
        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        stage.getIcons().add(appIcon);
        stage.initModality(Modality.APPLICATION_MODAL);

        /* Set sizing and position */
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        ScreenUtil.adaptForMultiScreens(stage, primaryScreenBounds.getMaxX() / 2,
                primaryScreenBounds.getMaxY() / 2);

        stage.showAndWait();
    }


}
