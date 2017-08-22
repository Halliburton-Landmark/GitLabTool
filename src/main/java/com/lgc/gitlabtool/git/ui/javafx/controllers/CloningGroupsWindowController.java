package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.GroupsUserService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.util.PathUtilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("unchecked")
public class CloningGroupsWindowController {
    private static final String FOLDER_CHOOSER_DIALOG = "Destination folder";

    // Uncomment if you want to log something
    // private static final Logger logger = LogManager.getLogger(CloningGroupsWindowController.class);

    private final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
            .getService(LoginService.class.getName());

    private final GroupsUserService _groupsService = (GroupsUserService) ServiceProvider.getInstance()
            .getService(GroupsUserService.class.getName());

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

        folderPath.textProperty().addListener((observable, oldValue, newValue) -> filterForOkButton());
        projectsList.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                filterForOkButton();
            };
        });
        setStyleAndDisableForIncorrectData();
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
        stage.close();

        String destinationPath = folderPath.getText();
        List<Group> selectedGroups = projectsList.getSelectionModel().getSelectedItems();

        CloneProgressDialog progressDialog = new CloneProgressDialog();
        progressDialog.setStartAction(() -> startClone(destinationPath, selectedGroups, progressDialog));
        progressDialog.showDialog();
    }

    private boolean startClone(String destinationPath, List<Group> selectedGroups, CloneProgressDialog progressDialog) {
        _groupsService.cloneGroups(selectedGroups, destinationPath, 
                new OperationProgressListener(progressDialog, ApplicationState.CLONE));
        return true;
    }

    @FXML
    public void onCancelButton() throws Exception {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void configureListView(ListView<Group> listView) {
        // config displayable string
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

    private void filterForOkButton() {
        if (isIncorrectPath()) {
            setStyleAndDisableForIncorrectData();
            return;
        } else if (projectsList.getSelectionModel().selectedItemProperty().isNull().get()) {
            folderPath.setStyle("-fx-border-color: green;");
            okButton.setDisable(true);
        } else {
            folderPath.setStyle("-fx-border-color: green;");
            okButton.setDisable(false);
        }
    }

    private boolean isIncorrectPath() {
        String text = folderPath.textProperty().get();
        if (text == null || text.isEmpty()) {
            return true;
        }
        Path path = Paths.get(text);
        return !PathUtilities.isExistsAndDirectory(path);
    }

    private void setStyleAndDisableForIncorrectData() {
        folderPath.setStyle("-fx-border-color: red;");
        okButton.setDisable(true);
    }
}
