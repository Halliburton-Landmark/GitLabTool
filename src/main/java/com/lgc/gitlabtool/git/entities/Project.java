package com.lgc.gitlabtool.git.entities;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import com.google.gson.annotations.SerializedName;
import com.lgc.gitlabtool.git.project.nature.operation.Operation;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;
import com.lgc.gitlabtool.git.util.PathUtilities;

/**
 * Class keeps data about project.
 *
 * You cannot change the name field to the class, otherwise
 * JSONRarser can't parse from json string to object this class.
 *
 * @author Lyska Lyudmila
 */
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private int _id;

    @SerializedName("http_url_to_repo")
    private String _httpUrlToRepo;

    @SerializedName("name")
    private String _name;

    @SerializedName("name_with_namespace")
    private String _nameWithNamespace;

    /** Path to the cloned project **/
    private String _path;
    private boolean _isCloned;
    private ProjectType _type;
    private ProjectStatus _projectStatus = new ProjectStatus();

    /**
     * Sets a project type
     * @param type project type
     */
    public void setProjectType(ProjectType type) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid value passed");
        }
        _type = type;
    }

    /**
     * Gets a type of the project
     */
    public ProjectType getProjectType() {
        return _type;
    }

    /**
     * Gets available operations for this project
     * @return set of operations
     */
    public Set<Operation> getAvailableOperations() {
        return _type.getAvailableOperations();
    }

    /**
     * Gets status of clone
     * @return status
     */
    public boolean isCloned() {
        return _isCloned;
    }

    /**
     * Sets status the project (project is cloned or not)
     * @param status
     */
    public void setClonedStatus(boolean status) {
        _isCloned = status;
    }
    /**
     * Sets path to the cloned project
     * @param path to the project
     */
    public void setPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("ERROR: Incorrect data. Value is null.");
        }
        Path pathToProject = Paths.get(path);
        if (checkPath(pathToProject)) {
            // TODO project must have /.git() folder.
            // The implementation of the method will be in the JGit class
            _path = path;
        }
    }

    protected boolean checkPath(Path pathToProject) {
        return PathUtilities.isExistsAndDirectory(pathToProject);
    }

    /**
     * Gets name with namespace (path includes all parent groups)
     *
     * @return path
     */
    public String getNameWithNamespace() {
        return _nameWithNamespace.replace(" ", "");
    }

    /**
     * Gets path to the cloned project
     * @return path to the cloned project
     */
    public String getPath() {
        return _path;
    }

    public int getId() {
        return _id;
    }

    /**
     * Gets project name with parent folders (excluding a main group).
     *
     * @return project name
     */
    public String getName() {
        if (getNameWithNamespace() != null) {
            return getNameWithNamespace().substring(getNameWithNamespace().indexOf("/") + 1, getNameWithNamespace().length());
        }
        return _name;
    }

    public String getHttpUrlToRepo() {
        return _httpUrlToRepo;
    }

    /**
     * Sets the status of project
     *
     * @param status - instance of {@link ProjectStatus}
     */
    public void setProjectStatus(ProjectStatus status) {
        if (status != null) {
            _projectStatus = status;
        }
    }

    /**
     * Returns status of project
     *
     * @return current {@link ProjectStatus}
     */
    public ProjectStatus getProjectStatus() {
        return _projectStatus;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_isCloned ? 1231 : 1237);
        result = prime * result + ((_path == null) ? 0 : _path.hashCode());
        result = prime * result + _id;
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
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
        Project other = (Project) obj;
        if (_isCloned != other._isCloned) {
            return false;
        }
        if (_path == null) {
            if (other._path != null) {
                return false;
            }
        } else if (!_path.equals(other._path)) {
            return false;
        }
        if (_id != other._id) {
            return false;
        }
        if (_name == null) {
            if (other._name != null) {
                return false;
            }
        } else if (!_name.equals(other._name)) {
            return false;
        }
        return true;
    }
}
