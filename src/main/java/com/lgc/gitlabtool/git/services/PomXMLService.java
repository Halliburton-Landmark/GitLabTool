package com.lgc.gitlabtool.git.services;

import java.util.Collection;

import com.lgc.gitlabtool.git.entities.Project;

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
    * Adds repository to a list of repositories in a pom.xml
    *
    * @param projects collection of projects in which it is necessary to make a replacement
    * @param id of repository
    * @param url of repository
    * @param layout of repository
    */
    void addRepository(Collection<Project> projects, String id, String url, String layout);

    /**
     * Removes repository from a list of repositories in a pom.xml
     *
     * @param projects collection of projects in which it is necessary to make a replacement
     * @param id of repository for remove
     */
    void removeRepository(Collection<Project> projects, String id);


    /**
     * Modifies the values in the repository to new ones.
     *
     * @param projects collection of projects in which it is necessary to make a replacement
     * @param oldId   repository id
     * @param newId   new repository id
     * @param newUrl  new repository url
     */
    void modifyRepository(Collection<Project> projects, String oldId, String newId, String newUrl, String newLayout);

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
}
