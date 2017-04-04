package com.ystrazhko.git.ui.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

/**
 * @author Yevhen Strazhko
 */
public class WelcomeWindowController {
    private static final String WINDOW_TITLE = "Cloning window";

    public void onCreateGroupspace(ActionEvent actionEvent) throws IOException {
        URL cloningGroupsWindowUrl = getClass().getClassLoader().getResource("CloningGroupsWindow.fxml");
        if (cloningGroupsWindowUrl == null) {
            return;
        }

        Parent root = FXMLLoader.load(cloningGroupsWindowUrl);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
