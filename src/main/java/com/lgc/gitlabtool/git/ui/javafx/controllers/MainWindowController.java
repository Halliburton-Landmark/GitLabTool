package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.EmptyProgressListener;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.CommitDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateNewBranchDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateProjectDialog;
import com.lgc.gitlabtool.git.ui.javafx.IncorrectProjectDialog;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuItems;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuManager;
import com.lgc.gitlabtool.git.ui.selection.ListViewKey;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainWindowController {
    private static final String HEDER_GROUP_TITLE = "Current group: ";
    private static final String SELECT_ALL_IMAGE_URL = "icons/select_all_20x20.png";
    private static final String DIVIDER_PROPERTY_NODE = "MainWindowController_Dividers";
    private static final String STATUS_DIALOG_TITLE = "Status dialog";
    private static final String STATUS_DIALOG_HEADER_COMMIT = "Commit statuses";
    private static final String STATUS_DIALOG_HEADER_PUSH = "Push statuses";

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

    @FXML
    private ListView<Project> projectsList;

    @FXML
    private Label leftLabel;

    @FXML
    private SplitPane splitPanelMain;

    @FXML
    private Label userId;

    @FXML
    public Button selectAllButton;

    public void beforeShowing() {
        String username = _loginService.getCurrentUser().getName();
        userId.setText(username);

        String groupTitle = _currentGroup.getName() + " [" + _currentGroup.getPathToClonedGroup() + "]";
        leftLabel.setText(HEDER_GROUP_TITLE + groupTitle);

        Image imageSelectAll = new Image(
                getClass().getClassLoader().getResource(SELECT_ALL_IMAGE_URL).toExternalForm());
        selectAllButton.setGraphic(new ImageView(imageSelectAll));

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

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_COMMIT).disableProperty().bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_SWITCH_BRANCH).disableProperty()
                .bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CREATE_BRANCH).disableProperty()
                .bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_COMMIT).disableProperty().bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_PUSH).disableProperty().bind(booleanBinding);

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

    private void configureListView(ListView<Project> listView) {
        // config displayable string
        listView.setCellFactory(p -> new ProjectListCell());

        // setup selection
        listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Project>) changed -> {
            if (areAllItemsSelected(listView)) {
                selectAllButton.setText("Deselect all");
                selectAllButton.setOnAction(action -> onDeselectAll());
            } else {
                selectAllButton.setText("Select all");
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
                evt.consume();

                ListCell<?> cell = (ListCell<?>) node;
                ListView<?> lv = cell.getListView();

                lv.requestFocus();

                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
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

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.REFRESH_PROJECTS.getId())
            .setOnAction(this::refreshLoadProjects);

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.CLONE_PROJECT_BUTTON.getId())
            .setOnAction(this::cloneShadowProject);

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.NEW_BRANCH_BUTTON.getId())
                .setOnAction(this::onNewBranchButton);

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.CREATE_PROJECT_BUTTON.getId())
                .setOnAction(this::createProjectButton);

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.COMMIT_BUTTON.getId())
                .setOnAction(this::onCommitAction);

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.PUSH_BUTTON.getId()).setOnAction(this::onPushAction);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CREATE_BRANCH)
                .setOnAction(this::onNewBranchButton);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_COMMIT).setOnAction(this::onCommitAction);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_PUSH).setOnAction(this::onPushAction);
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
        List<Project> allSelectedProjects = getSelectProjects();
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
            _logger.info("Refreshing projects...");
            refreshLoadProjects();
            _logger.info("Projects were refreshed!");
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
        List<Project> allSelectedProjects = getSelectProjects();
        List<Project> projectWithChanges = _gitService.getProjectsWithChanges(allSelectedProjects);

        if (projectWithChanges.isEmpty()) {
            showProjectsWithoutChangesMessage();
            return;
        }

        CommitDialog dialog = new CommitDialog();
        Map<Project, JGitStatus> commitStatuses = dialog.commitChanges(projectWithChanges);

        String dialogMessage = "%s projects were pushed successfully";
        showStatusDialog(commitStatuses, allSelectedProjects.size(), STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER_COMMIT,
                dialogMessage);

    }

    @FXML
    public void onPushAction(ActionEvent actionEvent) {
        List<Project> allSelectedProjects = getSelectProjects();
        List<Project> filteredProjects = allSelectedProjects.stream().filter(prj -> prj.isCloned())
                .collect(Collectors.toList());

        Map<Project, JGitStatus> pushStatuses = _gitService.push(filteredProjects, EmptyProgressListener.get());

        String dialogMessage = "%s projects were pushed successfully";
        showStatusDialog(pushStatuses, allSelectedProjects.size(), STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER_PUSH,
                dialogMessage);

    }

    @FXML
    public void cloneShadowProject(ActionEvent actionEvent) {
        List<Project> shadowProjects = getSelectProjects().stream()
                                                            .filter(project -> !project.isCloned())
                                                            .collect(Collectors.toList());
        if (shadowProjects == null || shadowProjects.isEmpty()) {
            _logger.info("Shadow projects for cloning have not been selected!");
            return;
        }
        Stage stage = (Stage) selectAllButton.getScene().getWindow();
        String path = _currentGroup.getPathToClonedGroup();

        CloneProgressDialog progressDialog = new CloneProgressDialog(stage, _currentGroup.getName(), ApplicationState.CLONE);
        progressDialog.setStartAction(() -> startClone(shadowProjects, path, progressDialog));
        progressDialog.showDialog();
    }

    private boolean startClone(List<Project> shadowProjects, String path,  CloneProgressDialog progressDialog) {
        _projectService.clone(shadowProjects, path,
                new CloneProgressListener(progressDialog, (obj) -> refreshLoadProjects(null)));
        return true;
    }

    private void showProjectsWithoutChangesMessage() {
        String noChangesMessage = "Selected projects do not have changes";
        StatusDialog statusDialog = new StatusDialog(STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER_COMMIT,
                noChangesMessage);
        statusDialog.showAndWait();
    }

    private void showStatusDialog(Map<Project, JGitStatus> statuses, int countProjects, String title, String header,
            String message) {
        StatusDialog statusDialog = new StatusDialog(title, header);
        statusDialog.showMessage(statuses, countProjects, message);
        statusDialog.showAndWait();
    }


    private List<Project> getSelectProjects() {
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
}
