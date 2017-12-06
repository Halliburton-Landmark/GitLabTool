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

    public void setPrivateTokenValue(String token){
        _currentUser.setPrivate_token(token);
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