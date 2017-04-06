package com.lgc.solutiontool.git.services;

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

/**
 * Class for work with mass changes in cloned files.
 *
 * @author Lyska Lyudmila
 */
class ReplacementServiceImpl implements ReplacementService {

    @Override
    public void replaceTextInFiles(String groupFolderPath, String fileName, String fromText, String toText) {
        if (groupFolderPath == null || fileName == null) {
            return;
        }
        if (fromText == null || toText == null) {
            return;
        }
        if (fromText.equals(toText)) {
            return;
        }

        Path path = Paths.get(groupFolderPath);
        // Check if the file exists
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Collection<Path> listProjects = getAllElementsInFolder(path);
            if (!listProjects.isEmpty()) {
                replaceTextInFiles(listProjects, fileName, fromText, toText);
            }
        } else {
            System.err.println("!ERROR: The file/directory " + path.getFileName() + " does not exist");
        }
    }

    @Override
    public void replaceTextInFiles(Collection<Path> pathsProjects, String fileName, String fromText, String toText) {
        if (pathsProjects == null || pathsProjects.isEmpty() || fileName == null) {
            return;
        }
        if (fromText == null || toText == null) {
            return;
        }
        if (fromText.equals(toText)) {
            return;
        }

        for (Path project : pathsProjects) {
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
                System.err.println("!ERROR:" + e.getMessage());
            }
        }
    }

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
            System.err.println("!ERROR:" + e.getMessage());
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
            System.err.println("!ERROR:" + e.getMessage());
        }
        return Optional.empty();
    }

    private void writeFile(Path path, String textFromFile) {
        try {
            FileUtils.writeStringToFile(path.toFile(), textFromFile, Charset.forName("utf-8"), false);
        } catch (IOException e) {
            System.err.println("!ERROR:" + e.getMessage());
        }
    }

}