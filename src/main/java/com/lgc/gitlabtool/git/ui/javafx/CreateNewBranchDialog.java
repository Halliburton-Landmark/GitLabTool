package com.lgc.gitlabtool.git.ui.javafx;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.NameValidator;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CreateNewBranchDialog extends Dialog<String> {

    private static final String DIALOG_TITLE = "Create new branch";
    private static final String STATUS_DIALOG_TITLE = "Branch Creating status";
    private static final String STATUS_DIALOG_HEADER = "Branch creating info";
    private static final String CHOOSE_BRANCH_NAME_MESSAGE = "Please choose a new branch name";
    private static final String WRONG_INPUT_MESSAGE = " is a wrong branch name. Please try again!";

    private static final Logger _logger = LogManager.getLogger(CreateNewBranchDialog.class);
    private static final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private final Label _messageLabel;
    private final Label _textLabel;
    private final TextField _branchNameField;
    private final CheckBox _checkoutBox;
    private final Button _createButton;
    private final Button _cancelButton;

    private List<Project> _projects;
    private final NameValidator _branchValidator = NameValidator.get();

    public List<Project> getProjects() {
        return _projects;
    }

    public void setProjects(List<Project> projects) {
        this._projects = projects;
    }

    public CreateNewBranchDialog() {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        _messageLabel = new Label(CHOOSE_BRANCH_NAME_MESSAGE);
        grid.add(_messageLabel, 0, 1, 3, 1);
        _textLabel = new Label("New branch: ");
        grid.add(_textLabel, 0, 3);
        _branchNameField = new TextField();
        _branchNameField.textProperty().addListener(getInputFilter());

        grid.add(_branchNameField, 1, 3, 2, 1);
        _checkoutBox = new CheckBox("Checkout new branch");
        grid.add(_checkoutBox, 1, 4);

        _createButton = new Button("Create Branch");
        _createButton.setDisable(true);
        _createButton.setOnAction(this::onCreateButton);
        _createButton.setDefaultButton(true);

        _cancelButton = new Button("Cancel");
        _cancelButton.setOnAction(event -> {
            getStage().close();
        });

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(_createButton, _cancelButton);
        grid.add(hbBtn, 2, 6);

        getDialogPane().setContent(grid);
        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = getStage();
        stage.setResizable(false);
        stage.setTitle(DIALOG_TITLE);
        stage.getIcons().add(appIcon);

         /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 150);

        initializeOnCloseEvent();
    }

    private Stage getStage() {
        return (Stage) getDialogPane().getScene().getWindow();
    }

    private void onCreateButton(ActionEvent event) {
        String newBranchName = _branchNameField.getText().trim();
        Map<Project, JGitStatus> results = _gitService.createBranch(getProjects(), newBranchName, false);

        boolean switchToBranch = _checkoutBox.isSelected();
        if (switchToBranch) {
            switchBranch(getProjects(), newBranchName);
        }

        getStage().close();

        createAndShowStatusDialog(getProjects(), results);
    }

    private boolean isInputValid(String input) {
        return _branchValidator.validateBranchName(input);
    }

    private void initializeOnCloseEvent() {
        Window window = getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> {
            getStage().close();
        });
    }

    private ChangeListener<? super String> getInputFilter() {
        return (observable, oldValue, newValue) -> {
            if (!_branchNameField.getText().isEmpty() && isInputValid(_branchNameField.getText())) {
                _createButton.setDisable(false);
                showMessage(CHOOSE_BRANCH_NAME_MESSAGE, Color.BLACK);
            } else {
                String message = _branchNameField.getText() + WRONG_INPUT_MESSAGE;
                showMessage(message, Color.RED);
                _createButton.setDisable(true);
                _logger.debug(message);
            }
        };
    }

    private void showMessage(String message, Color color) {
        _messageLabel.setText(message);
        _messageLabel.setTextFill(color);
    }
    
    private void switchBranch(List<Project> projects, String branchName) {
        List<Project> changedProjects = _gitService.getProjectsWithChanges(getProjects());

        if (changedProjects.isEmpty()) {
            // we do not show switching on statuses here
            // because we show the statuses of branches creation
            // In the same time we could see that branch is changed on the projects list panel
            _gitService.switchTo(projects, branchName, false);
        } else {
            ChangesCheckDialog alert = new ChangesCheckDialog();
            alert.launchConfirmationDialog(changedProjects, projects, branchName, this::switchBranch);
        }
    }

    private void createAndShowStatusDialog(List<Project> projects, Map<Project, JGitStatus> results) {
        String collapsedMessage = "New branch has been created in %s selected projects";

        StatusDialog statusDialog = new StatusDialog(STATUS_DIALOG_TITLE, STATUS_DIALOG_HEADER);
        statusDialog.showMessage(results, projects.size(), collapsedMessage);
        statusDialog.show();
    }
}
