package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.entities.Project;

/**
 * Service for working with ProjectStatus.
 *
 * @author Lyudmila Lyska
 */
public interface ProjectStatusService {

    /**
     * Updates project status.
     *
     * @param project - the project
     */
    void updateProjectStatus(Project project);
}
