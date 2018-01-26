package com.lgc.gitlabtool.git.ui.javafx.actions;

import com.lgc.gitlabtool.git.services.BackgroundService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.GLTAlert;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OpenFolderAction implements Action {

    private static final BackgroundService _backgroundService = ServiceProvider.getInstance()
            .getService(BackgroundService.class);
    private static final Logger _logger = LogManager.getLogger(OpenFolderAction.class);

    public void openFolder(String path) {
        String fileDoesNotExistMessage = "Specified file does not exist";
        Runnable openFolderTask = () -> {
            try {
                Desktop.getDesktop().open(new File(path));
            } catch (IOException e) {
                showAlert("The specified file has no associated application " + System.lineSeparator() +
                        "or the associated application fails to be launched", e);
            } catch (NullPointerException npe) {
                showAlert(fileDoesNotExistMessage, npe);
            } catch (UnsupportedOperationException uoe) {
                showAlert("Current platform does not support this action", uoe);
            } catch (SecurityException se) {
                showAlert("Denied read access to the file", se);
            } catch (IllegalArgumentException iae) {
                showAlert(fileDoesNotExistMessage, iae);
            }
        };
        _backgroundService.runInAWTThread(openFolderTask);
    }

    private void showAlert(String message, Throwable e) {
        _logger.error(message, e);
        Platform.runLater(() -> new GLTAlert(Alert.AlertType.ERROR, "Open folder issue", message, "").showAndWait());
    }
}
