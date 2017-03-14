package com.ystrazhko.git.entities;

/**
 * Class keeps data about user.
 *
 * You cannot change the name field to the class,
 * otherwise JSONRarser can't parse from json string to object this class.
 *
 * @author Lyska Lyudmila
 *
 */
public class User {
    /** The name of the user **/
    private final String name;

    /** The username of the user **/
    private final String username;

    /** The id of the user **/
    private final int id;

    /**  **/
    private final String state;

    /** The avatar's URL **/
    private final String avatar_url;

    /** The created date **/
    private final String created_at;

    /** The user is admin - true or false (default) **/
    private final boolean is_admin;

    /** The user's biography **/
    private final String bio;

    /** The Skype ID **/
    private final String skype;

    /** The LinkedIn **/
    private final String linkedin;

    /** The Twitter account **/
    private final String twitter;

    /** The Website URL **/
    private final String website_url;

    /** The user of the email **/
    private final String email;

    /**  **/
    private final int color_scheme_id;

    /** The limit projects each user can create **/
    private final int projects_limit;

    /**  **/
    private final String current_sign_in_at;

    /**  **/
    private final String[] identities;

    /** The user can create groups **/
    private final boolean can_create_group;

    /** The user can create projects **/
    private final boolean can_create_project;

    /**  **/
    private final boolean two_factor_enabled;

    /** The private token of the user **/
    private final String private_token;

    /**
     * Constructor to create an instance of the class.
     *
     * @param name
     * @param username
     * @param id
     * @param state
     * @param avatar_url
     * @param created_at
     * @param is_admin
     * @param bio
     * @param skype
     * @param linkedin
     * @param twitter
     * @param website_url
     * @param email
     * @param color_scheme_id
     * @param projects_limit
     * @param current_sign_in_at
     * @param identities
     * @param can_create_group
     * @param can_create_project
     * @param two_factor_enabled
     * @param private_token
     */
    public User(String name, String username, int id, String state, String avatar_url, String created_at,
            boolean is_admin, String bio, String skype, String linkedin, String twitter, String website_url,
            String email, int color_scheme_id, int projects_limit, String current_sign_in_at, String[] identities,
            boolean can_create_group, boolean can_create_project, boolean two_factor_enabled, String private_token) {
        super();
        this.name = name;
        this.username = username;
        this.id = id;
        this.state = state;
        this.avatar_url = avatar_url;
        this.created_at = created_at;
        this.is_admin = is_admin;
        this.bio = bio;
        this.skype = skype;
        this.linkedin = linkedin;
        this.twitter = twitter;
        this.website_url = website_url;
        this.email = email;
        this.color_scheme_id = color_scheme_id;
        this.projects_limit = projects_limit;
        this.current_sign_in_at = current_sign_in_at;
        this.identities = identities;
        this.can_create_group = can_create_group;
        this.can_create_project = can_create_project;
        this.two_factor_enabled = two_factor_enabled;
        this.private_token = private_token;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getAvatar_url() {
        return avatar_url;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * Gets the
     *
     * @return
     */
    public boolean isIs_admin() {
        return is_admin;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getBio() {
        return bio;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getSkype() {
        return skype;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getLinkedin() {
        return linkedin;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getTwitter() {
        return twitter;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getWebsite_url() {
        return website_url;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the
     *
     * @return
     */
    public int getColor_scheme_id() {
        return color_scheme_id;
    }

    /**
     * Gets the
     *
     * @return
     */
    public int getProjects_limit() {
        return projects_limit;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getCurrent_sign_in_at() {
        return current_sign_in_at;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String[] getIdentities() {
        return identities;
    }

    /**
     * Gets the
     *
     * @return
     */
    public boolean isCan_create_group() {
        return can_create_group;
    }

    /**
     * Gets the
     *
     * @return
     */
    public boolean isCan_create_project() {
        return can_create_project;
    }

    /**
     * Gets the
     *
     * @return
     */
    public boolean isTwo_factor_enabled() {
        return two_factor_enabled;
    }

    /**
     * Gets the
     *
     * @return
     */
    public String getPrivate_token() {
        return private_token;
    }

}
