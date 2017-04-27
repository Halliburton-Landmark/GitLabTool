package com.lgc.solutiontool.git.ui.toolbar;

import com.lgc.solutiontool.git.ui.ViewKey;
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
import java.util.Collections;
import java.util.List;

/**
 * Class for managing an toolbar with buttons
 *
 * @author Pavlo Pidhorniy
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

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static ToolbarManager getInstance() {
        if (instance == null) {
            instance = new ToolbarManager();
        }
        return instance;
    }

    /**
     * Create buttons for toolbar
     * Important note: invoke before create a child view in {@link ModularController}
     *
     * @param windowId Id of view where should be created toolbar
     * @return List of nodes with buttons
     */
    public List<Node> createToolbarItems(String windowId) {

        items = new ArrayList<>();

        if (!windowId.equals(ViewKey.WELCOME_WINDOW.getKey())) {
            items.add(createHomeButton());
        }

        for (ToolbarButtons button : ToolbarButtons.values()) {
            if (button.getViewKey().equals(windowId)) {
                items.add(createButton(button.getId(), button.getIconUrl(), button.getText(), button.getTooltip()));
            }
        }

        return items;
    }

    /**
     * Returns button by its identifier
     *
     * @param buttonId Id of button
     * @return Existing button with chosen id or empty button if id does not matches
     */
    public Button getButtonById(String buttonId) {
        if (items == null) {
            return new Button();
        }

        return items.stream()
                .filter(x -> x instanceof Button) //only buttons
                .filter(x -> x.getId().equals(buttonId)) //match by Id
                .findFirst() //first match
                .map(node -> (Button) node) //cast to button
                .orElseGet(Button::new); //result or new Button
    }

    /**
     * Returns all existing buttons on current view (if buttons has been created)
     *
     * @return all buttons from current view
     */
    public List<Button> getAllButtonsForCurrentView() {
        if (items == null) {
            return Collections.emptyList();
        }

        List<Button> buttons = new ArrayList<>();
        for (Node node : items) {
            //work only with buttons
            if (!(node instanceof Button)) {
                continue;
            }

            //except homeButton
            Button buttonNode = (Button) node;
            if (buttonNode.getId().equals(HOME_BUTTON_ID)) {
                continue;
            }

            buttons.add(buttonNode);
        }

        return buttons;
    }

    private Button createHomeButton() {
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
        URL modularWindow = getClass().getClassLoader().getResource(ViewKey.MODULAR_CONTAINER.getPath());
        if (modularWindow == null) {
            System.out.println("ERROR: Could not load fxml resource");
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
