package com.lgc.solutiontool.git.project.nature.projecttype;

import java.util.Set;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
* Interface for getting general info about a project type
*
* @author Lyudmila Lyska
*/
public interface ProjectType {

    /**
     * Gets id of type
     *
     * @return project type id
     */
    String getId();

    /**
     * Does a type of project has access to a operation?
     *
     * @param operation project operation
     * @return true - if a type has this operation, false - otherwise
     */
    boolean hasOperation(Operation operation);

    /**
     * Check if the project corresponds to a structure of a type
     *
     * @param projectPath path to a cloned project
     * @return true - if the project corresponds to a type structure, false - otherwise
     */
    boolean isProjectCorrespondsType(String projectPath);

    /**
     * Gets set of available operations for a type
     *
     * @return available operations
     */
    Set<Operation> getAvailableOperations();
}
