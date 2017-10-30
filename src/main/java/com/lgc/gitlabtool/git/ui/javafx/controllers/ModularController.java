package com.lgc.gitlabtool.git.ui.javafx.controllers;

import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getCommitHash;
import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getProjectNameWithVersion;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.listeners.stateListeners.StateListener;
import com.lgc.gitlabtool.git.listeners.updateProgressListener.UpdateProgressListener;
import com.lgc.gitlabtool.git.services.ClonedGroupsService;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.GroupsUserService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.PomXMLService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.AlertWithCheckBox;
import com.lgc.gitlabtool.git.ui.javafx.ChangesCheckDialog;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.CommitDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateNewBranchDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateProjectDialog;
import com.lgc.gitlabtool.git.ui.javafx.GLTAlert;
import com.lgc.gitlabtool.git.ui.javafx.JavaFXUI;
import com.lgc.gitlabtool.git.ui.javafx.ProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.PullProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.javafx.WorkIndicatorDialog;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuItems;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuManager;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import com.lgc.gitlabtool.git.util.ScreenUtil;
import com.lgc.gitlabtool.git.util.UserGuideUtil;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ModularController implements UpdateProgressListener {
    private static final Logger _logger = LogManager.getLogger(ModularController.class);
    private final ConsoleController _consoleController = ConsoleController.getInstance();
    private final String CLASS_ID = ModularController.class.getName();

    private static final String HEDER_GROUP_TITLE = "Current group: ";
    private static final String ABOUT_POPUP_TITLE = "About";
    private static final String CSS_PATH = "css/style.css";
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();
    private static final String ABOUT_POPUP_CONTENT = "Contacts: Yurii Pitomets (yurii.pitomets2@halliburton.com)";
    private static final String ABOUT_POPUP_HEADER =
            getProjectNameWithVersion() + " (" + getCommitHash() + "), powered by Luxoft";

    /***********************************************************************************************
     *
     * START OF GROUP-VIEW CONSTANTS BLOCK
     *
     */
    private static final String IMPORT_CHOOSER_TITLE = "Import Group";
    private static final String IMPORT_DIALOG_TITLE = "Import Status Dialog";
    private static final String FAILED_IMPORT_MESSAGE = "Import of group is Failed";

    private static final String REMOVE_GROUP_DIALOG_TITLE = "Remove Group";
    private static final String REMOVE_GROUP_STATUS_DIALOG_TITLE = "Import Status Dialog";
    private static final String FAILED_REMOVE_GROUP_MESSAGE = "Removing of group is Failed";

    private static final String CLONE_WINDOW_TITLE = "Cloning window";
    private static final String FAILED_HEADER_MESSAGE_LOAD_GROUP = "Failed loading cloned groups. ";
    private static final String FAILED_CONTENT_MESSAGE_LOAD_GROUP
            = "These groups may have been moved to another folder or deleted from disc: ";
    /*
     *
     * END OF GROUP-VIEW CONSTANTS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF PROJECTS-VIEW CONSTANTS BLOCK
     *
     */
    private static final String WORK_INDICATOR_START_MESSAGE = "Loading projects...";
    private static final String STATUS_DIALOG_TITLE = "Status dialog";
    private static final String STATUS_DIALOG_HEADER_COMMIT = "Commit statuses";
    private static final String EDIT_PROJECT_PROPERTIES = "Edit project properties";
    private static final String EDIT_POM_SELECTION_WARNING = "This operation unavailable for some projects: ";
    private static final String REVERT_START_MESSAGE = "Revert operation is starting...";
    private static final String REVERT_FINISH_MESSAGE = "Revert operation finished.";
    private static final String NO_ANY_PROJECT_FOR_OPERATION = "There isn't any proper project selected for %s operation";

    private static final String SWITCH_BRANCH_TITLE = "Switch branch";
    private static final String NEW_BRANCH_CREATION = "new branch creation";
    private static final String PULL_OPERATION_NAME = "pull";
    private static final String PUSH_OPERATION_NAME = "push";
    private static final String SWITCH_BEANCH_OPERATION_NAME = "switch branch";

    private static final String SELECT_ALL_IMAGE_URL = "icons/select_all_20x20.png";
    private static final String REFRESH_PROJECTS_IMAGE_URL = "icons/toolbar/refresh_projects_20x20.png";
    private static final String FILTER_SHADOW_PROJECTS_IMAGE_URL = "icons/toolbar/filter_shadow_projects_20x20.png";
    private static final String DIVIDER_PROPERTY_NODE = "MainWindowController_Dividers";
    /*
     *
     * END OF GROUP-VIEW CONSTANTS BLOCK
     *
     ***********************************************************************************************/


    /***********************************************************************************************
     *
     * START OF SERVICES BLOCK
     *
     */
    private static final ConsoleService _consoleService = (ConsoleService) ServiceProvider.getInstance()
            .getService(ConsoleService.class.getName());

    private static final GroupsUserService _groupService = (GroupsUserService) ServiceProvider.getInstance()
            .getService(GroupsUserService.class.getName());

    private static final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    private static final ClonedGroupsService _clonedGroupsService = (ClonedGroupsService) ServiceProvider.getInstance()
            .getService(ClonedGroupsService.class.getName());

    private static final ProjectService _projectService = (ProjectService) ServiceProvider.getInstance()
            .getService(ProjectService.class.getName());

    private static final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
            .getService(LoginService.class.getName());

    private static final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private static final PomXMLService _pomXmlService = (PomXMLService) ServiceProvider.getInstance()
            .getService(PomXMLService.class.getName());
    /*
     *
     * END OF GROUP-VIEW CONSTANTS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF FXML UI ELEMENTS BLOCK
     *
     */

    @FXML
    private Label userId;

    @FXML
    private Label groupPath;

    @FXML
    private VBox topBackground;

    @FXML
    private BorderPane mainPanelBackground;

    @FXML
    private ListView projectListView;

    @FXML
    private ListView groupListView;

    @FXML
    private Pane listPane;

    @FXML
    public TextFlow _console;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public ToolBar toolbar;

    @FXML
    public SplitPane parentPane;

    @FXML
    public BorderPane background;

    @FXML
    public MenuBar menuBar;

    /*
     *
     * END OF GROUP-VIEW CONSTANTS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF OTHER UI ELEMENTS BLOCK
     *
     */
    private List<Node> projectsWindowToolbarItems = new LinkedList<>();
    private List<Node> groupsWindowToolbarItems = new LinkedList<>();

    private List<Menu> projectsWindowMainMenuItems = new LinkedList<>();
    private List<Menu> groupsWindowMainMenuItems = new LinkedList<>();

    private HBox projectsToolbar;
    private WorkIndicatorDialog _workIndicatorDialog;
    /*
     *
     * END OF GROUP-VIEW CONSTANTS BLOCK
     *
     ***********************************************************************************************/

    private Preferences preferences;
    private Group _currentGroup;

    /***********************************************************************************************
     *
     * START OF INITIALIZATION BLOCK
     *
     */

    @FXML
    public void initialize() {

        _stateService.addStateListener(ApplicationState.CLONE, new CloneStateListener());
        toolbar.getStylesheets().add(getClass().getClassLoader().getResource(CSS_PATH).toExternalForm());

        userId.setText(_loginService.getCurrentUser().getName());

        initializeProjectsWindow();
        initializeGroupsWindow();

        updateCurrentConsole();
    }

    private void initializeProjectsWindow() {
        projectListView = new ListView();
        AnchorPane.setBottomAnchor(projectListView, 0.0);
        AnchorPane.setTopAnchor(projectListView, 30.0);
        AnchorPane.setLeftAnchor(projectListView, 0.0);
        AnchorPane.setRightAnchor(projectListView, 0.0);
        configureProjectsListView(projectListView);
        projectsWindowToolbarItems = ToolbarManager.getInstance().createToolbarItems(ViewKey.MAIN_WINDOW.getKey());
        initActionsToolBar(ViewKey.MAIN_WINDOW.getKey());

        projectsWindowMainMenuItems = MainMenuManager.getInstance().createMainMenuItems(ViewKey.MAIN_WINDOW.getKey());
        initActionsMainMenu(ViewKey.MAIN_WINDOW.getKey());
    }

    private void initializeGroupsWindow() {
        groupListView = new ListView();
        AnchorPane.setBottomAnchor(groupListView, 0.0);
        AnchorPane.setTopAnchor(groupListView, 0.0);
        AnchorPane.setLeftAnchor(groupListView, 0.0);
        AnchorPane.setRightAnchor(groupListView, 0.0);

        configureGroupListView(groupListView);

        groupsWindowToolbarItems = ToolbarManager.getInstance().createToolbarItems(ViewKey.GROUP_WINDOW.getKey());
        groupsWindowMainMenuItems = MainMenuManager.getInstance().createMainMenuItems(ViewKey.GROUP_WINDOW.getKey());

        initActionsToolBar(ViewKey.GROUP_WINDOW.getKey());
        initActionsMainMenu(ViewKey.GROUP_WINDOW.getKey());
        initProjectsToolbar();

        initializeGroupsDisableBinding(groupListView);
    }

    private void initProjectsToolbar() {
        if (projectsToolbar != null) {
            return;
        }

        projectsToolbar = new HBox();

        Image imageRefreshProjects = new Image(
                getClass().getClassLoader().getResource(REFRESH_PROJECTS_IMAGE_URL).toExternalForm());
        Image imageSelectAll = new Image(
                getClass().getClassLoader().getResource(SELECT_ALL_IMAGE_URL).toExternalForm());
        Image imageFilterShadow = new Image(
                getClass().getClassLoader().getResource(FILTER_SHADOW_PROJECTS_IMAGE_URL).toExternalForm());

        ToggleButton selectAllButton = new ToggleButton();
        selectAllButton.setTooltip(new Tooltip("Select all projects"));
        selectAllButton.setGraphic(new ImageView(imageSelectAll));

        Button refreshProjectsButton = new Button();
        refreshProjectsButton.setTooltip(new Tooltip("Refresh projects"));
        refreshProjectsButton.setGraphic(new ImageView(imageRefreshProjects));

        ToggleButton filterShadowProjects = new ToggleButton();
        filterShadowProjects.setTooltip(new Tooltip("Enable\\disable shadow projects"));
        filterShadowProjects.setGraphic(new ImageView(imageFilterShadow));

        projectsToolbar.getChildren().addAll(selectAllButton, refreshProjectsButton, filterShadowProjects);

    }

    private void initActionsToolBar(String windowId) {
        if (windowId.equals(ViewKey.GROUP_WINDOW.getKey())) {
            ToolbarManager.getInstance().getButtonById(ToolbarButtons.IMPORT_GROUP_BUTTON.getId())
                    .setOnAction(this::importGroupDialog);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.REMOVE_GROUP_BUTTON.getId())
                    .setOnAction(this::onRemoveGroup);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.CLONE_GROUP_BUTTON.getId())
                    .setOnAction(this::onCloneGroups);

        } else if (windowId.equals(ViewKey.MAIN_WINDOW.getKey())) {
            ToolbarManager.getInstance().getButtonById(ToolbarButtons.CHANGE_GROUP_BUTTON.getId())
                    .setOnAction(event -> loadGroupWindow());

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.SWITCH_BRANCH_BUTTON.getId())
                    .setOnAction(this::showSwitchBranchWindow);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.CLONE_PROJECT_BUTTON.getId())
                    .setOnAction(this::cloneShadowProject);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.NEW_BRANCH_BUTTON.getId())
                    .setOnAction(this::onNewBranchButton);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.CREATE_PROJECT_BUTTON.getId())
                    .setOnAction(this::createProjectButton);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.COMMIT_BUTTON.getId())
                    .setOnAction(this::onCommitAction);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.PUSH_BUTTON.getId())
                    .setOnAction(this::onPushAction);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.PULL_BUTTON.getId())
                    .setOnAction(this::onPullAction);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.REVERT_CHANGES.getId())
                    .setOnAction(this::onRevertChanges);

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.EDIT_PROJECT_PROPERTIES_BUTTON.getId())
                    .setOnAction(this::showEditProjectPropertiesWindow);
        }
    }

    private void initActionsMainMenu(String windowId) {
        if (windowId.equals(ViewKey.GROUP_WINDOW.getKey())) {
            MenuItem cloneGroup = MainMenuManager.getInstance().getButtonById(MainMenuItems.GROUP_WINDOW_CLONE_GROUP);
            cloneGroup.setOnAction(this::onCloneGroups);

            MenuItem exit = MainMenuManager.getInstance().getButtonById(MainMenuItems.GROUP_WINDOW_EXIT);
            exit.setOnAction(event -> exit());

            MenuItem about = MainMenuManager.getInstance().getButtonById(MainMenuItems.GROUP_WINDOW_ABOUT);
            about.setOnAction(event -> showAboutPopup());

            MenuItem userGuide = MainMenuManager.getInstance().getButtonById(MainMenuItems.GROUP_WINDOW_USER_GUIDE);
            userGuide.setOnAction(event -> UserGuideUtil.openUserGuide());
            userGuide.setAccelerator(new KeyCodeCombination(KeyCode.F1));

        } else if (windowId.equals(ViewKey.MAIN_WINDOW.getKey())) {
            MenuItem exit = MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_EXIT);
            exit.setOnAction(event -> exit());

            MenuItem about = MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_ABOUT);
            about.setOnAction(event -> showAboutPopup());

            MenuItem userGuide = MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_USER_GUIDE);
            userGuide.setOnAction(event -> UserGuideUtil.openUserGuide());
            userGuide.setAccelerator(new KeyCodeCombination(KeyCode.F1));

            MenuItem switchTo = MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_SWITCH_BRANCH);
            switchTo.setOnAction(this::showSwitchBranchWindow);

            MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CLONE_PROJECT)
                    .setOnAction(this::cloneShadowProject);

            MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CREATE_BRANCH)
                    .setOnAction(this::onNewBranchButton);

            MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_COMMIT)
                    .setOnAction(this::onCommitAction);

            MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_PUSH)
                    .setOnAction(this::onPushAction);

            MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_PULL)
                    .setOnAction(this::onPullAction);

            MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_REVERT)
                    .setOnAction(this::onRevertChanges);

        }
    }

    private void initializeGroupsDisableBinding(ListView listView) {
        BooleanBinding groupListBooleanBinding = listView.getSelectionModel().selectedItemProperty().isNull();
        ToolbarManager.getInstance().getAllButtonsForCurrentView().stream()
                .filter(x -> x.getId().equals(ToolbarButtons.REMOVE_GROUP_BUTTON.getId())
                        || x.getId().equals(ToolbarButtons.SELECT_GROUP_BUTTON.getId()))
                .forEach(x -> x.disableProperty().bind(groupListBooleanBinding));
    }

    /*
     *
     * END OF INITIALIZATION BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF VIEW SWITCHING BLOCK
     *
     */

    private void loadGroup(Group group) {
        _currentGroup = group;

        toolbar.getItems().clear();
        menuBar.getMenus().clear();

        toolbar.getItems().addAll(projectsWindowToolbarItems);
        menuBar.getMenus().addAll(projectsWindowMainMenuItems);

        _projectService.addUpdateProgressListener(this);

        String groupTitle = group.getName() + " [" + group.getPathToClonedGroup() + "]";
        groupPath.setText(HEDER_GROUP_TITLE + groupTitle);

        mainPanelBackground.setEffect(new GaussianBlur());
        topBackground.setEffect(new GaussianBlur());

        Stage stage = (Stage) toolbar.getScene().getWindow();

        _workIndicatorDialog = new WorkIndicatorDialog(stage, WORK_INDICATOR_START_MESSAGE);
        Runnable selectGroup = () -> {
            ProjectList.reset();
            ProjectList _projectsList = ProjectList.get(group);

            // UI updating
            Platform.runLater(() -> {
                projectListView.setItems(FXCollections.observableArrayList(_projectsList.getProjects()));
                mainPanelBackground.setEffect(null);
                topBackground.setEffect(null);
                _projectService.removeUpdateProgressListener(this);

            });
        };
        _workIndicatorDialog.executeAndShowDialog("Loading group", selectGroup, StageStyle.TRANSPARENT, stage);

        listPane.getChildren().clear();
        listPane.getChildren().add(projectListView);
        listPane.getChildren().add(projectsToolbar);

        setupProjectsDividerPosition(groupTitle);

    }

    public void loadGroupWindow() {
        toolbar.getItems().clear();
        menuBar.getMenus().clear();
        toolbar.getItems().addAll(groupsWindowToolbarItems);
        menuBar.getMenus().addAll(groupsWindowMainMenuItems);

        updateClonedGroups();

        listPane.getChildren().clear();
        listPane.getChildren().add(groupListView);

    }

    private void setupProjectsDividerPosition(String groupTitle){
        preferences = getPreferences(DIVIDER_PROPERTY_NODE);

        if (preferences != null) {
            String key = String.valueOf(groupTitle.hashCode());
            double splitPaneDivider = preferences.getDouble(key, 0.3);
            parentPane.setDividerPositions(splitPaneDivider);
        }

        parentPane.getDividers().get(1).positionProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (preferences != null) {
                        String key = String.valueOf(groupTitle.hashCode());
                        Double value = round(newValue.doubleValue(), 3);

                        preferences.putDouble(key, value);
                    }
                });
    }

    /*
     *
     * END OF VIEW SWITCHING BLOCK
     *
     ***********************************************************************************************/

    private static double round(double value, int places) {
        if (places < 0) {
            _logger.error("Incorrect input format");
            return value;
        }

        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    // Gets preferences by key, could return null
    private Preferences getPreferences(String key) {
        try {
            return Preferences.userRoot().node(key);
        } catch (IllegalArgumentException iae) {
            _logger.error("Consecutive slashes in path");
            return null;
        } catch (IllegalStateException ise) {
            _logger.error("Node has been removed with the removeNode() method");
            return null;
        } catch (NullPointerException npe) {
            _logger.error("Key is null");
            return null;
        }
    }

    private void updateClonedGroups() {

        List<Group> userGroups = _clonedGroupsService.loadClonedGroups();
        if (userGroups != null) {
            groupListView.setItems(FXCollections.observableList(userGroups));
            showNotExistGroups(_clonedGroupsService.getNotExistGroup());
        }

    }


    private void showNotExistGroups(Collection<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return;
        }
        StringBuffer infoAboutGroups = new StringBuffer();
        groups.forEach(group -> infoAboutGroups.append(group.getName() + " (" + group.getPathToClonedGroup() + ");"));
        _logger.warn(FAILED_HEADER_MESSAGE_LOAD_GROUP + FAILED_CONTENT_MESSAGE_LOAD_GROUP + infoAboutGroups);

        String namesAndPathsGroups = infoAboutGroups.toString().replace(";", ";\n\r");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert dialog = new StatusDialog(FAILED_HEADER_MESSAGE_LOAD_GROUP, FAILED_CONTENT_MESSAGE_LOAD_GROUP, namesAndPathsGroups);
                dialog.showAndWait();
            }
        });
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

        /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 150);

        alert.show();
    }

    private void showStatusDialog(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(_appIcon);

            /* Set sizing and position */
            ScreenUtil.adaptForMultiScreens(stage, 300, 150);

            alert.showAndWait();
        });
    }

    private void updateCurrentConsole() {
        _consoleController.setComponents(_console, scrollPane);
        _consoleController.updateConsole();
    }

    private void exit() {
        List<ApplicationState> activeStates = _stateService.getActiveStates();
        if (activeStates.isEmpty()) {
            Platform.exit();
        }
        JavaFXUI.showWarningAlertForActiveStates(activeStates);
    }

    @Override
    public void updateProgress(String progressMessage) {
        if (_workIndicatorDialog != null && progressMessage != null) {
            _workIndicatorDialog.updateProjectLabel(progressMessage);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((CLASS_ID == null) ? 0 : CLASS_ID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ModularController other = (ModularController) obj;
        if (CLASS_ID == null) {
            if (other.CLASS_ID != null) {
                return false;
            }
        } else if (!CLASS_ID.equals(other.CLASS_ID)) {
            return false;
        }
        return true;
    }


    private void configureGroupListView(ListView listView) {
        // config displayable string
        listView.setCellFactory(p -> new GroupListCell());
    }

    private void configureProjectsListView(ListView listView) {
        // config displayable string
        listView.getItems().clear();
        listView.setCellFactory(p -> new ProjectListCell());
        listView.refresh();
    }

    private class GroupListCell extends ListCell<Group> {
        final Tooltip tooltip = new Tooltip();

        @Override
        public void updateItem(Group item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setTooltip(null);
                return;

            }
            Text groupNameText = new Text(item.getName());
            groupNameText.setFont(Font.font(Font.getDefault().getFamily(), 14));

            String localPath = item.getPathToClonedGroup();
            Text localPathText = new Text(localPath);

            VBox vboxItem = new VBox(groupNameText, localPathText);
            setGraphic(vboxItem);

            tooltip.setText(item.getName() + " (" + localPath + ")");
            setTooltip(tooltip);

            setOnMouseClicked(loadGroupByDblClickHandler());
        }

        private EventHandler<? super MouseEvent> loadGroupByDblClickHandler() {
            return event -> {
                if (event.getButton() == MouseButton.PRIMARY &&
                        event.getClickCount() > 1) {
                    ListCell<?> c = (ListCell<?>) event.getSource();
                    Object rawGroup = c.getItem();
                    if (!Group.class.isInstance(rawGroup)) {
                        return;
                    }
                    Group selectedGroup = (Group) rawGroup;
                    loadGroup(selectedGroup);
                }
            };
        }

        private void showGroupWindow(Button showWelcomButton) throws IOException {
            loadGroupWindow();
        }
    }

    /***********************************************************************************************
     *
     * START OF GROUPS-VIEW ACTIONS BLOCK
     *
     */

    @FXML
    private void onCloneGroups(ActionEvent actionEvent) {
        URL cloningGroupsWindowUrl = getClass().getClassLoader().getResource(ViewKey.CLONING_GROUPS_WINDOW.getPath());
        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();

        if (cloningGroupsWindowUrl == null) {
            return;
        }

        try {
            Parent root = FXMLLoader.load(cloningGroupsWindowUrl);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle(CLONE_WINDOW_TITLE);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.getIcons().add(appIcon);

             /* Set sizing and position */
            double dialogWidth = 500;
            double dialogHeight = 400;

            ScreenUtil.adaptForMultiScreens(stage, dialogWidth, dialogHeight);

            stage.setHeight(dialogHeight);
            stage.setWidth(dialogWidth);

            stage.show();
        } catch (IOException e) {
            _logger.error("Could not load fxml resource: " + e.getMessage());
        }
    }

    @FXML
    private void onRemoveGroup(ActionEvent actionEvent) {
        Group group = (Group) groupListView.getSelectionModel().getSelectedItem();

        AlertWithCheckBox alert = new AlertWithCheckBox(AlertType.CONFIRMATION,
                REMOVE_GROUP_DIALOG_TITLE,
                "You want to remove the " + group.getName() + " group.",
                "Are you sure you want to delete it?",
                "remove group from a local disk",
                ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.NO) {
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Map<Boolean, String> status = _groupService.removeGroup(group, alert.isCheckBoxSelected());
            for (Entry<Boolean, String> mapStatus : status.entrySet()) {
                String headerMessage;
                if (!mapStatus.getKey()) {
                    headerMessage = FAILED_REMOVE_GROUP_MESSAGE;
                    showStatusDialog(REMOVE_GROUP_STATUS_DIALOG_TITLE, headerMessage, mapStatus.getValue());
                }
                updateClonedGroups();
            }
        });
        executor.shutdown();
    }

    @FXML
    private void importGroupDialog(ActionEvent actionEvent) {
        if (mainPanelBackground == null) {
            return;
        }
        Stage stage = (Stage) mainPanelBackground.getScene().getWindow();
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
                _consoleService.addMessage(FAILED_IMPORT_MESSAGE, MessageType.ERROR);
                showStatusDialog(IMPORT_DIALOG_TITLE, FAILED_IMPORT_MESSAGE,
                        "Failed to load group from " + selectedDirectory.getAbsolutePath());
            } else {
                Platform.runLater(() -> {
                    _consoleService.addMessage("Group successfully loaded.", MessageType.SUCCESS);
                    updateClonedGroups();
                });
            }
        });
        executor.shutdown();
    }

    /*
     *
     * END OF GROUPS-VIEW ACTIONS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF PROJECTS-VIEW ACTIONS BLOCK
     *
     */
    @FXML
    private void showSwitchBranchWindow(ActionEvent event) {
        try {
            List<Project> projects = projectListView.getSelectionModel().getSelectedItems();
            projects = ProjectList.getCorrectProjects(projects);
            if (projects.isEmpty()) {
                String message = String.format(MainWindowController.NO_ANY_PROJECT_FOR_OPERATION,
                        MainWindowController.SWITCH_BEANCH_OPERATION_NAME);
                _consoleService.addMessage(message, MessageType.ERROR);
                return;
            }

            URL switchBranchWindowUrl = getClass().getClassLoader().getResource(ViewKey.SWITCH_BRANCH_WINDOW.getPath());
            FXMLLoader loader = new FXMLLoader(switchBranchWindowUrl);
            Parent root = loader.load();

            SwitchBranchWindowController switchWindowController = loader.getController();
            switchWindowController.beforeShowing(projects);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(_appIcon);
            stage.setTitle(SWITCH_BRANCH_TITLE);
            stage.initModality(Modality.APPLICATION_MODAL);

            /* Set size and position */
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double dialogWidth = primaryScreenBounds.getMaxX() / 1.5;
            double dialogHeight = primaryScreenBounds.getMaxY() / 1.5;

            ScreenUtil.adaptForMultiScreens(stage, dialogWidth, dialogHeight);

            stage.setWidth(dialogWidth);
            stage.setHeight(dialogHeight);
            stage.setMinWidth(dialogWidth / 2);
            stage.setMinHeight(dialogHeight / 2);

            stage.show();
        } catch (IOException e) {
            _logger.error("Could not load fxml resource", e);
        }
    }

    @FXML
    private void cloneShadowProject(ActionEvent actionEvent) {
        List<Project> shadowProjects = getCurrentProjects().stream()
                .filter(project -> !project.isCloned())
                .collect(Collectors.toList());
        if (shadowProjects == null || shadowProjects.isEmpty()) {
            _consoleService.addMessage("Shadow projects for cloning have not been selected!", MessageType.ERROR);
            return;
        }
        String path = _currentGroup.getPathToClonedGroup();

        CloneProgressDialog progressDialog = new CloneProgressDialog();
        progressDialog.setStartAction(() -> startClone(shadowProjects, path, progressDialog));
        progressDialog.showDialog();
    }

    @FXML
    private void onNewBranchButton(ActionEvent actionEvent) {
        List<Project> clonedProjectsWithoutConflicts = ProjectList.getCorrectProjects(getCurrentProjects());
        if (!clonedProjectsWithoutConflicts.isEmpty()) {
            CreateNewBranchDialog dialog = new CreateNewBranchDialog(clonedProjectsWithoutConflicts);
            dialog.showAndWait();
        } else {
            _consoleService.addMessage(String.format(NO_ANY_PROJECT_FOR_OPERATION, NEW_BRANCH_CREATION), MessageType.ERROR);
        }
    }

    @FXML
    public void createProjectButton(ActionEvent actionEvent) {
        // dialog
        CreateProjectDialog dialog = new CreateProjectDialog(_currentGroup, null);
        dialog.showAndWait();
    }

    @FXML
    public void onCommitAction(ActionEvent actionEvent) {
        List<Project> projectWithChanges = _gitService.getProjectsWithChanges(getCurrentProjects());
        if (projectWithChanges.isEmpty()) {
            showProjectsWithoutChangesMessage();
            return;
        }

        CommitDialog dialog = new CommitDialog();
        dialog.commitChanges(projectWithChanges);
    }

    @FXML
    public void onPushAction(ActionEvent actionEvent) {
        List<Project> filteredProjects = ProjectList.getCorrectProjects(getCurrentProjects());
        if (!filteredProjects.isEmpty()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> _gitService.push(filteredProjects, new PushProgressListener()));
            executor.shutdown();
        } else {
            _consoleService.addMessage(String.format(NO_ANY_PROJECT_FOR_OPERATION, PUSH_OPERATION_NAME), MessageType.ERROR);
        }
    }

    @FXML
    public void onPullAction(ActionEvent actionEvent) {
        List<Project> projectsToPull = ProjectList.getCorrectProjects(getCurrentProjects());
        if (!projectsToPull.isEmpty()) {
            checkChangesAndPull(projectsToPull, new Object());
        } else {
            _consoleService.addMessage(String.format(NO_ANY_PROJECT_FOR_OPERATION, PULL_OPERATION_NAME), MessageType.ERROR);
        }
    }

    @FXML
    public void onRevertChanges(ActionEvent actionEvent) {
        GLTAlert alert = new GLTAlert(AlertType.CONFIRMATION, ApplicationState.REVERT.toString(),
                "Revert changes for selected projects", "Are you sure you want to do it?");
        alert.addButtons(ButtonType.CANCEL);
        alert.setTextButton(ButtonType.OK, "Revert");
        // check that user press OK button
        if(!alert.isOKButtonPressed(alert.showAndWait())) {
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> revert());
        executor.shutdown();
    }

    private void showEditProjectPropertiesWindow(ActionEvent event) {
        try {

            URL switchBranchWindowUrl = getClass().getClassLoader().getResource(ViewKey.EDIT_PROJECT_PROPERTIES.getPath());
            FXMLLoader loader = new FXMLLoader(switchBranchWindowUrl);
            Parent root = loader.load();

            EditProjectPropertiesController controller = loader.getController();

            List<Project> unavailableProjects = getUnavalibleProjectsForEditingPom(getCurrentProjects());
            if(!unavailableProjects.isEmpty()){
                String failedProjectsNames = unavailableProjects.stream()
                        .map(Project :: getName)
                        .collect(Collectors.toList())
                        .toString();
                _consoleService.addMessage(EDIT_POM_SELECTION_WARNING + failedProjectsNames, MessageType.ERROR);
                return;
            }

            controller.beforeStart(getIdSelectedProjects());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(AppIconHolder.getInstance().getAppIcoImage());
            stage.setTitle(EDIT_PROJECT_PROPERTIES);
            stage.initModality(Modality.APPLICATION_MODAL);

            /* Set size and position */
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double dialogWidth = primaryScreenBounds.getMaxX() / 2;
            double dialogHeight = primaryScreenBounds.getMaxY() / 2;

            ScreenUtil.adaptForMultiScreens(stage, dialogWidth, dialogHeight);

            stage.setWidth(dialogWidth);
            stage.setHeight(dialogHeight);
            stage.setMinWidth(dialogWidth / 2);
            stage.setMinHeight(dialogHeight / 2);

            stage.show();
        } catch (IOException e) {
            _logger.error("Could not load fxml resource", e);
        }
    }

    /*
     *
     * END OF PROJECTS-VIEW ACTIONS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF PROJECTS-VIEW OTHER METHODS BLOCK
     *
     */

    // USE ONLY FOR UNREPEATABLE OPERATION (like Revert, Push etc.)
    private List<Project> getCurrentProjects() {
        return projectListView.getSelectionModel().getSelectedItems();
    }

    // USE FOR REPEATABLE OPERATION (like SwitchBranch, EditPomXML etc.)
    private List<Integer> getIdSelectedProjects() {
        return ProjectList.getIdsProjects(getCurrentProjects());
    }

    private boolean startClone(List<Project> shadowProjects, String path,  CloneProgressDialog progressDialog) {
        _projectService.clone(shadowProjects, path,
                new OperationProgressListener(progressDialog, ApplicationState.CLONE, null));
        return true;
    }

    private void showProjectsWithoutChangesMessage() {
        String noChangesMessage = "Selected projects do not have changes";
        _consoleService.addMessage("Selected projects do not have changes", MessageType.SIMPLE);
        StatusDialog statusDialog = new StatusDialog(STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER_COMMIT,
                noChangesMessage);
        statusDialog.showAndWait();
    }

    private void finishAction(String message, MessageType type, ApplicationState state) {
        _stateService.stateOFF(state);
        _consoleService.addMessage(message, type);
    }

    private void checkChangesAndPull(List<Project> projects, Object item) {
        List<Project> changedProjects = _gitService.getProjectsWithChanges(projects);

        if (changedProjects.isEmpty()) {
            ProgressDialog progressDialog = new PullProgressDialog();
            progressDialog.setStartAction(() -> startPull(projects, progressDialog));
            progressDialog.showDialog();
        } else {
            ChangesCheckDialog changesCheckDialog = new ChangesCheckDialog();
            changesCheckDialog.launchConfirmationDialog(changedProjects, projects, item, this::checkChangesAndPull);
        }
    }

    private void startPull(List<Project> projects, ProgressDialog progressDialog) {
        OperationProgressListener pullProgressListener =
                new OperationProgressListener(progressDialog, ApplicationState.PULL);
        _gitService.pull(projects, pullProgressListener);
    }

    private void revert() {
        _stateService.stateON(ApplicationState.REVERT);
        _consoleService.addMessage(REVERT_START_MESSAGE, MessageType.SIMPLE);

        List<Project> correctProjects = ProjectList.getCorrectProjects(getCurrentProjects());
        List<Project> projectsWithChanges = _gitService.getProjectsWithChanges(correctProjects);
        _consoleService.addMessage(projectsWithChanges.size() + " selected projects have changes.", MessageType.SIMPLE);
        if (projectsWithChanges.isEmpty()) {
            finishAction(REVERT_FINISH_MESSAGE, MessageType.SIMPLE, ApplicationState.REVERT);
            return;
        }
        Map<Project, JGitStatus> statuses = _gitService.revertChanges(projectsWithChanges);
        _consoleService.addMessagesForStatuses(statuses, "Reverting changes");
        finishAction(REVERT_FINISH_MESSAGE, MessageType.SIMPLE, ApplicationState.REVERT);
    }

    private List<Project> getUnavalibleProjectsForEditingPom(List<Project> projects) {
        return projects.parallelStream()
                .filter(project -> !ProjectList.projectIsClonedAndWithoutConflicts(project)
                        || !_pomXmlService.hasPomFile(project))
                .collect(Collectors.toList());
    }

    /*
     *
     * END OF PROJECTS-VIEW OTHER METHODS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF INNER CLASSES BLOCK
     *
     */
    private class CloneStateListener implements StateListener {

        @Override
        public void handleEvent(ApplicationState changedState, boolean isActivate) {
            if (!isActivate) {
                updateClonedGroups();
            }
        }
    }

    public class PushProgressListener implements ProgressListener {

        public PushProgressListener() {
            _consoleService.addMessage("Push projects is started...", MessageType.SIMPLE);
            _stateService.stateON(ApplicationState.PUSH);
        }

        @Override
        public void onSuccess(Object... t) {
            if(t[0] instanceof Project) {
                String message = "Pushing the " + ((Project)t[0]).getName() + " project is successful!";
                _consoleService.addMessage(message, MessageType.SUCCESS);
            }
        }

        @Override
        public void onError(Object... t) {
            if(t[0] instanceof Project) {
                String message = "Failed pushing the " + ((Project)t[0]).getName() + " project!";
                _consoleService.addMessage(message, MessageType.ERROR);
            }
        }

        @Override
        public void onStart(Object... t) {}

        @Override
        public void onFinish(Object... t) {
            finishAction("Push projects is finished!", MessageType.SIMPLE, ApplicationState.PUSH);
        }
    }

    /*
     *
     * END OF INNER CLASSES BLOCK
     *
     ***********************************************************************************************/
}
