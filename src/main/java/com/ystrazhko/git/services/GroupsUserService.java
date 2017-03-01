package com.ystrazhko.git.services;

public interface GroupsUserService {

    /**
     * Gets user's groups
     *
     * @param userData json with data about user
     * @return json with data about groups of  user<br>
     * null, if an error occurred during the request
     */
    Object getGroups(String userData);

}
