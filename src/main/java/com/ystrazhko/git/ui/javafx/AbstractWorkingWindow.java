package com.ystrazhko.git.ui.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

abstract class AbstractWorkingWindow extends Stage {
    AbstractWorkingWindow() throws Exception {
        super();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("AbstractWorkingWindow.fxml"));
        Scene scene = new Scene(root);
        setScene(scene);
        setHeight(800);
        setWidth(1200);


//        BorderPane borderPane = new BorderPane();
//        HBox hBox = new HBox();
//        Text text = new Text("User name");
//        HBox.setHgrow(text, Priority.ALWAYS);
//        hBox.getChildren().add(text);
//        hBox.setAlignment(Pos.BASELINE_RIGHT);
//        borderPane.setTop(hBox);
//        borderPane.setCenter(getCenterNode());

    }

    protected abstract Node getCenterNode();
}
