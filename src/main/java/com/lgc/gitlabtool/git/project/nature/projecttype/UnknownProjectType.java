package com.lgc.gitlabtool.git.project.nature.projecttype;

/**
 * Implementation of type for projects whose a type which could not be determined
 *
 * @author Lyudmila Lyska
 */
public class UnknownProjectType extends ProjectTypeImpl {

    public UnknownProjectType() {
        super();
        setId("unknown");
    }

    @Override
    public boolean isProjectCorrespondsType(String projectPath) {
        return projectPath == null || projectPath.isEmpty() ? false : true;
    }

}
