package com.ystrazhko.git.entities;

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
    private Object[] projects;

    /**
     * Shared projects in group
     **/
    private Object[] shared_projects;

    /**
     * Constructor to create an instance of the class.
     *
     * @param id                     the id of the group
     * @param name                   the name of the group
     * @param path                   the path of the group
     * @param description            the group's description
     * @param visibility_level       the visibility level
     * @param ldap_cn                the ldap cn
     * @param ldap_access            the ldap access
     * @param lfs_enabled            enable/disable Large File Storage (LFS) for the projects in this group
     * @param avatar_url             the avatar's URL
     * @param web_url                the web's URL
     * @param request_access_enabled allow users to request member access
     * @param full_name              the full path of the group
     * @param full_path              the full path of the group
     * @param parent_id              the parent group id for creating nested group
     */
    public Group(int id, String name, String path, String description, Object visibility_level, Object ldap_cn, Object ldap_access, boolean lfs_enabled,
                 Object avatar_url, String web_url, boolean request_access_enabled, String full_name, String full_path,
                 Object parent_id, Object[] projects, Object[] shared_projects) {

        super();
        this.id = id;
        this.name = name;
        this.path = path;
        this.description = description;
        this.visibility_level = visibility_level;
        this.ldap_cn = ldap_cn;
        this.ldap_access = ldap_access;
        this.lfs_enabled = lfs_enabled;
        this.avatar_url = avatar_url;
        this.web_url = web_url;
        this.request_access_enabled = request_access_enabled;
        this.full_name = full_name;
        this.full_path = full_path;
        this.parent_id = parent_id;
        this.projects = projects;
        this.shared_projects = shared_projects;
    }

    public Group(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getVisibility_level() {
        return visibility_level;
    }

    public void setVisibility_level(Object visibility_level) {
        this.visibility_level = visibility_level;
    }

    public Object getLdap_cn() {
        return ldap_cn;
    }

    public void setLdap_cn(Object ldap_cn) {
        this.ldap_cn = ldap_cn;
    }

    public Object getLdap_access() {
        return ldap_access;
    }

    public void setLdap_access(Object ldap_access) {
        this.ldap_access = ldap_access;
    }

    public boolean isLfs_enabled() {
        return lfs_enabled;
    }

    public void setLfs_enabled(boolean lfs_enabled) {
        this.lfs_enabled = lfs_enabled;
    }

    public Object getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(Object avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public boolean isRequest_access_enabled() {
        return request_access_enabled;
    }

    public void setRequest_access_enabled(boolean request_access_enabled) {
        this.request_access_enabled = request_access_enabled;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getFull_path() {
        return full_path;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public Object getParent_id() {
        return parent_id;
    }

    public void setParent_id(Object parent_id) {
        this.parent_id = parent_id;
    }

    public Object[] getProjects() {
        return projects;
    }

    public void setProjects(Object[] projects) {
        this.projects = projects;
    }

    public Object[] getShared_projects() {
        return shared_projects;
    }

    public void setShared_projects(Object[] shared_projects) {
        this.shared_projects = shared_projects;
    }
}
