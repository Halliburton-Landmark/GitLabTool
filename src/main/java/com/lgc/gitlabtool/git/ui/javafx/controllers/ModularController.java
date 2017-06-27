package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.ClonedGroupsService;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.GroupsUserService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.AlertWithCheckBox;
import com.lgc.gitlabtool.git.ui.javafx.CommitDialog;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuItems;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuManager;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import com.lgc.gitlabtool.git.util.ProjectPropertiesUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import javafx.scene.control.ButtonBar;
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

    private static final Logger logger = LogManager.getLogger(ModularController.class);

    private static final String ABOUT_POPUP_TITLE = "About";

    private static final String ABOUT_POPUP_HEADER = "Gitlab tool v." + ProjectPropertiesUtil.getProjectVersion()
            + ", powered by Luxoft";

    private static final String ABOUT_POPUP_CONTENT = "Contacts: Yurii Pitomets (yurii.pitomets2@halliburton.com)";
    private static final String SWITCH_BRANCH_TITLE = "Switch branch";

    private static final String IMPORT_CHOOSER_TITLE = "Import Group";
    private static final String IMPORT_DIALOG_TITLE = "Import Status Dialog";
    private static final String SUCCESFUL_IMPORT_MESSAGE = "Import of group is Successful";
    private static final String FAILED_IMPORT_MESSAGE = "Import of group is Failed";

    private static final String REMOVE_GROUP_DIALOG_TITLE = "Remove Group";
    private static final String REMOVE_GROUP_STATUS_DIALOG_TITLE = "Import Status Dialog";
    private static final String FAILED_REMOVE_GROUP_MESSAGE = "Removing of group is Failed";

    private static final String STATUS_DISCARD_DIALOG_TITLE = "Discarding changes status";
    private static final String STATUS_DISCARD_DIALOG_HEADER = "Discarding changes info";

    private static final String STATUS_COMMIT_DIALOG_TITLE = "Committing changes status";
    private static final String STATUS_COMMIT_DIALOG_HEADER = "Committing changes info";

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


    private final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private final ClonedGroupsService _clonedGroupsService = (ClonedGroupsService) ServiceProvider.getInstance()
            .getService(ClonedGroupsService.class.getName());


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

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.REMOVE_GROUP_BUTTON.getId())
                    .setOnAction(this::onRemoveGroup);

        } else if (windowId.equals(ViewKey.MAIN_WINDOW.getKey())) {
            Button switchBranch = ToolbarManager.getInstance()
                    .getButtonById(ToolbarButtons.SWITCH_BRANCH_BUTTON.getId());
            switchBranch.setOnAction(event -> switchBranchAction());
        }
    }

    private void removeGroupDialog(Group selectedGroup) {
        AlertWithCheckBox alert = new AlertWithCheckBox(AlertType.CONFIRMATION,
                REMOVE_GROUP_DIALOG_TITLE,
                "Are you sure you want to delete the " + selectedGroup.getName() + "?",
                "",
                "remove group from a local disk",
                ButtonType.YES, ButtonType.NO);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.NO) {
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Map<Boolean, String> status = _groupService.removeGroup(selectedGroup, alert.isCheckBoxSelected());
            for (Entry<Boolean, String> mapStatus : status.entrySet()) {
                String headerMessage;
                if (!mapStatus.getKey()) {
                    headerMessage = FAILED_REMOVE_GROUP_MESSAGE;
                    showStatusDialog(REMOVE_GROUP_STATUS_DIALOG_TITLE, headerMessage, mapStatus.getValue());
                }
                _welcomeWindowController.refreshGroupsList();
            }
        });
        executor.shutdown();
    }

    @FXML
    public void onRemoveGroup(ActionEvent actionEvent) {
        Group group = _welcomeWindowController.getSelectedGroup();
        removeGroupDialog(group);
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

    private void switchBranchAction(){
        List<Project> allSelectedProjects = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
        List<Project> changedProjects = _gitService.getProjectsWithChanges(allSelectedProjects);
        if (changedProjects.isEmpty()) {
            showSwitchBranchWindow();
        } else {
            showSwitchBranchConfirmWindow(changedProjects);
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
            logger.error("Could not load fxml resource", e);
        }
    }

    private void showSwitchBranchConfirmWindow(List<Project> selectedProjects){

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Switch branch confirmation");
        alert.setHeaderText("This projects have uncommited changes");
        alert.setContentText("Would you like to commit the changes or discard ?");

        ButtonType commitButton = new ButtonType("Commit changes");
        ButtonType discardButton = new ButtonType("Discard changes");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(commitButton, discardButton, cancelButton);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == commitButton) {
          showCommitPushDialog(selectedProjects);

        } else if (result.get() == discardButton) {
            Map<Project, JGitStatus> discardStatuses =_gitService.discardChanges(selectedProjects);
            showDiscardStatusDialog(selectedProjects, discardStatuses);

        } else {
            alert.close();
        }
    }

    private void showCommitPushDialog(List<Project> projects) {
        CommitDialog dialog = new CommitDialog();
        Optional<ButtonType> commitResult = dialog.showAndWait();

        if (commitResult.get() == dialog.getCommitButton() || commitResult.get() == dialog.getCommitAndPushButton()) {
            String commitMessage = StringUtils.EMPTY;

            if (dialog.getCommitMessage() != null) {
                commitMessage = dialog.getCommitMessage();
            }

            boolean isPush = commitResult.get().equals(dialog.getCommitAndPushButton());

            Map<Project, JGitStatus> commitStatuses = _gitService.commitChanges(projects, commitMessage, isPush,
                    new SuccessfulOperationHandler(), new UnsuccessfulOperationHandler());

            showCommitStatusDialog(projects, commitStatuses);
        }
    }

    private void showCommitStatusDialog(List<Project> projects, Map<Project, JGitStatus> commitStatuses){
        String info;

        if (commitStatuses.size() < 6) {
            info = commitStatuses.entrySet().stream()
                    .map(pair -> pair.getKey().getName() + " - " + pair.getValue())
                    .collect(Collectors.joining("\n"));
        } else {

            long countSuccessfulDiscarding =
                    commitStatuses.entrySet().stream()
                            .map(Entry::getValue)
                            .filter(status -> status.equals(JGitStatus.SUCCESSFUL))
                            .count();
            if (countSuccessfulDiscarding == projects.size()) {
                info = "All changes was successfully commited";
            } else {
                info = "Committing changes was failed" + "\n"
                        + "Successfully: " + countSuccessfulDiscarding + " project(s)" + "\n"
                        + "Failed: " + (projects.size() - countSuccessfulDiscarding) + " project(s)";
            }
        }

        Alert statusDialog = new StatusDialog(STATUS_COMMIT_DIALOG_TITLE, STATUS_COMMIT_DIALOG_HEADER, info);
        statusDialog.showAndWait();
    }

    private void showDiscardStatusDialog(List<Project> projects, Map<Project, JGitStatus> discardStatuses){
        String info;

        if (discardStatuses.size() < 6) {
            info = discardStatuses.entrySet().stream()
                    .map(pair -> pair.getKey().getName() + " - " + pair.getValue())
                    .collect(Collectors.joining("\n"));
        } else {

            long countSuccessfulDiscarding =
                    discardStatuses.entrySet().stream()
                            .map(Entry::getValue)
                            .filter(status -> status.equals(JGitStatus.SUCCESSFUL))
                            .count();
            if (countSuccessfulDiscarding == projects.size()) {
                info = "All changes was successfully discarded";
            } else {
                info = "Discarding changes was failed" + "\n"
                        + "Successfully: " + countSuccessfulDiscarding + " project(s)" + "\n"
                        + "Failed: " + (projects.size() - countSuccessfulDiscarding) + " project(s)";
            }
        }

        Alert statusDialog = new StatusDialog(STATUS_DISCARD_DIALOG_TITLE, STATUS_DISCARD_DIALOG_HEADER, info);
        statusDialog.showAndWait();
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
        stage.initModality(Modality.APPLICATION_MODAL);

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
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                Group loadGroup = _groupService.importGroup(selectedDirectory.getAbsolutePath());
                if (loadGroup == null) {
                    showStatusDialog(IMPORT_DIALOG_TITLE, FAILED_IMPORT_MESSAGE,
                            "Failed to load group from " + selectedDirectory.getAbsolutePath());
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            _welcomeWindowController.refreshGroupsList();
                        }
                    });
                }
            });
            executor.shutdown();
        }
    }

    private void showStatusDialog(String title, String header, String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(header);
                alert.setContentText(content);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(_appIcon);
                alert.showAndWait();
            }
        });
    }

    /**
     * Handler for successful discard operation
     *
     * @author Pavlo Pidhorniy
     */
    class SuccessfulOperationHandler implements Consumer<Integer> {

        @Override
        public void accept(Integer percentage) {
            //TODO: use this handler for UI console
            logger.info("Progress: " + percentage + "%");
        }
    }

    /**
     * Handler for unsuccessful discard operation
     *
     * @author Pavlo Pidhorniy
     */
    class UnsuccessfulOperationHandler implements BiConsumer<Integer, String> {

        @Override
        public void accept(Integer percentage, String message) {
            //TODO: use this handler for UI console
            logger.error("!ERROR: " + message);
            logger.info("Progress: " + percentage + "%");
        }

    }
}
