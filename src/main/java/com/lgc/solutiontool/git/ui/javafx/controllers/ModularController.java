package com.lgc.solutiontool.git.ui.javafx.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.services.GroupsUserService;
import com.lgc.solutiontool.git.services.ProgramPropertiesService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.ui.ViewKey;
import com.lgc.solutiontool.git.ui.icon.AppIconHolder;
import com.lgc.solutiontool.git.ui.javafx.AlertWithOptOut;
import com.lgc.solutiontool.git.ui.mainmenu.MainMenuItems;
import com.lgc.solutiontool.git.ui.mainmenu.MainMenuManager;
import com.lgc.solutiontool.git.ui.toolbar.ToolbarButtons;
import com.lgc.solutiontool.git.ui.toolbar.ToolbarManager;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ModularController {

    private static final String ABOUT_POPUP_TITLE = "About";
    private static final String ABOUT_POPUP_HEADER = "Solution tool for GitLab, powered by Luxoft";
    private static final String ABOUT_POPUP_CONTENT = "Contacts: Yurii Pitomets (yurii.pitomets2@halliburton.com)";
    private static final String SWITCH_BRANCH_TITLE = "Switch branch";
    private static final String IMPORT_CHOOSER_TITLE = "Import Group";
    private static final String IMPORT_DIALOG_TITLE = "Import Status Dialog";
    private static final String SUCCESFUL_IMPORT_MESSAGE = "Import of group is Successful";
    private static final String FAILED_IMPORT_MESSAGE = "Import of group is Failed";

    private static final String CSS_PATH = "css/style.css";
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private MainWindowController _mainWindowController;
    private WelcomeWindowController _welcomeWindowController;

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

    private final GroupsUserService _groupService = (GroupsUserService) ServiceProvider.getInstance()
            .getService(GroupsUserService.class.getName());

    private final ProgramPropertiesService _programProperties = (ProgramPropertiesService) ServiceProvider.getInstance()
            .getService(ProgramPropertiesService.class.getName());

    public void loadWelcomeWindow() throws IOException {
        toolbar.getItems().addAll(ToolbarManager.getInstance().createToolbarItems(ViewKey.WELCOME_WINDOW.getKey()));
        menuBar.getMenus().addAll(MainMenuManager.getInstance().createToolbarItems(ViewKey.WELCOME_WINDOW.getKey()));
        initActionsMainMenu(ViewKey.WELCOME_WINDOW.getKey());
        initActionsToolBar(ViewKey.WELCOME_WINDOW.getKey());

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKey.WELCOME_WINDOW.getPath()));
        Node node = loader.load();
        _welcomeWindowController = loader.getController();

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
        initActionsToolBar(ViewKey.MAIN_WINDOW.getKey());

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKey.MAIN_WINDOW.getPath()));
        Node node = loader.load();

        _mainWindowController = loader.getController();
        _mainWindowController.setSelectedGroup(selectedGroup);
        _mainWindowController.beforeShowing();

        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);

        viewPane.getChildren().clear();
        viewPane.getChildren().add(node);
    }

    private void initActionsToolBar(String windowId) {
        if (windowId.equals(ViewKey.WELCOME_WINDOW.getKey())) {
            ToolbarManager.getInstance().getButtonById(ToolbarButtons.IMPORT_GROUP_BUTTON.getId())
                    .setOnAction(event -> importGroupDialog());
//            ToolbarManager.getInstance().getButtonById(ToolbarButtons.REMOVE_GROUP_BUTTON.getId())
//                    .setOnAction(event -> removeGroupDialog());
        } else if (windowId.equals(ViewKey.MAIN_WINDOW.getKey())) {
            Button switchBranch = ToolbarManager.getInstance()
                    .getButtonById(ToolbarButtons.SWITCH_BRANCH_BUTTON.getId());
            switchBranch.setOnAction(event -> showSwitchBranchWindow());
        }
    }

    public void removeGroupDialog(Group selectedGroup) {
        AlertWithOptOut alert = new AlertWithOptOut(AlertType.CONFIRMATION, "Remove group", null,
                "Are you sure you want to delete the " + selectedGroup.getName() + "?",
                "I want to remove group from a local disk", ButtonType.YES, ButtonType.NO);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);
        alert.showAndWait();
    }

//    public static Alert createAlertWithOptOut(AlertType type, String title, String headerText, String message,
//            String optOutMessage, Callback<Boolean, Void> optOutAction, ButtonType... buttonTypes) {
//        Alert alert = new Alert(type);
//        // Need to force the alert to layout in order to grab the graphic,
//        // as we are replacing the dialog pane with a custom pane
//        alert.getDialogPane().applyCss();
//        Node graphic = alert.getDialogPane().getGraphic();
//        // Create a new dialog pane that has a checkbox instead of the hide/show details button
//        // Use the supplied callback for the action of the checkbox
//        alert.setDialogPane(new DialogPane() {
//            @Override
//            protected Node createDetailsButton() {
//                CheckBox optOut = new CheckBox();
//                optOut.setText(optOutMessage);
//                optOut.setOnAction(e -> optOutAction.call(optOut.isSelected()));
//                return optOut;
//            }
//        });
//        alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
//        alert.getDialogPane().setContentText(message);
//        // Fool the dialog into thinking there is some expandable content
//        // a Group won't take up any space if it has no children
//        alert.getDialogPane().setExpandableContent(new javafx.scene.Group());
//        alert.getDialogPane().setExpanded(true);
//        // Reset the dialog graphic using the default style
//        alert.getDialogPane().setGraphic(graphic);
//        alert.setTitle(title);
//        alert.setHeaderText(headerText);
//        return alert;
//    }

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

    private void showSwitchBranchWindow() {
        try {
            EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
                if (_mainWindowController != null) {
                    _mainWindowController.refreshProjectsList();
                }
            };

            URL switchBranchWindowUrl = getClass().getClassLoader().getResource(ViewKey.SWITCH_BRANCH_WINDOW.getPath());
            FXMLLoader loader = new FXMLLoader(switchBranchWindowUrl);
            Parent root = loader.load();

            SwitchBranchWindowController controller = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setHeight(primaryScreenBounds.getMaxY() / 1.5);
            stage.setWidth(primaryScreenBounds.getMaxX() / 1.5);
            stage.getIcons().add(_appIcon);
            stage.setTitle(SWITCH_BRANCH_TITLE);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHiding(confirmCloseEventHandler);
            stage.show();
        } catch (IOException e) {
            System.out.println("Could not load fxml resource: IOException");
            e.printStackTrace();
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

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);

        alert.show();
    }

    private void importGroupDialog() {
        if (viewPane != null) {
            Stage stage = (Stage) viewPane.getScene().getWindow();
            stage.getIcons().add(_appIcon);

            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle(IMPORT_CHOOSER_TITLE);
            File selectedDirectory = chooser.showDialog(stage);
            if (selectedDirectory == null) {
                return;
            }
            new Thread(new ImportRunnable(selectedDirectory)).start();
        }
    }

    /**
     * Imports group from local disk to GitLab Tools workspace.
     *
     * @author Lyudmila Lyska
     */
    class ImportRunnable implements Runnable {

        private final File _selectedDirectory;

        public ImportRunnable(File selectedDirectory) {
            _selectedDirectory = selectedDirectory;
        }

        @Override
        public void run() {
            Map<Optional<Group>, String> loadGroup = _groupService.importGroup(_selectedDirectory.getAbsolutePath());

            for (Entry<Optional<Group>, String> mapGroup : loadGroup.entrySet()) {
                Optional<Group> optGroup = mapGroup.getKey();
                if (optGroup.isPresent()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            _programProperties.updateClonedGroups(Arrays.asList(optGroup.get()));
                            _welcomeWindowController.refreshGroupsList();
                        }
                    });
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        showStatusDialog(IMPORT_DIALOG_TITLE,
                                optGroup.isPresent() ? SUCCESFUL_IMPORT_MESSAGE : FAILED_IMPORT_MESSAGE,
                                mapGroup.getValue());
                    }
                });
            }
        }
    }

    private void showStatusDialog(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);
        alert.showAndWait();
    }
}
