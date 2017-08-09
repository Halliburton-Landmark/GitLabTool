package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.List;
import java.util.Map;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.PomXMLService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class EditProjectPropertiesController {

    private static final String EDIT_POM_TITLE = "Editing project properties";

    private static final String ADDING_REPO_COLLAPSED_MESSAGE = "Repository has been added in %s selected projects";
    private static final String EDITING_REPO_COLLAPSED_MESSAGE = "Repository has been edited in %s selected projects";
    private static final String REMOVING_REPO_COLLAPSED_MESSAGE = "Repository has been removed in %s selected projects";

    private static final String ADDING_REPO_HEADER_MESSAGE = "Adding repository status";
    private static final String EDITING_REPO_HEADER_MESSAGE = "Editing repository status";
    private static final String REMOVING_REPO_HEADER_MESSAGE = "Removing repository status";

    private final PomXMLService _pomXmlService = (PomXMLService) ServiceProvider.getInstance()
            .getService(PomXMLService.class.getName());

    @FXML
    private Button addButton;

    @FXML
    private TextField addLayoutField;

    @FXML
    private TextField addUrlField;

    @FXML
    private TextField addIdField;

    @FXML
    private Text releaseNameText;

    @FXML
    private Text eclipseVersionText;

    @FXML
    private TabPane editingTabs;

    @FXML
    private ListView currentProjectsListView;

    @FXML
    private Label projectsCountLabel;

    private List<Project> selectedProjects;

    public void beforeStart(List<Project> items) {
        selectedProjects = items;
        configureProjectsListView(currentProjectsListView);
        currentProjectsListView.setItems(FXCollections.observableArrayList(items));
        releaseNameText.setText(_pomXmlService.getReleaseName(items));
        eclipseVersionText.setText(_pomXmlService.getEclipseRelease(items));

        addButton.disableProperty().bind(getEmptyBinding(addIdField).or
                (getEmptyBinding(addLayoutField).or
                        (getEmptyBinding(addUrlField))));

    }

    private BooleanBinding getEmptyBinding(TextField textField) {
        return Bindings.createBooleanBinding(() -> textField.getText().isEmpty(), textField.textProperty());
    }

    private void configureProjectsListView(ListView listView) {
        //config displayable string
        listView.setCellFactory(p -> new ProjectListCell());

        //disabling selection
        listView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldvalue, newValue) -> Platform.runLater(() -> listView.getSelectionModel().select(-1)));
    }

    @FXML
    public void onAddRepo(ActionEvent actionEvent) {
        String id = addIdField.getText();
        String url = addUrlField.getText();
        String layout = addLayoutField.getText();

        Map<Project, JGitStatus> addStatuses = _pomXmlService.addRepository(selectedProjects, id, url, layout);

        showStatusDialog(addStatuses, selectedProjects.size(), ADDING_REPO_HEADER_MESSAGE, ADDING_REPO_COLLAPSED_MESSAGE);
    }

    private void showStatusDialog(Map<Project, JGitStatus> statuses, int countOfProjects, String header, String collapsedMessage) {
        StatusDialog statusDialog = new StatusDialog(EDIT_POM_TITLE, header);
        statusDialog.showMessage(statuses, countOfProjects, collapsedMessage);
        statusDialog.showAndWait();
    }
}
