package com.lgc.gitlabtool.git.ui.javafx.controllers;

import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getBuildTimestamp;
import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getCommitHash;
import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getProjectNameWithVersion;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.lgc.gitlabtool.git.services.GroupsService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.PomXMLService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.AlertWithCheckBox;
import com.lgc.gitlabtool.git.ui.javafx.ChangesCheckDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateProjectDialog;
import com.lgc.gitlabtool.git.ui.javafx.GLTAlert;
import com.lgc.gitlabtool.git.ui.javafx.GLTScene;
import com.lgc.gitlabtool.git.ui.javafx.GLTTheme;
import com.lgc.gitlabtool.git.ui.javafx.JavaFXUI;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.javafx.WorkIndicatorDialog;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ProjectListComparator;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listcells.ProjectListCell;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.listeners.PushProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.CloneProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.ProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.PullProgressDialog;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuManager;
import com.lgc.gitlabtool.git.ui.selection.ListViewKey;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.ui.toolbar.GLToolButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import com.lgc.gitlabtool.git.util.OpenTerminalUtil;
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
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
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
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ModularController implements UpdateProgressListener {
    private static final Logger _logger = LogManager.getLogger(ModularController.class);
    private final ConsoleController _consoleController = ConsoleController.getInstance();
    private final String CLASS_ID = ModularController.class.getName();

    private static final String HEADER_GROUP_TITLE = "Current group: ";
    private static final String ABOUT_POPUP_TITLE = "About";
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();
    private static final String ABOUT_POPUP_CONTENT = "Contacts: Yurii Pitomets (yurii.pitomets2@halliburton.com)";
    private static final String ABOUT_POPUP_HEADER =
            getProjectNameWithVersion() + " (" + getBuildTimestamp() + " " + getCommitHash() + "), powered by Luxoft";

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

    private static final String BRANCHES_TITLE = "Branches";
    private static final String PULL_OPERATION_NAME = "pull";
    private static final String PUSH_OPERATION_NAME = "push";
    private static final String BRANCES_OPERATION_NAME = "operation with branches";
    private static final String STASH_OPERATION_NAME = "stash";

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
    private static final ConsoleService _consoleService = ServiceProvider.getInstance()
            .getService(ConsoleService.class);

    private static final GroupsService _groupService = ServiceProvider.getInstance()
            .getService(GroupsService.class);

    private static final StateService _stateService = ServiceProvider.getInstance()
            .getService(StateService.class);

    private static final ClonedGroupsService _clonedGroupsService = ServiceProvider.getInstance()
            .getService(ClonedGroupsService.class);

    private static final ProjectService _projectService = ServiceProvider.getInstance()
            .getService(ProjectService.class);

    private static final LoginService _loginService = ServiceProvider.getInstance()
            .getService(LoginService.class);

    private static final GitService _gitService = ServiceProvider.getInstance()
            .getService(GitService.class);

    private static final PomXMLService _pomXmlService = ServiceProvider.getInstance()
            .getService(PomXMLService.class);

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
            .getService(ThemeService.class);

    private static final BackgroundService _backgroundService = ServiceProvider.getInstance()
            .getService(BackgroundService.class);

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
    private List<Node> projectsToolbarItems;

    private List<Menu> projectsWindowMainMenuItems;
    private List<Menu> groupsWindowMainMenuItems;

    private HBox projectsToolbar;
    private WorkIndicatorDialog _workIndicatorDialog;

    private ToggleButton selectAllButton;
    private ToggleButton filterShadowProjects;
    private Button refreshProjectsButton;

    private static final int PROJECTS_TOOLBAR_PADDING = 1;

    private static String css_path = "css/modular_light_style.css";
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

        userId.setText(_loginService.getCurrentUser().getName());
        userId.setTextFill(javafx.scene.paint.Color.DARKGRAY);

        initializeProjectsWindow();
        initializeGroupsWindow();
        initializeThemesMenu();

        loadGroupWindow();
        updateCurrentConsole();
    }

    private void initializeProjectsWindow() {
        projectListView = new ListView<>();
        AnchorPane.setBottomAnchor(projectListView, 0.0);
        AnchorPane.setTopAnchor(projectListView, 40.0 + (2 * PROJECTS_TOOLBAR_PADDING));
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
        projectsToolbar.setId("projectsToolbar");

        AnchorPane.setTopAnchor(projectsToolbar, 0.0);
        AnchorPane.setLeftAnchor(projectsToolbar, 0.0);
        AnchorPane.setRightAnchor(projectsToolbar, 0.0);

        ImageView imageViewRefreshProjects = _themeService.getStyledImageView(REFRESH_PROJECTS_IMAGE_URL);
        ImageView imageViewSelectAll = _themeService.getStyledImageView(SELECT_ALL_IMAGE_URL);
        ImageView imageViewFilterShadow = _themeService.getStyledImageView(FILTER_SHADOW_PROJECTS_IMAGE_URL);

        selectAllButton = new ToggleButton();
        selectAllButton.setTooltip(new Tooltip("Select all projects"));
        selectAllButton.setGraphic(imageViewSelectAll);
        selectAllButton.setOnAction(this::onSelectAll);

        refreshProjectsButton = new Button();
        refreshProjectsButton.setTooltip(new Tooltip("Refresh projects"));
        refreshProjectsButton.setGraphic(imageViewRefreshProjects);
        refreshProjectsButton.setOnAction(this::refreshLoadProjects);

        filterShadowProjects = new ToggleButton();
        filterShadowProjects.setTooltip(new Tooltip("Show/Hide shadow projects"));
        filterShadowProjects.setGraphic(imageViewFilterShadow);
        filterShadowProjects.setOnAction(this::onShowHideShadowProjects);

        projectsToolbarItems = new ArrayList<>(Arrays.asList(selectAllButton, refreshProjectsButton, filterShadowProjects));
        projectsToolbar.getChildren().clear();
        projectsToolbar.getChildren().addAll(projectsToolbarItems);
        projectsToolbar.setPadding(new Insets(PROJECTS_TOOLBAR_PADDING,0,PROJECTS_TOOLBAR_PADDING,0));
    }

    private void initActionsToolBar(String windowId) {
        if (windowId.equals(ViewKey.GROUPS_WINDOW.getKey())) {
            _toolbarManager.getButtonById(GLToolButtons.IMPORT_GROUP_BUTTON.getId()).setOnAction(this::importGroupDialog);
            _toolbarManager.getButtonById(GLToolButtons.REMOVE_GROUP_BUTTON.getId()).setOnAction(this::onRemoveGroup);
            _toolbarManager.getButtonById(GLToolButtons.CLONE_GROUP_BUTTON.getId()).setOnAction(this::onCloneGroups);
            _toolbarManager.getButtonById(GLToolButtons.SELECT_GROUP_BUTTON.getId()).setOnAction(this::loadGroup);
        } else if (windowId.equals(ViewKey.PROJECTS_WINDOW.getKey())) {
            _toolbarManager.getButtonById(GLToolButtons.CHANGE_GROUP_BUTTON.getId()).setOnAction(this::loadGroupWindow);
            _toolbarManager.getButtonById(GLToolButtons.BRANCHES_BUTTON.getId()).setOnAction(this::showBranchesWindow);
            _toolbarManager.getButtonById(GLToolButtons.CLONE_PROJECT_BUTTON.getId()).setOnAction(this::cloneShadowProject);
            _toolbarManager.getButtonById(GLToolButtons.CREATE_PROJECT_BUTTON.getId()).setOnAction(this::createProjectButton);
            _toolbarManager.getButtonById(GLToolButtons.STAGING_BUTTON.getId()).setOnAction(this::openGitStaging);
            _toolbarManager.getButtonById(GLToolButtons.PUSH_BUTTON.getId()).setOnAction(this::onPushAction);
            _toolbarManager.getButtonById(GLToolButtons.PULL_BUTTON.getId()).setOnAction(this::onPullAction);
            _toolbarManager.getButtonById(GLToolButtons.REVERT_CHANGES.getId()).setOnAction(this::onRevertChanges);
            _toolbarManager.getButtonById(GLToolButtons.EDIT_PROJECT_PROPERTIES_BUTTON.getId()).setOnAction(this::showEditProjectPropertiesWindow);
            _toolbarManager.getButtonById(GLToolButtons.STASH.getId()).setOnAction(this::showStashWindow);
        }
    }

    private void initActionsMainMenu(String windowId) {
        if (windowId.equals(ViewKey.GROUPS_WINDOW.getKey())) {
            _mainMenuManager.getButtonById(GLToolButtons.GROUP_WINDOW_CLONE_GROUP).setOnAction(this::onCloneGroups);
        } else if (windowId.equals(ViewKey.PROJECTS_WINDOW.getKey())) {
            _mainMenuManager.getButtonById(GLToolButtons.MAIN_CLONE_PROJECT).setOnAction(this::cloneShadowProject);
            _mainMenuManager.getButtonById(GLToolButtons.MAIN_STAGING).setOnAction(this::openGitStaging);
            _mainMenuManager.getButtonById(GLToolButtons.MAIN_PUSH).setOnAction(this::onPushAction);
            _mainMenuManager.getButtonById(GLToolButtons.MAIN_PULL).setOnAction(this::onPullAction);
            _mainMenuManager.getButtonById(GLToolButtons.MAIN_REVERT).setOnAction(this::onRevertChanges);
            _mainMenuManager.getButtonById(GLToolButtons.MAIN_BRANCHES).setOnAction(this::showBranchesWindow);
            _mainMenuManager.getButtonById(GLToolButtons.MAIN_STASH).setOnAction(this::showStashWindow);
        }

        MenuItem userGuide = _mainMenuManager.getButtonById(GLToolButtons.GENERAL_USER_GUIDE);
        userGuide.setOnAction(this::openUserGuide);
        userGuide.setAccelerator(new KeyCodeCombination(KeyCode.F1));
        _mainMenuManager.getButtonById(GLToolButtons.GENERAL_EXIT).setOnAction(this::exit);
        _mainMenuManager.getButtonById(GLToolButtons.GENERAL_ABOUT).setOnAction(this::showAboutPopup);

    }

    private void initializeGroupsDisableBinding(ListView<Group> listView) {
        BooleanBinding groupListBooleanBinding = listView.getSelectionModel().selectedItemProperty().isNull();
        _toolbarManager.getAllButtonsForCurrentView().stream()
                .filter(x -> x.getId().equals(GLToolButtons.REMOVE_GROUP_BUTTON.getId())
                        || x.getId().equals(GLToolButtons.SELECT_GROUP_BUTTON.getId()))
                .forEach(x -> x.disableProperty().bind(groupListBooleanBinding));
    }

    private void initProjectsWindowListeners() {
        _modularStateListener = new ProjectsWindowStateListener();
    }

    private void configureGroupListView(ListView<Group> listView) {
        // config displayable string
        listView.setCellFactory(p -> new GroupListCell());

        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Node node = event.getPickResult().getIntersectedNode();

            while (node != null && node != listView && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            if (node instanceof ListCell) {

                ListCell<Group> cell = (ListCell<Group>) node;
                ListView<Group> lv = cell.getListView();

                if (event.getButton() == MouseButton.SECONDARY) {
                    if (!cell.isEmpty()) {
                        List<Group> selectedItems = lv.getSelectionModel().getSelectedItems();
                        lv.setContextMenu(getGroupContextMenu(selectedItems));
                    } else {
                        lv.setContextMenu(null);
                    }
                }
            }
        });
    }

    private void configureProjectsListView(ListView<Project> listView) {
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

    private void initializeThemesMenu() {
        String themeMenuTitle = GLToolButtons.MainMenuInfo.THEMES.getName();

        addItemsToMenu(groupsWindowMainMenuItems, createThemesMenuItems(), themeMenuTitle);
        addItemsToMenu(projectsWindowMainMenuItems, createThemesMenuItems(), themeMenuTitle);
    }

    private void addItemsToMenu(List<Menu> parentMenu, List<MenuItem> childItems, String menuTitle) {
        parentMenu.stream()
                .filter(menu -> menu.getText().equals(menuTitle))
                .findFirst()
                .map(menu -> menu.getItems().addAll(childItems));
    }

    private List<MenuItem> createThemesMenuItems() {
        return Arrays.stream(GLTTheme.values())
                .map(theme -> {
                    MenuItem item = createMenuItem(theme.getThemeTitle(), theme.getIconPath());
                    item.setOnAction(event -> setTheme(theme.getKey()));
                    return item;
                })
                .collect(Collectors.toList());
    }

    private MenuItem createMenuItem(String title, String iconPath) {
        return new MenuItem(title, _themeService.getStyledImageView(iconPath));
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

        String groupTitle = group.getName() + " [" + group.getPath() + "]";
        groupPath.setText(HEADER_GROUP_TITLE + groupTitle);

        mainPanelBackground.setEffect(new GaussianBlur());
        topBackground.setEffect(new GaussianBlur());

        Stage stage = (Stage) toolbar.getScene().getWindow();

        _workIndicatorDialog = new WorkIndicatorDialog(stage, WORK_INDICATOR_START_MESSAGE);
        Runnable selectGroup = () -> {
            ProjectList.get(null).reset();
            _projectsList = ProjectList.get(_currentGroup);
            resetLoadingProgress();
            if (_projectsList.getProjects() == null) {
                loadGroupWindow(null);
                return;
            }
            hideShadowsAction();
        };
        _workIndicatorDialog.executeAndShowDialog("Loading group", selectGroup, StageStyle.TRANSPARENT, stage);

        listPane.getChildren().clear();
        listPane.getChildren().add(projectListView);
        listPane.getChildren().add(projectsToolbar);

        setupProjectsDividerPosition(groupTitle);
        addProjectsWindowListener();

    }

    private void resetLoadingProgress() {
        // UI updating
        Platform.runLater(() -> {
            // noinspection unchecked
            mainPanelBackground.setEffect(null);
            topBackground.setEffect(null);
            _projectService.removeUpdateProgressListener(this);
        });
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
            stage.setScene(new GLTScene(root));
            stage.getIcons().add(appIcon);

             /* Set sizing and position */
            double dialogWidth = 600;
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
        Platform.runLater(this::loadGroupWindow);
    }

    @FXML
    @SuppressWarnings("unused")
    private void loadGroup(ActionEvent actionEvent) {
        Group selectedGroup = groupListView.getSelectionModel().getSelectedItem();

        Group updatedGroup = _groupService.getGroupById(selectedGroup.getId());
        //_clonedGroupsService.removeGroups(groups)
        // here update group before loading
        loadGroup(selectedGroup);
    }

    private void updateGroupListView() {

    }

    private void openInTerminal(ActionEvent actionEvent) {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItems().get(0);
        OpenTerminalUtil.openInTerminal(selectedProject.getPath());
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
    private void showBranchesWindow(ActionEvent event) {
        try {
            List<Project> projects = projectListView.getSelectionModel().getSelectedItems();
            projects = _projectService.getCorrectProjects(projects);
            if (projects.isEmpty()) {
                String message = String.format(NO_ANY_PROJECT_FOR_OPERATION, BRANCES_OPERATION_NAME);
                _consoleService.addMessage(message, MessageType.ERROR);
                return;
            }

            URL branchesWindowUrl = getClass().getClassLoader().getResource(ViewKey.BRANCHES_WINDOW.getPath());
            FXMLLoader loader = new FXMLLoader(branchesWindowUrl);
            Parent root = loader.load();

            BranchesWindowController branchesWindowController  = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new GLTScene(root));
            stage.getIcons().add(_appIcon);
            stage.setTitle(BRANCHES_TITLE);
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

            branchesWindowController.beforeShowing(projects, stage);
            stage.show();
        } catch (IOException e) {
            _logger.error("Could not load fxml resource", e);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void showStashWindow(ActionEvent event) {
        try {
            List<Project> selectedProjects = projectListView.getSelectionModel().getSelectedItems();
            if (selectedProjects.isEmpty()) { // if nothing selected in a list then all projects are loaded
                selectedProjects = _projectService.getCorrectProjects(projectListView.getItems());
                if (selectedProjects.isEmpty()) {
                    String message = String.format(NO_ANY_PROJECT_FOR_OPERATION, STASH_OPERATION_NAME);
                    _consoleService.addMessage(message, MessageType.ERROR);
                    return;
                }
            }

            URL stashWindowUrl = getClass().getClassLoader().getResource(ViewKey.STASH_WINDOW.getPath());
            FXMLLoader loader = new FXMLLoader(stashWindowUrl);
            Parent root = loader.load();

            StashWindowController stashWindowController  = loader.getController();
            if(stashWindowController == null) {
                _logger.error("Failed getting StashWindowController.");
                return;
            }
            stashWindowController.beforeShowing(_projectService.getIdsProjects(selectedProjects));

            Scene scene = new GLTScene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(_appIcon);
            stage.setTitle("Stash changes");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(new StashEventHandler(stashWindowController));

            /* Set size and position */
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double minWidth = primaryScreenBounds.getMaxX() / 2.5;
            double minHeight = primaryScreenBounds.getMaxY() / 2;
            stage.setMinWidth(minWidth);
            stage.setMinHeight(minHeight);
            ScreenUtil.adaptForMultiScreens(stage, minWidth, minHeight);

            stage.show();
        } catch (IOException e) {
            _logger.error("Could not load fxml resource: " + e.getMessage());
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
        String path = _currentGroup.getPath();

        CloneProgressDialog progressDialog = new CloneProgressDialog();
        progressDialog.setStartAction(() -> startClone(shadowProjects, path, progressDialog));
        progressDialog.showDialog();
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
        List<Project> filteredProjects = getCorrectCurrentProjects();
        if (!filteredProjects.isEmpty()) {
            _backgroundService.runInBackgroundThread(() -> _gitService.push(filteredProjects, PushProgressListener.get()));
        } else {
            _consoleService.addMessage(String.format(NO_ANY_PROJECT_FOR_OPERATION, PUSH_OPERATION_NAME), MessageType.ERROR);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void onPullAction(ActionEvent actionEvent) {
        List<Project> projectsToPull = getCorrectCurrentProjects();
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

            Stage stage = new Stage();
            stage.setScene(new GLTScene(root));
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
                .map(Project::getPath)
                .forEach(this::openFolder);
    }

    private void onOpenGroupFolder(List<Group> items) {
        items.parallelStream()
                .map(Group::getPath)
                .forEach(this::openFolder);
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
        return _projectService.getIdsProjects(getCurrentProjects());
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

        List<Project> correctProjects = getCorrectCurrentProjects();
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
                .filter(project -> !_projectService.projectIsClonedAndWithoutConflicts(project)
                        || !_pomXmlService.hasPomFile(project))
                .collect(Collectors.toList());
    }

    private List<Project> getCorrectCurrentProjects() {
        return _projectService.getCorrectProjects(getCurrentProjects());
    }

    private void openFolder(String path) {
        String fileDoesNotExistMessage = "Specified file does not exist";
        Runnable openFolderTask = () -> {
            try {
                Desktop.getDesktop().open(new File(path));
            } catch (IOException e) {
                showAlert("The specified file has no associated application " + System.lineSeparator() +
                        "or the associated application fails to be launched", e);
            } catch (NullPointerException npe) {
                showAlert(fileDoesNotExistMessage, npe);
            } catch (UnsupportedOperationException uoe) {
                showAlert("Current platform does not support this action", uoe);
            } catch (SecurityException se) {
                showAlert("Denied read access to the file", se);
            } catch (IllegalArgumentException iae) {
                showAlert(fileDoesNotExistMessage, iae);
            }
        };
        _backgroundService.runInAWTThread(openFolderTask);
    }

    private void showAlert(String message, Throwable e) {
        _logger.error(message, e);
        Platform.runLater(() -> new GLTAlert(AlertType.ERROR, "Open folder issue", message, "").showAndWait());
    }

    @SuppressWarnings("ConstantConditions")
    private ContextMenu getContextMenu(List<Project> items) {

        ContextMenu contextMenu = new ContextMenu();
        if (items.size() > 0) {
            List<MenuItem> menuItems = new ArrayList<>();

            boolean hasShadow = _projectService.hasShadow(items);
            boolean hasCloned = _projectService.hasCloned(items);

            if (hasCloned) {
                MenuItem openFolder = createMenuItem(GLToolButtons.OPEN_FOLDER, this::onOpenFolder);
                MenuItem openInTerminal = createMenuItem(GLToolButtons.OPEN_IN_TERMINAL, this::openInTerminal);

                Menu subMenuGit = new Menu("Git");

                MenuItem itemBranches = createMenuItem(GLToolButtons.MAIN_BRANCHES, this::showBranchesWindow);
                MenuItem itemStaging = createMenuItem(GLToolButtons.MAIN_STAGING, this::openGitStaging);
                MenuItem itemPull = createMenuItem(GLToolButtons.MAIN_PULL, this::onPullAction);
                MenuItem itemPush = createMenuItem(GLToolButtons.MAIN_PUSH, this::onPushAction);
                MenuItem itemRevert = createMenuItem(GLToolButtons.MAIN_REVERT, this::onRevertChanges);
                MenuItem itemStash = createMenuItem(GLToolButtons.MAIN_STASH, this::showStashWindow);

                subMenuGit.getItems().addAll(itemBranches, itemStaging, itemPull, itemPush, itemRevert, itemStash);

                MenuItem itemEditProjectProp = createMenuItem(GLToolButtons.MAIN_EDIT_PROJECT_PROPERTIES,
                        this::showEditProjectPropertiesWindow);

                menuItems.add(openFolder);
                if (projectListView.getSelectionModel().getSelectedItems().size() == 1) {
                    menuItems.add(openInTerminal);
                }
                menuItems.add(subMenuGit);
                menuItems.add(itemEditProjectProp);
            }

            if (hasShadow) {
                MenuItem cloneProject = createMenuItem(GLToolButtons.MAIN_CLONE_PROJECT, this::cloneShadowProject);
                cloneProject.setText("Clone shadow project");
                menuItems.add(cloneProject);
            }

            contextMenu.getItems().addAll(menuItems);
        }
        return contextMenu;
    }

    /**
     * Creates {@link MenuItem} for specified {@link GLToolButtons} and set onAction EventHandler for it
     * @param button - instance of {@link GLToolButtons}
     * @param onAction - action that should be performed for this menu item
     * @return menu item for specified GLToolButtons
     */
    private MenuItem createMenuItem(GLToolButtons button, EventHandler<ActionEvent> onAction) {
        MenuItem item = new MenuItem(button.getText());
        ImageView itemImageView = _themeService.getStyledImageView(button.getIconUrl());
        item.setGraphic(itemImageView);
        item.setOnAction(onAction);
        return item;
    }

    private ContextMenu getGroupContextMenu(List<Group> items) {
        ContextMenu groupContextMenu = new ContextMenu();

        MenuItem openFolder = createMenuItem(GLToolButtons.OPEN_FOLDER, event -> onOpenGroupFolder(items));
        MenuItem loadGroup = createMenuItem(GLToolButtons.LOAD_GROUP, this::loadGroup);
        MenuItem removeGroup = createMenuItem(GLToolButtons.REMOVE_GROUP, this::onRemoveGroup);

        groupContextMenu.getItems().addAll(openFolder, loadGroup, removeGroup);
        return groupContextMenu;
    }

    private boolean areAllItemsSelected(ListView<?> listView) {
        return listView.getSelectionModel().getSelectedItems().size() == listView.getItems().size();
    }

    /**
     * Shadow projects should be at the end of list.
     */
    private void sortProjectsList() {
        Platform.runLater(() -> {
            List<Project> loadedProjects = _projectsList.getProjects();
            if (loadedProjects == null) {
                return;
            }
            Comparator<Project> comparator = new ProjectListComparator();
            ObservableList<Project> obsProjects = FXCollections.observableArrayList(_projectsList.getProjects());
            SortedList<Project> sortList = new SortedList<>(obsProjects);
            sortList.setComparator(comparator);
            projectListView.setItems(sortList);
            projectListView.refresh();
        });
    }

    private void refreshLoadProjects() {
        _projectsList.refreshLoadProjects();
        //checkProjectsList(); TODO : fix working with IncorrectProjectDialog
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
        _toolbarManager.getButtonById(GLToolButtons.CLONE_PROJECT_BUTTON.getId()).disableProperty().bind(bindingForCloned);
        _toolbarManager.getButtonById(GLToolButtons.BRANCHES_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(GLToolButtons.STAGING_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(GLToolButtons.PUSH_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(GLToolButtons.EDIT_PROJECT_PROPERTIES_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(GLToolButtons.PULL_BUTTON.getId()).disableProperty().bind(bindingForShadow);
        _toolbarManager.getButtonById(GLToolButtons.REVERT_CHANGES.getId()).disableProperty().bind(bindingForShadow);
    }

    private void setMainMenuDisableProperty(BooleanBinding bindingForShadow, BooleanBinding bindingForCloned) {
        _mainMenuManager.getButtonById(GLToolButtons.MAIN_CLONE_PROJECT).disableProperty().bind(bindingForCloned);
        _mainMenuManager.getButtonById(GLToolButtons.MAIN_STAGING).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(GLToolButtons.MAIN_BRANCHES).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(GLToolButtons.MAIN_PUSH).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(GLToolButtons.MAIN_PULL).disableProperty().bind(bindingForShadow);
        _mainMenuManager.getButtonById(GLToolButtons.MAIN_REVERT).disableProperty().bind(bindingForShadow);

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
        groups.forEach(group -> infoAboutGroups.append(group.getName()).append(" (").append(group.getPath()).append(");"));
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

    private void setTheme(String themeId){
        _themeService.setTheme(themeId);
        _themeService.styleScene(toolbar.getScene());

        refreshMainMenuToolbarIcons();
        _consoleController.updateConsole();
        projectListView.refresh();
    }

    private void refreshMainMenuToolbarIcons() {
        Stream.concat(projectsWindowMainMenuItems.stream(), groupsWindowMainMenuItems.stream())
                .map(Menu::getItems)
                .forEach(item -> {
                    item.forEach(q -> q.getGraphic().setEffect(getLightEffect()));
                });

        Stream<Node> mainToolbarStream = Stream.concat(projectsWindowToolbarItems.stream(), groupsWindowToolbarItems.stream());
        Stream.concat(mainToolbarStream, projectsToolbarItems.stream())
                .filter(item -> item instanceof Button || item instanceof ToggleButton)
                .forEach(item -> {
                    ((ButtonBase) item).getGraphic().setEffect(getLightEffect());
                });
    }

    private Effect getLightEffect(){
        boolean isDarkTheme = _themeService.getCurrentTheme().equals(GLTTheme.DARK_THEME);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(_themeService.getLightningCoefficient());

        return isDarkTheme ? colorAdjust : null;
    }

    private void showAboutPopup() {
        Alert alert = new GLTAlert(Alert.AlertType.INFORMATION);

        ImageView imageView = AppIconHolder.getInstance().getAppIcoImageView();
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);

        alert.setGraphic(imageView);
        alert.setTitle(ABOUT_POPUP_TITLE);
        alert.setHeaderText(ABOUT_POPUP_HEADER);
        alert.setContentText(ABOUT_POPUP_CONTENT);

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
            Label groupNameLabel = new Label(item.getName());
            groupNameLabel.setFont(Font.font(Font.getDefault().getFamily(), 14));

            String localPath = item.getPath();
            Label localPathLabel = new Label(localPath);

            VBox vboxItem = new VBox(groupNameLabel, localPathLabel);
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

    class StashEventHandler implements EventHandler<WindowEvent> {

        private final StashWindowController _stashWindowController;

        public StashEventHandler(StashWindowController stashWindowController) {
            _stashWindowController = stashWindowController;
        }

        @Override
        public void handle(WindowEvent event) {
            List<ApplicationState> activeAtates = _stateService.getActiveStates();
            if (!activeAtates.isEmpty() && activeAtates.contains(ApplicationState.STASH)) {
                event.consume();
                JavaFXUI.showWarningAlertForActiveStates(activeAtates);
                return;
            }
            _stashWindowController.dispose();
        }

    }

    //endregion
    /*
     *
     * END OF INNER CLASSES BLOCK
     *
     ***********************************************************************************************/
}
