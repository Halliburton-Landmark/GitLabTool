package com.lgc.solutiontool.git.project.nature.projecttype;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public class UnknownProjectType extends ProjectTypeImpl {

    public UnknownProjectType() {
        super();
        setId("unknown");
    }

    /**
     * Always returns false for this type
     */
    @Override
    public boolean isProjectCorrespondsType(String projectPath) {
        System.err.println("Type unknown has no structure");
        return false;
    }

}
