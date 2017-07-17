package com.lgc.gitlabtool.git.ui.javafx;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.GitService;
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

    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private final GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

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

        setTitle("Check changes dialog");
        setHeaderText("This projects have uncommited changes");
        setContentText("Would you like to commit changes or discard?");

        getDialogPane().getButtonTypes().setAll(commitButton, discardButton, cancelButton);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);

         /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 100);
    }

    private Map<Project, JGitStatus> commitChanges(List<Project> projectsWithChanges) {
        CommitDialog dialog = new CommitDialog();
        
        Map<Project, JGitStatus> commitStatuses = dialog.commitChanges(projectsWithChanges);
        
        return commitStatuses;
    }
    
    private Map<Project, JGitStatus> discardChanges(List<Project> changedProjects) {
        Map<Project, JGitStatus> discardStatuses = _gitService.discardChanges(changedProjects);
        
        showStatusDialog(changedProjects, discardStatuses, 
                SUCCESSFUL_DISCARD_HEADER_MESSAGE, 
                FAILED_DISCARDING_MESSAGE,
                STATUS_DISCARD_DIALOG_TITLE, 
                STATUS_DISCARD_DIALOG_HEADER);
        
        return discardStatuses;
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

    public ButtonType getCommitButton() {
        return commitButton;
    }

    public ButtonType getDiscardButton() {
        return discardButton;
    }

    public ButtonType getCancelButton() {
        return cancelButton;
    }
    
    /**
     * Launches the instance of this dialog and provides different 
     * actions depends on pushed button type.
     * <p>
     * If button type equals to commit button the {@link CommitDialog} will be invoked and
     * after that the code from <code>biConsumer</code> will be executed</br>
     * If button type equals to discard button the discard logic will be run 
     * and after that the code from <code>biConsumer</code> will be executed</br>
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
            Map<Project, JGitStatus> commitStatuses = commitChanges(changedProjects);
            executeBiConsumer(biConsumer, commitStatuses, selectedProjects, selectedBranchName);

        } else if (alert.getDiscardButton().equals(result.orElse(ButtonType.CANCEL))) {
            Map<Project, JGitStatus> discardStatuses = discardChanges(changedProjects);
            executeBiConsumer(biConsumer, discardStatuses, selectedProjects, selectedBranchName);
        } else {
            alert.close();
        }
    }
    
    /**
     * Executes code in biConsumer for each project that is not failed the commit or discard.
     * <p>
     * <code>operatinStatuses</code> is a map that contains the projects that had a changes
     * and statuses of operation (discard or commit)<br>
     * If operation status equals to {@link JGitStatus#FAILED} we should remove this project from 
     * list for execution in biConsumer
     * 
     * @param biConsumer - code that should be executed
     * @param operationStatuses - statuses of executed operation (discard or commit)
     * @param selectedProjects - list of selected projects
     * @param selectedBranchName - branch name
     */
    private void executeBiConsumer(BiConsumer<List<Project>, String> biConsumer,
            Map<Project, JGitStatus> operationStatuses, List<Project> selectedProjects, String selectedBranchName) {

        List<Project> projectsForExecution = selectedProjects.stream() 
                .filter(project -> operationStatuses.get(project) == null 
                    || operationStatuses.get(project) != JGitStatus.FAILED)
                .collect(Collectors.toList());

        NullCheckUtil.acceptBiConsumer(biConsumer, projectsForExecution, selectedBranchName);
    }

}


