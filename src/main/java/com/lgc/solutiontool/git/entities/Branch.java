package com.lgc.solutiontool.git.entities;

import com.lgc.solutiontool.git.jgit.BranchType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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

    private void setBranchType(BranchType bType) {
        branchType = bType;
    }

    private void setBranchName(BranchType bName) {
        branchType = bName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(branchName).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Branch))
            return false;
        if (obj == this)
            return true;

        Branch rhs = (Branch) obj;
        return new EqualsBuilder().
                append(branchName, rhs.branchName).
                isEquals();
    }
}
