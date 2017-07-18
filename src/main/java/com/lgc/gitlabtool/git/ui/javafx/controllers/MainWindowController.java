package com.lgc.gitlabtool.git.ui.javafx.controllers;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.CreateNewBranchDialog;
import com.lgc.gitlabtool.git.ui.javafx.CreateProjectDialog;
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

public class MainWindowController {
    private static final String HEDER_GROUP_TITLE = "Current group: ";
    private static final String SELECT_ALL_IMAGE_URL = "icons/main/select_all.png";
    private static final String DIVIDER_PROPERTY_NODE = "MainWindowController_Dividers";

    private List<Project> _projects;

    private Group _currentGroup;
    private Preferences preferences;
    private static final Logger _logger = LogManager.getLogger(MainWindowController.class);

    private static final LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());

    private static final ProjectService _projectService =
            (ProjectService) ServiceProvider.getInstance().getService(ProjectService.class.getName());

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

        Image imageSelectAll = new Image(getClass().getClassLoader().getResource(SELECT_ALL_IMAGE_URL).toExternalForm());
        selectAllButton.setGraphic(new ImageView(imageSelectAll));

        preferences = getPreferences(DIVIDER_PROPERTY_NODE);

        if (preferences != null) {
            double splitPaneDivider = preferences.getDouble(groupTitle, 0.3);
            splitPanelMain.setDividerPositions(splitPaneDivider);
        }

        configureListView(projectsList);

        splitPanelMain.getDividers().get(0).positionProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (preferences != null) {
                        preferences.putDouble(groupTitle, newValue.doubleValue());
                    }
                });

        setDisablePropertyForButtons();

        //TODO: Additional thread should be placed to services
        Thread t = new Thread(this::refreshProjectList);
        t.setName("Refresh project list");
        t.start();

        configureToolbarCommands();
        initNewBranchButton();
    }

    private void setDisablePropertyForButtons() {
        BooleanBinding booleanBinding = projectsList.getSelectionModel().selectedItemProperty().isNull();

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.NEW_BRANCH_BUTTON.getId()).disableProperty()
                .bind(booleanBinding);
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.SWITCH_BRANCH_BUTTON.getId()).disableProperty()
                .bind(booleanBinding);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_SWITCH_BRANCH).disableProperty()
                .bind(booleanBinding);
        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CREATE_BRANCH).disableProperty()
                .bind(booleanBinding);
    }

    public void setSelectedGroup(List<Project> projects, Group group) {
        _projects = projects;
        _currentGroup = group;
    }

    public void refreshProjectsList(){
        projectsList.refresh();
    }

    public void onSelectAll() {
        if (projectsList != null && projectsList.getItems() != null && !projectsList.getItems().isEmpty()) {
            projectsList.getSelectionModel().selectAll();
            projectsList.requestFocus();
        }
    }

    public void onDeselectAll(){
        if (projectsList != null && projectsList.getItems() != null && !projectsList.getItems().isEmpty()) {
            projectsList.getSelectionModel().clearSelection();
            projectsList.requestFocus();
        }
    }

    private void configureToolbarCommands() {
    }

    private void refreshProjectList() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<Project> sortedProjectList = _projects.stream()
                        .sorted((project1, project2) -> Boolean.compare(project2.isCloned(), project1.isCloned()))
                        .collect(Collectors.toList());
                ObservableList<Project> projectsObservableList = FXCollections.observableList(sortedProjectList);
                projectsList.setItems(projectsObservableList);
            }
        });
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
        } catch (NullPointerException npe){
            _logger.error("Key is null");
            return null;
        }
    }

    private void configureListView(ListView<Project> listView) {
        //config displayable string
        listView.setCellFactory(p -> new ProjectListCell());

        //setup selection
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

    private boolean areAllItemsSelected(ListView<?> listView) {
        return listView.getSelectionModel().getSelectedItems().size() == listView.getItems().size();
    }

    private void initNewBranchButton() {
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.NEW_BRANCH_BUTTON.getId())
                .setOnAction(this::onNewBranchButton);

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.CREATE_PROJECT_BUTTON.getId())
                .setOnAction(this::createProjectButton);

        ToolbarManager.getInstance().getButtonById(ToolbarButtons.REFRESH_PROJECTS.getId())
        .setOnAction(this::refreshLoadProjects);

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_CREATE_BRANCH)
                .setOnAction(this::onNewBranchButton);

    }

    @FXML
    public void onNewBranchButton(ActionEvent actionEvent) {
        showCreateNewBranchDialog();
        refreshProjectsList();
    }

    @FXML
    public void createProjectButton(ActionEvent actionEvent) {
        // dialog
        CreateProjectDialog dialog = new CreateProjectDialog(_currentGroup, (obj) -> refreshLoadProjects(null));
        dialog.showAndWait();
    }

    private void showCreateNewBranchDialog() {
        List<Project> allSelectedProjects = projectsList.getSelectionModel().getSelectedItems();
        List<Project> clonedProjects =
                allSelectedProjects.stream()
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
            _projects = (List<Project>) _projectService.loadProjects(_currentGroup);
            refreshProjectList();
            _logger.info("Projects were refreshed!");
        });
        executor.shutdown();
    }
}
