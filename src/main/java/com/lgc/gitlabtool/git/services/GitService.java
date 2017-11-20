package com.lgc.gitlabtool.git.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;

/**
 * Service for working with Git features.
 *
 * @author Pavlo Pidhorniy
 */
public interface GitService extends Service {

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
     * @param progress the listener for obtaining data on the process of performing the operation
     *
     * @return map with projects and theirs statuses of switching
     */
    Map<Project, JGitStatus> switchTo(List<Project> projects, Branch branch, ProgressListener progress);

    /**
     * Switches projects to selected branch
     *
     * @param projects projects that need to be switched
     * @param branchName name of the branch
     * @param isRemote <code>true</code> if the branch has {@link BranchType#REMOTE}
     * @param progress the listener for obtaining data on the process of performing the operation
     * @return map with projects and theirs statuses of switching
     */
    Map<Project, JGitStatus> switchTo(List<Project> projects, String branchName, boolean isRemote, ProgressListener progress);

    /**
     * Gets projects that have uncommited changes
     *
     * @param projects projects that need to be checked
     * @return list of projects that has uncommited changes
     */
    List<Project> getProjectsWithChanges(List<Project> projects);

    /**
     * Reverts uncommited changes
     *
     * @param projects projects that need to be resets
     * @return list of projects that and their discard statuses
     */
    Map<Project, JGitStatus> revertChanges(List<Project> projects);

    /**
     * Commit changes to selectedProjects
     *
     * @param projects          projects that contains changes
     * @param commitMessage     message for commit
     * @param isPushImmediately if true - make push operation after commiting, if false - make commit without pushing
     * @param progressListener Listener for obtaining data on the process of performing the operation.
     */
    Map<Project, JGitStatus> commitChanges(List<Project> projects, String commitMessage, boolean isPushImmediately,
                       ProgressListener progressListener);

    /**
     * Creates new branch
     *
     * @param projects     the projects that needs new branch
     * @param branchName   new branch name
     * @param startPoint   corresponds to the start-point option; if <code>null</code>, the current HEAD will be used
     * @param force        if <code>true</code> and the branch with the given name
     *                     already exists, the start-point of an existing branch will be
     *                     set to a new start-point; if false, the existing branch will
     *                     not be changed
     * @return map with projects and theirs statuses of branch creating
     */
    Map<Project, JGitStatus> createBranch(List<Project> projects, String branchName, String startPoint, boolean force);

    /**
     * Returns the set of selected type of branches
     *
     * @param projects     projects list
     * @param branchType   selected {@link BranchType}
     * @param isOnlyCommon if <code>true</code> returns only common branches for all projects and otherwise if <code>false</code>
     * @return set of the branches or empty set if such type of branches does not exist for this projects
     */
    Set<Branch> getBranches(List<Project> projects, BranchType branchType, boolean isOnlyCommon);

    /**
     * Returns current branch name for selected project
     *
     * @param project - selected project
     * @return current branch name for selected project or <code>null</code> if project has no branches (unreachable state)
     */
    String getCurrentBranchName(Project project);

    /**
     * Pushed selected projects to upstream
     *
     * @param projects -         list of projects
     * @param progressListener - listener for obtaining data on the process of performing the operation
     * @return map of operation statuses
     */
    Map<Project, JGitStatus> push(List<Project> projects, ProgressListener progressListener);

    /**
     * Pulls changes in selected projects from upstream
     *
     * @param projects - selected projects
     * @param progressListener - instance of {@link OperationProgressListener}
     * @return <code>true</code> if pull operation works well and <code>false</code> otherwise
     */
    boolean pull(List<Project> projects, OperationProgressListener progressListener);

    /**
     * Checks that project has any references.
     *
     * @param project the cloned project
     * @return <code>true</code> if project has any references, <code>false</code> if project does not have references.
     */
    public boolean hasAtLeastOneReference(Project project);

    /**
     * Returns count of commits ahead and behind index
     *
     * @param project - project to show status
     * @param branchName - the name of branch
     * @return array of ahead and behind commits counts<br>
     *         Array consists of two parameters:
     *         first is the count of commits ahead Index, <br>
     *         second is the count of commits behind Index
     */
    public int[] getAheadBehindIndexCounts(Project project, String branchName);

    /**
     * Checks whether the project has conflicts and uncommitted changes.
     *
     * @param  project the project
     * @return array of values. Array consists of two parameters:
     *         - has conflicts: <true> is has, otherwise  <false>.
     *         - has changes: <true> is has, otherwise  <false>.
     */
    public boolean[] hasConflictsAndChanges(Project project);

    /**
     * Starts canceling process for cloning. This may take some time.
     */
    void cancelClone();
}
