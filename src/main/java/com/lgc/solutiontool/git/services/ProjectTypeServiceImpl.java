package com.lgc.solutiontool.git.services;

import java.util.HashSet;
import java.util.Set;

import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.project.nature.projecttype.DSGProjectType;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;

/**
 * Service for working with a type of projects
 *
 * @author Lyudmila Lyska
 */
public class ProjectTypeServiceImpl implements ProjectTypeService {
    private final Set<ProjectType> _types;

    public ProjectTypeServiceImpl() {
        _types = new HashSet<>();
        _types.add(new DSGProjectType());
    }

    @Override
    public ProjectType getProjectType(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Invalid data. Project is null.");
        }
        String path = project.getPathToClonedProject();
        for (ProjectType projectType : _types) {
            if (projectType.isProjectCorrespondsType(path)) {
                return projectType;
            }
        }
        return UNKNOWN_TYPE;
    }

    @Override
    public ProjectType getTypeById(String idType) {
        if (idType != null) {
            for (ProjectType projectType : _types) {
                if (projectType.getId().equals(idType)) {
                    return projectType;
                }
            }
        }
        return UNKNOWN_TYPE;
    }
}
