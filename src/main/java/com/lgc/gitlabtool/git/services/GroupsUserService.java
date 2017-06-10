package com.lgc.gitlabtool.git.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.statuses.CloningStatus;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.User;

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
     * @param group           Group for cloning.
     * @param destinationPath Local path of workspace.
     * @param onSuccess       method for tracking the success progress of cloning,
     *                        where <Integer> is a percentage of progress,
     *                        <Project> is a cloned project.
     * @param onError         method for tracking the errors during cloning,
                              where <Integer> is a percentage of progress, <String> error message.
     * @return cloned group
     */
    Group cloneGroup(Group group, String destinationPath, BiConsumer<Integer, Project> onSuccess, BiConsumer<Integer, String> onError);

    /**
     * Clones list of user's groups
     *
     * @param groups          List of groups for cloning
     * @param destinationPath Local path of workspace
     * @param onSuccess       method for tracking the success progress of cloning,
     *                        where <Integer> is a percentage of progress,
     *                        <Project> is a cloned project.
     * @param onError         method for tracking the errors during cloning,
                              where <Integer> is a percentage of progress, <String> error message.
     * @return Groups and their cloning statuses
     */
    Map<Group, CloningStatus> cloneGroups(List<Group> groups, String destinationPath,
                                          BiConsumer<Integer, Project> onSuccess, BiConsumer<Integer, String> onError);

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
