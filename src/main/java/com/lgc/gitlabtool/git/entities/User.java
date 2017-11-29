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
    private String username;

    /** The id of the user **/
    private int id;

    private String state;

    /** The avatar's URL **/
    private String avatar_url;

    /** The created date **/
    private String created_at;

    /** The user is admin - true or false (default) **/
    private boolean is_admin;

    /** The user's biography **/
    private String bio;

    /** The Skype ID **/
    private String skype;

    /** The LinkedIn **/
    private String linkedin;

    /** The Twitter account **/
    private String twitter;

    /** The Website URL **/
    private String website_url;

    /** The user of the email **/
    private final String email;

    private int color_scheme_id;

    /** The limit projects each user can create **/
    private int projects_limit;

    private String current_sign_in_at;

    private Object identities;

    /** The user can create groups **/
    private boolean can_create_group;

    /** The user can create projects **/
    private boolean can_create_project;

    private boolean two_factor_enabled;

    /** The private token of the user **/
    private String private_token;

    public User(String name, String email) {
        if (name == null || email == null || name.isEmpty() || email.isEmpty()) { // TODO valid
            throw new IllegalArgumentException("Incorrect data!");
        }
        this.name = name;
        this.email = email;
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