package com.lgc.gitlabtool.git.entities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

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
    private int id;

    /**
     * The name of the group
     **/
    private String name;

    /**
     * The path of the group
     **/
    private String path;

    /**
     * The group's description
     **/
    private String description;

    /**
     * The visibility level
     **/
    private int visibility_level;

    /**
     * The ldap cn
     **/
    private Object ldap_cn;

    /**
     * The ldap access
     **/
    private Object ldap_access;

    /**
     * Enable/disable Large File Storage (LFS) for the projects in this group
     **/
    private boolean lfs_enabled;

    /**
     * The avatar's URL
     **/
    private Object avatar_url;

    /**
     * The web's URL
     **/
    private String web_url;

    /**
     * Allow users to request member access
     **/
    private boolean request_access_enabled;

    /**
     * The full path of the group
     **/
    private String full_name;

    /**
     * The full path of the group
     **/
    private String full_path;

    /**
     * The parent group id for creating nested group
     **/
    private Object parent_id;

    /**
     * Projects in group
     **/
    private Collection<Project> projects;

    /**
     * Shared projects in group
     **/
    private Object[] shared_projects;


    private String _pathToClonedGroup;

    private boolean _isCloned;


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
     * Gets path to the cloned group
     *
     * @return path to the group
     */
    public String getPathToClonedGroup() {
        return _pathToClonedGroup;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public Object getVisibility_level() {
        return visibility_level;
    }

    public Object getLdap_cn() {
        return ldap_cn;
    }

    public Object getLdap_access() {
        return ldap_access;
    }

    public boolean isLfs_enabled() {
        return lfs_enabled;
    }

    public Object getAvatar_url() {
        return avatar_url;
    }

    public String getWeb_url() {
        return web_url;
    }

    public boolean isRequest_access_enabled() {
        return request_access_enabled;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getFull_path() {
        return full_path;
    }

    public Object getParent_id() {
        return parent_id;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public Object[] getShared_projects() {
        return shared_projects;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_isCloned ? 1231 : 1237);
        result = prime * result + ((_pathToClonedGroup == null) ? 0 : _pathToClonedGroup.hashCode());
        result = prime * result + ((full_name == null) ? 0 : full_name.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((web_url == null) ? 0 : web_url.hashCode());
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
        if (full_name == null) {
            if (other.full_name != null) {
                return false;
            }
        } else if (!full_name.equals(other.full_name)) {
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
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        if (web_url == null) {
            if (other.web_url != null) {
                return false;
            }
        } else if (!web_url.equals(other.web_url)) {
            return false;
        }
        return true;
    }
}