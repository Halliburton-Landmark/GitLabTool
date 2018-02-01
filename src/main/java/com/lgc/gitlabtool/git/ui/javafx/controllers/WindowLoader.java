package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.JavaFXUI;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class for loading new windows by fxml which which are used outside {@link ModularController}
 *
 * @author Lyudmila Lyska
 */
public class WindowLoader {

    private static WindowLoader _windowLoader;

    private static final Logger logger = LogManager.getLogger(WindowLoader.class);
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private static final String STAGING_WINDOW_TITLE = "Git Staging";
    private static final String PROJECTS_DOES_NOT_HAVE_CHANGED_FILE = "Selected projects doesn't have new or conflicting files";
    private static final String GIT_STAGING_OPERATION_NAME = "Git Staging";

    private static final ConsoleService _consoleService = ServiceProvider.getInstance().getService(ConsoleService.class);
    private static final GitService _gitService = ServiceProvider.getInstance().getService(GitService.class);
    private static final StateService _stateService = ServiceProvider.getInstance().getService(StateService.class);
    private static final ProjectService _projectService = ServiceProvider.getInstance().getService(ProjectService.class);

    public static WindowLoader get() {
        if (_windowLoader == null) {
            _windowLoader = new WindowLoader();
        }
        return _windowLoader;
    }

    /**
     * Loads Git Staging window.
     *
     * @param selectedProjects the selected projects.
     */
    public void loadGitStageWindow(List<Project> selectedProjects) {
        try {
            if (selectedProjects == null) {
                selectedProjects = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
            }
            selectedProjects = selectedProjects.stream()
                                               .filter(Project::isCloned)
                                               .collect(Collectors.toList());
            if (selectedProjects.isEmpty()) {
                String message = String.format(ModularController.NO_ANY_PROJECT_FOR_OPERATION, GIT_STAGING_OPERATION_NAME);
                _consoleService.addMessage(message, MessageType.ERROR);
                return;
            }

            Collection<ChangedFile> files = new ArrayList<>();
            selectedProjects.forEach(project -> files.addAll(_gitService.getChangedFiles(project)));
            if (files.isEmpty()) {
                _consoleService.addMessage(PROJECTS_DOES_NOT_HAVE_CHANGED_FILE, MessageType.ERROR);
                return;
            }

            URL gitStagingWindowUrl = getClass().getClassLoader().getResource(ViewKey.GIT_STAGING_WINDOW.getPath());
            FXMLLoader loader = new FXMLLoader(gitStagingWindowUrl);
            Parent root = loader.load();

            GitStagingWindowController gitStagingWindowController = loader.getController();
            gitStagingWindowController.beforeShowing(_projectService.getIdsProjects(selectedProjects), files);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(_appIcon);
            stage.setTitle(STAGING_WINDOW_TITLE);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(event -> {
                List<ApplicationState> activeStates = _stateService.getActiveStates();
                if (!activeStates.isEmpty() || activeStates.contains(gitStagingWindowController.getStagingStates())) {
                    event.consume();
                    JavaFXUI.showWarningAlertForActiveStates(activeStates);
                    return;
                }
                gitStagingWindowController.isDisposed();
            });

            /* Set sizing and position */
            double dialogMinSize = 700;
            double preferredWidth = 900;
            stage.setWidth(preferredWidth);
            ScreenUtil.adaptForMultiScreens(stage, dialogMinSize, dialogMinSize);
            // if we set the minimum size only in fxml then window does not respond to them.
            stage.setMinHeight(dialogMinSize);
            stage.setMinWidth(dialogMinSize);
            stage.show();
        } catch (IOException e) {
            logger.error("Error loading Staging window " + e.getMessage());
        }
    }

}
