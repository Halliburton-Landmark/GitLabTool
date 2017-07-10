package com.lgc.gitlabtool.git.services;

import java.util.Collection;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;

public interface ProjectService {

    /**
     * Gets projects' group from gitlab
     *
     * @param group Group
     * @return Collection with projects' of group<br>
     * null, if an error occurred during the request
     */
    Collection<Project> getProjects(Group group);

    /**
     * We load the list of projects that we have on the local disk.
     *
     * We get the list of projects from the gitlab and we update their type,
     * status and the path to the local folder when taking.
     *
     * @param  group the group for which we need to load projects
     * @return the list of projects
     */
    Collection<Project> loadProjects(Group group);

    Project createProject(Group group, String name, String idProjectType);
}
