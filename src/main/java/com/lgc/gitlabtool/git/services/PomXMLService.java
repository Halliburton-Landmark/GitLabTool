package com.lgc.gitlabtool.git.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;

/**
 * Service for changes of a group's pom.xml.
 *
 * @author Lyska Lyudmila
 */
public interface PomXMLService {

    /**
     * Changes parent version
     *
     * @param projects collection of projects in which it is necessary to make a replacement
     * @param newVersion new version
     */
    void changeParentVersion(Collection<Project> projects, String newVersion);

    /**
     * Changes group name in all a pom.xml file
     *
     * @param projects collection of projects in which it is necessary to make a replacement
     * @param oldName old name of a group
     * @param newName new name of a group
     */
    void changeGroupName(Collection<Project> projects, String oldName, String newName);

    /**
     * Changes release name in properties of a pom.xml file
     *
     * @param projects collection of projects in which it is necessary to make a replacement
     * @param newName new release name
     */
    void changeReleaseName(Collection<Project> projects, String newName);

    /**
     * Gets set of repositories for selected projects
     *
     * @param projects collection of projects in which it is necessary to get list of repositories
     * @param isCommon if true - would be returned common repositories for all selected branches, if false - for at least one of them
     *
     * @return set of repositories
     */
    Set<String> getReposIds(List<Project> projects, Boolean isCommon);

    /**
     * Adds repository to a list of repositories in a pom.xml
     *
     * @param projects collection of projects in which it is necessary to make a replacement
     * @param id       of repository
     * @param url      of repository
     * @param layout   of repository
     *
     * @return list of statuses
     */
    Map<Project, Boolean> addRepository(Collection<Project> projects, String id, String url, String layout);

    /**
     * Removes repository from a list of repositories in a pom.xml
     *
     * @param projects collection of projects in which it is necessary to make a replacement
     * @param id       of repository for remove
     *
     * @return list of statuses
     */
    Map<Project, Boolean> removeRepository(Collection<Project> projects, String id);


    /**
     * Modifies the values in the repository to new ones.
     *
     * @param projects collection of projects in which it is necessary to make a replacement
     * @param oldId   repository id
     * @param newId   new repository id
     * @param newUrl  new repository url
     *
     * @return list of statuses
     */
    Map<Project, Boolean> modifyRepository(Collection<Project> projects, String oldId, String newId, String newUrl, String newLayout);

    /**
     * Gets the name of release
     *
     * @param projects collection of projects
     */
    String getReleaseName(Collection<Project> projects);

    /**
     * Gets the release of eclipse
     *
     * @param projects collection of projects
     */
    String getEclipseRelease(Collection<Project> projects);

    /**
     * Gets the layout of repository
     *
     * @param projects collection of projects
     * @param id if of repository
     */
    String getLayout(List<Project> projects, String id);

    /**
     * Gets the url of repository
     *
     * @param projects collection of projects
     * @param id if of repository
     */
    String getUrl(List<Project> projects, String id);

    /**
     * Check that project contains selected repository
     *
     * @param project project for checking
     * @param repo    repository for checking
     * @return true if project contains selected repository
     */
    boolean containsRepository(Project project, String repo);

    /**
     * Returns true if all projects have Pom.xml file
     *
     * @param projects projects for checking
     * @return true if all projects have Pom.xml
     */
    boolean hasPomFile(List<Project> projects);

    /**
     * Returns true if project has Pom.xml file
     *
     * @param project project for checking
     * @return true if projects has Pom.xml
     */
    boolean hasPomFile(Project project);

}
