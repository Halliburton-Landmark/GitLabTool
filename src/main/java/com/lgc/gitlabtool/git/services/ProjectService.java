package com.lgc.gitlabtool.git.services;

import java.util.Collection;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;

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

    /**
     * Creates new project in the group on GitLab and creates its local copy.
     *
     * @param group the group where we create new project
     * @param name  the name of new project
     * @param projectType the type in accordance with which the file structure on the disk will be created
     * @param progressListener the listener which processes the process of creating a project (returns
     *        the status of the operation, the created project, transmits information for the UI)
     *
     * This method nothing return. We get all info from progressListener.
     */
    void createProject(Group group, String name, ProjectType projectType, ProgressListener progressListener);

    /**
     * Checks that project with this name hasn't existed yet.
     *
     * @param group the group where we create new project
     * @param nameProject the name of project
     * @return true - if project with this name has already existed, otherwise - false.
     */
    boolean isProjectExists(Group group, String nameProject);

    /**
     * Clones shadow projects
     *
     * @param projects         projects which didn't clone.
     * @param destinationPath  the local path of parent group
     * @param progressListener listener for obtaining data on the process of performing the operation.
     */
    void clone (List<Project> projects,  String destinationPath, ProgressListener progressListener);
}
