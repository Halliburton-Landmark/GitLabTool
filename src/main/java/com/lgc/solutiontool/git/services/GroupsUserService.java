package com.lgc.solutiontool.git.services;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.statuses.CloningStatus;

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

    /**
     * Clones user's group
     *
     * @param group           Group for cloning
     * @param destinationPath Local path of workspace
     * @return Cloning status
     */
    CloningStatus cloneGroup(Group group, String destinationPath);

    /**
     * Clones list of user's groups
     *
     * @param groups          List of groups for cloning
     * @param destinationPath Local path of workspace
     * @return Groups and their cloning statuses
     */
    Map<Group, CloningStatus> cloneGroups(List<Group> groups, String destinationPath);

    /**
     * Gets group by id
     *
     * @param idGroup Id of group
     * @return Group
     */
    Group getGroupById(int idGroup);
}
