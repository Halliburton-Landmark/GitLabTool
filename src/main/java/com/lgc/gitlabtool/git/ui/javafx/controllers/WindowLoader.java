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
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.GitService;
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

public class WindowLoader {

    private static WindowLoader _windowLoader;

    private static final Logger logger = LogManager.getLogger(WindowLoader.class);
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private static final String STAGING_WINDOW_TITLE = "Git Staging";
    private static final String PROJECTS_DOESNT_HAVE_CHANGED_FILE = "Selected projects doesn't have new or conflicting files";

    private static final ConsoleService _consoleService = (ConsoleService) ServiceProvider.getInstance()
            .getService(ConsoleService.class.getName());

    private static final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private static final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    public static WindowLoader get() {
        if (_windowLoader == null) {
            _windowLoader = new WindowLoader();
        }
        return _windowLoader;
    }

    public void loadGitStageWindow(List<Project> selectedProjects) {
        try {
            if (selectedProjects == null) {
                selectedProjects = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
            }
            selectedProjects = selectedProjects.stream()
                                               .filter(Project::isCloned)
                                               .collect(Collectors.toList());
            if (selectedProjects.isEmpty()) {
                String message = String.format(MainWindowController.NO_ANY_PROJECT_FOR_OPERATION,
                        MainWindowController.STAGE_REMOVE_NEW_FILES_OPERATION_NAME);
                _consoleService.addMessage(message, MessageType.ERROR);
                return;
            }

            Collection<ChangedFile> files = new ArrayList<>();
            selectedProjects.forEach(project -> files.addAll(_gitService.getChangedFiles(project)));
            if (files.isEmpty()) {
                _consoleService.addMessage(PROJECTS_DOESNT_HAVE_CHANGED_FILE, MessageType.ERROR);
                return;
            }

            URL gitStagingWindowUrl = getClass().getClassLoader().getResource(ViewKey.GIT_STAGING_WINDOW.getPath());
            FXMLLoader loader = new FXMLLoader(gitStagingWindowUrl);
            Parent root = loader.load();

            GitStagingWindowController gitStagingWindowController = loader.getController();
            gitStagingWindowController.beforeShowing(ProjectList.getIdsProjects(selectedProjects), files);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(_appIcon);
            stage.setTitle(STAGING_WINDOW_TITLE);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(event -> {
                List<ApplicationState> activeAtates = _stateService.getActiveStates();
                if (!activeAtates.isEmpty() || activeAtates.contains(gitStagingWindowController.getStagingStates())) {
                    event.consume();
                    JavaFXUI.showWarningAlertForActiveStates(activeAtates);
                    return;
                }
                gitStagingWindowController.isDisposed();
            });

            /* Set sizing and position */
            double dialogWidth = 550;
            double dialogHeight = 600;
            double preferedWidth = 800;
            stage.setWidth(preferedWidth);
            ScreenUtil.adaptForMultiScreens(stage, dialogWidth, dialogHeight);
            // if we set the minimum size only in fxml then window does not respond to them.
            stage.setMinHeight(dialogHeight);
            stage.setMinWidth(dialogWidth);
            stage.show();
        } catch (IOException e) {
            logger.error("Error loading Staging window " + e.getMessage());
        }
    }

}
