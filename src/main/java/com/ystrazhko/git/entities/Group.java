package com.ystrazhko.git.entities;

/**
 * Class keeps data about group.
 *
 * You cannot change the name field to the class,
 * otherwise JSONRarser can't parse from json string to object this class.
 *
 * @author Lyska Lyudmila
 *
 */
public class Group {
    /** The id of the group **/
    private final int id;

    /** The name of the group **/
    private final String name;

    /** The path of the group **/
    private final String path;

    /** The group's description **/
    private final String description;

    /** Enable/disable Large File Storage (LFS) for the projects in this group **/
    private final boolean lfs_enabled;

    /** Gets the group's visibility. Can be private, internal, or public **/
    private final String visibility;

    /** The avatar's URL **/
    private final String avatar_url;

    /** The web's URL **/
    private final String web_url;

    /** Allow users to request member access **/
    private final boolean request_access_enabled;

    /** The full name of the group **/
    private final String full_name;

    /** The full path of the group **/
    private final String full_path;

    /** The parent group id for creating nested group **/
    private final String parent_id;

    /**
     * Constructor to create an instance of the class.
     *
     * @param id
     * @param name
     * @param path
     * @param description
     * @param lfs_enabled
     * @param visibility
     * @param avatar_url
     * @param web_url
     * @param request_access_enabled
     * @param full_name
     * @param full_path
     * @param parent_id
     */
    public Group(int id, String name, String path, String description, boolean lfs_enabled, String visibility,
            String avatar_url, String web_url, boolean request_access_enabled, String full_name, String full_path,
            String parent_id) {

        super();
        this.id = id;
        this.name = name;
        this.path = path;
        this.description = description;
        this.lfs_enabled = lfs_enabled;
        this.visibility = visibility;
        this.avatar_url = avatar_url;
        this.web_url = web_url;
        this.request_access_enabled = request_access_enabled;
        this.full_name = full_name;
        this.full_path = full_path;
        this.parent_id = parent_id;
    }

    /**
     * Gets the id of the group
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the group
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the path of the group
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the group's description
     *
     * @return the group's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets enable/disable Large File Storage (LFS) for the projects in this group
     *
     * @return enable/disable
     */
    public boolean isLfs_enabled() {
        return lfs_enabled;
    }

    /**
     * Gets the group's visibility.
     *
     * @return the visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Gets the avatar's URL
     *
     * @return the avatar's URL
     */
    public String getAvatar_url() {
        return avatar_url;
    }

    /**
     * Gets the web's URL
     *
     * @return the web's URL
     */
    public String getWeb_url() {
        return web_url;
    }

    /**
     * Gets allow users to request member access
     *
     * @return allow users to request member access
     */
    public boolean isRequest_access_enabled() {
        return request_access_enabled;
    }

    /**
     * Gets the full name of the group
     *
     * @return the full name
     */
    public String getFull_name() {
        return full_name;
    }

    /**
     * Gets the full path of the group
     *
     * @return the full path
     */
    public String getFull_path() {
        return full_path;
    }

    /**
     * Gets the parent group id
     *
     * @return the parent group id
     */
    public String getParent_id() {
        return parent_id;
    }

}
