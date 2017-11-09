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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_fileName == null) ? 0 : _fileName.hashCode());
        result = prime * result + ((_project == null) ? 0 : _project.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChangedFile other = (ChangedFile) obj;
        if (_fileName == null) {
            if (other._fileName != null) {
                return false;
            }
        } else if (!_fileName.equals(other._fileName)) {
            return false;
        }
        if (_project == null) {
            if (other._project != null) {
                return false;
            }
        } else if (!_project.equals(other._project)) {
            return false;
        }
        return true;
    }


}
