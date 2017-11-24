package com.lgc.gitlabtool.git.jgit;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.entities.Project;

public class ChangedFilesUtils {

    /**
     *
     * @param changedFiles
     * @return
     */
    public static List<String> getFileNames(List<ChangedFile> changedFiles) {
        return changedFiles.stream()
                           .map(ChangedFile::getFileName)
                           .collect(Collectors.toList());
    }

    /**
     *
     * @param fileNames
     * @param project
     * @param sourceList
     * @return
     */
    public static List<ChangedFile> findChangedFiles(List<String> fileNames, Project project, List<ChangedFile> sourceList) {
        return fileNames.stream()
                        .map(fileName -> findChangedFile(fileName, sourceList))
                        .filter(optionalFile -> optionalFile.isPresent())
                        .map(Optional::get)
                        .collect(Collectors.toList());
    }

    private static Optional<ChangedFile> findChangedFile(String fileName, List<ChangedFile> sourceList) {
        return sourceList.stream()
                         .filter(file -> Objects.equals(file.getFileName(), fileName))
                         .findFirst();
    }
}
