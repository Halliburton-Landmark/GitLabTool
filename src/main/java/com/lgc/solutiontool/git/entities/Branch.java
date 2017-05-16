package com.lgc.solutiontool.git.entities;

import com.lgc.solutiontool.git.jgit.BranchType;

/**
 * Class keeps data about branch.
 *
 * @author Pavlo Pidhorniy
 */
public class Branch {

    /**
     * The name of the branch
     **/
    private String branchName;

    /**
     * The type of the branch
     **/
    private BranchType branchType;

    /**
     * Constructor to create an instance of the class.
     *
     * @param name the name of the branch
     * @param type the type of the branch
     */
    public Branch(String name, BranchType type) {
        branchName = name;
        branchType = type;
    }

    /**
     * Gets name of branch
     *
     * @return status
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Gets type of branch
     *
     * @return status
     */
    public BranchType getBranchType() {
        return branchType;
    }
}
