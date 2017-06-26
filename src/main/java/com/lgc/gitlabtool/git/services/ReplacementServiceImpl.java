package com.lgc.gitlabtool.git.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for work with mass changes in cloned files.
 *
 * @author Lyska Lyudmila
 */
class ReplacementServiceImpl implements ReplacementService {

    private static final Logger logger = LogManager.getLogger(ReplacementServiceImpl.class);

    @Override
    public void replaceTextInFiles(String groupFolderPath, String fileName, String fromText, String toText) {
        if (groupFolderPath == null || fileName == null) {
            return; //TODO: throw new exception or write error in log
        }
        if (fromText == null || toText == null) {
            return; //TODO: throw new exception or write error in log
        }
        if (fromText.equals(toText)) {
            return; //TODO: write error in log
        }

        Path path = Paths.get(groupFolderPath);
        // Check if the file exists
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Collection<Path> listProjects = getAllElementsInFolder(path);
            if (!listProjects.isEmpty()) {
                replaceText(listProjects, fileName, fromText, toText);
            }
        } else {
            logger.error("The file/directory " + path.getFileName() + " does not exist");
        }
    }

    @Override
    public void replaceTextInFiles(Collection<String> pathsProjects, String fileName, String fromText, String toText) {
        if (pathsProjects == null || pathsProjects.isEmpty() || fileName == null) {
            return;
        }
        Collection<Path> projects = getPaths(pathsProjects);
        replaceText(projects, fileName, fromText, toText);
    }

    private void replaceText(Collection<Path> projects, String fileName, String fromText, String toText) {
        if (fromText == null || toText == null) {
            return;
        }
        if (fromText.equals(toText)) {
            return;
        }

        for (Path project : projects) {
            try {
                if(project == null) {
                    continue;
                }
                Files.walkFileTree(project, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (!attrs.isDirectory() && file.getFileName().toString().equals(fileName)) {
                            replaceText(file, fromText, toText);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.error("Error replacing text: " + e.getMessage());
            }
        }
    }

    // TODO: This method need replace with method getFolders from the PathUtilities class or move it there
    private Collection<Path> getAllElementsInFolder(Path path) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            Collection<Path> listProjects = new ArrayList<>();
            for (Path file : stream) {
                if (Files.isDirectory(file)) {
                    listProjects.add(file);
                }
            }
            return listProjects;
        } catch (IOException | DirectoryIteratorException e) {
            // IOException cannot be thrown during the iteration.
            // It can only be thrown by the newDirectoryStream method.
            logger.error("Error getting folders: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private void replaceText(Path path, String fromText, String toText) {
        Optional<String> text = readFile(path);
        if (!text.isPresent()) {
            return;
        }

        String textFromFile = text.get();
        if(!textFromFile.contains(fromText)) {
            return;
        }

        textFromFile = textFromFile.replace(fromText, toText);
        writeFile(path, textFromFile);
    }

    private Optional<String> readFile(Path path) {
        try {
            return Optional.of(FileUtils.readFileToString(path.toFile(), Charset.forName("utf-8")));
        } catch (IOException e) {
            logger.error("Error reading file: " + e.getMessage());
        }
        return Optional.empty();
    }

    private void writeFile(Path path, String textFromFile) {
        try {
            FileUtils.writeStringToFile(path.toFile(), textFromFile, Charset.forName("utf-8"), false);
        } catch (IOException e) {
            logger.error("Error writing file: " + e.getMessage());
        }
    }

    private Collection<Path> getPaths(Collection<String> projects) {
        Collection<Path> paths = new ArrayList<>();
        for (String string : projects) {
            paths.add(Paths.get(string));
        }
        return paths;
    }

}