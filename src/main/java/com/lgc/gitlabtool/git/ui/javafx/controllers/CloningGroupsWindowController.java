package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.services.GroupsUserService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ProjectTypeService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.statuses.CloningStatus;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;

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


    private final LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());

    private final GroupsUserService _groupsService =
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

        Map<Group, CloningStatus> statuses = _groupsService.cloneGroups(selectedGroups, destinationPath,
                new SuccessfulOperationHandler(), new UnsuccessfulOperationHandler());

        String dialogMessage = statuses.entrySet().stream()
                .map(x -> x.getKey().getName() + "  -  " + x.getValue().getMessage())
                .collect(Collectors.joining("\n"));
        cloningStatusDialog(dialogMessage);

        stage.close();
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

    /**
     * Handler for successful operation.
     *
     * @author Lyudmila Lyska
     */
    class SuccessfulOperationHandler implements BiConsumer<Integer, Project> {

        @Override
        public void accept(Integer percentage, Project project) {
            System.out.println("Progress: " + percentage + "%"); // TODO: in log or UI console

            // Determine the project type
            ProjectTypeService prTypeService = (ProjectTypeService) ServiceProvider.getInstance()
                    .getService(ProjectTypeService.class.getName());
            project.setProjectType(prTypeService.getProjectType(project));
        }

    }

    /**
     * Handler for unsuccessful operation
     *
     * @author Lyudmila Lyska
     */
    class UnsuccessfulOperationHandler implements BiConsumer<Integer, String> {

        @Override
        public void accept(Integer percentage, String message) {
            // TODO: in log or UI console
            System.err.println("!ERROR: " + message);
            System.out.println("Progress: " + percentage + "%");
        }

    }
}
