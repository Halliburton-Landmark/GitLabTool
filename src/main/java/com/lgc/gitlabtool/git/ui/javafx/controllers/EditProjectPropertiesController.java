package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.listeners.stateListeners.AbstractStateListener;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.BackgroundService;
import com.lgc.gitlabtool.git.services.PomXMLService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EditProjectPropertiesController extends AbstractStateListener {

    private static final String EDIT_POM_TITLE = "Editing project properties";

    private static final String ADDING_REPO_COLLAPSED_MESSAGE = "Repository has been added in %s selected projects";
    private static final String EDITING_REPO_COLLAPSED_MESSAGE = "Repository has been edited in %s selected projects";
    private static final String REMOVING_REPO_COLLAPSED_MESSAGE = "Repository has been removed in %s selected projects";

    private static final String ADDING_REPO_HEADER_MESSAGE = "Adding repository status";
    private static final String EDITING_REPO_HEADER_MESSAGE = "Editing repository status";
    private static final String REMOVING_REPO_HEADER_MESSAGE = "Removing repository status";

    private static final PomXMLService _pomXmlService = ServiceProvider.getInstance()
            .getService(PomXMLService.class);

    private static final StateService _stateService = ServiceProvider.getInstance()
            .getService(StateService.class);

    private static final BackgroundService _backgroundService = ServiceProvider.getInstance()
            .getService(BackgroundService.class);

    private Stage _stage;

    {
        _stateService.addStateListener(ApplicationState.LOAD_PROJECTS, this);
        _stateService.addStateListener(ApplicationState.UPDATE_PROJECT_STATUSES, this);
    }

    @FXML
    private CheckBox addIsCommit;

    @FXML
    private CheckBox editIsCommit;

    @FXML
    private CheckBox removeIsCommit;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    @FXML
    private CheckBox removeOnlyCommon;

    @FXML
    private CheckBox editOnlyCommon;

    @FXML
    private ListView<String> removeListView;

    @FXML
    private TextField editLayoutField;

    @FXML
    private TextField editUrlField;

    @FXML
    private TextField editIdField;

    @FXML
    private ComboBox<String> editListRepoCombo;

    @FXML
    private Button addButton;

    @FXML
    private TextField addLayoutField;

    @FXML
    private TextField addUrlField;

    @FXML
    private TextField addIdField;

    @FXML
    private Label releaseNameText;

    @FXML
    private Label eclipseVersionText;

    @FXML
    private TabPane editingTabs;

    @FXML
    private ListView<Project> currentProjectsListView;

    @FXML
    private Label projectsCountLabel;

    private final String EMPTY_STRING = StringUtils.EMPTY;

    private List<Integer> _selectProjectsIds;
    private final ProjectList _projectList = ProjectList.get(null);;

    private List<Project> getProjectsByIds() {
        return _projectList.getProjectsByIds(_selectProjectsIds);
    }

    void beforeStart(List<Integer> items, Stage stage) {
        _stage = stage;
        _stage.addEventFilter(WindowEvent.WINDOW_HIDDEN, event -> dispose());

        _selectProjectsIds = items;
        List<Project> projects = getProjectsByIds();

        configureProjectsListView(currentProjectsListView);
        currentProjectsListView.setItems(FXCollections.observableArrayList(projects));

        releaseNameText.setText(_pomXmlService.getReleaseName(projects));
        eclipseVersionText.setText(_pomXmlService.getEclipseRelease(projects));

        addButton.disableProperty().bind(getEmptyBinding(addIdField).or
                (getEmptyBinding(addLayoutField).or
                        (getEmptyBinding(addUrlField))));

        editButton.disableProperty().bind(editListRepoCombo.valueProperty().isNull().or
                (getEmptyBinding(editLayoutField).or
                        (getEmptyBinding(editIdField).or
                                (getEmptyBinding(editIdField)))));

        removeButton.disableProperty().bind(removeListView.getSelectionModel().selectedItemProperty().isNull());

        configureEditTab();
        configureRemoveTab();

        refreshComponents();
    }

    private void configureEditTab() {
        editOnlyCommon.setOnAction(event -> {
            reloadEditReposComboBox();
        });

        editListRepoCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String idRepo = editListRepoCombo.getValue();
            editIdField.setText(idRepo);

            filteringProjectsListView(idRepo);

            List<Project> filteredProjects = currentProjectsListView.getItems();
            editLayoutField.setText(_pomXmlService.getLayout(filteredProjects, idRepo));
            editUrlField.setText(_pomXmlService.getUrl(filteredProjects, idRepo));

        });
    }

    private void configureRemoveTab() {
        removeOnlyCommon.setOnAction(event -> {
            reloadRemoveReposList();
        });

        removeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            String idRepo = newValue;
            filteringProjectsListView(idRepo);
        });
    }

    private BooleanBinding getEmptyBinding(TextField textField) {
        return Bindings.createBooleanBinding(() -> textField.getText().isEmpty(), textField.textProperty());
    }

    private void configureProjectsListView(ListView listView) {
        //config displayable string
        listView.setCellFactory(p -> new ProjectListCell());

        //disabling selection
        listView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldvalue, newValue) -> Platform.runLater(() ->
                        listView.getSelectionModel().select(-1)));
    }

    @FXML
    public void onAddRepo(ActionEvent actionEvent) {
        String id = addIdField.getText();
        String url = addUrlField.getText();
        String layout = addLayoutField.getText();

        List<Project> filteredProjects = currentProjectsListView.getItems();
        Map<Project, Boolean> addStatuses = _pomXmlService.addRepository(filteredProjects, id, url, layout);
        showSimpleStatusDialog(addStatuses, _selectProjectsIds.size(), EDIT_POM_TITLE,
                ADDING_REPO_HEADER_MESSAGE, ADDING_REPO_COLLAPSED_MESSAGE);

        if (addIsCommit.isSelected()) {
            getStatusesAndCommitProjects(addStatuses);
        }
        refreshComponents();
    }

    @FXML
    public void onEditRepo(ActionEvent actionEvent) {
        String oldId = editListRepoCombo.getValue();
        String newId = editIdField.getText();
        String newUrl = editUrlField.getText();
        String newLayout = editLayoutField.getText();

        List<Project> filteredProjects = currentProjectsListView.getItems();
        Map<Project, Boolean> editStatuses = _pomXmlService.modifyRepository(filteredProjects,
                oldId, newId, newUrl, newLayout);
        showSimpleStatusDialog(editStatuses, filteredProjects.size(), EDIT_POM_TITLE,
                EDITING_REPO_HEADER_MESSAGE, EDITING_REPO_COLLAPSED_MESSAGE);

        if (editIsCommit.isSelected()) {
            getStatusesAndCommitProjects(editStatuses);
        }
        refreshComponents();
    }

    @FXML
    public void onRemoveRepo(ActionEvent actionEvent) {
        String id = removeListView.getSelectionModel().getSelectedItem();
        if (!confirmDeleting(id)) {
            return;
        }

        List<Project> filteredProjects = currentProjectsListView.getItems();
        Map<Project, Boolean> removeStatuses = _pomXmlService.removeRepository(filteredProjects, id);
        showSimpleStatusDialog(removeStatuses, filteredProjects.size(), EDIT_POM_TITLE,
                REMOVING_REPO_HEADER_MESSAGE, REMOVING_REPO_COLLAPSED_MESSAGE);

        if (removeIsCommit.isSelected()) {
            getStatusesAndCommitProjects(removeStatuses);
        }
        refreshComponents();
    }

    private void getStatusesAndCommitProjects(Map<Project, Boolean> statuses) {
        List<Project> projectWithChanges = getSuccessfulProjectsFromStatuses(statuses);
        commitProjects(projectWithChanges);
    }

    private boolean confirmDeleting(String id) {
        Alert closeConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete?");
        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(ButtonType.OK);

        exitButton.setText("Delete");
        closeConfirmation.setHeaderText("Confirm deleting '" + id + "' repository");
        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
        Stage stage = (Stage) removeListView.getScene().getWindow();
        closeConfirmation.initOwner(stage);

        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
        return ButtonType.OK.equals(closeResponse.orElse(ButtonType.CANCEL));
    }

    private List<Project> getSuccessfulProjectsFromStatuses(Map<Project, Boolean> statuses) {
        return statuses.entrySet()
                       .stream()
                       .filter(entry -> entry.getValue().equals(true))
                       .map(Map.Entry::getKey)
                       .collect(Collectors.toList());
    }

    private void refreshComponents() {
        reloadEditReposComboBox();
        reloadRemoveReposList();

        addIdField.setText(EMPTY_STRING);
        addLayoutField.setText(EMPTY_STRING);
        addUrlField.setText(EMPTY_STRING);

        editIdField.setText(EMPTY_STRING);
        editLayoutField.setText(EMPTY_STRING);
        editUrlField.setText(EMPTY_STRING);

        refreshProjectList();
    }

    private void refreshProjectList() {
        currentProjectsListView.getItems().clear();
        currentProjectsListView.setItems(FXCollections.observableArrayList(getProjectsByIds()));
    }

    private void reloadEditReposComboBox() {
        boolean editIsCommon = editOnlyCommon.isSelected();
        Set<String> editComboRepositories = _pomXmlService.getReposIds(getProjectsByIds(), editIsCommon);
        editListRepoCombo.getItems().clear();
        editListRepoCombo.setItems(FXCollections.observableArrayList(editComboRepositories));
    }

    private void reloadRemoveReposList() {
        boolean removeIsCommon = removeOnlyCommon.isSelected();
        Set<String> removeListRepositories = _pomXmlService.getReposIds(getProjectsByIds(), removeIsCommon);
        removeListView.getItems().clear();
        removeListView.setItems(FXCollections.observableArrayList(removeListRepositories));
    }

    private void filteringProjectsListView(String idRepo) {
        List<Project> filteredProjectList = new ArrayList<>();

        //filtering projects
        for (Project project : getProjectsByIds()) {
            if (_pomXmlService.containsRepository(project, idRepo)) {
                filteredProjectList.add(project);
            }
        }

        currentProjectsListView.getItems().clear();
        currentProjectsListView.setItems(FXCollections.observableArrayList(filteredProjectList));
    }

    private void showSimpleStatusDialog(Map<Project, Boolean> statuses, int countOfProjects,
                                        String title, String header, String collapsedMessage) {
        StatusDialog statusDialog = new StatusDialog(title, header);
        statusDialog.showMessageForSimpleStatuses(statuses, countOfProjects, collapsedMessage);
        statusDialog.showAndWait();
    }

    private void commitProjects(List<Project> projects) {
        WindowLoader.get().loadGitStageWindow(projects);
    }

    @FXML
    public void onReloadButton(ActionEvent actionEvent) {
        refreshComponents();
    }

    @Override
    public void handleEvent(ApplicationState changedState, boolean isActivate) {
        if (!isActivate) {
            _backgroundService.runInBackgroundThread(() -> {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        boolean isUpdated = false;
                        String idRepo = editListRepoCombo.getValue();
                        if (idRepo != null) {
                            filteringProjectsListView(idRepo);
                            isUpdated = true;
                        }

                        idRepo = removeListView.getSelectionModel().getSelectedItem();
                        if (idRepo != null) {
                            filteringProjectsListView(idRepo);
                            isUpdated = true;
                        }

                        if (!isUpdated) {
                            refreshProjectList();
                        }
                    }
                });
            });
        }
    }
}
