package com.lgc.solutiontool.git.entities;

import java.util.Collection;

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
    private Object visibility_level;

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

    public boolean isCloned() {
        return _isCloned;
    }

    public void setClonedStatus(boolean status) {
        _isCloned = status;
    }

    public void setPathToClonedGroup(String path) {
        //TODO valid
        _pathToClonedGroup = path;
    }

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
}