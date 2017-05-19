package com.lgc.solutiontool.git.ui.javafx;

import java.net.URL;
import java.util.NoSuchElementException;

import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.ui.UserInterface;
import com.lgc.solutiontool.git.ui.ViewKey;
import com.lgc.solutiontool.git.ui.icon.AppIconHolder;
import com.lgc.solutiontool.git.ui.javafx.controllers.ModularController;
import com.lgc.solutiontool.git.ui.javafx.dto.DialogDTO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class JavaFXUI extends Application implements UserInterface {
    private Image appIcon;

    @Override
    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        appIcon = AppIconHolder.getInstance().getAppIcoImage();

        URL modularWindow = getClass().getClassLoader().getResource(ViewKey.MODULAR_CONTAINER.getPath());
        if (modularWindow == null) {
            System.out.println("ERROR: Could not load fxml resource");
            return;
        }

        showLoginDialog();
        FXMLLoader fxmlLoader = new FXMLLoader(modularWindow);
        Parent root = fxmlLoader.load();

        ModularController modularController = fxmlLoader.getController();
        modularController.loadWelcomeWindow();

        Scene scene = new Scene(root);

        primaryStage.setTitle("Solution Tool for GitLab");
        primaryStage.setScene(scene);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setHeight(primaryScreenBounds.getMaxY() / 1.5);
        primaryStage.setWidth(primaryScreenBounds.getMaxX() / 1.5);
        primaryStage.getIcons().add(appIcon);
        primaryStage.show();

    }

    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog();
        Stage stage = (Stage) loginDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);
        loginDialog.showAndWait();
    }
}
