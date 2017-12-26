package com.lgc.gitlabtool.git.jgit;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.entities.Project;

/**
 * Util class for working with {@link ChangedFile}.
 *
 * @author Lyudmila Lyska
 */
public class ChangedFilesUtils {

    /**
     * Gets list of file names from the changedFiles list.
     *
     * @param  changedFiles the changed files
     * @return a list of file names
     */
    public List<String> getFileNames(List<ChangedFile> changedFiles) {
        return changedFiles.stream()
                           .map(ChangedFile::getFileName)
                           .collect(Collectors.toList());
    }

    /**
     * Gets list of changed files by the list of file names using source list.
     *
     * @param  fileNames  the list of file names
     * @param  project    the project from which were received these files
     * @param  sourceList the source from which got the list of file names.
     * @return a list of changed files found by names in the source list.
     */
    public List<ChangedFile> getChangedFiles(List<String> fileNames, Project project, List<ChangedFile> sourceList) {
        return fileNames.stream()
                        .map(fileName -> getChangedFile(fileName, sourceList))
                        .filter(optionalFile -> optionalFile.isPresent())
                        .map(Optional::get)
                        .collect(Collectors.toList());
    }

    private Optional<ChangedFile> getChangedFile(String fileName, List<ChangedFile> sourceList) {
        return sourceList.stream()
                         .filter(file -> Objects.equals(file.getFileName(), fileName))
                         .findFirst();
    }
}
