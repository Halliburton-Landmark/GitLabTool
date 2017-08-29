package com.lgc.gitlabtool.git.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

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

    /**
     * Opens the file with user system preferred editor.
     * If user's system has no registered application to open such kind of file, <br>
     * the file won't be opened and log message will be added.
     * 
     * @param document - the file to be opened
     */
    public static void open(File document) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                _logger.debug("Start to open file: " + document.getName());
                desktop.open(document);
                _logger.debug("File opened");
            } else {
                _logger.warn("There is no application registered to open file: " + document.getName());
            }
        } catch (IOException e) {
            _logger.debug(e.getMessage());
        }
    }

    /**
     * Opens the file with user system preferred editor.
     * If user's system has no registered application to open such kind of file, <br>
     * the file won't be opened and log message will be added.
     * 
     * @param pathToFile - path to file to be opened
     */
    public static void open(String pathToFile) {
        File document = new File(pathToFile);
        open(document);
    }

}
