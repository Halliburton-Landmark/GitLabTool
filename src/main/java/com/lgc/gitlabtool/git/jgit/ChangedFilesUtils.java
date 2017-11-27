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
    public static List<ChangedFile> getChangedFiles(List<String> fileNames, Project project, List<ChangedFile> sourceList) {
        return fileNames.stream()
                        .map(fileName -> getChangedFile(fileName, sourceList))
                        .filter(optionalFile -> optionalFile.isPresent())
                        .map(Optional::get)
                        .collect(Collectors.toList());
    }

    private static Optional<ChangedFile> getChangedFile(String fileName, List<ChangedFile> sourceList) {
        Optional<ChangedFile> foundFile = sourceList.stream()
                                         .filter(file -> Objects.equals(file.getFileName(), fileName))
                                         .findFirst();
        //If the file was added to the index, it can no longer have conflicts even if we do a reset.
        if (foundFile.isPresent()) {
            foundFile.get().setHasConflicting(false);
        }
        return foundFile;
    }
}
