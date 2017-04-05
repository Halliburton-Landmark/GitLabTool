package com.ystrazhko.git.services;

import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.entities.User;
import com.ystrazhko.git.statuses.CloningStatus;

import java.util.List;
import java.util.Map;

public interface GroupsUserService {

    /**
     * Gets user's groups
     *
     * @param user User with groups
     * @return List of groups for user <br>
     * null, if an error occurred during the request
     */
    Object getGroups(User user);

    CloningStatus cloneGroup(Group group, String destinationPath);

    Map<Group, CloningStatus> cloneGroups(List<Group> groups, String destinationPath);
}
