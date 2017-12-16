package com.lgc.gitlabtool.git.ui.toolbar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.javafx.controllers.ModularController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Class for managing an toolbar with buttons
 *
 * @author Pavlo Pidhorniy
 */
public class ToolbarManager {
    private static final Logger logger = LogManager.getLogger(ToolbarManager.class);
    private static final String CHANGE_GROUP_BUTTON_ICON_URL = "icons/toolbar/change_group_20x20.png";
    private static final String CHANGE_GROUP_BUTTON_TOOLTIP = "Change current group";
    private static final String CHANGE_GROUP_BUTTON_ID = "changeGroupButton";

    private Map<String, Boolean> enableMap = new HashMap<>();

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
        for (GLToolButtons button : GLToolButtons.values()) {
            if (button.getViewKey().equals(windowId)) {
                Button btn = createButton(button.getId(), button.getIconUrl(), button.getText(), button.getTooltip());
                if (btn != null && button.getIconSize() == GLToolButtons.GLToolConstants.ICON_SIZE_20) {
                    items.add(btn);
                }
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
     * Temporary lock all buttons on toolbar and makes backup of disable states
     */
    public void lockButtons() {
        if (items == null || enableMap == null) {
            return;
        }

        items.stream().filter(x -> x instanceof Button)
                .map(node -> (Button) node)
                .forEach(button -> {
                    enableMap.put(button.getId(), button.isDisable());
                    button.setDisable(true);
                });
    }

    /**
     * Unlock all buttons on toolbar after locking and restore backup disable states
     */
    public void unlockButtons() {
        if (items == null || enableMap == null) {
            return;
        }

        items.stream()
                .filter(x -> x instanceof Button)
                .map(node -> (Button) node)
                .forEach(button -> {
                    if (enableMap.containsKey(button.getId())) {
                        button.setDisable(enableMap.get(button.getId()));
                    }
                });
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
            if (buttonNode.getId().equals(CHANGE_GROUP_BUTTON_ID)) {
                continue;
            }

            buttons.add(buttonNode);
        }

        return buttons;
    }

    private Button createButton(String buttonId, String imgPath, String btnText, String tooltipText) {
        if (getClass().getClassLoader().getResource(imgPath) == null) return null;
        Image btnImage = new Image(getClass().getClassLoader().getResource(imgPath).toExternalForm());
        Button button = new Button(btnText, new ImageView(btnImage));
        button.setTooltip(new Tooltip(tooltipText));
        button.setId(buttonId);

        return button;
    }
}
