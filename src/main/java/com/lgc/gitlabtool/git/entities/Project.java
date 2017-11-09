package com.lgc.gitlabtool.git.entities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

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
public class Project {
    private int id;
    private String http_url_to_repo;
    private String name;
    /** Path to the cloned project **/
    private String _pathToClonedProject;
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
    public void setPathToClonedProject(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("ERROR: Incorrect data. Value is null.");
        }
        Path pathToProject = Paths.get(path);
        if (checkPath(pathToProject)) {
            // TODO project must have /.git() folder.
            // The implementation of the method will be in the JGit class
            _pathToClonedProject = path;
        }
    }

    protected boolean checkPath(Path pathToProject) {
        return PathUtilities.isExistsAndDirectory(pathToProject);
    }

    /**
     * Gets path to the cloned project
     * @return path to the cloned project
     */
    public String getPath() {
        return _pathToClonedProject;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHttp_url_to_repo() {
        return http_url_to_repo;
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
        result = prime * result + ((_pathToClonedProject == null) ? 0 : _pathToClonedProject.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (_pathToClonedProject == null) {
            if (other._pathToClonedProject != null) {
                return false;
            }
        } else if (!_pathToClonedProject.equals(other._pathToClonedProject)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
