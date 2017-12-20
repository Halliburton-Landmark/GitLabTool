package com.lgc.gitlabtool.git.ui.javafx.controllers;

import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getCommitHash;
import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getProjectNameWithVersion;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.lgc.gitlabtool.git.services.BackgroundService;
import com.lgc.gitlabtool.git.services.ClonedGroupsService;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.GroupsUserService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.PomXMLService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.AlertWithCheckBox;
import com.lgc.gitlabtool.git.ui.javafx.ChangesCheckDialog;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateNewBranchDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateProjectDialog;
import com.lgc.gitlabtool.git.ui.javafx.GLTAlert;
import com.lgc.gitlabtool.git.ui.javafx.IncorrectProjectDialog;
import com.lgc.gitlabtool.git.ui.javafx.JavaFXUI;
import com.lgc.gitlabtool.git.ui.javafx.ProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.PullProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.javafx.WorkIndicatorDialog;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ProjectListComparator;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.listeners.PushProgressListener;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuItems;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuManager;
import com.lgc.gitlabtool.git.ui.selection.ListViewKey;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import com.lgc.gitlabtool.git.util.ScreenUtil;
import com.lgc.gitlabtool.git.util.ShutDownUtil;
import com.lgc.gitlabtool.git.util.UserGuideUtil;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
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

    private static final String HEADER_GROUP_TITLE = "Current group: ";
    private static final String ABOUT_POPUP_TITLE = "About";
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();
    private static final String ABOUT_POPUP_CONTENT = "Contacts: Yurii Pitomets (yurii.pitomets2@halliburton.com)";
    private static final String ABOUT_POPUP_HEADER =
            getProjectNameWithVersion() + " (" + getCommitHash() + "), powered by Luxoft";

    private static final ToolbarManager _toolbarManager = ToolbarManager.getInstance();
    private static final MainMenuManager _mainMenuManager = MainMenuManager.getInstance();

    /***********************************************************************************************
     *
     * START OF GROUP-VIEW CONSTANTS BLOCK
     *
     */
    //region GROUP-VIEW CONSTANTS
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
    private static final Double DEFAULT_GROUPS_DIVIDER = 0.3;
    //endregion
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
    //region PROJECTS-VIEW CONSTANTS
    private static final String WORK_INDICATOR_START_MESSAGE = "Loading projects...";
    private static final String EDIT_PROJECT_PROPERTIES = "Edit project properties";
    private static final String EDIT_POM_SELECTION_WARNING = "This operation unavailable for some projects: ";
    private static final String REVERT_START_MESSAGE = "Revert operation is starting...";
    private static final String REVERT_FINISH_MESSAGE = "Revert operation finished.";
    public static final String NO_ANY_PROJECT_FOR_OPERATION = "There isn't any proper project selected for %s operation";

    private static final String CHECKOUT_BRANCH_TITLE = "Checkout branch";
    private static final String NEW_BRANCH_CREATION = "new branch creation";
    private static final String PULL_OPERATION_NAME = "pull";
    private static final String PUSH_OPERATION_NAME = "push";
    private static final String CHECKOUT_BEANCH_OPERATION_NAME = "checkout branch";

    private static final String SELECT_ALL_IMAGE_URL = "icons/select_all_20x20.png";
    private static final String REFRESH_PROJECTS_IMAGE_URL = "icons/toolbar/refresh_projects_20x20.png";
    private static final String FILTER_SHADOW_PROJECTS_IMAGE_URL = "icons/toolbar/filter_shadow_projects_20x20.png";
    private static final String DIVIDER_PROPERTY_NODE = "MainWindowController_Dividers";
    private static final String PREF_NAME_HIDE_SHADOWS = "is_hide_shadows";
    ////endregion
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
    //region SERVICES
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

    private static final BackgroundService _backgroundService = (BackgroundService) ServiceProvider.getInstance()
            .getService(BackgroundService.class.getName());
    //endregion
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
    //region FXML UI ELEMENTS

    @FXML
    private Label userId;

    @FXML
    private Label groupPath;

    @FXML
    private VBox topBackground;

    @FXML
    private BorderPane mainPanelBackground;

    @FXML
    private ListView<Project> projectListView;

    @FXML
    private ListView<Group> groupListView;

    @FXML
    private Pane listPane;

    @FXML
    private TextFlow _console;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ToolBar toolbar;

    @FXML
    private MenuBar menuBar;

    @FXML
    private SplitPane dividerMainPane;

    //endregion
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
    //region OTHER UI ELEMENTS
    private List<Node> projectsWindowToolbarItems;
    private List<Node> groupsWindowToolbarItems;

    private List<Menu> projectsWindowMainMenuItems;
    private List<Menu> groupsWindowMainMenuItems;

    private HBox projectsToolbar;
    private WorkIndicatorDialog _workIndicatorDialog;

    private ToggleButton selectAllButton;
    private ToggleButton filterShadowProjects;
    private Button refreshProjectsButton;

    private static String css_path = "css/style.css";
    //endregion
    /*
     *
     * END OF OTHER UI ELEMENTS BLOCK
     *
     ***********************************************************************************************/

    private Preferences preferences;
    private Group _currentGroup;
    private String _currentView;
    private ProjectList _projectsList;
    private StateListener _modularStateListener;

    /***********************************************************************************************
     *
     * START OF INITIALIZATION BLOCK
     *
     */
    //region INITIALIZATION

    @FXML
    @SuppressWarnings("ConstantConditions")
    private void initialize() {
        preferences = getPreferences(DIVIDER_PROPERTY_NODE);

        _stateService.addStateListener(ApplicationState.CLONE, new GroupsWindowStateListener());
        toolbar.getStylesheets().add(getClass().getClassLoader().getResource(css_path).toExternalForm());

        userId.setText(_loginService.getCurrentUser().getName());

        initializeProjectsWindow();
        initializeGroupsWindow();

        loadGroupWindow();
        updateCurrentConsole();
    }

    private void initializeProjectsWindow() {
        projectListView = new ListView<>();
        AnchorPane.setBottomAnchor(projectListView, 0.0);
        AnchorPane.setTopAnchor(projectListView, 30.0);
        AnchorPane.setLeftAnchor(projectListView, 0.0);
        AnchorPane.setRightAnchor(projectListView, 0.0);
        configureProjectsListView(projectListView);
        projectsWindowToolbarItems = _toolbarManager.createToolbarItems(ViewKey.PROJECTS_WINDOW.getKey());
        initActionsToolBar(ViewKey.PROJECTS_WINDOW.getKey());

        projectsWindowMainMenuItems = _mainMenuManager.createMainMenuItems(ViewKey.PROJECTS_WINDOW.getKey());
        initActionsMainMenu(ViewKey.PROJECTS_WINDOW.getKey());

        initProjectsToolbar();
        initProjectsWindowListeners();
        setDisablePropertyForButtons();
    }

    private void initializeGroupsWindow() {
        groupListView = new ListView<>();
        AnchorPane.setBottomAnchor(groupListView, 0.0);
        AnchorPane.setTopAnchor(groupListView, 0.0);
        AnchorPane.setLeftAnchor(groupListView, 0.0);
        AnchorPane.setRightAnchor(groupListView, 0.0);

        configureGroupListView(groupListView);

        groupsWindowToolbarItems = _toolbarManager.createToolbarItems(ViewKey.GROUPS_WINDOW.getKey());
        groupsWindowMainMenuItems = _mainMenuManager.createMainMenuItems(ViewKey.GROUPS_WINDOW.getKey());
        initActionsMainMenu(ViewKey.GROUPS_WINDOW.getKey());
        initActionsToolBar(ViewKey.GROUPS_WINDOW.getKey());
        initializeGroupsDisableBinding(groupListView);
    }

    @SuppressWarnings("ConstantConditions")
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

        selectAllButton = new ToggleButton();
        selectAllButton.setTooltip(new Tooltip("Select all projects"));
        selectAllButton.setGraphic(new ImageView(imageSelectAll));
        selectAllButton.setOnAction(this::onSelectAll);

        refreshProjectsButton = new Button();
        refreshProjectsButton.setTooltip(new Tooltip("Refresh projects"));
        refreshProjectsButton.setGraphic(new ImageView(imageRefreshProjects));
        refreshProjectsButton.setOnAction(this::refreshLoadProjects);

        filterShadowProjects = new ToggleButton();
        filterShadowProjects.setTooltip(new Tooltip("Show/Hide shadow projects"));
        filterShadowProjects.setGraphic(new ImageView(imageFilterShadow));
        filterShadowProjects.setOnAction(this::onShowHideShadowProjects);

        projectsToolbar.getChildren().addAll(selectAllButton, refreshProjectsButton, filterShadowProjects);

    }

    private void initActionsToolBar(String windowId) {
        if (windowId.equals(ViewKey.GROUPS_WINDOW.getKey())) {
            _toolbarManager.getButtonById(ToolbarButtons.IMPORT_GROUP_BUTTON.getId())
                    .setOnAction(this::importGroupDialog);

            _toolbarManager.getButtonById(ToolbarButtons.REMOVE_GROUP_BUTTON.getId())
                    .setOnAction(this::onRemoveGroup);

            _toolbarManager.getButtonById(ToolbarButtons.CLONE_GROUP_BUTTON.getId())
                    .setOnAction(this::onCloneGroups);

            _toolbarManager.getButtonById(ToolbarButtons.SELECT_GROUP_BUTTON.getId())
                    .setOnAction(this::loadGroup);

        } else if (windowId.equals(ViewKey.PROJECTS_WINDOW.getKey())) {
            _toolbarManager.getButtonById(ToolbarButtons.CHANGE_GROUP_BUTTON.getId())
                    .setOnAction(this::loadGroupWindow);

            _toolbarManager.getButtonById(ToolbarButtons.CHECKOUT_BRANCH_BUTTON.getId())
                    .setOnAction(this::showCheckoutBranchWindow);

            _toolbarManager.getButtonById(ToolbarButtons.CLONE_PROJECT_BUTTON.getId())
                    .setOnAction(this::cloneShadowProject);

            _toolbarManager.getButtonById(ToolbarButtons.NEW_BRANCH_BUTTON.getId())
                    .setOnAction(this::onNewBranchButton);

            _toolbarManager.getButtonById(ToolbarButtons.CREATE_PROJECT_BUTTON.getId())
                    .setOnAction(this::createProjectButton);

            _toolbarManager.getButtonById(ToolbarButtons.STAGING_BUTTON.getId())
                    .setOnAction(this::openGitStaging);

            _toolbarManager.getButtonById(ToolbarButtons.PUSH_BUTTON.getId())
                    .setOnAction(this::onPushAction);

            _toolbarManager.getButtonById(ToolbarButtons.PULL_BUTTON.getId())
                    .setOnAction(this::onPullAction);

            _toolbarManager.getButtonById(ToolbarButtons.REVERT_CHANGES.getId())
                    .setOnAction(this::onRevertChanges);

            _toolbarManager.getButtonById(ToolbarButtons.EDIT_PROJECT_PROPERTIES_BUTTON.getId())
                    .setOnAction(this::showEditProjectPropertiesWindow);

        }
    }

    private void initActionsMainMenu(String windowId) {
        if (windowId.equals(ViewKey.GROUPS_WINDOW.getKey())) {
            _mainMenuManager.getButtonById(MainMenuItems.GROUP_WINDOW_CLONE_GROUP).setOnAction(this::onCloneGroups);

        } else if (windowId.equals(ViewKey.PROJECTS_WINDOW.getKey())) {
            _mainMenuManager.getButtonById(MainMenuItems.MAIN_CLONE_PROJECT).setOnAction(this::cloneShadowProject);
            _mainMenuManager.getButtonById(MainMenuItems.MAIN_CREATE_BRANCH).setOnAction(this::onNewBranchButton);
            _mainMenuManager.getButtonById(MainMenuItems.MAIN_STAGING).setOnAction(this::openGitStaging);
            _mainMenuManager.getButtonById(MainMenuItems.MAIN_PUSH).setOnAction(this::onPushAction);
            _mainMenuManager.getButtonById(MainMenuItems.MAIN_PULL).setOnAction(this::onPullAction);
            _mainMenuManager.getButtonById(MainMenuItems.MAIN_REVERT).setOnAction(this::onRevertChanges);
            _mainMenuManager.getButtonById(MainMenuItems.MAIN_CHECKOUT_BRANCH).setOnAction(this::showCheckoutBranchWindow);

        }

        MenuItem userGuide = _mainMenuManager.getButtonById(MainMenuItems.GENERAL_USER_GUIDE);
        userGuide.setOnAction(this::openUserGuide);
        userGuide.setAccelerator(new KeyCodeCombination(KeyCode.F1));
        _mainMenuManager.getButtonById(MainMenuItems.GENERAL_EXIT).setOnAction(this::exit);
        _mainMenuManager.getButtonById(MainMenuItems.GENERAL_ABOUT).setOnAction(this::showAboutPopup);
    }

    private void initializeGroupsDisableBinding(ListView listView) {
        BooleanBinding groupListBooleanBinding = listView.getSelectionModel().selectedItemProperty().isNull();
        _toolbarManager.getAllButtonsForCurrentView().stream()
                .filter(x -> x.getId().equals(ToolbarButtons.REMOVE_GROUP_BUTTON.getId())
                        || x.getId().equals(ToolbarButtons.SELECT_GROUP_BUTTON.getId()))
                .forEach(x -> x.disableProperty().bind(groupListBooleanBinding));
    }

    private void initProjectsWindowListeners() {
        _modularStateListener = new ProjectsWindowStateListener();
    }

    @SuppressWarnings("unchecked")
    private void configureGroupListView(ListView listView) {
        // config displayable string
        listView.setCellFactory(p -> new GroupListCell());
    }

    @SuppressWarnings("unchecked")
    private void configureProjectsListView(ListView listView) {
        // config displayable string
        listView.getItems().clear();
        listView.setCellFactory(p -> new ProjectListCell());

        // setup selection
        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Project>) changed -> {
            if (areAllItemsSelected(listView)) {
                selectAllButton.setSelected(true);
                selectAllButton.setOnAction(this::onDeselectAll);
            } else {
                selectAllButton.setSelected(false);
                selectAllButton.setOnAction(this::onSelectAll);
            }
        });

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            while (node != null && node != listView && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            if (node instanceof ListCell) {

                ListCell<Project> cell = (ListCell<Project>) node;
                ListView<Project> lv = cell.getListView();

                if (evt.getButton() == MouseButton.SECONDARY) {
                    if (!cell.isEmpty()) {
                        List<Project> selectedItems = lv.getSelectionModel().getSelectedItems();
                        lv.setContextMenu(getContextMenu(selectedItems));
                    } else {
                        lv.setContextMenu(null);
                    }
                }
            }
        });

        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Project>) change ->
                {
                    setDisablePropertyForButtons();
                    SelectionsProvider.getInstance().setSelectionItems(ListViewKey.MAIN_WINDOW_PROJECTS.getKey(),
                            listView.getSelectionModel().getSelectedItems());
                }
        );


        listView.refresh();

    }

    public static String getCss_path() {
        return css_path;
    }

    public static void setCss_path(String css_path) {
        ModularController.css_path = css_path;
    }

    //endregion
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
    //region VIEW SWITCHING

    private void loadGroup(Group group) {
        _currentView = ViewKey.PROJECTS_WINDOW.getKey();
        _currentGroup = group;

        toolbar.getItems().clear();
        menuBar.getMenus().clear();

        toolbar.getItems().addAll(projectsWindowToolbarItems);
        menuBar.getMenus().addAll(projectsWindowMainMenuItems);

        _projectService.addUpdateProgressListener(this);

        String groupTitle = group.getName() + " [" + group.getPathToClonedGroup() + "]";
        groupPath.setText(HEADER_GROUP_TITLE + groupTitle);

        mainPanelBackground.setEffect(new GaussianBlur());
        topBackground.setEffect(new GaussianBlur());

        Stage stage = (Stage) toolbar.getScene().getWindow();

        _workIndicatorDialog = new WorkIndicatorDialog(stage, WORK_INDICATOR_START_MESSAGE);
        Runnable selectGroup = () -> {
            ProjectList.reset();
            _projectsList = ProjectList.get(_currentGroup);
            hideShadowsAction();

            // UI updating
            Platform.runLater(() -> {
                //noinspection unchecked
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
        addProjectsWindowListener();

    }

    private void loadGroupWindow() {
        _currentView = ViewKey.GROUPS_WINDOW.getKey();

        toolbar.getItems().clear();
        menuBar.getMenus().clear();
        toolbar.getItems().addAll(groupsWindowToolbarItems);
        menuBar.getMenus().addAll(groupsWindowMainMenuItems);

        removeProjectsWindowListener();
        updateClonedGroups();

        listPane.getChildren().clear();
        listPane.getChildren().add(groupListView);

        dividerMainPane.setDividerPositions(DEFAULT_GROUPS_DIVIDER);
    }

    private void setupProjectsDividerPosition(String groupTitle) {

        if (preferences != null) {
            String key = String.valueOf(groupTitle.hashCode());
            double splitPaneDivider = preferences.getDouble(key, 0.3);
            dividerMainPane.setDividerPositions(splitPaneDivider);
        }

        dividerMainPane.getDividers().get(0).positionProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (preferences != null && Objects.equals(_currentView, ViewKey.PROJECTS_WINDOW.getKey())) {
                        String key = String.valueOf(groupTitle.hashCode());
                        Double value = roundTo3(newValue.doubleValue());

                        preferences.putDouble(key, value);
                    }
                });
    }

    private void addProjectsWindowListener() {
        _stateService.addStateListener(ApplicationState.CLONE, _modularStateListener);
        _stateService.addStateListener(ApplicationState.COMMIT, _modularStateListener);
        _stateService.addStateListener(ApplicationState.PUSH, _modularStateListener);
        _stateService.addStateListener(ApplicationState.PULL, _modularStateListener);
        _stateService.addStateListener(ApplicationState.CREATE_PROJECT, _modularStateListener);
        _stateService.addStateListener(ApplicationState.CHECKOUT_BRANCH, _modularStateListener);
        _stateService.addStateListener(ApplicationState.EDIT_POM, _modularStateListener);
        _stateService.addStateListener(ApplicationState.REVERT, _modularStateListener);
        _stateService.addStateListener(ApplicationState.LOAD_PROJECTS, _modularStateListener);
        _stateService.addStateListener(ApplicationState.UPDATE_PROJECT_STATUSES, _modularStateListener);
    }

    private void removeProjectsWindowListener() {
        _stateService.removeStateListener(ApplicationState.CLONE, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.COMMIT, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.PUSH, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.PULL, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.CREATE_PROJECT, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.CHECKOUT_BRANCH, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.EDIT_POM, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.REVERT, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.LOAD_PROJECTS, _modularStateListener);
        _stateService.removeStateListener(ApplicationState.UPDATE_PROJECT_STATUSES, _modularStateListener);
    }

    //endregion
    /*
     *
     * END OF VIEW SWITCHING BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF GROUPS-VIEW ACTIONS BLOCK
     *
     */
    //region GROUPS-VIEW ACTIONS

    @FXML
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    private void onRemoveGroup(ActionEvent actionEvent) {
        Group group = groupListView.getSelectionModel().getSelectedItem();

        AlertWithCheckBox alert = new AlertWithCheckBox(AlertType.CONFIRMATION,
                REMOVE_GROUP_DIALOG_TITLE,
                "You want to remove the " + group.getName() + " group.",
                "Are you sure you want to delete it?",
                "remove group from a local disk",
                ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(ButtonType.NO) == ButtonType.NO) {
            return;
        }
        _backgroundService.runInBackgroundThread(() -> {
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
    }

    @SuppressWarnings("unused")
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

        _backgroundService.runInBackgroundThread(() -> {

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
    }

    @FXML
    @SuppressWarnings("unused")
    private void loadGroupWindow(ActionEvent actionEvent) {
        loadGroupWindow();
    }

    @FXML
    @SuppressWarnings("unused")
    private void loadGroup(ActionEvent actionEvent) {
        Group selectedGroup = groupListView.getSelectionModel().getSelectedItem();
        loadGroup(selectedGroup);
    }

    //endregion
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
    //region PROJECT-VIEW ACTIONS
    @FXML
    @SuppressWarnings("unused")
    private void showCheckoutBranchWindow(ActionEvent event) {
        try {
            List<Project> projects = projectListView.getSelectionModel().getSelectedItems();
            projects = ProjectList.getCorrectProjects(projects);
            if (projects.isEmpty()) {
                String message = String.format(NO_ANY_PROJECT_FOR_OPERATION, CHECKOUT_BEANCH_OPERATION_NAME);
                _consoleService.addMessage(message, MessageType.ERROR);
                return;
            }

            URL checkoutBranchWindowUrl = getClass().getClassLoader().getResource(ViewKey.CHECKOUT_BRANCH_WINDOW.getPath());
            FXMLLoader loader = new FXMLLoader(checkoutBranchWindowUrl);
            Parent root = loader.load();

            CheckoutBranchWindowController checkoutWindowController  = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(_appIcon);
            stage.setTitle(CHECKOUT_BRANCH_TITLE);
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

            checkoutWindowController.beforeShowing(projects, stage);
            stage.show();
        } catch (IOException e) {
            _logger.error("Could not load fxml resource", e);
        }
    }

    @FXML
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    private void createProjectButton(ActionEvent actionEvent) {
        CreateProjectDialog dialog = new CreateProjectDialog(_currentGroup, null);
        dialog.showAndWait();
    }

    @FXML
    @SuppressWarnings("unused")
    private void openGitStaging(ActionEvent actionEvent) {
        WindowLoader.get().loadGitStageWindow(null);
    }

    @FXML
    @SuppressWarnings("unused")
    private void onPushAction(ActionEvent actionEvent) {
        List<Project> filteredProjects = ProjectList.getCorrectProjects(getCurrentProjects());
        if (!filteredProjects.isEmpty()) {
            _backgroundService.runInBackgroundThread(() -> _gitService.push(filteredProjects, PushProgressListener.get()));
        } else {
            _consoleService.addMessage(String.format(NO_ANY_PROJECT_FOR_OPERATION, PUSH_OPERATION_NAME), MessageType.ERROR);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void onPullAction(ActionEvent actionEvent) {
        List<Project> projectsToPull = ProjectList.getCorrectProjects(getCurrentProjects());
        if (!projectsToPull.isEmpty()) {
            checkChangesAndPull(projectsToPull, new Object());
        } else {
            _consoleService.addMessage(String.format(NO_ANY_PROJECT_FOR_OPERATION, PULL_OPERATION_NAME), MessageType.ERROR);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void onRevertChanges(ActionEvent actionEvent) {
        GLTAlert alert = new GLTAlert(AlertType.CONFIRMATION, ApplicationState.REVERT.toString(),
                "Revert changes for selected projects", "Are you sure you want to do it?");
        alert.addButtons(ButtonType.CANCEL);
        alert.setTextButton(ButtonType.OK, "Revert");
        // check that user press OK button
        if (!alert.isOKButtonPressed(alert.showAndWait())) {
            return;
        }
        _backgroundService.runInBackgroundThread(this::revert);
    }

    @FXML
    @SuppressWarnings("unused")
    private void showEditProjectPropertiesWindow(ActionEvent event) {
        try {

            URL editProjectPropertiesWindowUrl = getClass().getClassLoader().getResource(ViewKey.EDIT_PROJECT_PROPERTIES.getPath());
            FXMLLoader loader = new FXMLLoader(editProjectPropertiesWindowUrl);
            Parent root = loader.load();

            EditProjectPropertiesController controller = loader.getController();

            List<Project> unavailableProjects = getUnavailableProjectsForEditingPom(getCurrentProjects());
            if (!unavailableProjects.isEmpty()) {
                String failedProjectsNames = unavailableProjects.stream()
                        .map(Project::getName)
                        .collect(Collectors.toList())
                        .toString();
                _consoleService.addMessage(EDIT_POM_SELECTION_WARNING + failedProjectsNames, MessageType.ERROR);
                return;
            }

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

            controller.beforeStart(getIdSelectedProjects(), stage);
            stage.show();
        } catch (IOException e) {
            _logger.error("Could not load fxml resource", e);
        }
    }

    private void onSelectAll() {
        if (projectListView != null && projectListView.getItems() != null && !projectListView.getItems().isEmpty()) {
            projectListView.getSelectionModel().selectAll();
            projectListView.requestFocus();
        }
    }

    private void onDeselectAll() {
        if (projectListView != null && projectListView.getItems() != null && !projectListView.getItems().isEmpty()) {
            projectListView.getSelectionModel().clearSelection();
            projectListView.requestFocus();
        }
    }

    @SuppressWarnings("unused")
    private void onOpenFolder(ActionEvent event) {
        getCurrentProjects().parallelStream()
                .filter(Project::isCloned)
                .forEach(this::openProjectFolder);
    }

    @FXML
    @SuppressWarnings({"unchecked", "unused"})
    private void onShowHideShadowProjects(ActionEvent actionEvent) {
        preferences.putBoolean(PREF_NAME_HIDE_SHADOWS, filterShadowProjects.isSelected());
        hideShadowsAction();
    }

    @FXML
    @SuppressWarnings("unused")
    private void refreshLoadProjects(ActionEvent actionEvent) {
        _backgroundService.runInBackgroundThread(this::refreshLoadProjects);
    }

    @FXML
    @SuppressWarnings("unused")
    private void onSelectAll(ActionEvent actionEvent) {
        onSelectAll();
    }

    @FXML
    @SuppressWarnings("unused")
    private void onDeselectAll(ActionEvent actionEvent) {
        onDeselectAll();
    }
    //endregion
    /*
     *
     * END OF PROJECTS-VIEW ACTIONS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF GENERAL ACTIONS BLOCK
     *
     */
    //region GEMERAL ACTIONS

    @FXML
    @SuppressWarnings("unused")
    private void exit(ActionEvent actionEvent) {
        exit();
    }

    @FXML
    @SuppressWarnings("unused")
    private void showAboutPopup(ActionEvent actionEvent) {
        showAboutPopup();
    }

    @FXML
    @SuppressWarnings("unused")
    private void openUserGuide(ActionEvent actionEvent){
        UserGuideUtil.openUserGuide();
    }
    //endregion
    /*
     *
     * END OF GENERAL ACTIONS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF PROJECTS-VIEW OTHER METHODS BLOCK
     *
     */
    //region PROJECTS-VIEW OTHER METHODS

    //used for rounding divider positions value
    private static double roundTo3(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    // Gets preferences by key, could return null (used for divider position)
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

    /**
     * USE ONLY FOR UNREPEATABLE OPERATION (like Revert, Push etc.)
     *
     * @return list of projects
     */
    @SuppressWarnings("unchecked")
    private List<Project> getCurrentProjects() {
        return projectListView.getSelectionModel().getSelectedItems();
    }

    /**
     * USE FOR REPEATABLE OPERATION (like CheckoutBranch, EditPomXML etc.)
     *
     * @return list of projects
     */
    private List<Integer> getIdSelectedProjects() {
        return ProjectList.getIdsProjects(getCurrentProjects());
    }

    private boolean startClone(List<Project> shadowProjects, String path, CloneProgressDialog progressDialog) {
        _projectService.clone(shadowProjects, path,
                new OperationProgressListener(progressDialog, ApplicationState.CLONE, null));
        return true;
    }

    private void hideShadowsAction() {
        Platform.runLater(() -> {
            if (preferences != null) {
                Boolean isHide = preferences.getBoolean(PREF_NAME_HIDE_SHADOWS, false);
                filterShadowProjects.setSelected(isHide);
                if (isHide) {
                    ObservableList<Project> obsList = FXCollections.observableArrayList(_projectsList.getClonedProjects());
                    projectListView.setItems(obsList);
                    projectListView.refresh();
                } else {
                    sortProjectsList();
                }
            }
        });
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

    private List<Project> getUnavailableProjectsForEditingPom(List<Project> projects) {
        return projects.parallelStream()
                .filter(project -> !ProjectList.projectIsClonedAndWithoutConflicts(project)
                        || !_pomXmlService.hasPomFile(project))
                .collect(Collectors.toList());
    }

    private void openProjectFolder(Project project) {
        try {
            Desktop.getDesktop().open(new File(project.getPath()));
        } catch (IOException e) {
            _logger.error("The specified file has no associated application or the associated application fails to be launched");
        } catch (NullPointerException npe) {
            _logger.error("File is null");
        } catch (UnsupportedOperationException uoe) {
            _logger.error("Current platform does not support this action");
        } catch (SecurityException se) {
            _logger.error("Denied read access to the file");
        } catch (IllegalArgumentException iae) {
            _logger.error("The specified file doesn't exist");
        }
    }

    @SuppressWarnings("ConstantConditions")
    private ContextMenu getContextMenu(List<Project> items) {

        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> menuItems = new ArrayList<>();

        boolean hasShadow = _projectService.hasShadow(items);
        boolean hasCloned = _projectService.hasCloned(items);

        if (hasCloned) {
            String openFolderIcoUrl = "icons/mainmenu/folder_16x16.png";
            Image openFolderIco = new Image(getClass().getClassLoader().getResource(openFolderIcoUrl).toExternalForm());
            MenuItem openFolder = new MenuItem();
            openFolder.setText("Open project folder");
            openFolder.setOnAction(this::onOpenFolder);
            openFolder.setGraphic(new ImageView(openFolderIco));
            menuItems.add(openFolder);
        }

        if (hasShadow) {
            String cloneProjectIcoUrl = "icons/mainmenu/clone_16x16.png";
            Image cloneProjectIco = new Image(getClass().getClassLoader().getResource(cloneProjectIcoUrl).toExternalForm());
            MenuItem cloneProject = new MenuItem();
            cloneProject.setText("Clone shadow project");
            cloneProject.setOnAction(this::cloneShadowProject);
            cloneProject.setGraphic(new ImageView(cloneProjectIco));
            menuItems.add(cloneProject);
        }

        contextMenu.getItems().addAll(menuItems);
        return contextMenu;
    }

    private boolean areAllItemsSelected(ListView<?> listView) {
        return listView.getSelectionModel().getSelectedItems().size() == listView.getItems().size();
    }

    private void sortAndCheckProjects() {
        sortProjectsList();
        checkProjectsList();
    }

    /**
     * Shadow projects should be at the end of list.
     */
    private void sortProjectsList() {
        Platform.runLater(() -> {
            Comparator<Project> comparator = new ProjectListComparator();
            ObservableList<Project> obsProjects = FXCollections.observableArrayList(_projectsList.getProjects());
            SortedList<Project> sortList = new SortedList<>(obsProjects);
            sortList.setComparator(comparator);
            projectListView.setItems(sortList);
            projectListView.refresh();
        });
    }

    private void checkProjectsList() {
        List<Project> incorrectProjects = findIncorrectProjects();
        if (incorrectProjects.isEmpty()) {
            return;
        }
        Platform.runLater(() -> {
            IncorrectProjectDialog dialog = new IncorrectProjectDialog();
            dialog.showDialog(incorrectProjects, (obj) -> refreshLoadProjects());
        });
    }

    private List<Project> findIncorrectProjects() {
        return _projectsList.getProjects().parallelStream()
                .filter(this::isIncorrectProject)
                .collect(Collectors.toList());
    }

    private boolean isIncorrectProject(Project project) {
        // we check only cloned projects
        return project.isCloned() && !_gitService.hasAtLeastOneReference(project);
    }

    private void refreshLoadProjects() {
        _projectsList.refreshLoadProjects();
        hideShadowsAction();
    }

    private BooleanBinding booleanBindingForShadowProjects() {
        return Bindings.createBooleanBinding(() ->
                        getCurrentProjects().stream()
                                .filter(Objects::nonNull)
                                .allMatch(project -> !project.isCloned()),
                Stream.of(projectListView.getSelectionModel()).map(SelectionModel::selectedItemProperty).toArray(Observable[]::new));
    }

    private BooleanBinding booleanBindingForClonedProjects() {
        return Bindings.createBooleanBinding(() ->
                        getCurrentProjects().stream()
                                .filter(Objects::nonNull)
                                .allMatch(Project::isCloned),
                Stream.of(projectListView.getSelectionModel()).map(SelectionModel::selectedItemProperty).toArray(Observable[]::new));
    }

    private void setDisablePropertyForButtons() {
        BooleanBinding booleanBindingDefault = projectListView.getSelectionModel().selectedItemProperty().isNull();
        // We lock git operation (all except "clone") if shadow projects was selected.
        BooleanBinding booleanBindingForShadow = booleanBindingForShadowProjects().or(booleanBindingDefault);
        // We lock clone operation if cloned projects was selected.
        BooleanBinding booleanBindingForCloned = booleanBindingForClonedProjects().or(booleanBindingDefault);

        setToolbarDisableProperty(booleanBindingForShadow, booleanBindingForCloned);
        setMainMenuDisableProperty(booleanBindingForShadow, booleanBindingForCloned);
    }

    private void setToolbarDisableProperty(BooleanBinding bindingForShadow, BooleanBinding bindingForCloned) {
        _toolbarManager.getButtonById(ToolbarButtons.CLONE_PROJECT_BUTTON.getId()).disableProperty().bind(bindingForCloned);
        _toolbarManager.getButtonById(ToolbarButtons.NEW_BRANCH_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(ToolbarButtons.CHECKOUT_BRANCH_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(ToolbarButtons.STAGING_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(ToolbarButtons.PUSH_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(ToolbarButtons.EDIT_PROJECT_PROPERTIES_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(ToolbarButtons.PULL_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(ToolbarButtons.REVERT_CHANGES.getId()).disableProperty().bind(bindingForShadow);
    }

    private void setMainMenuDisableProperty(BooleanBinding bindingForShadow, BooleanBinding bindingForCloned) {
        _mainMenuManager.getButtonById(MainMenuItems.MAIN_CLONE_PROJECT).disableProperty().bind(bindingForCloned);
        _mainMenuManager.getButtonById(MainMenuItems.MAIN_STAGING).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(MainMenuItems.MAIN_CHECKOUT_BRANCH).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(MainMenuItems.MAIN_CREATE_BRANCH).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(MainMenuItems.MAIN_PUSH).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(MainMenuItems.MAIN_PULL).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(MainMenuItems.MAIN_REVERT).disableProperty().bind(bindingForShadow);

    }
    //endregion
    /*
     *
     * END OF PROJECTS-VIEW OTHER METHODS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF GROUPS-VIEW OTHER METHODS BLOCK
     *
     */
    //region GROUPS-VIEW OTHER METHODS

    private void updateClonedGroups() {

        List<Group> userGroups = _clonedGroupsService.loadClonedGroups();
        if (userGroups != null) {
            //noinspection unchecked
            groupListView.setItems(FXCollections.observableList(userGroups));
            showNotExistGroups(_clonedGroupsService.getNotExistGroup());
        }

    }

    private void showNotExistGroups(Collection<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return;
        }
        StringBuffer infoAboutGroups = new StringBuffer();
        groups.forEach(group -> infoAboutGroups.append(group.getName()).append(" (").append(group.getPathToClonedGroup()).append(");"));
        _logger.warn(FAILED_HEADER_MESSAGE_LOAD_GROUP + FAILED_CONTENT_MESSAGE_LOAD_GROUP + infoAboutGroups);

        String namesAndPathsGroups = infoAboutGroups.toString().replace(";", ";\n\r");
        Platform.runLater(() -> {
            Alert dialog = new StatusDialog(FAILED_HEADER_MESSAGE_LOAD_GROUP, FAILED_CONTENT_MESSAGE_LOAD_GROUP, namesAndPathsGroups);
            dialog.showAndWait();
        });
    }

    //endregion
    /*
     *
     * END OF GROUPS-VIEW OTHER METHODS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF GENERAL OTHER METHODS BLOCK
     *
     */
    //region GENERAL OTHER METHODS

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
            StatusDialog statusDialog = new StatusDialog(title, header, content);
            statusDialog.showAndWait();
        });
    }

    private void updateCurrentConsole() {
        _consoleController.setComponents(_console, scrollPane);
        _consoleController.updateConsole();
    }

    private void exit() {
        List<ApplicationState> activeStates = _stateService.getActiveStates();
        if (activeStates.isEmpty()) {
            ShutDownUtil.shutdown();
            Platform.exit();
        }
        JavaFXUI.showWarningAlertForActiveStates(activeStates);
    }

    //endregion
    /*
     *
     * END OF GENERAL OTHER METHODS BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF UpdateProgressListener IMPLEMENTING BLOCK
     *
     */
    //region UpdateProgressListener IMPLEMENTING
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
    //endregion
    /*
     *
     * END OF UpdateProgressListener IMPLEMENTING BLOCK
     *
     ***********************************************************************************************/

    /***********************************************************************************************
     *
     * START OF INNER CLASSES BLOCK
     *
     */
    //region INNER CLASSES

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
    }

    private class GroupsWindowStateListener implements StateListener {

        @Override
        public void handleEvent(ApplicationState changedState, boolean isActivate) {
            if (!isActivate) {
                updateClonedGroups();
            }
        }
    }

    private class ProjectsWindowStateListener implements StateListener {

        @Override
        public void handleEvent(ApplicationState state, boolean isActivate) {
            if (!isActivate) {
                _backgroundService.runInBackgroundThread(() -> updateProjectsByState(state));
            }
            handleRefreshButtonState(state, isActivate);
        }

        private void handleRefreshButtonState(ApplicationState state, boolean isActivate) {
            if (state == ApplicationState.LOAD_PROJECTS) {
                refreshProjectsButton.setDisable(isActivate);
            }
        }

        private void updateProjectsByState(ApplicationState state) {
            if (state == ApplicationState.CREATE_PROJECT) {
                refreshLoadProjects();
                return;
            } else if (state == ApplicationState.CLONE) {
                sortProjectsList();
                return;
            } else if (state != ApplicationState.LOAD_PROJECTS && state != ApplicationState.UPDATE_PROJECT_STATUSES) {
                _projectsList.updateProjectStatuses();
            } else {
                projectListView.refresh();
            }
        }
    }

    //endregion
    /*
     *
     * END OF INNER CLASSES BLOCK
     *
     ***********************************************************************************************/
}
