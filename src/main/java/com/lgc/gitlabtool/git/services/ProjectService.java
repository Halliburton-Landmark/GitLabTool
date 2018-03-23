package com.lgc.gitlabtool.git.services;

import java.util.Collection;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.listeners.updateProgressListener.UpdateProgressGenerator;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;

public interface ProjectService extends UpdateProgressGenerator, Service {

    /**
     * Gets projects' group from GitLab
     *
     * @param group the group
     * @return a collection of group projects.<br>
     * Can return <code>null</code>, if an error occurred during the request.
     */
    Collection<Project> getProjects(Group group);

    /**
     * We load the list of projects that we have on the local disk.
     *
     * We get the list of projects from the GitLab and we update their type,
     * status and the path to the local folder when taking.
     *
     * @param  group the group for which we need to load projects
     * @return a collection of group projects.<br>
     * Can return <code>null</code>, if an error occurred during the request.
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
     * Clones shadow projects
     *
     * @param projects         projects which didn't clone.
     * @param destinationPath  the local path of parent group
     * @param progressListener listener for obtaining data on the process of performing the operation.
     *                         We must call StateService::stateOFF for this state
     *                         in the ProgressListener::onFinish method.
     */
    void clone (List<Project> projects,  String destinationPath, ProgressListener progressListener);

    /**
     * Indicates that projects contains at least one shadow project
     *
     * @param projects list of projects
     * @return true if list contains shadow project
     */
    boolean hasShadow(List<Project> projects);

    /**
     * Indicates that projects contains at least one cloned project
     *
     * @param projects list of projects
     * @return true if list contains cloned project
     */
    boolean hasCloned(List<Project> projects);

    /**
     * Gets and sets ProjectType, ProjectStatus and isCloned field to a project
     * @param project the project
     * @return <code>true</code> if project status and type are updated successfully, otherwise <code>false</code>
     */
    boolean updateProjectTypeAndStatus(Project project);

    /**
     * Updates project statuses.
     *
     * @param projects - the projects list
     * @return <code>true</code> if project status is updated successfully, otherwise <code>false</code>
     */
    boolean updateProjectStatuses(List<Project> projects);

    /**
     * Updates project status.
     *
     * @param project - the cloned project
     * @return <code>true</code> if project status is updated successfully, otherwise <code>false</code>
     */
    boolean updateProjectStatus(Project project);

    /**
     * Gets list of projects ids.
     *
     * @param projects the project list
     * @return ids list
     */
    List<Integer> getIdsProjects(List<Project> projects);

    /**
     * Gets a filtered projects list which doesn't have shadow projects and projects with conflicts.
     *
     * @param  projects the list which need to filter
     * @return filtered list
     */
     List<Project> getCorrectProjects(List<Project> projects);

     /**
      * Checks that project is cloned and doesn't have conflicts.
      *
      * @param project the project for checking
      * @return <code>true</code> if project ready for operation,
      *         <code>false</code> otherwise. Also, in this case we add message to IU console and log.
      */
     boolean projectIsClonedAndWithoutConflicts(Project project);


}
