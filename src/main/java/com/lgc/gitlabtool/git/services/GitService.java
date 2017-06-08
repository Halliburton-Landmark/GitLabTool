package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.entities.Branch;

import java.util.List;
import java.util.Map;

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
    
    /**
     * Switches projects to selected branch
     * 
     * @param projects projects that need to be switched
     * @param branch selected branch
     * @return map with projects and theirs statuses of switching
     */
    Map<Project, JGitStatus> switchTo(List<Project> projects, Branch branch);
}
