package com.lgc.gitlabtool.git.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.lgc.gitlabtool.git.services.BackgroundService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.GLTAlert;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for launching files by user's system preferred editor
 * 
 * @author Igor Khlaponin
 *
 */
public class FileLauncherUtil {

    private static final Logger _logger = LogManager.getLogger(FileLauncherUtil.class);
    private static final String ERROR_MESSAGE = "Could not open the file";

    /**
     * Opens the file with user system preferred editor.
     * If user's system has no registered application to open such kind of file, <br>
     * the file won't be opened and log message will be added.
     * 
     * @param document - the file to be opened
     */
    public static void open(File document) {
        if (document == null) {
            _logger.warn(ERROR_MESSAGE);
            return;
        }
        Runnable openDocumentTask = () -> {
            try {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    _logger.debug("Start to open file: " + document.getName());
                    desktop.open(document);
                    _logger.debug("File opened");
                } else {
                    String message = "There is no application registered to open file: " + document.getName();
                    _logger.warn(message);
                    Platform.runLater(() -> {
                        new GLTAlert(AlertType.WARNING, "Open file dialog", message, "").showAndWait();
                    });
                }
            } catch (IllegalArgumentException | IOException e) {
                _logger.debug(ERROR_MESSAGE + ": " + e.getMessage());
            }
        };
        BackgroundService backgroundService = ServiceProvider.getInstance().getService(BackgroundService.class);
        backgroundService.runInAWTThread(openDocumentTask);
    }

    /**
     * Opens the file with user system preferred editor.
     * If user's system has no registered application to open such kind of file 
     * or file does not exist,
     * the file won't be opened and log message will be added.
     * 
     * @param pathToFile - path to file to be opened
     */
    public static void open(String pathToFile) {
        if (pathToFile == null || pathToFile.isEmpty()) {
            _logger.warn(ERROR_MESSAGE);
            return;
        }
        File document = new File(pathToFile);
        open(document);
    }

}
