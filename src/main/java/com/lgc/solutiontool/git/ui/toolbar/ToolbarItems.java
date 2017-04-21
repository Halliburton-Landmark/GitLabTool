package com.lgc.solutiontool.git.ui.toolbar;


import com.lgc.solutiontool.git.ui.ViewKeys;
import com.lgc.solutiontool.git.ui.javafx.controllers.ModularController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
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
public class ToolbarItems {
    private static ToolbarItems instance = null;

    protected ToolbarItems() {
    }

    public static ToolbarItems getInstance() {
        if (instance == null) {
            instance = new ToolbarItems();
        }
        return instance;
    }

    public List<Node> getToolbarItems(String windowId) throws IOException {
        List<Node> items = new ArrayList<>();

        if (windowId.equals(ViewKeys.MAIN_WINDOW.getKey())) {
            Image homeImage = new Image(getClass().getClassLoader().getResource("icons/home.png").toExternalForm());
            Button homeButton = new Button("Welcome page", new ImageView(homeImage));
            homeButton.setTooltip(new Tooltip("Go to welcome page"));
            homeButton.setOnAction((e) -> {
                try {
                    showWelcomePage(homeButton);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

            Image addRepoImage = new Image(getClass().getClassLoader().getResource("icons/main/add.png").toExternalForm());
            Button addRepoButton = new Button("Add repository", new ImageView(addRepoImage));
            addRepoButton.setTooltip(new Tooltip("Add repository"));

            Image removeRepoImage = new Image(getClass().getClassLoader().getResource("icons/main/remove.png").toExternalForm());
            Button removeRepoButton = new Button("Remove repository", new ImageView(removeRepoImage));
            removeRepoButton.setTooltip(new Tooltip("Remove repository"));

            Image editRepoImage = new Image(getClass().getClassLoader().getResource("icons/main/edit.png").toExternalForm());
            Button editRepoButton = new Button("Edit repository", new ImageView(editRepoImage));
            editRepoButton.setTooltip(new Tooltip("Edit repository"));

            Image newBranchImage = new Image(getClass().getClassLoader().getResource("icons/main/new_branch.png").toExternalForm());
            Button newBranchButton = new Button("New branch", new ImageView(newBranchImage));
            newBranchButton.setTooltip(new Tooltip("Create new branch"));

            Image switchBrancheImage = new Image(getClass().getClassLoader().getResource("icons/main/switch.png").toExternalForm());
            Button switchBrancheButton = new Button("Switch branch", new ImageView(switchBrancheImage));
            switchBrancheButton.setTooltip(new Tooltip("Switch to another branch"));

            items.add(homeButton);
            items.add(new Separator());
            items.add(addRepoButton);
            items.add(removeRepoButton);
            items.add(editRepoButton);
            items.add(new Separator());
            items.add(newBranchButton);
            items.add(switchBrancheButton);

        } else if (windowId.equals(ViewKeys.WELCOME_WINDOW.getKey())) {
            Image cloneImage = new Image(getClass().getClassLoader().getResource("icons/welcome/clone.png").toExternalForm());
            Button cloneButton = new Button("Clone group", new ImageView(cloneImage));
            cloneButton.setTooltip(new Tooltip("Clone group"));

            Image removeImage = new Image(getClass().getClassLoader().getResource("icons/welcome/remove.png").toExternalForm());
            Button removeButton = new Button("Remove group", new ImageView(removeImage));
            removeButton.setTooltip(new Tooltip("Remove selected group"));

            Image importGroupImage = new Image(getClass().getClassLoader().getResource("icons/welcome/import.png").toExternalForm());
            Button importGroupButton = new Button("Import group", new ImageView(importGroupImage));
            importGroupButton.setTooltip(new Tooltip("Import group from disk"));

            Image selectGroupImage = new Image(getClass().getClassLoader().getResource("icons/welcome/select.png").toExternalForm());
            Button selectGroupButton = new Button("Load group", new ImageView(selectGroupImage));
            selectGroupButton.setTooltip(new Tooltip("Load selected group"));

            items.add(selectGroupButton);
            items.add(new Separator());
            items.add(cloneButton);
            items.add(removeButton);
            items.add(importGroupButton);
        }

        return items;
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
