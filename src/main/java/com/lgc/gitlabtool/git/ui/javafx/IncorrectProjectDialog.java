package com.lgc.gitlabtool.git.ui.javafx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.EmptyProgressListener;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.util.NullCheckUtil;
import com.lgc.gitlabtool.git.util.PathUtilities;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Dialog for actions with incorrect projects.
 * We can initialization of projects or delete projects from a local disk.
 *
 * @author Lyudmila Lyska
 */
public class IncorrectProjectDialog extends Alert {

    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private static final Logger _logger = LogManager.getLogger(IncorrectProjectDialog.class);

    private final ButtonType _initButton;
    private final ButtonType _deleteButton;

    public IncorrectProjectDialog() {
        super(AlertType.WARNING);

        _initButton = new ButtonType("Init projects");
        _deleteButton = new ButtonType("Delete projects");

        setTitle("Incorrect projects dialog");
        setHeaderText("You have incorrect projects");
        setContentText("Would you like to initialization of projects or delete projects from a disk?");

        getDialogPane().getButtonTypes().setAll(_initButton, _deleteButton);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);

        /* Set sizing and position */
        ScreenUtil.adaptForMultiScreens(stage, 300, 100);
    }

    /**
     * Shows dialog
     *
     * @param inccorectProjects the incorrect projects (projects which don't have any references/commits)
     * @param finalAction       the action that should be performed after initialization or delete.
     */
    public void showDialog(List<Project> inccorectProjects, Consumer<Object> finalAction) {
        IncorrectProjectDialog alert = this;
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            _logger.error("Failed getting a button type in the IncorrectProjectDialog");
            alert.close();
            return;
        }

        String titleMessage;
        String headerMessage;
        String contentMessage;
        Map<Project, JGitStatus> statuses = new HashMap<>();

        if (alert.getInitButton().equals(result.get())) {
            _logger.info("Initialization of projects...");
            // make first commit to GitLab repository
            statuses = commitAndPush(inccorectProjects, "Project initialization");
            titleMessage = "Initialization status dialog";
            headerMessage = "Initialization of projects";
            contentMessage = null;
        } else {
            _logger.info("Deleting projects from the local disk...");
            // delete folders with incorrect projects from the local disc
            long count = inccorectProjects.stream()
                                          .filter(project -> PathUtilities.deletePath(project.getPath()))
                                          .count();

            titleMessage = "Delete status dialog";
            headerMessage = "Delete projects from the local disk";
            contentMessage = count + " out of " + inccorectProjects.size() + " projects has been deleted successfuly!";
        }
        NullCheckUtil.acceptConsumer(finalAction, null);

        StatusDialog dialog = new StatusDialog(titleMessage, headerMessage, contentMessage);
        if(contentMessage == null) {
            dialog.showMessage(statuses, statuses.size(), "\n");
        }
        dialog.show();
    }

    private ButtonType getInitButton() {
        return _initButton;
    }

    private Map<Project, JGitStatus> commitAndPush(List<Project> projects, String message) {
        return _gitService.commitChanges(projects, message, true, EmptyProgressListener.get());
    }
}
