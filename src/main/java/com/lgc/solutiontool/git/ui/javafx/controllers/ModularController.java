package com.lgc.solutiontool.git.ui.javafx.controllers;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.ui.ViewKeys;
import com.lgc.solutiontool.git.ui.toolbar.ToolbarManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ModularController {

    @FXML
    public Pane consolePane;

    @FXML
    public AnchorPane viewPane;

    @FXML
    public ToolBar toolbar;

    @FXML
    public void initialize() {
        toolbar.getStylesheets().add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());
    }

    public void loadWelcomeWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKeys.WELCOME_WINDOW.getPath()));
        Node node = loader.load();

        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        viewPane.getChildren().clear();
        viewPane.getChildren().add(node);

        toolbar.getItems().addAll(ToolbarManager.getInstance().getToolbarItems(ViewKeys.WELCOME_WINDOW.getKey()));
    }

    public void loadMainWindow(Group selectedGroup) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKeys.MAIN_WINDOW.getPath()));
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

        toolbar.getItems().addAll(ToolbarManager.getInstance().getToolbarItems(ViewKeys.MAIN_WINDOW.getKey()));
    }

}
