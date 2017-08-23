package com.lgc.gitlabtool.git.ui.javafx.controllers;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.PomXMLService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.ChangesCheckDialog;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.CommitDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateNewBranchDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateProjectDialog;
import com.lgc.gitlabtool.git.ui.javafx.IncorrectProjectDialog;
import com.lgc.gitlabtool.git.ui.javafx.ProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.PullProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuItems;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuManager;
import com.lgc.gitlabtool.git.ui.selection.ListViewKey;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainWindowController {
    private static final String HEDER_GROUP_TITLE = "Current group: ";
    private static final String SELECT_ALL_IMAGE_URL = "icons/select_all_20x20.png";
    private static final String REFRESH_PROJECTS_IMAGE_URL = "icons/toolbar/refresh_projects_20x20.png";
    private static final String FILTER_SHADOW_PROJECTS_IMAGE_URL = "icons/toolbar/filter_shadow_projects_20x20.png";
    private static final String DIVIDER_PROPERTY_NODE = "MainWindowController_Dividers";
    private static final String STATUS_DIALOG_TITLE = "Status dialog";
    private static final String STATUS_DIALOG_HEADER_COMMIT = "Commit statuses";
    private static final String STATUS_DIALOG_HEADER_PUSH = "Push statuses";
    private static final String EDIT_PROJECT_PROPERTIES = "Edit project properties";
    private static final String EDIT_POM_SELECTION_WARNING = "Selected projects do not have Pom.xml file or contains shadow project";

    private List<Project> _projects;

    private Group _currentGroup;
    private Preferences preferences;
    private static final Logger _logger = LogManager.getLogger(MainWindowController.class);

    private static final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
            .getService(LoginService.class.getName());

    private static final ProjectService _projectService = (ProjectService) ServiceProvider.getInstance()
            .getService(ProjectService.class.getName());

    private static final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private static final PomXMLService _pomXmlService = (PomXMLService) ServiceProvider.getInstance()
            .getService(PomXMLService.class.getName());

    private static final ConsoleService _consoleService = (ConsoleService) ServiceProvider.getInstance()
            .getService(ConsoleService.class.getName());

    private static final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());


    @FXML
    private ListView<Project> projectsList;

    @FXML
    private Label leftLabel;

    @FXML
    private SplitPane splitPanelMain;

    @FXML
    private Label userId;

    @FXML
    private ToggleButton selectAllButton;

    @FXML
    private Button refreshProjectsButton;

    @FXML
    private ToggleButton filterShadowProjects;

    public void beforeShowing() {
        String username = _loginService.getCurrentUser().getName();
        userId.setText(username);

        String groupTitle = _currentGroup.getName() + " [" + _currentGroup.getPathToClonedGroup() + "]";
        leftLabel.setText(HEDER_GROUP_TITLE + groupTitle);


        Image imageRefreshProjects = new Image(
                getClass().getClassLoader().getResource(REFRESH_PROJECTS_IMAGE_URL).toExternalForm());
        Image imageSelectAll = new Image(
                getClass().getClassLoader().getResource(SELECT_ALL_IMAGE_URL).toExternalForm());
        Image imageFilterShadow = new Image(
                getClass().getClassLoader().getResource(FILTER_SHADOW_PROJECTS_IMAGE_URL).toExternalForm());
        selectAllButton.setGraphic(new ImageView(imageSelectAll));
        refreshProjectsButton.setGraphic(new ImageView(imageRefreshProjects));
        filterShadowProjects.setGraphic(new ImageView(imageFilterShadow));

        preferences = getPreferences(DIVIDER_PROPERTY_NODE);

        if (preferences != null) {
            String key = String.valueOf(groupTitle.hashCode());
            double splitPaneDivider = preferences.getDouble(key, 0.3);
            splitPanelMain.setDividerPositions(splitPaneDivider);
        }

        configureListView(projectsList);

        splitPanelMain.getDividers().get(0).positionProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (preferences != null) {
                        String key = String.valueOf(groupTitle.hashCode());
                        Double value = round(newValue.doubleValue(), 3);

                        preferences.putDouble(key, value);
                    }
                });
        setDisablePropertyForButtons();
        configureToolbarCommands();
        initToolbarMainMenuActions();
    }

    private void setDisablePropertyForButtons() {
        BooleanBinding booleanBinding = projectsList.getSelectionModel().selectedItemProperty().isNull();

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.CLONE_PROJECT_BUTTON.getId()).disableProperty()
                .bind(booleanBinding);
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.NEW_BRANCH_BUTTON.getId()).disableProperty()
                .bind(booleanBinding);
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.SWITCH_BRANCH_BUTTON.getId()).disableProperty()
                .bind(booleanBinding);
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.COMMIT_BUTTON.getId()).disableProperty()
                .bind(booleanBinding);
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.PUSH_BUTTON.getId()).disableProperty()
                .bind(booleanBinding);
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.EDIT_PROJECT_PROPERTIES_BUTTON.getId())
                .disableProperty().bind(booleanBinding);
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.PULL_BUTTON.getId()).disableProperty()
                .bind(booleanBinding);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_COMMIT).disableProperty().bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_SWITCH_BRANCH).disableProperty()
                .bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CREATE_BRANCH).disableProperty()
                .bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_COMMIT).disableProperty().bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_PUSH).disableProperty().bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_PULL).disableProperty().bind(booleanBinding);

    }

    public void setSelectedGroup(Group group) {
        _currentGroup = group;
        refreshLoadProjects();
    }

    /**
     * Updates current projects list. Method don't load projects from the GitLab
     */
    public void updateProjectsList(){
        projectsList.refresh();
    }

    public void onSelectAll() {
        if (projectsList != null && projectsList.getItems() != null && !projectsList.getItems().isEmpty()) {
            projectsList.getSelectionModel().selectAll();
            projectsList.requestFocus();
        }
    }

    public void onDeselectAll() {
        if (projectsList != null && projectsList.getItems() != null && !projectsList.getItems().isEmpty()) {
            projectsList.getSelectionModel().clearSelection();
            projectsList.requestFocus();
        }
    }

    private void configureToolbarCommands() {
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

    private void onOpenFolder(ActionEvent event) {
        getSelectedProjects().parallelStream()
                .filter(Project::isCloned)
                .forEach(this::openProjectFolder);
    }

    private void openProjectFolder(Project project){
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

    private void configureListView(ListView<Project> listView) {
        // config displayable string
        listView.setCellFactory(p -> new ProjectListCell());

        // setup selection
        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Project>) changed -> {
            if (areAllItemsSelected(listView)) {
                selectAllButton.setSelected(true);
                selectAllButton.setOnAction(action -> onDeselectAll());
            } else {
                selectAllButton.setSelected(false);
                selectAllButton.setOnAction(action -> onSelectAll());
            }
        });

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
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

        listView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Project>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Project> change) {
                SelectionsProvider.getInstance().setSelectionItems(ListViewKey.MAIN_WINDOW_PROJECTS.getKey(),
                        listView.getSelectionModel().getSelectedItems());
            }
        });
    }

    private static double round(double value, int places) {
        if (places < 0) {
            _logger.error("Incorrect input format");
            return value;
        }

        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    private boolean areAllItemsSelected(ListView<?> listView) {
        return listView.getSelectionModel().getSelectedItems().size() == listView.getItems().size();
    }

    private void initToolbarMainMenuActions() {
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

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CLONE_PROJECT)
                .setOnAction(this::cloneShadowProject);

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.EDIT_PROJECT_PROPERTIES_BUTTON.getId())
                .setOnAction(event -> showEditProjectPropertiesWindow());

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CREATE_BRANCH)
                .setOnAction(this::onNewBranchButton);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_COMMIT)
                .setOnAction(this::onCommitAction);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_PUSH)
                .setOnAction(this::onPushAction);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_PULL)
                .setOnAction(this::onPullAction);
    }

    @FXML
    public void onNewBranchButton(ActionEvent actionEvent) {
        showCreateNewBranchDialog();
        updateProjectsList();
    }

    @FXML
    public void createProjectButton(ActionEvent actionEvent) {
        // dialog
        CreateProjectDialog dialog = new CreateProjectDialog(_currentGroup, (obj) -> refreshLoadProjects());
        dialog.showAndWait();
    }

    private void showCreateNewBranchDialog() {
        List<Project> allSelectedProjects = getSelectedProjects();
        List<Project> clonedProjects = allSelectedProjects.stream()
                                                          .filter(prj -> prj.isCloned())
                                                          .collect(Collectors.toList());
        CreateNewBranchDialog dialog = new CreateNewBranchDialog(clonedProjects);
        dialog.showAndWait();
    }

    @FXML
    public void refreshLoadProjects(ActionEvent actionEvent) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            refreshLoadProjects();
        });
        executor.shutdown();
    }

    private void refreshLoadProjects() {
        _projects = (List<Project>) _projectService.loadProjects(_currentGroup);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sortProjectsList();
                projectsList.refresh();
            }
        });
        checkProjectsList();
    }

    // shadow projects in the end list
    private void sortProjectsList() {
        List<Project> sortedList = _projects.stream().sorted(this::compareProjects)
                                                     .collect(Collectors.toList());
        ObservableList<Project> projectsObservableList = FXCollections.observableList(sortedList);
        projectsList.setItems(projectsObservableList);
    }

    private int compareProjects(Project firstProject, Project secondProject) {
        return Boolean.compare(secondProject.isCloned(), firstProject.isCloned());
    }

    @FXML
    public void onCommitAction(ActionEvent actionEvent) {
        List<Project> allSelectedProjects = getSelectedProjects();
        List<Project> projectWithChanges = _gitService.getProjectsWithChanges(allSelectedProjects);

        if (projectWithChanges.isEmpty()) {
            showProjectsWithoutChangesMessage();
            return;
        }

        CommitDialog dialog = new CommitDialog();
        Map<Project, JGitStatus> commitStatuses = dialog.commitChanges(projectWithChanges);

        if (commitStatuses != null) {
            String dialogMessage = "%s projects were pushed successfully";
            showStatusDialog(commitStatuses, allSelectedProjects.size(), STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER_COMMIT,
                    dialogMessage);
        }
    }

    @FXML
    public void onPushAction(ActionEvent actionEvent) {
        List<Project> allSelectedProjects = getSelectedProjects();
        List<Project> filteredProjects = allSelectedProjects.stream().filter(prj -> prj.isCloned())
                .collect(Collectors.toList());

        Map<Project, JGitStatus> pushStatuses = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            _consoleService.addMessage("Push projects is started...", MessageType.SIMPLE);
            _stateService.stateON(ApplicationState.PUSH);
            pushStatuses.putAll(_gitService.push(filteredProjects, new PushProgressListener()));
//            We don't need in the Status dialog here if we use UI console and executor
//            String dialogMessage = "%s projects were pushed successfully";
//            showStatusDialog(pushStatuses, allSelectedProjects.size(), STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER_PUSH,
//                    dialogMessage);
            _consoleService.addMessage("Push projects is finished!", MessageType.SIMPLE);
        });
        executor.shutdown();
    }

    @FXML
    public void cloneShadowProject(ActionEvent actionEvent) {
        List<Project> shadowProjects = getSelectedProjects().stream()
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

    private void showEditProjectPropertiesWindow() {
        try {

            URL switchBranchWindowUrl = getClass().getClassLoader().getResource(ViewKey.EDIT_PROJECT_PROPERTIES.getPath());
            FXMLLoader loader = new FXMLLoader(switchBranchWindowUrl);
            Parent root = loader.load();

            EditProjectPropertiesController controller = loader.getController();

            List<Project> selectedProjects = getSelectedProjects();

            if(!isAvalibleEditPomXml(selectedProjects)){
                _consoleService.addMessage(EDIT_POM_SELECTION_WARNING, MessageType.ERROR);
                return;
            }

            controller.beforeStart(selectedProjects);
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

    private boolean isAvalibleEditPomXml(List<Project> projects){
        boolean hasShadow = projects.parallelStream()
                .filter(project -> !project.isCloned()).count() > 0;
        boolean hasPomFile = _pomXmlService.hasPomFile(projects);

        return !(hasShadow || !hasPomFile);
    }
    @FXML
    public void onShowHideShadowProjects(ActionEvent actionEvent) {
        if (filterShadowProjects.isSelected()) {
            FilteredList<Project> list = new FilteredList<>(FXCollections.observableArrayList(_projects), Project::isCloned);
            projectsList.setItems(list);
        } else {
            projectsList.setItems(FXCollections.observableArrayList(_projects));
            sortProjectsList();
        }
    }

    private boolean startClone(List<Project> shadowProjects, String path,  CloneProgressDialog progressDialog) {
        _projectService.clone(shadowProjects, path,
                new OperationProgressListener(progressDialog, ApplicationState.CLONE, (obj) -> refreshLoadProjects(null)));
        return true;
    }

    private void showProjectsWithoutChangesMessage() {
        String noChangesMessage = "Selected projects do not have changes";
        _consoleService.addMessage("Selected projects do not have changes", MessageType.SIMPLE);
        StatusDialog statusDialog = new StatusDialog(STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER_COMMIT,
                noChangesMessage);
        statusDialog.showAndWait();
    }

    private void showStatusDialog(Map<Project, JGitStatus> statuses, int countProjects, String title, String header,
            String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StatusDialog statusDialog = new StatusDialog(title, header);
                statusDialog.showMessage(statuses, countProjects, message);
                statusDialog.showAndWait();
            }
        });
    }


    private List<Project> getSelectedProjects() {
        return projectsList.getSelectionModel().getSelectedItems();
    }

    private void checkProjectsList() {
        List<Project> incorrectProjects = findIncorrectProjects();
        if (incorrectProjects.isEmpty()) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                IncorrectProjectDialog dialog = new IncorrectProjectDialog();
                dialog.showDialog(incorrectProjects, (obj) -> refreshLoadProjects());
            }
        });
    }

    private List<Project> findIncorrectProjects() {
        return _projects.parallelStream()
                        .filter(this::isIncorrectProject)
                        .collect(Collectors.toList());
    }

    private boolean isIncorrectProject(Project project) {
        if (!project.isCloned()) {
            return false; // we check only cloned projects
        }
        return !_gitService.hasAtLeastOneReference(project);
    }

    @FXML
    public void onPullAction(ActionEvent actionEvent) {
        List<Project> projectsToPull = getSelectedProjects().stream()
                .filter(project -> project.isCloned())
                .collect(Collectors.toList());
        checkChangesAndPull(projectsToPull, new Object());
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

    public class PushProgressListener implements ProgressListener {

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
            _stateService.stateOFF(ApplicationState.PUSH);
        }
    }

}
