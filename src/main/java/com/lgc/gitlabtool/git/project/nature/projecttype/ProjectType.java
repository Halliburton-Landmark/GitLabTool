package com.lgc.gitlabtool.git.project.nature.projecttype;

import java.util.Set;

import com.lgc.gitlabtool.git.project.nature.operation.Operation;

/**
* Interface for getting general info about a project type.
*
* The project type gives us information about:
*   - What operations are available to this type
*   - What file structure a project should have, to correspond to a certain type.
*
* For example:
*   DSGProjectType (DSGProjectType) has:
*     - id: "com.lgc.dsg"
 *    - iconUrl: "icons/project/dsg_project.png"
*     - operations: all git operations, changing pom.xml and text file etc.
*     - structure:  all projects of DSGProjectType should have a pom.xml file in the project's root folder
*                   and a pom.xml file in a plugins folder.
*
* @author Lyudmila Lyska
*/
public interface ProjectType {

    static final String ID_KEY = "id";

    /**
     * Gets id of type
     *
     * @return project type id
     */
    String getId();

    /**
     * Gets url of icon
     *
     * @return project icon url
     */
    String getIconUrl();

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
     * @param  projectPath path to a cloned project
     * @return true - if the project corresponds to a given structure (the project has an identified file system),
     *         false - otherwise
     */
    boolean isProjectCorrespondsType(String projectPath);

    /**
     * Gets set of available operations for a type
     *
     * @return available operations
     */
    Set<Operation> getAvailableOperations();

    /**
     * Gets structures of type.
     * We get set of string which has paths of files for this type.
     * Set of string is unmodifiable set.
     *
     * @return paths of files for this type
     */
    Set<String> getStructures();
}
