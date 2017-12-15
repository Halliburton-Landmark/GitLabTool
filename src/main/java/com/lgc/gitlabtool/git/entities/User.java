package com.lgc.gitlabtool.git.entities;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("name")
    private final String name;

    /** The username of the user **/
    @SerializedName("username")
    private String username;

    /** The id of the user **/
    @SerializedName("id")
    private int id;

    @SerializedName("state")
    private String state;

    /** The avatar's URL **/
    @SerializedName("avatar_url")
    private String avatarUrl;

    /** The created date **/
    @SerializedName("created_at")
    private String createdAt;

    /** The user is admin - true or false (default) **/
    @SerializedName("is_admin")
    private boolean isAdmin;

    /** The user's biography **/
    @SerializedName("bio")
    private String bio;

    /** The Skype ID **/
    @SerializedName("skype")
    private String skype;

    /** The LinkedIn **/
    @SerializedName("linkedin")
    private String linkedin;

    /** The Twitter account **/
    @SerializedName("twitter")
    private String twitter;

    /** The Website URL **/
    @SerializedName("website_url")
    private String websiteUrl;

    /** The user of the email **/
    @SerializedName("email")
    private final String email;

    @SerializedName("color_scheme_id")
    private int colorSchemeId;

    /** The limit projects each user can create **/
    @SerializedName("projects_limit")
    private int projectsLimit;

    @SerializedName("current_sign_in_at")
    private String currentSignInAt;

    @SerializedName("identities")
    private Object identities;

    /** The user can create groups **/
    @SerializedName("can_create_group")
    private boolean canCreateGroup;

    /** The user can create projects **/
    @SerializedName("can_create_project")
    private boolean canCreateProject;

    @SerializedName("two_factor_enabled")
    private boolean twoFactorEnabled;

    /** The private token of the user **/
    @SerializedName("private_token")
    private String privateToken;

    /** The OAuth2 token of a user **/
    private String oauth2Token;

    public User(String name, String email) {
        if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
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
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Gets the created date
     *
     * @return the created date
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the user is admin
     *
     * @return the user is admin
     */
    public boolean isAdmin() {
        return isAdmin;
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
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    /**
     * Gets the user of the email
     *
     * @return the user of the email
     */
    public String getEmail() {
        return email;
    }

    public int getColorSchemeId() {
        return colorSchemeId;
    }

    /**
     * Gets the limit projects each user can create
     *
     * @return the limit projects each user can create
     */
    public int getProjectsLimit() {
        return projectsLimit;
    }

    public String getCurrentSignInAt() {
        return currentSignInAt;
    }

    public Object getIdentities() {
        return identities;
    }

    /**
     * Gets the user can create groups
     *
     * @return the user can create groups
     */
    public boolean isCanCreateGroup() {
        return canCreateGroup;
    }

    /**
     * Gets the user can create projects
     *
     * @return the user can create projects
     */
    public boolean isCanCreateProject() {
        return canCreateProject;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    /**
     * Gets the private token of the user
     *
     * @return the private token of the user
     */
    public String getPrivateToken() {
        return privateToken;
    }

    /**
     * Set value of a private token
     * @param token value of a private token
     */
    public void setPrivateToken(String token){
        privateToken = token;
    }

    /**
     * Gets the OAuth2 token of a current user
     *
     * @return OAuth2 token of a current user
     */
    public String getOAuth2token() {
        return oauth2Token;
    }

    /**
     * Set value of a OAuth2 token
     * @param token value of a OAuth2 token
     */
    public void setOAuth2token(String token){
        oauth2Token = token;
    }

}