package com.lgc.solutiontool.git.project.nature.projecttype;

import java.util.Set;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
*
* @author Lyudmila Lyska
*/
public interface ProjectType {

    /**
     *
     * @return
     */
    String getId();

    /**
     *
     * @param operation
     * @return
     */
    boolean hasOperation(Operation operation);

    /**
     *
     *
     * @param projectPath
     * @return
     */
    boolean isProjectCorrespondsType(String projectPath);

    /**
     *
     * @return
     */
    Set<Operation> getAvailableOperations();
}
