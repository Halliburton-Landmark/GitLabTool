package com.lgc.gitlabtool.git.services;

/**
 * Additional service for using in pair with PomXMLService
 * Contains methods for making changes in POM.xml file
 * Based on VTD-XML library
 *
 * @author Pavlo Pidhorniy
 */
public interface PomXmlEditService {

    /**
     * Adds repository to a list of repositories in a pom.xml
     *
     * @param path   path to pom.xml file
     * @param id     id of repository
     * @param url    url of repository
     * @param layout layout of repository
     * @return status of operation
     */
    boolean addRepository(String path, String id, String url, String layout);

    /**
     * Removes repository from a list of repositories in a pom.xml
     *
     * @param path path to pom.xml file
     * @param id   id of repository for removing
     * @return status of operation
     */
    boolean removeRepository(String path, String id);

    /**
     * Modifies the values in the repository to new ones.
     *
     * @param path   path to pom.xml file
     * @param oldId  repository id
     * @param newId  new repository id
     * @param newUrl new repository url
     * @return status of operation
     */
    boolean modifyRepository(String path, String oldId, String newId, String newUrl, String newLayout);

}
