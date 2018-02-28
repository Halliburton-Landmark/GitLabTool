package com.lgc.gitlabtool.git.entities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
     * The id of a parentgroup
     **/
    @SerializedName("parent_id")
    private Integer _parentId;

    /**
     * The name of the group
     **/
    @SerializedName("name")
    private String _name;

    /**
     * The full path of the group. Including parent group.
     **/
    @SerializedName("full_path")
    private String _fullPath;

    private String _path;

    private boolean _isCloned;

    private transient final List<Group> _subGroups = new ArrayList<>();

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
    public void setPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Invalid value passed");
        }
        Path pathToGroup = Paths.get(path);
        if (PathUtilities.isExistsAndDirectory(pathToGroup)) {
            _path = path;
        }
    }

    /**
     * Gets full GitLab path of group ("name of parent group (if it has) / a name of current group").
     *
     * @return full path
     */
    public String getFullPath() {
        return _fullPath;
    }

    /**
     * Get id of parent group
     *
     * @return parent id
     */
    public Integer getParentId() {
        return _parentId;
    }

    /**
     * Adds subgroup to the current group
     *
     * @param subGroup the subgroup of the current group
     */
    public void addSubGroup(Group subGroup) {
        if (subGroup != null) {
            _subGroups.add(subGroup);
        }
    }

    /**
     * Gets list of subgroups
     *
     * @return unmodifiable list
     */
    public List<Group> getSubGroups() {
        return Collections.unmodifiableList(_subGroups);
    }

    /**
     * Gets path to the cloned group
     *
     * @return path to the group
     */
    public String getPath() {
        return _path;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
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
        Group other = (Group) obj;
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