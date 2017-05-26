package com.lgc.solutiontool.git.services;

import com.lgc.solutiontool.git.entities.Branch;
import com.lgc.solutiontool.git.entities.Project;

import java.util.List;

/**
 * Service for working with Git features.
 *
 * @author Pavlo Pidhorniy
 */
public interface GitService {

    /**
     * Checks that project has selected branches
     *
     * @param project  project for checking
     * @param branches branches that need to be checked
     * @param isCommon if true - checking will occur for all selected branches, if false - for at least one of them.
     * @return true if project contains selected branches, false if does not contains
     */
    boolean containsBranches(Project project, List<Branch> branches, boolean isCommon);
}
