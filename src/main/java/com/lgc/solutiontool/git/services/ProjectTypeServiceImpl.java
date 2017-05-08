package com.lgc.solutiontool.git.services;

import java.util.HashSet;
import java.util.Set;

import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.project.nature.projecttype.DSGProjectType;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;
import com.lgc.solutiontool.git.project.nature.projecttype.UnknownProjectType;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public class ProjectTypeServiceImpl implements ProjectTypeService {

    private final Set<ProjectType> _types;
    private final ProjectType UNKNOWN_TYPE = new UnknownProjectType();

    public ProjectTypeServiceImpl() {
        _types = new HashSet<>();
        _types.add(new DSGProjectType());
    }

    @Override
    public ProjectType getTypeProject(Project project) {
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
    public boolean isTypeExist(String idType) {
        if (idType != null && !idType.isEmpty()) {
            for (ProjectType projectType : _types) {
                if (projectType.getId().equals(idType)) {
                    return true;
                }
            }
        }
        return false;
    }

}
