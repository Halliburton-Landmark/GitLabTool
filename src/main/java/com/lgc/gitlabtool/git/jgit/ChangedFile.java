package com.lgc.gitlabtool.git.jgit;

import com.lgc.gitlabtool.git.entities.Project;

public class ChangedFile {
    private Project _project;
    private String _fileName;

    public ChangedFile(Project project, String fileName) {
        _project = project;
        _fileName = fileName;
    }

    public Project getProject() {
        return _project;
    }

    public void setProject(Project project) {
        _project = project;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        _fileName = fileName;
    }

}
