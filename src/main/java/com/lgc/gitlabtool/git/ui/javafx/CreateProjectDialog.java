package com.lgc.gitlabtool.git.ui.javafx;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ProjectTypeService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CreateProjectDialog extends Dialog<String> {

    private static final String DIALOG_TITLE = "Create Project Dialog";

    private final Label _nameLabel;
    private final Label _typeLabel;
    private final TextField _projectNameField;
    private final ComboBox<String> _typeComboBox;
    private final Button _createButton;
    private final Button _cancelButton;

    private final Group _selectGroup;
    private final ProgressListener _progressListener;

    private static final ProjectTypeService _typeServies = (ProjectTypeService) ServiceProvider.getInstance()
            .getService(ProjectTypeService.class.getName());

    private static final ProjectService _projectService =
            (ProjectService) ServiceProvider.getInstance().getService(ProjectService.class.getName());


    public CreateProjectDialog(Group selectGroup, ProgressListener progress) {
        _selectGroup = selectGroup;
        _progressListener = progress;

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        _nameLabel = new Label("Name project: ");
        grid.add(_nameLabel, 0, 2);
        _projectNameField = new TextField();
        //_branchNameField.textProperty().addListener(getInputFilter());

        grid.add(_projectNameField, 1, 2, 2, 1);
        ProjectTypeService typeServies = (ProjectTypeService) ServiceProvider.getInstance().getService(ProjectTypeService.class.getName());
        List<String> idsTypes = (List<String>) typeServies.getAllIdTypes();

        _typeLabel = new Label("Type project: ");
        grid.add(_typeLabel, 0, 3);

        ObservableList<String> options = FXCollections.observableArrayList(idsTypes);
        _typeComboBox = new ComboBox<String>(options);
        _typeComboBox.getSelectionModel().select(idsTypes.get(idsTypes.size()-1));
        grid.add(_typeComboBox, 1, 3);

        _createButton = new Button("Create Project");
        //_createButton.setDisable(true);
        _createButton.setOnAction(this::onCreateButton);
        _createButton.setDefaultButton(true);

        _cancelButton = new Button("Cancel");
        _cancelButton.setOnAction(event -> {
            closeDialog();
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(_createButton, _cancelButton);
        grid.add(hbBtn, 2, 6);

        getDialogPane().setContent(grid);
        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle(DIALOG_TITLE);
        stage.getIcons().add(appIcon);

         /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 150);

        Window window = getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> {
            closeDialog();
        });
    }

    private void onCreateButton(ActionEvent event) {
        String idType = _typeComboBox.getSelectionModel().getSelectedItem();
        ProjectType projectType = _typeServies.getTypeById(idType);
        Map<Project, String> results = _projectService
                .createProject(_selectGroup, _projectNameField.getText(), projectType, _progressListener);
        closeDialog();

        for (Entry<Project, String> result : results.entrySet()) {
            Project project = result.getKey();
            String contantMessage = result.getValue();

            String headerMessage = (project == null) ?
                        "Error creating project in the " + _selectGroup.getName() + " group." :
                        "Success creating project in the " + _selectGroup.getName() + " group.";

            StatusDialog statusDialog = new StatusDialog("Status of creating project", headerMessage, contantMessage);
            statusDialog.showAndWait();
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.close();
    }

}
