package com.lgc.solutiontool.git.services;

import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;

/**
 * Service for working with a type of projects
 *
 * @author Lyudmila Lyska
 */
public interface ProjectTypeService {

    /**
     * Defines type for project and returns it.
     * @param project a cloned project
     * @return project type
     */
    ProjectType getTypeProject(Project project);

    /**
     * Checks existence of a type
     *
     * @param idType type id
     * @return true - if a type with it name already exist, false - otherwise
     */
    boolean isTypeExist(String idType);
}
