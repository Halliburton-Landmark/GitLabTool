package com.lgc.solutiontool.git.services;

import java.util.List;

import com.lgc.solutiontool.git.entities.Group;

public interface ClonedGroupsService {

    void setClonedGroups(List<Group> groups);

    /**
     * Gets a list of cloned groups
     *
     * @return list of cloned groups
     */
    List<Group> getClonedGroups();

    /**
     * Adds new cloned groups to the current and updates properties
     */
    void addGroups(List<Group> groups);

    /**
     * Removes new cloned groups to the current and updates properties
     */
    boolean removeGroups(List<Group> groups);

    /**
     * Loads a list with currently cloned groups
     */
    List<Group> loadClonedGroups();

    /**
    *
    */
   void updateClonedGroups();
}
