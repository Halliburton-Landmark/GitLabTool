package com.lgc.solutiontool.git.ui.javafx.controllers;

import com.lgc.solutiontool.git.services.GroupsUserService;
import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.jgit.JGit;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.eclipse.jgit.api.errors.JGitInternalException;

import java.io.File;
import java.util.List;

@SuppressWarnings("unchecked")
public class CloningGroupsWindowController {
    private static final String FOLDER_CHOOSER_DIALOG = "Destination folder";
    private static final String CLONING_STATUS_ALERT_TITLE = "Cloning status";
    private static final String ALERT_SUCCESSFUL_HEADER = "Operation finished";
    private static final String ALERT_FAIL_HEADER = "Operation failed";
    private static final String ALERT_SUCCESSFUL_MESSAGE = "Cloning process was successful";


    private LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());

    private GroupsUserService _groupsService =
            (GroupsUserService) ServiceProvider.getInstance().getService(GroupsUserService.class.getName());

    @FXML
    private TextField folderPath;

    @FXML
    private ListView<Group> projectsList;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button browseButton;

    @FXML
    public void initialize() {
        List<Group> userGroups = (List<Group>) _groupsService.getGroups(_loginService.getCurrentUser());
        ObservableList<Group> myObservableList = FXCollections.observableList(userGroups);

        configureListView(projectsList);
        projectsList.setItems(myObservableList);

        BooleanBinding booleanBinding =
                projectsList.getSelectionModel().selectedItemProperty().isNull().or(
                        folderPath.textProperty().isEqualTo(""));

        okButton.disableProperty().bind(booleanBinding);
    }

    @FXML
    public void onBrowseButton() throws Exception {
        Stage stage = (Stage) browseButton.getScene().getWindow();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(FOLDER_CHOOSER_DIALOG);
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            folderPath.setText(selectedDirectory.getCanonicalPath());
        }
    }

    @FXML
    public void onOkButton() throws Exception {
        Stage stage = (Stage) okButton.getScene().getWindow();

        String destinationPath = folderPath.getText();
        List<Group> selectedGroups = projectsList.getSelectionModel().getSelectedItems();
        try {
            for (Group groupItem : selectedGroups) {
                JGit.getInstance().clone(groupItem, destinationPath);
            }

            cloningStatusDialog(true, ALERT_SUCCESSFUL_HEADER, ALERT_SUCCESSFUL_MESSAGE);
            stage.close();
        } catch (JGitInternalException ex) {
            //TODO: catching should be moved to service and added return value with status of cloning
            cloningStatusDialog(false, ALERT_FAIL_HEADER, ex.getLocalizedMessage());
        }
    }

    @FXML
    public void onCancelButton() throws Exception {
        Stage stage = (Stage) cancelButton.getScene().getWindow();

        stage.close();
    }

    private void cloningStatusDialog(boolean isSuccessful, String header, String content) {
        Alert.AlertType alertType;
        if (isSuccessful) {
            alertType = Alert.AlertType.INFORMATION;
        } else {
            alertType = Alert.AlertType.ERROR;
        }
        Alert alert = new Alert(alertType);
        alert.setTitle(CLONING_STATUS_ALERT_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
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

        //setup selection
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            while (node != null && node != listView && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            if (node instanceof ListCell) {
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

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
    }
}
