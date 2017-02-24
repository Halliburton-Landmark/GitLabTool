package com.ystrazhko.git.services;

public interface GroupsUserService {

    /**
     * Gets groups of users
     *
     * @param userData json with data about user
     * @return json with data about groups of  user
     *
     * null, if an error occurred during the request
     */
    Object getGroups(String userData);

}
