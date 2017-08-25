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
    private ProjectStatus _projectStatus = ProjectStatus.DEFAULT;

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
     * Shows if project has uncommitted changes
     * 
     * @return <code>true</code> if project has uncommitted changes and <code>false</code> otherwise
     */
    public boolean hasUncommittedChanges() {
        return _projectStatus == ProjectStatus.HAS_CHANGES;
    }

    /**
     * Shows if project has conflicts
     * 
     * @return <code>true</code> if project has conflicts and <code>false</code> otherwise
     */
    public boolean hasConflicts() {
        return _projectStatus == ProjectStatus.HAS_CONFLICTS;
    }

    /**
     * Sets the status of project
     * 
     * @param status - instance of {@link ProjectStatus}
     */
    public void setProjectStatus(ProjectStatus status) {
        _projectStatus = status;
    }

    /**
     * Returns status of project
     * 
     * @return current {@link ProjectStatus}
     */
    public ProjectStatus getProjectStatus() {
        return _projectStatus;
    }

}
