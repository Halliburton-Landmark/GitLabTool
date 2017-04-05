package com.lgc.solutiontool.git.services;

import com.lgc.solutiontool.git.entities.User;

public interface GroupsUserService {

    /**
     * Gets user's groups
     *
     * @param user User with groups
     * @return List of groups for user <br>
     * null, if an error occurred during the request
     */
    Object getGroups(User user);

}
