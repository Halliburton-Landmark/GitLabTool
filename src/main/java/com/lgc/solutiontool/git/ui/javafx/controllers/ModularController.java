package com.lgc.solutiontool.git.ui.javafx.controllers;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.ui.ViewKey;
import com.lgc.solutiontool.git.ui.toolbar.ToolbarManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ModularController {

    private static final String CSS_PATH = "css/style.css";

    @FXML
    public Pane consolePane;

    @FXML
    public AnchorPane viewPane;

    @FXML
    public ToolBar toolbar;

    @FXML
    public void initialize() {
        toolbar.getStylesheets().add(getClass().getClassLoader().getResource(CSS_PATH).toExternalForm());
    }

    public void loadWelcomeWindow() throws IOException {
        toolbar.getItems().addAll(ToolbarManager.getInstance().createToolbarItems(ViewKey.WELCOME_WINDOW.getKey()));
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKey.WELCOME_WINDOW.getPath()));
        Node node = loader.load();

        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        viewPane.getChildren().clear();
        viewPane.getChildren().add(node);
    }

    public void loadMainWindow(Group selectedGroup) throws IOException {
        toolbar.getItems().addAll(ToolbarManager.getInstance().createToolbarItems(ViewKey.MAIN_WINDOW.getKey()));

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKey.MAIN_WINDOW.getPath()));
        Node node = loader.load();

        MainWindowController controller = loader.getController();
        controller.setSelectedGroup(selectedGroup);
        controller.beforeShowing();

        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        viewPane.getChildren().clear();
        viewPane.getChildren().add(node);
    }

}
