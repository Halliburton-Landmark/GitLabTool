package com.ystrazhko.git.ui.javafx;

import com.ystrazhko.git.ui.UserInterface;

import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFXUI extends Application implements UserInterface {

    @Override
    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new LoginWindow(primaryStage);
    }

}
