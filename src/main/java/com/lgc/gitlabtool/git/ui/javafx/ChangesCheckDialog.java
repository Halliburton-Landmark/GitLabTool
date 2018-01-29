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
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.javafx.controllers.WindowLoader;
import com.lgc.gitlabtool.git.util.NullCheckUtil;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * This is the instance of {@link GLTAlert} dialog</br>
 * Could be shown to prevent some processes execution if we have uncommitted changes in projects
 */
public class ChangesCheckDialog extends GLTAlert {

    private final GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
            .getService(ThemeService.class.getName());

    private static final String STATUS_DISCARD_DIALOG_TITLE = "Discarding changes status";
    private static final String STATUS_DISCARD_DIALOG_HEADER = "Discarding changes info";

    private static final String SUCCESSFUL_DISCARD_HEADER_MESSAGE = "All changes was successfully discarded";
    private static final String FAILED_DISCARDING_MESSAGE = "Discarding changes was failed";


    private final ButtonType commitButton;
    private final ButtonType revertButton;
    private final ButtonType cancelButton;

    public ChangesCheckDialog() {
        super(AlertType.WARNING, "Check changes dialog", "This projects have uncommited changes",
              "Would you like to commit changes or discard?");

        commitButton = new ButtonType("Commit changes");
        revertButton = new ButtonType("Revert changes");
        cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);;

        getDialogPane().getButtonTypes().setAll(commitButton, revertButton, cancelButton);
    }

    private Map<Project, JGitStatus> revertChanges(List<Project> changedProjects) {
        Map<Project, JGitStatus> revertStatuses = _gitService.revertChanges(changedProjects);

        showStatusDialog(changedProjects, revertStatuses,
                SUCCESSFUL_DISCARD_HEADER_MESSAGE,
                FAILED_DISCARDING_MESSAGE,
                STATUS_DISCARD_DIALOG_TITLE,
                STATUS_DISCARD_DIALOG_HEADER);

        return revertStatuses;
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

    public ButtonType getRevertButton() {
        return revertButton;
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
     * @param selectedItem - parameter for biConsumer
     * @param biConsumer - the code that should be performed if <code>changedProjects</code>
     *                     list is not empty.
     * @see {@link BiConsumer}
     */
    public void launchConfirmationDialog(List<Project> changedProjects, List<Project> selectedProjects,
            Object selectedItem, BiConsumer<List<Project>, Object> biConsumer) {

        ChangesCheckDialog alert = this;
        Optional<ButtonType> result = alert.showAndWait();

        if (alert.getCommitButton().equals(result.orElse(ButtonType.CANCEL))) {
            WindowLoader.get().loadGitStageWindow(changedProjects);
        } else if (alert.getRevertButton().equals(result.orElse(ButtonType.CANCEL))) {
            Map<Project, JGitStatus> revertStatuses = revertChanges(changedProjects);
            executeBiConsumer(biConsumer, revertStatuses, selectedProjects, selectedItem);
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
     * @param selectedItem - selected item
     */
    private void executeBiConsumer(BiConsumer<List<Project>, Object> biConsumer,
            Map<Project, JGitStatus> operationStatuses, List<Project> selectedProjects, Object selectedItem) {

        List<Project> projectsForExecution = selectedProjects.stream()
                .filter(project -> isNotFailedOperation(operationStatuses, project))
                .collect(Collectors.toList());

        NullCheckUtil.acceptBiConsumer(biConsumer, projectsForExecution, selectedItem);
    }

    /**
     * Checks if project has not been change</br>
     * If it was changed checks if operation (commit or discard) is not failed
     *
     * @param operationStatuses - statuses of executed operation (discard or commit)
     * @param project - current project
     * @return <code>true</code> if <code>operationStatuses</code> does not has current project
     *                 or project's status in <code>operationStatuses</code> does not equals to {@link JGitStatus#FAILED}
     *                 else returns <code>false</code>
     */
    private boolean isNotFailedOperation(Map<Project, JGitStatus> operationStatuses, Project project) {
        if (operationStatuses != null) {
            JGitStatus currentStatus = operationStatuses.get(project);
            return currentStatus == null || currentStatus != JGitStatus.FAILED;
        }
        return false;
    }

}


