package com.lgc.solutiontool.git.ui.javafx;

import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.ui.UserInterface;
import com.lgc.solutiontool.git.ui.javafx.dto.DialogDTO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class JavaFXUI extends Application implements UserInterface {

    @Override
    public void run(String[] args) {
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
        primaryStage.show();
        Label userLabel = (Label) scene.lookup("#userId");
        userLabel.setText(_loginService.getCurrentUser().getName());
    }

    private void showloginDialog() {
        LoginDialog ld = new LoginDialog();
        DialogDTO dialogParams = ld.showAndWait().orElseThrow(() -> new RuntimeException("Error in LoginDialog"));
        _loginService.setServerUrl(dialogParams.getServerURL());
        _loginService.login(dialogParams.getLogin(), dialogParams.getPassword());
    }
}
