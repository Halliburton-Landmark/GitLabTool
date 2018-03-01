package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.preferences.ApplicationPreferences;
import com.lgc.gitlabtool.git.preferences.PreferencesNodes;
import com.lgc.gitlabtool.git.services.GroupService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.CloneProgressDialog;
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

    private final LoginService _loginService = ServiceProvider.getInstance().getService(LoginService.class);

    private final GroupService _groupsService = ServiceProvider.getInstance()
            .getService(GroupService.class);

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

    private static final String PREF_NAME = "path_to_group";

    private Collection<Group> _allGroups;

    private ApplicationPreferences getPrefs() {
        return ((ApplicationPreferences) ServiceProvider.getInstance()
                .getService(ApplicationPreferences.class)).node(PreferencesNodes.CLONING_GROUP_NODE);
    }

    @FXML
    public void initialize() {
        _allGroups = _groupsService.getGroups(_loginService.getCurrentUser());
        List<Group> mainGroups = _groupsService.getOnlyMainGroups((List<Group>)_allGroups);
        ObservableList<Group> myObservableList = FXCollections.observableList(mainGroups);
        configureListView(projectsList);
        projectsList.setItems(myObservableList);

        folderPath.textProperty().addListener((observable, oldValue, newValue) -> filterForOkButton());
        projectsList.setOnMouseClicked((EventHandler<Event>) event -> filterForOkButton());
        setStyleAndDisableForIncorrectData();

        String propertyValue = getPrefs().get(PREF_NAME, StringUtils.EMPTY);
        if (propertyValue != null) {
            folderPath.setText(propertyValue);
        }
    }

    @FXML
    public void onBrowseButton() throws Exception {
        Stage stage = (Stage) browseButton.getScene().getWindow();
        browseButton.getScene().getStylesheets()
                .add(getClass().getClassLoader().getResource("css/modular_dark_style.css").toExternalForm());


        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(FOLDER_CHOOSER_DIALOG);
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            String path = selectedDirectory.getCanonicalPath();
            getPrefs().put(PREF_NAME, path);
            folderPath.setText(path);
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
                            String itemText = item.getName() + " (@" + item.getFullPath() + ")";
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
