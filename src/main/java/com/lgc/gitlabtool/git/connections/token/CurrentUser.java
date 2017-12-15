package com.lgc.gitlabtool.git.connections.token;

import com.lgc.gitlabtool.git.entities.User;

/**
 * The class contains information about the current logged-in user.
 *
 * @author Lyska Lyudmila
 */
public class CurrentUser {
    private User _currentUser;
    private static CurrentUser _instance;

    private static final String PRIVATE_TOKEN_KEY = "Authorization";

    private CurrentUser() {}

    public static CurrentUser getInstance() {
        if (_instance == null) {
            _instance = new CurrentUser();
        }
        return _instance;
    }

    /**
     * Gets the key private token
     * @return private token key
     */
    public String getPrivateTokenKey() {
        return PRIVATE_TOKEN_KEY;
    }

    /**
     * Gets a value private token of a current user
     * @return value
     */
    public String getPrivateTokenValue() {
        return _currentUser.getPrivate_token();
    }

    /**
     * Set the value of a private token for a current user.
     * @param token value of a private token
     */
    public void setPrivateTokenValue(String token){
        _currentUser.setPrivate_token(token);
    }

    /**
     * Set the value of a OAuth2 token  for a current user.
     * @param token value of OAuth2 token
     */
    public void setOAuth2TokenValue(String token){
        _currentUser.setOAuth2token(token);
    }

    /**
     * Get the value of a OAuth2 token for a current user
     * @return value
     */
    public String getOAuth2TokenValue() {
        return _currentUser.getOAuth2token();
    }

    /**
     * Sets a current user
     * @param user the current logged-in user
     */
    public void setCurrentUser(User user) {
        _currentUser = user;
    }

    /**
     * Gets a current logged-in user.
     * @return a user
     */
    public User getCurrentUser() {
        return _currentUser;
    }
}