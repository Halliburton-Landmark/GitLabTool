package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ClonedGroupsService;
import com.lgc.gitlabtool.git.services.GroupsUserService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.dialogs.CloneProgressDialog;
import com.lgc.gitlabtool.git.ui.dialogs.CloneProgressDialog.CloningMessageStatus;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.PathUtilities;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("unchecked")
public class CloningGroupsWindowController {
    private static final String FOLDER_CHOOSER_DIALOG = "Destination folder";
    private static final String CLONING_STATUS_ALERT_TITLE = "Cloning info";
    private static final String CLONING_STATUS_ALERT_HEADER = "Cloning statuses:";

    //Uncomment if you want to log something
    //private static final Logger logger = LogManager.getLogger(CloningGroupsWindowController.class);

    private final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
            .getService(LoginService.class.getName());

    private final GroupsUserService _groupsService = (GroupsUserService) ServiceProvider.getInstance()
            .getService(GroupsUserService.class.getName());

    private final ClonedGroupsService _clonedGroupsService = (ClonedGroupsService) ServiceProvider.getInstance()
            .getService(ClonedGroupsService.class.getName());

    private final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

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

        folderPath.textProperty().addListener(getInputFilter());
        setStyleAndDisableForIncorrectData();
    }

    private ChangeListener<? super String> getInputFilter() {
        return (observable, oldValue, newValue) -> {
            if (isIncorrectPath()) {
                setStyleAndDisableForIncorrectData();
            } else {
                folderPath.setStyle("-fx-border-color: green;");
                okButton.setDisable(false);
            }
        };
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

        Group selectedGroup = selectedGroups.get(0);
        CloneProgressDialog progressDialog = new CloneProgressDialog(stage, selectedGroup.getName(), ApplicationState.CLONE);
        _stateService.stateON(ApplicationState.CLONE);
        _groupsService.cloneGroups(selectedGroups, destinationPath,
                new CloneProgressListener(selectedGroup, destinationPath, progressDialog));
    }

    @FXML
    public void onCancelButton() throws Exception {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void cloningStatusDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(CLONING_STATUS_ALERT_TITLE);
        alert.setHeaderText(CLONING_STATUS_ALERT_HEADER);
        alert.setContentText(content);

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);

         /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 300);

        alert.showAndWait();
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

    /**
     * Listener for responding to the process of cloning a group
     *
     * @author Lyudmila Lyska
     */
    class CloneProgressListener implements ProgressListener {

        private final CloneProgressDialog _progressDialog;
        private final Group _group;
        private final String _localPath;

        public CloneProgressListener(Group group, String localPath, CloneProgressDialog progressDialog) {
            if (progressDialog == null || group == null || localPath == null) {
                throw new IllegalAccessError("Invalid parameters");
            }
            _progressDialog = progressDialog;
            _group = group;
            _localPath = localPath;
        }

        @Override

        public void onSuccess(Object... t) {
            if (t[0] instanceof Project) {
                Project project = (Project) t[0];
                _progressDialog.addMessageToConcole(project.getName() + " project is successful cloned!",
                        CloningMessageStatus.SUCCESS);
            }
            if (t[1] instanceof Double) {
                double progress = (Double) t[1];
                _progressDialog.updateProgressBar(progress);
            }
            if (t[2] instanceof String) {
                _progressDialog.addMessageToConcole((String)t[2], CloningMessageStatus.SUCCESS);
            }
        }

        @Override
        public void onError(Object... t) {
            if (t[0] instanceof Double) {
                double progress = (Double) t[0];
                _progressDialog.updateProgressBar(progress);
            }
            if (t[1] instanceof String) {
                String message = (String) t[1];
                _progressDialog.addMessageToConcole(message, CloningMessageStatus.ERROR);
            }
        }

        @Override
        public void onStart(Object... t) {
            if (t[0] instanceof Project) {
                Project project = (Project) t[0];
                _progressDialog.updateProjectLabel(project.getName());
            }
        }

        @Override
        public void onFinish(Object... t) {
            _stateService.stateOFF(ApplicationState.CLONE);
            if (t[1] instanceof Boolean) {
                if ((Boolean) t[1]) {
                    _group.setClonedStatus(true);
                    _group.setPathToClonedGroup(_localPath + File.separator + _group.getName());
                    _clonedGroupsService.addGroups(Arrays.asList(_group));
                }
            }
            String messageStatus = t[0] instanceof String ? (String) t[0] : JGit.FINISH_CLONE_MESSAGE;
            _progressDialog.addMessageToConcole(messageStatus, CloningMessageStatus.SIMPLE);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    _progressDialog.resetProgress();
                    cloningStatusDialog(messageStatus);
                }
            });

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
