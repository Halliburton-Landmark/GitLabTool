package com.lgc.solutiontool.git.services;

import java.util.Collection;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;

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
