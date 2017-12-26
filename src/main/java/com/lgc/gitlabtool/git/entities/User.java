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
    private final String _name;

    /** The username of the user **/
    @SerializedName("username")
    private String _username;

    /** The id of the user **/
    @SerializedName("id")
    private int _id;

    @SerializedName("state")
    private String _state;

    /** The avatar's URL **/
    @SerializedName("avatar_url")
    private String _avatarUrl;

    /** The created date **/
    @SerializedName("created_at")
    private String _createdAt;

    /** The user is admin - true or false (default) **/
    @SerializedName("is_admin")
    private boolean _isAdmin;

    /** The user's biography **/
    @SerializedName("bio")
    private String _bio;

    /** The Skype ID **/
    @SerializedName("skype")
    private String _skype;

    /** The LinkedIn **/
    @SerializedName("linkedin")
    private String _linkedin;

    /** The Twitter account **/
    @SerializedName("twitter")
    private String _twitter;

    /** The Website URL **/
    @SerializedName("website_url")
    private String _websiteUrl;

    /** The user of the email **/
    @SerializedName("email")
    private final String _email;

    @SerializedName("color_scheme_id")
    private int _colorSchemeId;

    /** The limit projects each user can create **/
    @SerializedName("projects_limit")
    private int _projectsLimit;

    @SerializedName("current_sign_in_at")
    private String _currentSignInAt;

    @SerializedName("identities")
    private Object _identities;

    /** The user can create groups **/
    @SerializedName("can_create_group")
    private boolean _canCreateGroup;

    /** The user can create projects **/
    @SerializedName("can_create_project")
    private boolean _canCreateProject;

    @SerializedName("two_factor_enabled")
    private boolean _twoFactorEnabled;

    /** The private token of the user **/
    @SerializedName("private_token")
    private String _privateToken;

    /** The OAuth2 token of a user **/
    private String _oauth2Token;

    public User(String name, String email) {
        if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Incorrect data!");
        }
        this._name = name;
        this._email = email;
    }

    /**
     * Gets the name of the user
     *
     * @return the name of the user
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the username of the user
     *
     * @return the username of the user
     */
    public String getUsername() {
        return _username;
    }

    /**
     * Gets the id of the user
     *
     * @return the id of the user
     */
    public int getId() {
        return _id;
    }

    public String getState() {
        return _state;
    }

    /**
     * Gets the avatar's URL
     *
     * @return
     */
    public String getAvatarUrl() {
        return _avatarUrl;
    }

    /**
     * Gets the created date
     *
     * @return the created date
     */
    public String getCreatedAt() {
        return _createdAt;
    }

    /**
     * Gets the user is admin
     *
     * @return the user is admin
     */
    public boolean isAdmin() {
        return _isAdmin;
    }

    /**
     * Gets the user's biography
     *
     * @return the user's biography
     */
    public String getBio() {
        return _bio;
    }

    /**
     * Gets the Skype ID
     *
     * @return the Skype ID
     */
    public String getSkype() {
        return _skype;
    }

    /**
     * Gets the LinkedIn
     *
     * @return the LinkedIn
     */
    public String getLinkedin() {
        return _linkedin;
    }

    /**
     * Gets the Twitter account
     *
     * @return the Twitter account
     */
    public String getTwitter() {
        return _twitter;
    }

    /**
     * Gets the Website URL
     *
     * @return the Website URL
     */
    public String getWebsiteUrl() {
        return _websiteUrl;
    }

    /**
     * Gets the user of the email
     *
     * @return the user of the email
     */
    public String getEmail() {
        return _email;
    }

    public int getColorSchemeId() {
        return _colorSchemeId;
    }

    /**
     * Gets the limit projects each user can create
     *
     * @return the limit projects each user can create
     */
    public int getProjectsLimit() {
        return _projectsLimit;
    }

    public String getCurrentSignInAt() {
        return _currentSignInAt;
    }

    public Object getIdentities() {
        return _identities;
    }

    /**
     * Gets the user can create groups
     *
     * @return the user can create groups
     */
    public boolean isCanCreateGroup() {
        return _canCreateGroup;
    }

    /**
     * Gets the user can create projects
     *
     * @return the user can create projects
     */
    public boolean isCanCreateProject() {
        return _canCreateProject;
    }

    public boolean isTwoFactorEnabled() {
        return _twoFactorEnabled;
    }

    /**
     * Gets the private token of the user
     *
     * @return the private token of the user
     */
    public String getPrivateToken() {
        return _privateToken;
    }

    /**
     * Set value of a private token
     * @param token value of a private token
     */
    public void setPrivateToken(String token){
        _privateToken = token;
    }

    /**
     * Gets the OAuth2 token of a current user
     *
     * @return OAuth2 token of a current user
     */
    public String getOAuth2token() {
        return _oauth2Token;
    }

    /**
     * Set value of a OAuth2 token
     * @param token value of a OAuth2 token
     */
    public void setOAuth2token(String token){
        _oauth2Token = token;
    }

}