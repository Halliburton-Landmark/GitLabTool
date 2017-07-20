package com.lgc.gitlabtool.git.ui.dialogs;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * CommitDialog class allows to create a window for committing changes.
 *
 * @author Pavlo Pidhorniy, Igor Khlaponin
 */
public class CommitDialog extends Dialog<String> {
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();
    private static final Logger _logger = LogManager.getLogger(ChangesCheckDialog.class);

    private static final String STATUS_COMMIT_DIALOG_TITLE = "Committing changes status";
    private static final String STATUS_COMMIT_DIALOG_HEADER = "Committing changes info";

    private static final String STATUS_PUSH_DIALOG_TITLE = "Pushing changes status";
    private static final String STATUS_PUSH_DIALOG_HEADER = "Pushing changes info";

    private Button _commitButton;
    private Button _commitAndPushButton;
    private Button _cancelButton;
    private TextArea _textArea;
    
    private List<Project> _projectsWithChanges;
    private Map<Project, JGitStatus> _commitStatuses;
    
    private final GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

    public CommitDialog() {
        super();

        _commitButton = new Button("Commit");
        _commitButton.setDisable(true);
        _commitButton.setOnAction(this::onCommitButton);
        _commitAndPushButton = new Button("Commit and Push");
        _commitAndPushButton.setDisable(true);
        _commitAndPushButton.setOnAction(this::onCommitAndPushButton);
        _cancelButton = new Button("Cancel");
        _cancelButton.setOnAction(this::onCancelButton);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(_commitButton, _commitAndPushButton, _cancelButton);
        
        Label label = new Label("Commit message:");
        _textArea = new TextArea();
        _textArea.textProperty().addListener(getInputListener());
        VBox.setVgrow(_textArea, Priority.ALWAYS);
        VBox expContent = new VBox();
        expContent.getChildren().addAll(label, _textArea, hbBtn);
        getDialogPane().setContent(expContent);
        

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);

        setTitle("Commit and Push dialog");
        setHeaderText("This projects have uncommited changes");

         /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 350, 200);

    }

    private List<Project> getProjectsWithChanges() {
        return _projectsWithChanges;
    }
    
    private Map<Project, JGitStatus> getCommitStatuses() {
        return _commitStatuses;
    }

    private ChangeListener<? super String> getInputListener() {
        return (observable, oldValue, newValue) -> {
            if (_textArea.getText().isEmpty()) {
                _commitButton.setDisable(true);
                _commitAndPushButton.setDisable(true);
            } else {
                _commitButton.setDisable(false);
                _commitAndPushButton.setDisable(false);
            }
        };
    }

    private void onCommitButton(ActionEvent event) {
        commitChanges(getProjectsWithChanges(), false);
        showStatusDialog(false);
        closeDialog();
    }

    private void onCommitAndPushButton(ActionEvent event) {
        commitChanges(getProjectsWithChanges(), true);
        showStatusDialog(true);
        closeDialog();
    }

    private void commitChanges(List<Project> projects, boolean pushToUpstream) {
        
        String commitMessage = _textArea.getText();
        _commitStatuses = _gitService.commitChanges(projects, commitMessage, pushToUpstream,
                new CommitProgressListener());
    }

    private void onCancelButton(ActionEvent event) {
        closeDialog();
    }

    private void closeDialog() {
        ((Stage) getDialogPane().getScene().getWindow()).close();
    }

    private void showStatusDialog(boolean isPushToUpstream) {
        String dialogTitle = isPushToUpstream ? STATUS_PUSH_DIALOG_TITLE : STATUS_COMMIT_DIALOG_TITLE;
        String dialogHeader = isPushToUpstream ? STATUS_PUSH_DIALOG_HEADER : STATUS_COMMIT_DIALOG_HEADER;
        
        String info = "Successfully: %s project(s)\n"
                    + "Failed: %s project(s)";
        
        long countOfSuccessfulOperations = getCommitStatuses().entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(status -> status.equals(JGitStatus.SUCCESSFUL))
                .count();
        
        StatusDialog statusDialog = new StatusDialog(dialogTitle, dialogHeader);
        statusDialog.showMessage(getCommitStatuses(), getProjectsWithChanges().size(), info, 
                String.valueOf(countOfSuccessfulOperations), 
                String.valueOf(getProjectsWithChanges().size() - countOfSuccessfulOperations));
    }

    
    /**
     * Shows the instance of this dialog and wait until committing will be performed
     * 
     * @param changedProjects - projects with changes that should be committed
     * @return commit statuses
     */
    public Map<Project, JGitStatus> commitChanges(List<Project> changedProjects) {
        _projectsWithChanges = changedProjects;
        this.showAndWait();

        return _commitStatuses;
    }

    class CommitProgressListener implements ProgressListener {

        @Override
        public void onSuccess(Object... t) {
            if (t[0] instanceof Integer) {
                showProgress(t[0]);
            }
        }

        @Override
        public void onError(Object... t) {
            showProgress(t[0]);
        }

        private void showProgress(Object object) {
            if (object instanceof Integer) {
                _logger.info("Progress: " + object + "%");
            }
        }

        @Override
        public void onStart(Object... t) {}

        @Override
        public void onFinish(Object... t) {}

    }

}
