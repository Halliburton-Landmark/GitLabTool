package com.lgc.gitlabtool.git.ui.javafx;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SwitchBranchConfirmDialog extends Alert {

    private static final Logger logger = LogManager.getLogger(SwitchBranchConfirmDialog.class);

    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private final GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

    private static final String STATUS_COMMIT_DIALOG_TITLE = "Committing changes status";
    private static final String STATUS_COMMIT_DIALOG_HEADER = "Committing changes info";

    ButtonType commitButton;
    ButtonType discardButton;
    ButtonType cancelButton;

    public SwitchBranchConfirmDialog() {
        super(AlertType.WARNING);

        commitButton = new ButtonType("Commit changes");
        discardButton = new ButtonType("Discard changes");
        cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        setTitle("Switch branch confirmation");
        setHeaderText("This projects have uncommited changes");
        setContentText("Would you like to commit the changes or discard ?");

        getDialogPane().getButtonTypes().setAll(commitButton, discardButton, cancelButton);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);

    }

    public void showCommitPushDialog(List<Project> projects) {
        CommitDialog dialog = new CommitDialog();
        Optional<ButtonType> commitResult = dialog.showAndWait();

        if (commitResult.get() == dialog.getCommitButton() || commitResult.get() == dialog.getCommitAndPushButton()) {
            String commitMessage = StringUtils.EMPTY;

            boolean isPush = commitResult.get().equals(dialog.getCommitAndPushButton());

            if (dialog.getCommitMessage() == null || dialog.getCommitMessage().isEmpty()) {
                showEmptyCommitMessageWarning();
                return;
            }

            Map<Project, JGitStatus> commitStatuses = _gitService.commitChanges(projects, commitMessage, isPush,
                    new SwitchBranchProgressListener());

            String headerMessage = "All changes was successfully commited";
            String failedMessage = "Committing changes was failed";
            showStatusDialog(projects, commitStatuses,
                    headerMessage, failedMessage, STATUS_COMMIT_DIALOG_TITLE, STATUS_COMMIT_DIALOG_HEADER);
        }
    }

    public void showEmptyCommitMessageWarning(){
        // TODO: It's temporary solution. Should be rewrite CommitDialog for using Button instead ButtonTypes.
        // After that we can use disablers on buttons.
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Empty commit message");
        alert.setContentText("Please enter commit message");

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);

        alert.showAndWait();
    }

    public void showStatusDialog(List<Project> projects, Map<Project, JGitStatus> discardStatuses,
                                 String headerMessage, String failedMessage, String dialogTitle, String dialogHeader) {
        String info;

        if (discardStatuses.size() < StatusDialog.MAX_ROW_COUNT_IN_STATUS_DIALOG) {
            info = discardStatuses.entrySet().stream()
                    .map(pair -> pair.getKey().getName() + " - " + pair.getValue())
                    .collect(Collectors.joining("\n"));
        } else {

            long countSuccessfulDiscarding =
                    discardStatuses.entrySet().stream()
                            .map(Map.Entry::getValue)
                            .filter(status -> status.equals(JGitStatus.SUCCESSFUL))
                            .count();
            if (countSuccessfulDiscarding == projects.size()) {
                info = headerMessage;
            } else {
                info = failedMessage + "\n"
                        + "Successfully: " + countSuccessfulDiscarding + " project(s)" + "\n"
                        + "Failed: " + (projects.size() - countSuccessfulDiscarding) + " project(s)";
            }
        }

        Alert statusDialog = new StatusDialog(dialogTitle, dialogHeader, info);
        statusDialog.showAndWait();
    }

    public ButtonType getCommitButton(){
        return commitButton;
    }

    public ButtonType getDiscardButton(){
        return discardButton;
    }

    public ButtonType getCancelButton(){
        return cancelButton;
    }

    class SwitchBranchProgressListener implements ProgressListener {

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
                logger.info("Progress: " + object + "%");
            }
        }

        @Override
        public void onStart(Object... t) {}

        @Override
        public void onFinish(Object... t) {}

    }
}


