package com.lgc.solutiontool.git.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.statuses.CloningStatus;

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
     * Clones list of user's groups
     *
     * @param groups          List of groups for cloning
     * @param destinationPath Local path of workspace
     * @param
     * @return Groups and their cloning statuses
     */
    Map<Group, CloningStatus> cloneGroups(List<Group> groups, String destinationPath, ProgressListener progressListener);

    /**
     * Gets group by id
     *
     * @param idGroup Id of group
     * @return Group
     */
    Group getGroupById(int idGroup);

    /**
     * Imports a group from the local repository. Gets all data about a group from the GitLab.
     * Also, updates statuses, types and local paths of cloned projects.
     *
     * @param  groupPath path to cloned group
     * @throws IllegalArgumentException if data is incorrect
     * @return Optional of loaded group or Optional.empty() and a error message.
     */
    Map<Optional<Group>, String> importGroup(String groupPath);

    /**
    * Removes a group from the workspace
    *
    * @param group                 the group for deletion
    * @param isRemoveFromLocalDisk if <true> remove group from local disk.
    *                              !!! WARNING: We always pass <false> in the removeGroup method,
    *                              because removing a group from a local disk requires modification.
    *                              When we deleting ".git" folder we getting AccessDeniedException or folder
    *                              is deleted only after close application
    *                              (Problem with threads(appears after import or clone group)).
    *
    * @return status and message operation.
    */
    Map<Boolean, String> removeGroup(Group group, boolean isRemoveFromLocalDisk);

}
