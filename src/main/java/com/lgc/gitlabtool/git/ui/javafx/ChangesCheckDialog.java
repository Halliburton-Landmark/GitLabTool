package com.lgc.gitlabtool.git.ui.javafx;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
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
import com.lgc.gitlabtool.git.util.NullCheckUtil;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This is the instance of {@link Alert} dialog</br>
 * Could be shown to prevent some processes execution if we have uncommitted changes in projects
 */
public class ChangesCheckDialog extends Alert {

    private static final Logger logger = LogManager.getLogger(ChangesCheckDialog.class);

    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private final GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

    private static final String STATUS_COMMIT_DIALOG_TITLE = "Committing changes status";
    private static final String STATUS_COMMIT_DIALOG_HEADER = "Committing changes info";

    private static final String STATUS_PUSH_DIALOG_TITLE = "Pushing changes status";
    private static final String STATUS_PUSH_DIALOG_HEADER = "Pushing changes info";
    
    private static final String STATUS_DISCARD_DIALOG_TITLE = "Discarding changes status";
    private static final String STATUS_DISCARD_DIALOG_HEADER = "Discarding changes info";
    
    private static final String SUCCESSFUL_DISCARD_HEADER_MESSAGE = "All changes was successfully discarded";
    private static final String FAILED_DISCARDING_MESSAGE = "Discarding changes was failed";
    

    private ButtonType commitButton;
    private ButtonType discardButton;
    private ButtonType cancelButton;

    public ChangesCheckDialog() {
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

         /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 100);
    }

    public ButtonType showCommitPushDialog(List<Project> projects) {
        CommitDialog dialog = new CommitDialog();
        Optional<ButtonType> commitResult = dialog.showAndWait();

        if (commitResult.get() == dialog.getCommitButton() || commitResult.get() == dialog.getCommitAndPushButton()) {
            String commitMessage = StringUtils.EMPTY;

            boolean isPush = commitResult.get().equals(dialog.getCommitAndPushButton());

            if (dialog.getCommitMessage() == null || dialog.getCommitMessage().isEmpty()) {
                showEmptyCommitMessageWarning();
                return ButtonType.CANCEL;
            }

            commitMessage = dialog.getCommitMessage();
            Map<Project, JGitStatus> commitStatuses = _gitService.commitChanges(projects, commitMessage, isPush,
                    new ChangesCheckProgressListener());

            String headerMessage = "All changes was successfully commited";
            String failedMessage = "Committing changes was failed";
            String dialogTitle = isPush ? STATUS_PUSH_DIALOG_TITLE : STATUS_COMMIT_DIALOG_TITLE;
            String dialogHeader = isPush ? STATUS_PUSH_DIALOG_HEADER : STATUS_COMMIT_DIALOG_HEADER;
            showStatusDialog(projects, commitStatuses,
                    headerMessage, failedMessage, dialogTitle, dialogHeader);
        }

        return commitResult.get();

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

         /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 100);

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
    
    /**
     * Launches the instance of this dialog and provides different 
     * actions depends on pushed button type.
     * <p>
     * If button type equals to commit button the {@link CommitDialog} will be invoked and
     * after that the code from <code>biConsumer</code> will be invoked</br>
     * If button type equals to {@link ButtonType#CANCEL} - discard logic will be run 
     * and after that the code from <code>biConsumer</code> will be invoked</br>
     * Else the window will be closed
     * 
     * @param changedProjects - list of projects have been changed
     * @param selectedProjects - total list of selected projects
     * @param selectedBranchName - name of the selected branch
     * @param biConsumer - the code that should be performed if <code>changedProjects</code>
     *                     list is not empty. </br>
     *                     The parameters in <code>biConsumer</code> are:</br>
     *                     <code>List&lt;Project&gt;</code> is a list of projects</br>
     *                     <code>String</code> is a name of the branch
     * @see {@link BiConsumer}
     */
    public void launchConfirmationDialog(List<Project> changedProjects, List<Project> selectedProjects,
            String selectedBranchName, BiConsumer<List<Project>, String> biConsumer) {

        ChangesCheckDialog alert = this;
        Optional<ButtonType> result = alert.showAndWait();

        if (alert.getCommitButton().equals(result.orElse(ButtonType.CANCEL))) {
            ButtonType resultCommitPushDialog = alert.showCommitPushDialog(changedProjects);

            if (!resultCommitPushDialog.getButtonData().equals(ButtonBar.ButtonData.CANCEL_CLOSE)) {
                NullCheckUtil.acceptBiConsumer(biConsumer, selectedProjects, selectedBranchName);
            }
        } else if (alert.getDiscardButton().equals(result.orElse(ButtonType.CANCEL))) {
            Map<Project, JGitStatus> discardStatuses = _gitService.discardChanges(changedProjects);

            alert.showStatusDialog(changedProjects, discardStatuses, 
                    SUCCESSFUL_DISCARD_HEADER_MESSAGE, 
                    FAILED_DISCARDING_MESSAGE,
                    STATUS_DISCARD_DIALOG_TITLE, 
                    STATUS_DISCARD_DIALOG_HEADER);

            NullCheckUtil.acceptBiConsumer(biConsumer, selectedProjects, selectedBranchName);
        } else {
            alert.close();
        }
    }

    class ChangesCheckProgressListener implements ProgressListener {

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


