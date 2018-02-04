package com.lgc.gitlabtool.git.ui.table;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created by Oleksandr Kozlov on 03.02.2018.
 */
public class TableViewDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = getClass().getClassLoader().getResource("fxml/HistoryTable.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("History");
        primaryStage.show();

    }
}
