package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.stash.StashItem;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.listeners.stateListeners.StateListener;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;

public class StashWindowController implements StateListener {

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


    /**************************************** SERVICES ****************************************/

    private static final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

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

        ObservableList<Project> items = FXCollections.observableArrayList(getProjectNames());
        _projectListView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
        _projectListView.setCellFactory(project -> new ProjectListCell());
        _projectListView.setItems(items);

        _stashListTitledPane.expandedProperty().addListener(new StashListChangeListener());
        _createButton.disableProperty().bind(_stashMessageTextField.textProperty().isEmpty());
    }

    @FXML
    public void onCreateStashAction(ActionEvent event) {
        List<Project> projects = _projectListView.getItems();
        String stashMessage = _stashMessageTextField.getText();
        boolean includeUntracked = _includeUntrackedComboBox.isSelected();
        _gitService.createStash(projects, stashMessage, includeUntracked);
    }

    private List<Project> getProjectNames() {
        return _projectList.getProjectsByIds(_projectsIds);
    }



    /**
     *  The first time you open a tab, you get a list of stashes
     *
     * @author Lyudmila Lyska
     */
    class StashListChangeListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (_stashListView.getItems().isEmpty()) {
                _stashListTitledPane.expandedProperty().removeListener(this);
                System.out.println("my listener");
            }
        }

    }



    @Override
    public void handleEvent(ApplicationState changedState, boolean isActivate) {
        System.out.println("Update statuses handler");
    }
}
