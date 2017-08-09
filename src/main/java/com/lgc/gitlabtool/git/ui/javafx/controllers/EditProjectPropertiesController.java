package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.List;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.services.PomXMLService;
import com.lgc.gitlabtool.git.services.ServiceProvider;

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

        _pomXmlService.addRepository(selectedProjects, id, url, layout);
    }
}
