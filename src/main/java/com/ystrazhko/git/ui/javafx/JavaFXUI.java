package com.ystrazhko.git.ui.javafx;

import com.ystrazhko.git.services.LoginService;
import com.ystrazhko.git.services.ServiceProvider;
import com.ystrazhko.git.ui.UserInterface;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import java.util.Optional;

public class JavaFXUI extends Application implements UserInterface {

	@Override
	public void run(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
//        new WelcomeWindow().show();
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("WelcomeWindow.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
		primaryStage.setTitle("Solution Tool for GitLab");
		primaryStage.setScene(scene);
		primaryStage.setHeight(800);
		primaryStage.setWidth(1200);
		primaryStage.setOnShowing(this::login);
		primaryStage.show();
	}

	private void login(WindowEvent windowEvent) {
		LoginDialog ld = new LoginDialog();
		Pair<String, String> loginAndPassword = ld.showAndWait().orElseThrow(() -> new RuntimeException("Error in LoginDialog"));
		LoginService loginService = (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());
		loginService.login(loginAndPassword.getKey(), loginAndPassword.getValue());
	}
}
