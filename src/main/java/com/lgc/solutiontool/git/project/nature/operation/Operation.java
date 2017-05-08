package com.lgc.solutiontool.git.project.nature.operation;

import java.util.EnumSet;

/**
 *
 * @author Lyudmila Lyska
 */
public enum Operation {

    CLONE, PULL, CREATE_BRANCH, SWITCH_TO_BRANCH, DELETE_BRANCH, COMMIT, PUSH, GET_PROJECT_STATUS,
    CHANGE_POM_FILE,
    REPLACEMENT_TEXT_IN_FILE;

    /**
     * Set of operations for working with a git
     */
    public static EnumSet<Operation> GIT_OPERATIONS;

    public static EnumSet<Operation> MIN_OPERATIONS;

    static {
        GIT_OPERATIONS = EnumSet.of(CLONE, PULL, COMMIT, PUSH,
                CREATE_BRANCH, SWITCH_TO_BRANCH, DELETE_BRANCH, GET_PROJECT_STATUS);

        MIN_OPERATIONS = EnumSet.of(REPLACEMENT_TEXT_IN_FILE);
        MIN_OPERATIONS.addAll(GIT_OPERATIONS);


    }
}
