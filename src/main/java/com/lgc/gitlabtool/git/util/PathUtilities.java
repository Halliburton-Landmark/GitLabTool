package com.lgc.gitlabtool.git.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
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
     * @param path path on disk
     * @return names of found folders
     */
    public static Collection<String> getFolders(Path path) {
        if (!isExistsAndDirectory(path)) {
            return Collections.emptyList();
        }
        Collection<String> folders = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            stream.forEach((dir) -> folders.add(dir.getFileName().toString()));
        } catch (IOException e) {
            logger.error("", e);
        }
        return folders;
    }

    public static boolean deletePath(Path path) {
        if (!Files.exists(path)) {
            return false;
        }
        try {
            FileUtils.touch(path.toFile());
            FileUtils.forceDelete(path.toFile());
//            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
//                @Override
//                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
//                    File file = path.toFile();
//                    file.setWritable(true);
//                    Files.delete(Paths.get(file.toURI()));
//                    return FileVisitResult.CONTINUE;
//                }
//
//                @Override
//                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
//                    if (e == null) {
//                        File file = dir.toFile();
//                        file.setWritable(true);
//                        Files.delete(Paths.get(file.toURI()));
//                        return FileVisitResult.CONTINUE;
//                    } else {
//                        throw e;
//                    }
//                }
//            });
            return true;
        } catch (IOException e) {
            logger.error("Error deleting path: " + e.getMessage());
        }
        return false;
    }
}
