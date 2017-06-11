package com.lgc.gitlabtool.git.entities;

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

    private final int color_scheme_id;

    /** The limit projects each user can create **/
    private final int projects_limit;

    private final String current_sign_in_at;

    private final Object identities;

    /** The user can create groups **/
    private final boolean can_create_group;

    /** The user can create projects **/
    private final boolean can_create_project;

    private final boolean two_factor_enabled;

    /** The private token of the user **/
    private final String private_token;

    /**
     * Constructor to create an instance of the class.
     *
     * @param name the name of the user
     * @param username the username of the user
     * @param id the id of the user
     * @param state
     * @param avatar_url the avatar's URL
     * @param created_at the created date
     * @param is_admin the user is admin
     * @param bio the user's biography
     * @param skype the Skype ID
     * @param linkedin the LinkedIn
     * @param twitter the Twitter account
     * @param website_url the Website URL
     * @param email the user of the email
     * @param color_scheme_id
     * @param projects_limit the limit projects each user can create
     * @param current_sign_in_at
     * @param identities
     * @param can_create_group the user can create groups
     * @param can_create_project the user can create projects
     * @param two_factor_enabled
     * @param private_token the private token of the user
     */
    public User(String name, String username, int id, String state, String avatar_url, String created_at,
                boolean is_admin, String bio, String skype, String linkedin, String twitter, String website_url,
                String email, int color_scheme_id, int projects_limit, String current_sign_in_at, Object identities,
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
     * Gets the name of the user
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the username of the user
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the id of the user
     *
     * @return the id of the user
     */
    public int getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    /**
     * Gets the avatar's URL
     *
     * @return
     */
    public String getAvatar_url() {
        return avatar_url;
    }

    /**
     * Gets the created date
     *
     * @return the created date
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * Gets the user is admin
     *
     * @return the user is admin
     */
    public boolean isIs_admin() {
        return is_admin;
    }

    /**
     * Gets the user's biography
     *
     * @return the user's biography
     */
    public String getBio() {
        return bio;
    }

    /**
     * Gets the Skype ID
     *
     * @return the Skype ID
     */
    public String getSkype() {
        return skype;
    }

    /**
     * Gets the LinkedIn
     *
     * @return the LinkedIn
     */
    public String getLinkedin() {
        return linkedin;
    }

    /**
     * Gets the Twitter account
     *
     * @return the Twitter account
     */
    public String getTwitter() {
        return twitter;
    }

    /**
     * Gets the Website URL
     *
     * @return the Website URL
     */
    public String getWebsite_url() {
        return website_url;
    }

    /**
     * Gets the user of the email
     *
     * @return the user of the email
     */
    public String getEmail() {
        return email;
    }

    public int getColor_scheme_id() {
        return color_scheme_id;
    }

    /**
     * Gets the limit projects each user can create
     *
     * @return the limit projects each user can create
     */
    public int getProjects_limit() {
        return projects_limit;
    }

    public String getCurrent_sign_in_at() {
        return current_sign_in_at;
    }

    public Object getIdentities() {
        return identities;
    }

    /**
     * Gets the user can create groups
     *
     * @return the user can create groups
     */
    public boolean isCan_create_group() {
        return can_create_group;
    }

    /**
     * Gets the user can create projects
     *
     * @return the user can create projects
     */
    public boolean isCan_create_project() {
        return can_create_project;
    }

    public boolean isTwo_factor_enabled() {
        return two_factor_enabled;
    }

    /**
     * Gets the private token of the user
     *
     * @return the private token of the user
     */
    public String getPrivate_token() {
        return private_token;
    }

}