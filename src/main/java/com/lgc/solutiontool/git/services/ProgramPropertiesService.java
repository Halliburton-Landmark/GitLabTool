package com.lgc.solutiontool.git.services;

import java.util.List;

import com.lgc.solutiontool.git.entities.Group;

public interface ProgramPropertiesService {

    void setClonedGroups(List<Group> groups);

    /**
     * Gets a list of cloned groups
     *
     * @return list of cloned groups
     */
    List<Group> getClonedGroups();

    /**
     * Updates local storage using the current properties
     */
    public void updateClonedGroups(List<Group> groups);

    /**
     * Loads a list with currently cloned groups
     */
    public List<Group> loadClonedGroups();


}
