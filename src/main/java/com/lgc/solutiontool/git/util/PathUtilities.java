package com.lgc.solutiontool.git.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class of utilities for working with java.nio.file.Path class.
 *
 * @author Lyudmila Lyska
 */
public class PathUtilities {

    private static final Logger logger = LogManager.getLogger(JSONParser.class);
    public static final String PATH_NOT_EXISTS_OR_NOT_DIRECTORY = "The transmitted path does not exist or is not a directory.";

    /**
     * Checks path is exist and it is directory.
     *
     * @param path path on disk
     * @return <true> - it exist and directory, otherwise - <false>.
     */
    public static boolean isExistsAndDirectory(Path path) {
        return path != null && Files.exists(path) && Files.isDirectory(path);
    }

    /**
     * Checks path is exist and it is regular file.
     *
     * @param path path on disk
     * @return <true> - it exist and regular file, otherwise - <false>.
     */
    public static boolean isExistsAndRegularFile(Path path) {
        return path != null && Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Gets all folders from directory
     *
     * @param  path path on disk
     * @return names of found folders
     */
    public static Collection<String> getFolders(Path path) {
        if (!isExistsAndDirectory(path)) {
            return Collections.emptyList();
        }
        Collection<String> folders = new ArrayList<>();
        try {
            Files.newDirectoryStream(path).forEach((dir) -> folders.add(dir.getFileName().toString()));
        } catch (IOException e) {
            logger.error("", e);
        }
        return folders;
    }

}
