package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.stash.GroupStash;
import com.lgc.gitlabtool.git.jgit.stash.Stash;
import com.lgc.gitlabtool.git.jgit.stash.StashItem;
import com.lgc.gitlabtool.git.listeners.stateListeners.AbstractStateListener;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listcells.StashListCell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;

public class StashWindowController extends AbstractStateListener {

    @FXML
    private ListView<Project> _projectListView;

    /************************************** CREATE STASH **************************************/
    @FXML
    private TextField _stashMessageTextField;

    @FXML
    private CheckBox _includeUntrackedComboBox;

    @FXML
    private Button _createButton;

    /*************************************** STASH LIST ***************************************/

    @FXML
    private TitledPane _stashListTitledPane;

    @FXML
    private TitledPane _createStashTitledPane;

    @FXML
    private ListView<StashItem> _stashListView;

    @FXML
    private Button _applyButton;

    @FXML
    private Button _dropButton;

    /**************************************** SERVICES ****************************************/

    private static final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private static final ConsoleService _consoleService = (ConsoleService) ServiceProvider.getInstance()
            .getService(ConsoleService.class.getName());

    private static final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    /******************************************************************************************/

    private final ProjectList _projectList = ProjectList.get(null);
    private final List<Integer> _projectsIds = new ArrayList<>();

    /**
     *
     *
     * @param projectsIds
     */
    public void beforeShowing(List<Integer> projectsIds) {
        _stateService.addStateListener(ApplicationState.UPDATE_PROJECT_STATUSES, this);
        _projectsIds.addAll(projectsIds);
        _projectListView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
        _projectListView.setCellFactory(project -> new ProjectListCell());
        updateProjectListView();

        _stashListTitledPane.expandedProperty().addListener(new StashListChangeListener());
        _createStashTitledPane.expandedProperty().addListener(new CreateStashChangeListener());
        _stashListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _stashListView.setCellFactory(project -> new StashListCell());
        _stashListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> filterProjectList());

        _applyButton.disableProperty().bind(_stashListView.getSelectionModel().selectedItemProperty().isNull());
        _dropButton.disableProperty().bind(_stashListView.getSelectionModel().selectedItemProperty().isNull());
        _createButton.disableProperty().bind(_stashMessageTextField.textProperty().isEmpty());
    }

    private void updateProjectList() {
        _stashListView.getSelectionModel().clearSelection();
        if (_projectsIds.size() != _projectListView.getItems().size()) {
            updateProjectListView();
        }
    }

    @FXML
    public void onCreateStashAction(ActionEvent event) {
        boolean includeUntracked = _includeUntrackedComboBox.isSelected();
        List<Project> projects = getProjects();
        List<Project> changedProjects = getChangedProjects(projects, includeUntracked);
        if (changedProjects.isEmpty()) {
            showStatusDialog("Status of creating stash", "Selected projects don't have changes",
                             "Creating stash for " + projects.size() + " projects failed.");
        } else {
            String stashMessage = _stashMessageTextField.getText();
            String message = "Creating stash available for " + changedProjects.size() + " of " + projects.size() + " project(s).";
            _consoleService.addMessage(message, MessageType.SIMPLE);
            Map<Project, Boolean> results = _gitService.createStash(changedProjects, stashMessage, includeUntracked);
            List<Project> successfulResultProjects = getSuccessfulProjects(results);
            updateContentOfLists(changedProjects);
            _stashMessageTextField.setText(StringUtils.EMPTY);
            _includeUntrackedComboBox.setSelected(false);

            showStatusDialog("Status of creating stash", "Creating stash operation is finished.",
                    "Stash is successfully created for " + successfulResultProjects.size() + " of " + results.size() + " project(s).");

        }
    }

    @FXML
    public void onApplyStashAction(ActionEvent event) {
        StashItem selectedStash = _stashListView.getSelectionModel().getSelectedItem();
        _gitService.applyStashes(selectedStash, new ApplyStashProgressListener());
    }

    @FXML
    public void onDropStashAction(ActionEvent event) {
        StashItem selectedStash = _stashListView.getSelectionModel().getSelectedItem();
        Map<Project, Boolean> results = _gitService.stashDrop(selectedStash);
        List<Project> changedProjects = getSuccessfulProjects(results);
        updateContentOfLists(changedProjects);

        showStatusDialog("Status of droping stash", "Droping stash operation is finished.",
                "Stash is successfully droped for " + changedProjects.size() + " of " + results.size() + " project(s).");
    }

    @Override
    public void handleEvent(ApplicationState changedState, boolean isActivate) {
        if (!isActivate) {
            _projectListView.refresh();
        }
    }

    private void updateContentOfLists(List<Project> changedProjects) {
        _projectList.updateProjectStatuses(changedProjects);
        updateStashListView();
    }

    private List<Project> getSuccessfulProjects(Map<Project, Boolean> results) {
        return results.entrySet().stream()
                                 .filter(entry -> entry.getValue() != false)
                                 .map(entry -> entry.getKey())
                                 .collect(Collectors.toList());
    }

    private List<Project> getChangedProjects(List<Project> projects, boolean includeUntracked) {
        return projects.stream()
                       .filter(project -> isChangedProject(project, includeUntracked))
                       .collect(Collectors.toList());
    }

    private boolean isChangedProject(Project project, boolean includeUntracked) {
        ProjectStatus status = project.getProjectStatus();
        return includeUntracked ? status.hasChanges() || status.hasNewUntrackedFiles() : status.hasChanges();
    }

    private List<Project> getProjects() {
        return _projectList.getProjectsByIds(_projectsIds);
    }

    private void showStatusDialog(String titleText, String headerText, String contentText) {
        _consoleService.addMessage(headerText, MessageType.determineMessageType(headerText));
        _consoleService.addMessage(contentText, MessageType.determineMessageType(contentText));

        StatusDialog statusDialog = new StatusDialog(titleText, headerText);
        statusDialog.setContentText(contentText);
        statusDialog.showAndWait();
    }

    private void filterProjectList() {
        _projectListView.getItems().clear();
        StashItem selectedItem = _stashListView.getSelectionModel().getSelectedItem();
        List<Project> projects = selectedItem == null ? getProjects() : getStashProjects(selectedItem);
        ObservableList<Project> items = FXCollections.observableArrayList(projects);
        _projectListView.setItems(items);
    }

    private List<Project> getStashProjects(StashItem selectedItem) {
        List<Project> projects = new ArrayList<>();
        if (selectedItem instanceof GroupStash) {
            GroupStash group = (GroupStash) selectedItem;
            List<Project> groupProjects = group.getGroup().stream()
                                                          .map(stash -> stash.getProject())
                                                          .collect(Collectors.toList());
            projects.addAll(groupProjects);
        } else {
            projects.add(((Stash)selectedItem).getProject());
        }
        return projects;
    }

    private void updateStashListView() {
        _stashListView.setItems(FXCollections.observableArrayList(_gitService.getStashList(getProjects())));
    }

    private void updateProjectListView() {
        _projectListView.setItems(FXCollections.observableArrayList(getProjects()));
    }

    class StashListChangeListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                if (_stashListView.getItems().isEmpty()) {
                    updateStashListView();
                }
                _createStashTitledPane.setExpanded(false);
            }
        }

    }

    class CreateStashChangeListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                updateProjectList();
                _stashListTitledPane.setExpanded(false);
            }
        }

    }

    class ApplyStashProgressListener implements ProgressListener {
        private int _successCount = 0;
        private int _allStashes = 0;
        private final List<Project> _changedProjects = new ArrayList<>();

        @Override
        public void onSuccess(Object... t) {
            _successCount++;
            if (t[0] instanceof Project) {
                Project project = (Project) t[0];
                String message = "Stash is applied successfully for " + project.getName() + " project";
                _consoleService.addMessage(message, MessageType.SUCCESS);
                _changedProjects.add(project);
            }
        }

        @Override
        public void onError(Object... t) {
            if (t[0] instanceof String) {
                _consoleService.addMessage((String)t[0], MessageType.ERROR);
            }
        }

        @Override
        public void onStart(Object... t) {
            _stateService.stateON(ApplicationState.STASH);
            if (t[0] instanceof Integer) {
                _allStashes = (Integer)t[0];
            }
        }

        @Override
        public void onFinish(Object... t) {
            _stateService.stateOFF(ApplicationState.STASH);
            _projectList.updateProjectStatuses(_changedProjects);

            showStatusDialog("Status of applying stash", "Applying stash operation is finished.",
                    "Stash is successfully applied for " + _successCount + " of " + _allStashes + " project(s).");
        }

    }
}
