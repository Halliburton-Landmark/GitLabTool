package com.lgc.solutiontool.git.services;

import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public interface ProjectTypeService {

    /**
     *
     * @param project
     * @return
     */
    ProjectType getTypeProject(Project project);

    /**
     *
     * @param idType
     * @return
     */
    boolean isTypeExist(String idType);
}
