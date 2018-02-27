package com.lgc.gitlabtool.git.entities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.lgc.gitlabtool.git.util.PathUtilities;

/**
 * Class keeps data about group.
 * <p>
 * You cannot change the name field to the class,
 * otherwise JSONRarser can't parse from json string to object this class.
 *
 * @author Lyska Lyudmila
 */
public class Group {
    /**
     * The id of the group
     **/
    @SerializedName("id")
    private int _id;

    /**
     * The name of the group
     **/
    @SerializedName("name")
    private String _name;

    /**
     * The path of the group
     **/
    @SerializedName("path")
    private String _path;

    /**
     * The full path of the group. Including parent group.
     **/
    @SerializedName("full_path")
    private String _fullPath;

    /**
     * Projects in group
     **/
    @SerializedName("projects")
    private transient Collection<Project> _projects;

    private String _pathToClonedGroup;

    private boolean _isCloned;

    private final List<Group> _subGroups = new ArrayList<>();

    public Group(){}

    /**
     * Gets status of clone
     *
     * @return status
     */
    public boolean isCloned() {
        return _isCloned;
    }

    /**
     * Sets status the group (group is cloned or not)
     *
     * @param status
     */
    public void setClonedStatus(boolean status) {
        _isCloned = status;
    }

    /**
     * Sets path to the cloned group
     *
     * @param path to the group
     */
    public void setPathToClonedGroup(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Invalid value passed");
        }
        Path pathToGroup = Paths.get(path);
        if (PathUtilities.isExistsAndDirectory(pathToGroup)) {
            _pathToClonedGroup = path;
        }
    }

    /**
     * Gets full path of group. It contains parent group
     *
     * @return
     */
    public String getFullPath() {
        return _fullPath;
    }

    /**
     * Gets path to the cloned group
     *
     * @return path to the group
     */
    public String getPathToClonedGroup() {
        return _pathToClonedGroup;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getPath() {
        return _path;
    }


    public Collection<Project> getProjects() {
        return _projects;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_isCloned ? 1231 : 1237);
        result = prime * result + ((_pathToClonedGroup == null) ? 0 : _pathToClonedGroup.hashCode());
        result = prime * result + _id;
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        result = prime * result + ((_path == null) ? 0 : _path.hashCode());
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
        Group other = (Group) obj;
        if (_isCloned != other._isCloned) {
            return false;
        }
        if (_pathToClonedGroup == null) {
            if (other._pathToClonedGroup != null) {
                return false;
            }
        } else if (!_pathToClonedGroup.equals(other._pathToClonedGroup)) {
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
        if (_path == null) {
            if (other._path != null) {
                return false;
            }
        } else if (!_path.equals(other._path)) {
            return false;
        }
        return true;
    }

    public List<Group> getSubGroups() {
        return _subGroups;
    }
}