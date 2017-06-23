package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.services.ClonedGroupsService;
import com.lgc.gitlabtool.git.services.GroupsUserService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.CloneProgressDialog.CloningMessageStatus;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("unchecked")
public class CloningGroupsWindowController {
    private static final String FOLDER_CHOOSER_DIALOG = "Destination folder";
    private static final String CLONING_STATUS_ALERT_TITLE = "Cloning info";
    private static final String CLONING_STATUS_ALERT_HEADER = "Cloning statuses:";

    private static final Logger logger = LogManager.getLogger(CloningGroupsWindowController.class);

    private final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
            .getService(LoginService.class.getName());

    private final GroupsUserService _groupsService = (GroupsUserService) ServiceProvider.getInstance()
            .getService(GroupsUserService.class.getName());

    private final ClonedGroupsService _clonedGroupsService = (ClonedGroupsService) ServiceProvider.getInstance()
            .getService(ClonedGroupsService.class.getName());

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

        BooleanBinding booleanBinding = projectsList.getSelectionModel().selectedItemProperty().isNull()
                .or(folderPath.textProperty().isEqualTo(""));

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

        Group selectedGroup = selectedGroups.get(0);

        CloneProgressDialog progressDialog = new CloneProgressDialog(stage, selectedGroup.getName());
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

        alert.showAndWait();
    }

    private void configureListView(ListView listView) {
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

        // setup selection
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
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    _group.setClonedStatus(true);
                    _group.setPathToClonedGroup(_localPath + File.separator + _group.getName());
                    _clonedGroupsService.addGroups(Arrays.asList(_group));

                    final String messageStatus = t[0] instanceof String ? (String) t[0] : JGit.FINISH_CLONE_MESSAGE;
                    _progressDialog.addMessageToConcole(messageStatus, CloningMessageStatus.SIMPLE);
                    _progressDialog.resetProgress();
                    cloningStatusDialog(messageStatus);
                }
            });
        }
    }
}
