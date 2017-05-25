package com.lgc.solutiontool.git.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A class of utilities for working with java.nio.file.Path class.
 *
 * @author Lyudmila Lyska
 */
public class PathUtilities {

    /**
     * Checks path is exist and it is directory.
     *
     * @param path on disk for verification
     * @return <true> - it exist and directory, otherwise - <false>.
     */
    public static boolean isExistsAndDirectory(Path path) {
        return path != null && Files.exists(path) && Files.isDirectory(path);
    }

    /**
     * Checks path is exist and it is regular file.
     *
     * @param on disk for verification
     * @return <true> - it exist and regular file, otherwise - <false>.
     */
    public static boolean isExistsAndRegularFile(Path path) {
        return path != null && Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Gets all folders from directory
     *
     * @param  path of directory to retrieve folders
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
            e.printStackTrace();
        }
        return folders;
    }

}
