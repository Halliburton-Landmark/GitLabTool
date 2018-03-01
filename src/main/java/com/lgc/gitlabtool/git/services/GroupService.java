package com.lgc.gitlabtool.git.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.User;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;

public interface GroupService extends Service {

    /**
     * Gets user's groups
     *
     * @param user User with groups
     * @return List of groups for user <br>
     * null, if an error occurred during the request
     */
    Collection<Group> getGroups(User user);

    /**
     * Clones list of user's group and adds their to the ClonedGroups class.
     *
     * @param group            selected group
     * @param projects         selected projects for cloning. If it is <code>null</code> clone all projects in group
     * @param destinationPath  Local path of workspace
     * @param progressListener Listener for obtaining data on the process of performing the operation.
     *                         We must call StateService::stateOFF for this state
     *                         in the ProgressListener::onFinish method.
     */
    void cloneGroup(Group group, List<Project> projects, String destinationPath, OperationProgressListener progressListener);

    /**
     * Imports a group from the local repository. Gets all data about a group from the GitLab.
     * Also, updates statuses, types and local paths of cloned projects.
     *
     * @param  groupPath path to cloned group
     * @throws IllegalArgumentException if data is incorrect
     * @return loaded group
     */
    Group importGroup(String groupPath);

    /**
    * Removes a group from the workspace
    *
    * @param group                 the group for deletion
    * @param isRemoveFromLocalDisk if <true> remove group from local disk, otherwise - false
    *
    * @return status and message operation.
    */
    Map<Boolean, String> removeGroup(Group group, boolean isRemoveFromLocalDisk);

    /**
     * Gets only main group (it is groups which doesn't have parent group)
     *
     * @param groups the list of all groups
     *
     * @return a list of main group
     */
    List<Group> getOnlyMainGroups(List<Group> groups);

    /**
     * Reloads group. Sends request to GitLab. Sets to loaded group:
     *    - sub groups (which was got from the GitLab),
     *    - a clone status and a path from an old group (which was passed as a parameter).
     *
     * @param group the group which need to reload. Object doesn't changed
     * @return loaded group (new object).
     */
    Group reloadGroup(Group group);

}