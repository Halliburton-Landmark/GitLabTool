package com.lgc.gitlabtool.git.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.jgit.stash.Stash;
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
     * Checkouts projects to selected branch
     *
     * @param projects projects that need to be checked out
     * @param branch selected branch
     * @param progress the listener for obtaining data on the process of performing the operation
     *
     * @return map with projects and theirs checkout statuses
     */
    Map<Project, JGitStatus> checkoutBranch(List<Project> projects, Branch branch, ProgressListener progress);

    /**
     * Checkouts projects to selected branch
     *
     * @param projects projects that need to be checked out
     * @param branchName name of the branch
     * @param isRemote <code>true</code> if the branch has {@link BranchType#REMOTE}
     * @param progress the listener for obtaining data on the process of performing the operation
     * @return map with projects and theirs checkout statuses
     */
    Map<Project, JGitStatus> checkoutBranch(List<Project> projects, String branchName, boolean isRemote, ProgressListener progress);

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

    /** This method return tracking branch.
    *
    * @param project
    * @return tracking branch.
    */
   public String getTrackingBranch(Project project);

    /**
     * Gets ChangedFiles for project.
     *
     * @param  project the project
     * @return a ChangedFiles list
     */
    List<ChangedFile> getChangedFiles(Project project);

    /**
     * Adds untracked files to index.
     *
     * @param  files the map of projects and changed files
     * @return the list of added files
     */
    List<ChangedFile> addUntrackedFilesToIndex(Map<Project, List<ChangedFile>> files);

    /**
     * Resets changed files to head
     *
     * @param  files the map which has projects and their changed files
     * @return a list of changed files
     */
    List<ChangedFile> resetChangedFiles(Map<Project, List<ChangedFile>> files);

    /**
     * Gets ProjectStatus for project.
     * We use {@link Status} for getting info about conflicting, untracked files etc.
     * Also, use it for checking the presence of uncommitted changes.
     * Gets current branch name, ahead and behind indexes using {@link Git}.
     *
     * @param  project the project
     * @return ProjectStatus for the project.
     */
    ProjectStatus getProjectStatus(Project project);

    /**
     * Gets branches of project
     *
     * @param projects    cloned project
     * @param brType      type branch
     * @param onlyCommon  if value is <code>true</code> return only common branches of projects,
     *                    if <code>false</code> return all branches.
     * @return a list of branches
     */
    Set<Branch> getBranches(Collection<Project> projects, BranchType brType, boolean onlyCommon);

    /**
     * Replaces changed files with HEAD revision
     *
     * @param changedFiles the files for replacing
     */
    void replaceWithHEADRevision(Collection<ChangedFile> changedFiles);

   /**
    * Creates stash for projects
    *
    * @param  projects the cloned projects
    * @param  stashMessage the stash message
    * @param  includeUntracked <code>true</code> if need to include untracked file to stash, otherwise <code>false</code>
    * @return a map of operation statuses
    */
   Map<Project, Boolean> createStash(List<Project> projects, String stashMessage, boolean includeUntracked);

   /**
    * Gets list of stashes for projects
    *
    * @param  projects  the cloned projects
    * @return a list of projects' stashes
    */
   List<Stash> getStashList(List<Project> projects);

   /**
    * Applies stash for the project
    *
    * @param stash the stash for applying
    * @param progressListener the listener for obtaining data on the process of performing the operation
    */
   void applyStashes(Stash stash, ProgressListener progressListener);

   /**
    * Drops stash from the project
    *
    * @param  stash the stash which need to drop
    * @return a map of operation statuses
    */
   Map<Project, Boolean> stashDrop(Stash stash);

   /**
    * Deletes branch from projects
    *
    * @param  projects the cloned projects
    * @param  deletedBranch the branch which will be deleted
    * @return a map of operation statuses
    */
   Map<Project, Boolean> deleteBranch(List<Project> projects, Branch deletedBranch);
}
