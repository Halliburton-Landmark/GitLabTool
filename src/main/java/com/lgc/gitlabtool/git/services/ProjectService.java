package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;

import java.util.Collection;

public interface ProjectService {

    /**
     * Gets projects' group
     *
     * @param group Group
     * @return Collection with projects' of group<br>
     * null, if an error occurred during the request
     */
    Collection<Project> getProjects(Group group);
}
