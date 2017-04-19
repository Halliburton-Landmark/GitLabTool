package com.lgc.solutiontool.git.ui.javafx.controllers;

import com.lgc.solutiontool.git.properties.ProgramProperties;
import com.lgc.solutiontool.git.entities.Group;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author Yevhen Strazhko
 */
public class WelcomeWindowController {
    private static final String WINDOW_TITLE = "Cloning window";

    @FXML
    private ListView groupList;

    @FXML
    private Button onLoadSelectedGroup;

    @FXML
    public void initialize() {
        configureListView(groupList);
        new Thread(this::updateClonedGroups).start();

        BooleanBinding booleanBinding = groupList.getSelectionModel().selectedItemProperty().isNull();
        onLoadSelectedGroup.disableProperty().bind(booleanBinding);
    }

    @FXML
    public void onCloneGroups(ActionEvent actionEvent) throws IOException {
        URL cloningGroupsWindowUrl = getClass().getClassLoader().getResource("CloningGroupsWindow.fxml");
        if (cloningGroupsWindowUrl == null) {
            return;
        }

        Parent root = FXMLLoader.load(cloningGroupsWindowUrl);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.setOnHidden(we -> updateClonedGroups());

        stage.show();
    }

    private void updateClonedGroups() {
        List<Group> userGroups = ProgramProperties.getInstance().getClonedGroups();
        ObservableList<Group> groupsObservableList = FXCollections.observableList(userGroups);

        groupList.getItems().clear();
        groupList.getSelectionModel().clearSelection();

        groupList.setItems(groupsObservableList);

    }

    private void configureListView(ListView listView) {
        //config displayable string
        listView.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
            @Override
            public ListCell<Group> call(ListView<Group> p) {

                return new ListCell<Group>() {
                    @Override
                    protected void updateItem(Group item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            String itemText = item.getName() + " (@" + item.getPath() + ") ";
                            setText(itemText);
                        }
                    }
                };
            }
        });
    }

    @FXML
    public void onLoadSelectedGroup(ActionEvent actionEvent) throws IOException {
        Group selectedGroup = (Group) groupList.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainWindow.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setHeight(800);
        stage.setWidth(1200);

        MainWindowController controller = loader.getController();
        controller.setSelectedGroup(selectedGroup);
        controller.beforeShowing();

        Stage previousStage = (Stage) onLoadSelectedGroup.getScene().getWindow();
        previousStage.close();

        stage.setOnHiding(event -> Platform.runLater(() -> {
            previousStage.show();
            stage.close();
        }));
        stage.show();
    }
}
