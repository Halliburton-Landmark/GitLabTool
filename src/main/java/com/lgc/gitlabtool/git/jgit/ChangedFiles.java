package com.lgc.gitlabtool.git.jgit;

import java.util.Collection;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Project;

public class ChangedFiles {

    private Project _project;
    private Collection<String> _stagedFiles;
    private Collection<String> _unstagedFiles;

    public ChangedFiles(Project project, Collection<String> stagedFiles, Collection<String> unstagedFiles) {
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

    public Collection<String> getStagedFiles() {
        return _stagedFiles;
    }

    public void setStagedFiles(List<String> stagedFiles) {
        _stagedFiles = stagedFiles;
    }

    public Collection<String> getUnstagedFiles() {
        return _unstagedFiles;
    }

    public void setUnstagedFiles(List<String> unstagedFiles) {
        _unstagedFiles = unstagedFiles;
    }
}
