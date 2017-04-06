package com.lgc.solutiontool.git.ui.javafx.controllers;

import com.lgc.solutiontool.git.properties.ProgramProperties;
import com.lgc.solutiontool.git.entities.Group;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    public ListView groupList;

    @FXML
    public void onCreateGroupspace(ActionEvent actionEvent) throws IOException {
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



        stage.show();
    }

    private void configureListView(ListView listView) {
        //config displayable string
        listView.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
            @Override
            public ListCell<Group> call(ListView<Group> p) {

                return new ListCell<Group>() {
                    @Override
                    protected void updateItem(Group item, boolean bln) {
                        super.updateItem(item, bln);
                        if (item != null) {
                            String itemText = item.getName() + " (@" + item.getPath() + ") ";
                            setText(itemText);
                        }
                    }
                };
            }
        });
    }

    public void loadLocalGroups(ActionEvent actionEvent) {
        List<Group> userGroups = ProgramProperties.getInstance().getClonedGroups();
        ObservableList<Group> myObservableList = FXCollections.observableList(userGroups);

        configureListView(groupList);
        groupList.setItems(myObservableList);
    }
}
