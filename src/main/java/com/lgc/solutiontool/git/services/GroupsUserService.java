package com.lgc.solutiontool.git.services;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
     * Clones user's group
     *
     * @param group           Group for cloning
     * @param destinationPath Local path of workspace
     * @param onSuccess method for tracking the success progress of cloning
+    * @param onError method for tracking the errors during cloning
+    * @return cloned group
     */
    Group cloneGroup(Group group, String destinationPath, Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError);

    /**
     * Clones list of user's groups
     *
     * @param groups          List of groups for cloning
     * @param destinationPath Local path of workspace
     * @param onSuccess method for tracking the success progress of cloning
     * @param onError method for tracking the errors during cloning
     * @return Groups and their cloning statuses
     */
    Map<Group, CloningStatus> cloneGroups(List<Group> groups, String destinationPath, Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError);

    /**
     * Gets group by id
     *
     * @param idGroup Id of group
     * @return Group
     */
    Group getGroupById(int idGroup);
}
