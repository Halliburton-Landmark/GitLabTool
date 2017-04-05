package com.lgc.solutiontool.git.services;

public interface ProjectService {

    /**
     * Gets projects' group
     *
     * @param idGroup id of group
     * @return json with data about projects' of group<br>
     * null, if an error occurred during the request
     */
    Object getProjects(String idGroup);
}
