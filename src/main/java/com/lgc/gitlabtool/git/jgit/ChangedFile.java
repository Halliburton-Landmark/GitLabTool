package com.lgc.gitlabtool.git.jgit;

import org.apache.commons.io.FilenameUtils;

import com.lgc.gitlabtool.git.entities.Project;

public class ChangedFile {
    private Project _project;
    private String _fileName;
    private boolean _hasConflicting;
    private String _fileExtension;

    public ChangedFile(Project project, String fileName, boolean hasConflicting) {
        setProject(project);
        setFileName(fileName);
        setHasConflicting(hasConflicting);
    }

    public Project getProject() {
        return _project;
    }

    public String getFileName() {
        return _fileName;
    }

    public String getFileExtension() {
        return _fileExtension;
    }

    public boolean isHasConflicting() {
        return _hasConflicting;
    }

    private void setProject(Project project) {
        _project = project;
    }

    private void setFileName(String fileName) {
        _fileName = fileName;
        setFileExtension(FilenameUtils.getExtension(fileName));
    }

    private void setFileExtension(String fileName) {
        _fileExtension = FilenameUtils.getExtension(fileName);
    }

    public void setHasConflicting(boolean hasConflicting) {
        _hasConflicting = hasConflicting;
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
