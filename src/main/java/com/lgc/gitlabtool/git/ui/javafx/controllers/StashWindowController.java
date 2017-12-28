package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;
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
        _stashListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _stashListView.setCellFactory(project -> new StashListCell());
        _stashListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> filterProjectList());
        _stashListView.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    _stashListView.getSelectionModel().clearSelection();
                    updateProjectListView();
                }
            }
        });
        _applyButton.disableProperty().bind(_stashListView.getSelectionModel().selectedItemProperty().isNull());
        _dropButton.disableProperty().bind(_stashListView.getSelectionModel().selectedItemProperty().isNull());
        _createButton.disableProperty().bind(_stashMessageTextField.textProperty().isEmpty());
    }

    @FXML
    public void onCreateStashAction(ActionEvent event) {
        boolean includeUntracked = _includeUntrackedComboBox.isSelected();
        List<Project> projects = _projectListView.getItems();
        List<Project> changedProjects = getChangedProjects(projects, includeUntracked);
        if (changedProjects.isEmpty()) {
            showStatusDialog("Status of creating stash",
                             "Create stash for " + projects.size() + " projects failed.",
                             "Selected projects don't have changes");
        } else {
            String stashMessage = _stashMessageTextField.getText();
            _consoleService.addMessage(changedProjects.size() + " of " + projects.size() + " have changes", MessageType.SIMPLE);
            _gitService.createStash(changedProjects, stashMessage, includeUntracked);
            _projectList.updateProjectStatuses(changedProjects);
            updateStashListView();
            _stashMessageTextField.setText(StringUtils.EMPTY);
            _includeUntrackedComboBox.setSelected(false);
        }
    }


    @FXML
    public void onApplyStashAction(ActionEvent event) {
        System.out.println("we applied stash =)"); // TODO
    }

    @FXML
    public void onDropStashAction(ActionEvent event) {
        System.out.println("we droped stash =("); // TODO
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
        _consoleService.addMessage(headerText, MessageType.determineMessageType(headerText)); // TODO
        _consoleService.addMessage(contentText, MessageType.SIMPLE);

        StatusDialog statusDialog = new StatusDialog(titleText, headerText);
        statusDialog.setContentText(contentText);
        statusDialog.showAndWait();
    }

    /**
     *  The first time you open a tab, you get a list of stashes
     *
     * @author Lyudmila Lyska
     */
    class StashListChangeListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            _stashListTitledPane.expandedProperty().removeListener(this);
            if (_stashListView.getItems().isEmpty()) {
                updateStashListView();
            }
        }

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

    @Override
    public void handleEvent(ApplicationState changedState, boolean isActivate) {
        if (!isActivate) {
            updateContentLists();
        }
    }

    private void updateContentLists() {
        _projectListView.getItems().clear();
        _stashListView.getItems().clear();
        updateProjectListView();
        updateStashListView();
    }

    private void updateStashListView() {
        _stashListView.setItems(FXCollections.observableArrayList(_gitService.getStashList(getProjects())));
    }

    private void updateProjectListView() {
        _projectListView.setItems(FXCollections.observableArrayList(getProjects()));
    }
}
