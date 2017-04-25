package com.lgc.solutiontool.git.ui.toolbar;


import com.lgc.solutiontool.git.ui.ViewKeys;
import com.lgc.solutiontool.git.ui.javafx.controllers.ModularController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ToolbarManager {
    private static final String HOME_BUTTON_ICON_URL = "icons/home.png";
    private static final String HOME_BUTTON_TEXT = "Welcome page";
    private static final String HOME_BUTTON_TOOLTIP = "Go to welcome page";
    private static final String HOME_BUTTON_ID = "homeButton";

    private static ToolbarManager instance = null;

    private List<Node> items;

    private ToolbarManager() {
    }

    public static ToolbarManager getInstance() {
        if (instance == null) {
            instance = new ToolbarManager();
        }
        return instance;
    }

    public List<Node> getToolbarItems(String windowId) throws IOException {
        items = new ArrayList<>();

        if (!windowId.equals(ViewKeys.WELCOME_WINDOW.getKey())) {
            items.add(createHomeButton());
        }

        for (ToolbarButtons button : ToolbarButtons.values()) {
            if (button.getWindowKey().equals(windowId)) {
                items.add(createButton(button.getId(), button.getIconUrl(), button.getText(), button.getTooltip()));
            }
        }

        return items;
    }

    public Button getButton(String key) {
        if (items == null) {
            return new Button();
        }

        for (Node node : items) {
            if (!(node instanceof Button)) {
                continue;
            }

            Button button = (Button) node;
            if (button.getId().equals(key)) {
                return button;
            }
        }

        return new Button();
    }

    private Button createHomeButton(){
        Image homeImage = new Image(getClass().getClassLoader().getResource(HOME_BUTTON_ICON_URL).toExternalForm());
        Button homeButton = new Button(HOME_BUTTON_TEXT, new ImageView(homeImage));
        homeButton.setTooltip(new Tooltip(HOME_BUTTON_TOOLTIP));
        homeButton.setId(HOME_BUTTON_ID);
        homeButton.setOnAction((e) -> {
            try {
                showWelcomePage(homeButton);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return homeButton;
    }

    private Button createButton(String buttonId, String imgPath, String btnText, String tooltipText) {
        Image btnImage = new Image(getClass().getClassLoader().getResource(imgPath).toExternalForm());
        Button button = new Button(btnText, new ImageView(btnImage));
        button.setTooltip(new Tooltip(tooltipText));
        button.setId(buttonId);

        return button;
    }

    private void showWelcomePage(Button showWelcomButton) throws IOException {
        URL modularWindow = getClass().getClassLoader().getResource(ViewKeys.MODULAR_CONTAINER.getPath());
        if (modularWindow == null) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(modularWindow);
        Parent root = fxmlLoader.load();

        ModularController myControllerHandle = fxmlLoader.getController();
        myControllerHandle.loadWelcomeWindow();

        Stage previousStage = (Stage) showWelcomButton.getScene().getWindow();
        previousStage.setScene(new Scene(root));
    }
}
