package com.lgc.solutiontool.git.ui.javafx;

import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.ui.UserInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;

public class JavaFXUI extends Application implements UserInterface {
    private Image appIcon;

    @Override
    public void run(String[] args) {
        appIcon = new Image(getClass().getClassLoader().getResource("icons/gitlab.png").toExternalForm());
        launch(args);
    }

    private LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {
        showloginDialog();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("WelcomeWindow.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setTitle("Solution Tool for GitLab");
        primaryStage.setScene(scene);
        primaryStage.setHeight(800);
        primaryStage.setWidth(1200);
        primaryStage.getIcons().add(appIcon);
        primaryStage.show();
        Label userLabel = (Label) scene.lookup("#userId");
        userLabel.setText(_loginService.getCurrentUser().getName());
    }

    private void showloginDialog() {
        LoginDialog ld = new LoginDialog();
        Stage stage = (Stage) ld.getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);

        Pair<String, String> loginAndPassword = ld.showAndWait().orElseThrow(() -> new RuntimeException("Error in LoginDialog"));
        _loginService.login(loginAndPassword.getKey(), loginAndPassword.getValue());
    }
}
