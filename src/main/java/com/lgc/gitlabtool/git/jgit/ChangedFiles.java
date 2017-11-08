package com.lgc.gitlabtool.git.jgit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Project;

public class ChangedFiles {

    private Project _project;
    private Collection<ChangedFile> _stagedFiles;
    private Collection<ChangedFile> _unstagedFiles;

    public ChangedFiles(Project project, Collection<ChangedFile> stagedFiles, Collection<ChangedFile> unstagedFiles) {
        _project = project;
        _stagedFiles = stagedFiles;
        _unstagedFiles = unstagedFiles;
    }

    public Project getProject() {
        return _project;
    }

    public void setProject(Project project) {
        _project = project;
    }

    public Collection<ChangedFile> getStagedFiles() {
        return _stagedFiles;
    }

    public void setStagedFiles(List<ChangedFile> stagedFiles) {
        _stagedFiles = stagedFiles;
    }

    public Collection<ChangedFile> getUnstagedFiles() {
        return _unstagedFiles;
    }

    public void setUnstagedFiles(List<ChangedFile> unstagedFiles) {
        _unstagedFiles = unstagedFiles;
    }

    public static Collection<ChangedFile> getChangedFiles(Collection<String> filesName, Project project) {
        Collection<ChangedFile> files = new ArrayList<>();
        if (project == null || filesName == null || filesName.isEmpty()) {
            return files;
        }
        for (String fileName : filesName) {
            if (fileName == null) {
                continue;
            }
            files.add(new ChangedFile(project, fileName));
        }
        return files;
    }
}
