package com.lgc.gitlabtool.git.ui.javafx.controllers;

import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getCommitHash;
import static com.lgc.gitlabtool.git.util.ProjectPropertiesUtil.getProjectNameWithVersion;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.listeners.updateProgressListener.UpdateProgressListener;
import com.lgc.gitlabtool.git.services.ClonedGroupsService;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.GroupsUserService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.AlertWithCheckBox;
import com.lgc.gitlabtool.git.ui.javafx.JavaFXUI;
import com.lgc.gitlabtool.git.ui.javafx.WorkIndicatorDialog;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuItems;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuManager;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import com.lgc.gitlabtool.git.util.ScreenUtil;
import com.lgc.gitlabtool.git.util.UserGuideUtil;

import javafx.application.Platform;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ModularController implements UpdateProgressListener {
    private static final Logger logger = LogManager.getLogger(ModularController.class);

    private final String CLASS_ID = ModularController.class.getName();
    private static final String ABOUT_POPUP_TITLE = "About";
    private static final String ABOUT_POPUP_HEADER =
            getProjectNameWithVersion() + " (" + getCommitHash() + "), powered by Luxoft";
    private static final String ABOUT_POPUP_CONTENT = "Contacts: Yurii Pitomets (yurii.pitomets2@halliburton.com)";
    private static final String SWITCH_BRANCH_TITLE = "Switch branch";
    private static final String IMPORT_CHOOSER_TITLE = "Import Group";
    private static final String IMPORT_DIALOG_TITLE = "Import Status Dialog";
    private static final String FAILED_IMPORT_MESSAGE = "Import of group is Failed";
    private static final String REMOVE_GROUP_DIALOG_TITLE = "Remove Group";
    private static final String REMOVE_GROUP_STATUS_DIALOG_TITLE = "Import Status Dialog";
    private static final String FAILED_REMOVE_GROUP_MESSAGE = "Removing of group is Failed";
    private static final String CSS_PATH = "css/style.css";
    private static final String WORK_INDICATOR_START_MESSAGE = "Loading projects...";
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    /*
    SERVICES
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

    List<Node> nodes = new LinkedList<>();
   // private MainWindowController _mainWindowController;
   // private GroupWindowController _groupWindowController;

    private final ConsoleController _consoleController = ConsoleController.getInstance();

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

    private List<Node> projectsWindowToolbarItems = new LinkedList<>();
    private List<Node> groupsWindowToolbarItems = new LinkedList<>();

    private List<Menu> projectsWindowMainMenuItems = new LinkedList<>();
    private List<Menu> groupsWindowMainMenuItems = new LinkedList<>();

    private HBox projectsToolbar;

    private WorkIndicatorDialog _workIndicatorDialog;

    @FXML
    public void initialize() {
        toolbar.getStylesheets().add(getClass().getClassLoader().getResource(CSS_PATH).toExternalForm());

        initializeProjectsWindow();
        initializeGroupsWindow();

        updateCurrentConsole();
    }

    private void initializeProjectsWindow(){
        projectListView = new ListView();
        AnchorPane.setBottomAnchor(projectListView, 0.0);
        AnchorPane.setTopAnchor(projectListView, 25.0);
        AnchorPane.setLeftAnchor(projectListView, 0.0);
        AnchorPane.setRightAnchor(projectListView, 0.0);
        configureProjectsListView(projectListView);
        projectsWindowToolbarItems = ToolbarManager.getInstance().createToolbarItems(ViewKey.MAIN_WINDOW.getKey());
        initActionsToolBar(ViewKey.MAIN_WINDOW.getKey());

        projectsWindowMainMenuItems = MainMenuManager.getInstance().createMainMenuItems(ViewKey.MAIN_WINDOW.getKey());
        initActionsMainMenu(ViewKey.MAIN_WINDOW.getKey());
    }

    private void initializeGroupsWindow(){
        groupListView = new ListView();
        AnchorPane.setBottomAnchor(groupListView, 0.0);
        AnchorPane.setTopAnchor(groupListView, 0.0);
        AnchorPane.setLeftAnchor(groupListView, 0.0);
        AnchorPane.setRightAnchor(groupListView, 0.0);
        configureGroupListView(groupListView);
        groupsWindowToolbarItems = ToolbarManager.getInstance().createToolbarItems(ViewKey.GROUP_WINDOW.getKey());
        initActionsToolBar(ViewKey.GROUP_WINDOW.getKey());

        groupsWindowMainMenuItems = MainMenuManager.getInstance().createMainMenuItems(ViewKey.GROUP_WINDOW.getKey());
        initActionsMainMenu(ViewKey.GROUP_WINDOW.getKey());

        initProjectsToolbar();
    }

    private void initProjectsToolbar(){
        if(projectsToolbar != null){
            return;
        }

        projectsToolbar = new HBox();
        toolbar.setMaxHeight(25.0);

        ToggleButton selectAllButton = new ToggleButton();
        selectAllButton.setTooltip(new Tooltip("Select all projects"));

        Button refreshProjectsButton = new Button();
        refreshProjectsButton.setTooltip(new Tooltip("Refresh projects"));

        ToggleButton filterShadowProjects = new ToggleButton();
        filterShadowProjects.setTooltip(new Tooltip("Enable\\disable shadow projects"));

        projectsToolbar.getChildren().addAll(selectAllButton, refreshProjectsButton, filterShadowProjects);

    }

    public void loadGroupWindow() {
        toolbar.getItems().clear();
        menuBar.getMenus().clear();
        toolbar.getItems().addAll(groupsWindowToolbarItems);
        menuBar.getMenus().addAll(groupsWindowMainMenuItems);
      //  initActionsMainMenu(ViewKey.GROUP_WINDOW.getKey());

//        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKey.GROUP_WINDOW.getPath()));
//        Node node = loader.load();
//
//        _groupWindowController = loader.getController();
//        updateClonedGroups();
//
//        AnchorPane.setTopAnchor(node, 0.0);
//        AnchorPane.setRightAnchor(node, 0.0);
//        AnchorPane.setLeftAnchor(node, 0.0);
//        AnchorPane.setBottomAnchor(node, 0.0);
//
//        viewPane.getChildren().clear();
//        viewPane.getChildren().add(node);
        updateClonedGroups();

        listPane.getChildren().clear();
        listPane.getChildren().add(groupListView);

    }

    private void updateClonedGroups() {

        List<Group> userGroups = _clonedGroupsService.loadClonedGroups();
        if (userGroups != null) {
            groupListView.setItems(FXCollections.observableList(userGroups));
        }

    }

    public void loadMainWindow(Group selectedGroup) throws IOException {
        toolbar.getItems().clear();
        menuBar.getMenus().clear();
        _projectService.addUpdateProgressListener(this);
      //  toolbar.getItems().addAll(ToolbarManager.getInstance().getToolbarItems(ViewKey.MAIN_WINDOW.getKey()));
        menuBar.getMenus().addAll(MainMenuManager.getInstance().createMainMenuItems(ViewKey.MAIN_WINDOW.getKey()));
        initActionsMainMenu(ViewKey.MAIN_WINDOW.getKey());
        initActionsToolBar(ViewKey.MAIN_WINDOW.getKey());

        ToolbarManager.getInstance().lockButtons();
        MainMenuManager.getInstance().lockButtons();

        Stage stage = (Stage) toolbar.getScene().getWindow();
//        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(ViewKey.MAIN_WINDOW.getPath()));
//        Node node = loader.load();
//
//        _mainWindowController = loader.getController();
//
//        _workIndicatorDialog = new WorkIndicatorDialog(stage, WORK_INDICATOR_START_MESSAGE);
//
//        Runnable selectGroup = () -> {
//            _mainWindowController.setSelectedGroup(selectedGroup);
//
//            // UI updating
//            Platform.runLater(() -> {
//                ToolbarManager.getInstance().unlockButtons();
//                MainMenuManager.getInstance().unlockButtons();
//
//                _mainWindowController.beforeShowing();
//
//                AnchorPane.setTopAnchor(node, 0.0);
//                AnchorPane.setRightAnchor(node, 0.0);
//                AnchorPane.setLeftAnchor(node, 0.0);
//                AnchorPane.setBottomAnchor(node, 0.0);
//
//                viewPane.getChildren().clear();
//                viewPane.getChildren().add(node);
//
//                _projectService.removeUpdateProgressListener(this);
//            });
//        };
//
//        _workIndicatorDialog.execute(selectGroup);
//
//        Parent loadingStage = _workIndicatorDialog.getStage().getScene().getRoot();
//
//        StackPane pane = new StackPane();
//        pane.getChildren().add(loadingStage);
//
//        AnchorPane.setTopAnchor(pane, 0.0);
//        AnchorPane.setRightAnchor(pane, 0.0);
//        AnchorPane.setLeftAnchor(pane, 0.0);
//        AnchorPane.setBottomAnchor(pane, 0.0);
//
//        viewPane.getChildren().add(pane);

        listPane.getChildren().clear();
        listPane.getChildren().add(projectListView);

        updateCurrentConsole();
    }

    private void initActionsToolBar(String windowId) {
        System.out.println("  _________    INIT ACTIONS FOR TOOLBAR _______ " + windowId);
        if (windowId.equals(ViewKey.GROUP_WINDOW.getKey())) {
            ToolbarManager.getInstance().getButtonById(ToolbarButtons.IMPORT_GROUP_BUTTON.getId())
                    .setOnAction(event -> importGroupDialog());

            ToolbarManager.getInstance().getButtonById(ToolbarButtons.REMOVE_GROUP_BUTTON.getId())
                    .setOnAction(this::onRemoveGroup);

        } else if (windowId.equals(ViewKey.MAIN_WINDOW.getKey())) {
            Button changeGroup = ToolbarManager.getInstance()
                    .getButtonById(ToolbarButtons.CHANGE_GROUP_BUTTON.getId());
            changeGroup.setOnAction(event -> loadGroupWindow());

            Button switchBranch = ToolbarManager.getInstance()
                    .getButtonById(ToolbarButtons.SWITCH_BRANCH_BUTTON.getId());
            switchBranch.setOnAction(event -> switchBranchAction());
        }
    }

    private void removeGroupDialog(Group selectedGroup) {
        AlertWithCheckBox alert = new AlertWithCheckBox(AlertType.CONFIRMATION,
                REMOVE_GROUP_DIALOG_TITLE,
                "You want to remove the " + selectedGroup.getName() + " group.",
                "Are you sure you want to delete it?",
                "remove group from a local disk",
                ButtonType.YES, ButtonType.NO);

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
              //  _groupWindowController.refreshGroupsList();
            }
        });
        executor.shutdown();
    }

    @FXML
    public void onRemoveGroup(ActionEvent actionEvent) {
    //    Group group = _groupWindowController.getSelectedGroup();
      //  removeGroupDialog(group);
    }

    private void initActionsMainMenu(String windowId) {
        System.out.println("  _________    INIT ACTIONS FOR MAINMENU  _______ " + windowId);
        if (windowId.equals(ViewKey.GROUP_WINDOW.getKey())) {
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
            switchTo.setOnAction(event -> showSwitchBranchWindow());

        }
    }

    private void switchBranchAction() {
        showSwitchBranchWindow();
    }

    private void showSwitchBranchWindow() {
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
            logger.error("Could not load fxml resource", e);
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
        stage.initModality(Modality.APPLICATION_MODAL);

        /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 150);

        alert.show();
    }

    private void importGroupDialog() {
//        if (viewPane != null) {
//            Stage stage = (Stage) viewPane.getScene().getWindow();
//            stage.getIcons().add(_appIcon);
//
//            DirectoryChooser chooser = new DirectoryChooser();
//            chooser.setTitle(IMPORT_CHOOSER_TITLE);
//            File selectedDirectory = chooser.showDialog(stage);
//            if (selectedDirectory == null) {
//                return;
//            }
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.submit(() -> {
//
//                Group loadGroup = _groupService.importGroup(selectedDirectory.getAbsolutePath());
//                if (loadGroup == null) {
//                    _consoleService.addMessage(FAILED_IMPORT_MESSAGE, MessageType.ERROR);
//                    showStatusDialog(IMPORT_DIALOG_TITLE, FAILED_IMPORT_MESSAGE,
//                            "Failed to load group from " + selectedDirectory.getAbsolutePath());
//                } else {
//                    Platform.runLater(() -> {
//                        _consoleService.addMessage("Group successfully loaded.", MessageType.SUCCESS);
//                        //_groupWindowController.refreshGroupsList();
//                    });
//                }
//            });
//            executor.shutdown();
//        }
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


    /*


                   __________________________________
                  |                                 |
                  |*** START OF MAGIC CONTROLLER ***|
                  |_________________________________|



     */
    private void loadGroup(Group group) {
        toolbar.getItems().clear();
        menuBar.getMenus().clear();
        _projectService.addUpdateProgressListener(this);
        toolbar.getItems().addAll(projectsWindowToolbarItems);
        menuBar.getMenus().addAll(projectsWindowMainMenuItems);

        //TODO: REWRITE ACTIONS INITIALIZATION
       // initActionsMainMenu(ViewKey.MAIN_WINDOW.getKey());

        ToolbarManager.getInstance().lockButtons();
        MainMenuManager.getInstance().lockButtons();
        mainPanelBackground.setEffect(new GaussianBlur());
        topBackground.setEffect(new GaussianBlur());

        Stage stage = (Stage) toolbar.getScene().getWindow();

      //  background.getChildren().add();

     // nodes = background.getChildren();
      //  background.getChildren().clear();

        _workIndicatorDialog = new WorkIndicatorDialog(stage, WORK_INDICATOR_START_MESSAGE);
        Runnable selectGroup = () -> {
            ProjectList.reset();
            ProjectList _projectsList = ProjectList.get(group);

            // UI updating
            Platform.runLater(() -> {


//                _mainWindowController.beforeShowing();
//
//                AnchorPane.setTopAnchor(node, 0.0);
//                AnchorPane.setRightAnchor(node, 0.0);
//                AnchorPane.setLeftAnchor(node, 0.0);
//                AnchorPane.setBottomAnchor(node, 0.0);
//
//                viewPane.getChildren().clear();
//                viewPane.getChildren().add(node);
                projectListView.setItems(FXCollections.observableArrayList(_projectsList.getProjects()));
                ToolbarManager.getInstance().unlockButtons();
                MainMenuManager.getInstance().unlockButtons();
                mainPanelBackground.setEffect(null);
                topBackground.setEffect(null);
                _projectService.removeUpdateProgressListener(this);

            });
        };
        _workIndicatorDialog.executeAndShowDialog("Loading group", selectGroup, StageStyle.TRANSPARENT, stage);

//        ProjectList.reset();
//        ProjectList _projectsList = ProjectList.get(group);


        listPane.getChildren().clear();
        listPane.getChildren().add(projectListView);
        listPane.getChildren().add(projectsToolbar);
//        ToolbarManager.getInstance().unlockButtons();
//        MainMenuManager.getInstance().unlockButtons();
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

}
