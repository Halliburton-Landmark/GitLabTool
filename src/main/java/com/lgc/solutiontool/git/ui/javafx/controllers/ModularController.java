package com.lgc.solutiontool.git.ui.javafx.controllers;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.ui.ViewKey;
import com.lgc.solutiontool.git.ui.icon.AppIconHolder;
import com.lgc.solutiontool.git.ui.mainmenu.MainMenuItems;
import com.lgc.solutiontool.git.ui.mainmenu.MainMenuManager;
import com.lgc.solutiontool.git.ui.toolbar.ToolbarManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ModularController {

    private static final String ABOUT_POPUP_TITLE = "About";
    private static final String ABOUT_POPUP_HEADER = "Solution tool for GitLab, powered by Luxoft";
    private static final String ABOUT_POPUP_CONTENT = "Contacts: Yurii Pitomets (yurii.pitomets2@halliburton.com)";

    private static final String CSS_PATH = "css/style.css";

    @FXML
    public Pane consolePane;

    @FXML
    public AnchorPane viewPane;

    @FXML
    public ToolBar toolbar;

    @FXML
    public SplitPane parentPane;

    @FXML
    public MenuBar menuBar;

    @FXML
    public void initialize() {
        toolbar.getStylesheets().add(getClass().getClassLoader().getResource(CSS_PATH).toExternalForm());
    }

    public void loadWelcomeWindow() throws IOException {
        toolbar.getItems().addAll(ToolbarManager.getInstance().createToolbarItems(ViewKey.WELCOME_WINDOW.getKey()));
        menuBar.getMenus().addAll(MainMenuManager.getInstance().createToolbarItems(ViewKey.WELCOME_WINDOW.getKey()));
        initActionsMainMenu(ViewKey.WELCOME_WINDOW.getKey());

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKey.WELCOME_WINDOW.getPath()));
        Node node = loader.load();

        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        viewPane.getChildren().clear();
        viewPane.getChildren().add(node);

        parentPane.getItems().remove(consolePane);
    }

    public void loadMainWindow(Group selectedGroup) throws IOException {
        toolbar.getItems().addAll(ToolbarManager.getInstance().createToolbarItems(ViewKey.MAIN_WINDOW.getKey()));
        menuBar.getMenus().addAll(MainMenuManager.getInstance().createToolbarItems(ViewKey.MAIN_WINDOW.getKey()));
        initActionsMainMenu(ViewKey.MAIN_WINDOW.getKey());

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

    private void initActionsMainMenu(String windowId) {
        if (windowId.equals(ViewKey.WELCOME_WINDOW.getKey())) {
            MenuItem exit = MainMenuManager.getInstance().getButtonById(MainMenuItems.WELCOME_EXIT);
            exit.setOnAction(event -> Platform.exit());

            MenuItem about = MainMenuManager.getInstance().getButtonById(MainMenuItems.WELCOME_ABOUT);
            about.setOnAction(event -> showAboutPopup());

        } else if (windowId.equals(ViewKey.MAIN_WINDOW.getKey())) {
            MenuItem exit = MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_EXIT);
            exit.setOnAction(event -> Platform.exit());

            MenuItem about = MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_ABOUT);
            about.setOnAction(event -> showAboutPopup());
        }
    }

    private void showAboutPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        ImageView imageView = AppIconHolder.getInstance().getAppIcoImageView();
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);

        alert.setGraphic(imageView);
        alert.setTitle(ABOUT_POPUP_TITLE);
        alert.setHeaderText(ABOUT_POPUP_HEADER);
        alert.setContentText(ABOUT_POPUP_CONTENT);

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);

        alert.show();
    }
}
