package com.lgc.solutiontool.git.services;

import java.util.Collection;

/**
 * Service for changes of a group's pom.xml.
 *
 * @author Lyska Lyudmila
 */
public interface PomXMLService {

    /**
     * Changes parent version
     *
     * @param pomMngs collection of PomXMLManager in which it is necessary to make a replacement
     * @param newVersion new version
     */
    void changeParentVersion(Collection<PomXMLManager> pomMngs, String newVersion);

    /**
     * Changes group name in all a pom.xml file
     *
     * @param pomMngs collection of PomXMLManager in which it is necessary to make a replacement
     * @param oldName old name of a group
     * @param newName new name of a group
     */
    void changeGroupName(Collection<PomXMLManager> pomMngs, String oldName, String newName);

    /**
     * Changes release name in properties of a pom.xml file
     *
     * @param pomMngs collection of PomXMLManager in which it is necessary to make a replacement
     * @param newName new release name
     */
    void changeReleaseName(Collection<PomXMLManager> pomMngs, String newName);

   /**
    * Adds repository to a list of repositories in a pom.xml
    *
    * @param collection of PomXMLManager in which it is necessary to make a replacement
    * @param id of repository
    * @param url of repository
    */
    void addRepository(Collection<PomXMLManager> pomMngs, String id, String url);

    /**
     * Removes repository from a list of repositories in a pom.xml
     *
     * @param pomMngs collection of PomXMLManager in which it is necessary to make a replacement
     * @param id of repository for remove
     */
    void removeRepository(Collection<PomXMLManager> pomMngs, String id);


    /**
     * Modifies the values in the repository to new ones.
     *
     * @param pomMngs collection of PomXMLManager in which it is necessary to make a replacement
     * @param oldId   repository id
     * @param newId   new repository id
     * @param newUrl  new repository url
     */
   void modifyRepository(Collection<PomXMLManager> pomMngs, String oldId, String newId, String newUrl);
}
