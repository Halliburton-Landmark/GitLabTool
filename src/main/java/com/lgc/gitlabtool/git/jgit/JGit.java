package com.lgc.gitlabtool.git.jgit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.EmptyProgressMonitor;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;

import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.User;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.util.PathUtilities;


/**
 * Class for work with Git:
 *
 * - clone a group, project or URL of repository;
 * - pull, commit and push of projects;
 * - create, delete and switch to branch.
 *
 * @author Lyska Lyudmila
 */
public class JGit {
    private static final Logger logger = LogManager.getLogger(JGit.class);
    private static final JGit _jgit;
    private final String ERROR_MSG_NOT_CLONED = " project is not cloned. The operation is impossible";

    public static final String FINISH_CLONE_MESSAGE = "The cloning process is finished.";
    private static final String CANCEL_CLONE_MESSAGE = "Cloning process was canceled.";
    private static final String ORIGIN_PREFIX = "origin/";
    private static final String WRONG_PARAMETERS = "Wrong parameters for obtaining branches.";

    static {
        _jgit = new JGit();
    }

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static JGit getInstance() {
        return _jgit;
    }

    /**
     * Checks that project has any references.
     *
     * @param project the cloned project
     * @return <true> if project has any references, <false> if project does not have references.
     */
    public boolean hasAtLeastOneReference(Project project) {
        if (project == null || !project.isCloned()) {
            return false;
        }
        try (Git git = getGit(project.getPath())) {
            Collection<Ref> refs = git.getRepository().getAllRefs().values();
            return !refs.isEmpty();
        } catch (Exception e) {
            logger.error("Error getting references!");
        }
        return false;
    }

    private boolean _isCloneCancelled = false;

    /**
     * Gets branches of project a certain type
     *
     * @param project cloned project
     * @param brType  type branch
     * @return a list of branches
     */
    public List<Branch> getBranches(Project project, BranchType brType) {
        if (project == null || brType == null) {
            logger.error(WRONG_PARAMETERS);
            throw new IllegalArgumentException(WRONG_PARAMETERS);
        }
        if (!project.isCloned()) {
            logger.error(project.getName() + ERROR_MSG_NOT_CLONED);
            return Collections.emptyList();
        }

        ListMode mode = brType.equals(BranchType.LOCAL) ? null : ListMode.valueOf(brType.toString());
        return getListShortNamesOfBranches(getRefs(project, mode));
    }

    /**
     * Gets branches of project
     *
     * @param projects    cloned project
     * @param brType      type branch
     * @param onlyCommon if value is <true> return only common branches of projects, if <false> return all branches.
     * @return a list of branches
     */
    public Set<Branch> getBranches(Collection<Project> projects, BranchType brType, boolean onlyCommon) {
        if (projects == null || brType == null) {
            logger.error(WRONG_PARAMETERS);
            throw new IllegalArgumentException(WRONG_PARAMETERS);
        }
        ListMode mode = brType.equals(BranchType.LOCAL) ? null : ListMode.valueOf(brType.toString());
        Set<Branch> branches = new HashSet<>();
        projects.stream().forEach((pr) -> {
            if (pr == null) {
                return;
            }
            if (!pr.isCloned()) {
                logger.debug(pr.getName() + ERROR_MSG_NOT_CLONED);
                return;
            }
            List<Branch> shortNamesBranches = getListShortNamesOfBranches(getRefs(pr, mode));
            mergeCollections(branches, shortNamesBranches, onlyCommon);
        });
        return branches;
    }

    /**
     * Reverts changes of project
     *
     * @param  project the project
     * @return a JGitStatus
     */
    public JGitStatus revertChanges(Project project) {
        if (project == null) {
            logger.error(WRONG_PARAMETERS);
            throw new IllegalArgumentException(WRONG_PARAMETERS);
        }

        if (!project.isCloned()) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        try (Git git = getGit(project.getPath())) {
            git.reset().setMode(ResetCommand.ResetType.HARD).call();
            project.getProjectStatus().setHasChanges(false);
            return JGitStatus.SUCCESSFUL;
        } catch (GitAPIException | IOException e) {
            logger.error("Failed to discard changed for the " + project.getName() +" project: ", e);
        }

        return JGitStatus.FAILED;
    }

    private <T> void mergeCollections(Collection<T> first, Collection<T> second, boolean onlyGeneral) {
        if (onlyGeneral && !first.isEmpty()) { // TODO: TEST IT (Can repository hasn't branches?)
            first.retainAll(second);
        } else {
            first.addAll(second);
        }
    }

    /**
     * Clones all projects from the group
     *
     * @param projects   the projects for cloning
     * @param localPath  localPath the path to where will clone all the projects of the group
     * @param progressListener listener for obtaining data on the process of performing the operation.
     */
    public boolean clone(Collection<Project> projects, String localPath, ProgressListener progressListener) {
        _isCloneCancelled = false;
        if (projects == null || localPath == null) {
            String errorMsg = "Cloning error. Projects or local path is null.";
            progressListener.onError(100, errorMsg);
            progressListener.onFinish(FINISH_CLONE_MESSAGE);
            throw new IllegalArgumentException(errorMsg);
        }
        cloneGroupInBackgroundThread(projects, progressListener, localPath);
        return true;
    }

    private void cloneGroupInBackgroundThread(Collection<Project> projects,
                                         ProgressListener progressListener,
                                         String groupPath) {
        Runnable task = () -> {
            progressListener.onStart("Clonning process started");
            long step = 100 / projects.size();
            long currentProgress = 0;
            for (Project project : projects) {
                if (!_isCloneCancelled) {
                    currentProgress += step;
                    progressListener.onStart(project);
                    if (!clone(project, groupPath)) {
                        String errorMsg = "Cloning error of the " + project.getName() + " project";
                        progressListener.onError(currentProgress, errorMsg);
                        logger.info(errorMsg);
                        continue;
                    }
                    progressListener.onSuccess(currentProgress, project, JGitStatus.SUCCESSFUL);
                    logger.info("The " + project.getName() + " project was successfully cloned.");
                }
            }
            progressListener.onFinish(_isCloneCancelled ? CANCEL_CLONE_MESSAGE : FINISH_CLONE_MESSAGE);
        };

        Thread t = new Thread(task, "Clone Group Thread");
        t.start();
    }

    public void cancelClone() {
        _isCloneCancelled = true;
    }

    /**
     * Gets status project
     *
     * @param project the cloned project
     * @return a status of the project
     */
    public Optional<Status> getStatusProject(Project project) {
        if (project == null) {
            return Optional.empty();
        }
        String path = project.getPath();
        if (path == null) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return Optional.empty();
        }

        try (Git git = getGit(path)) {
            Status status = git.status().call();
            if (status != null) {

                return Optional.of(status);
            }
        } catch (NoWorkTreeException | GitAPIException | IOException e) {
            logger.error("Error getting status " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Adds untracked files for commit
     *
     * @param files names of files that need to add
     * @param project the cloned project
     */
    public List<String> addUntrackedFileForCommit(Collection<String> files, Project project) {
        if (files == null || project == null) {
            throw new IllegalArgumentException("Incorrect data: project is " + project + ", files is " + files);
        }

        List<String> addedFiles = new ArrayList<>();
        try (Git git = getGit(project.getPath())) {
            files.stream().forEach((file) -> {
                if (file != null) {
                    try {

                        git.add().addFilepattern(file).call();
                        addedFiles.add(file);
                    } catch (GitAPIException e) {
                        logger.error("Could not add the " + file + " file");
                        logger.error("!ERROR: " + e.getMessage());
                    }
                }
            });
            git.close();
            return addedFiles;
        } catch (IOException e) {
            logger.error("Error opening repository " + project.getPath() + " " + e.getMessage());
        }
        return addedFiles;
    }

    /**
     * Makes pull of the project
     *
     * @param  project the cloned project
     * @return JGitStatus pull result
     */
    public JGitStatus pull (Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Incorrect data: project is null");
        }
        if (!project.isCloned()) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        try (Git git = getGit(project.getPath())) {
            PullResult pullResult = git.pull().call();
            MergeResult mer = pullResult.getMergeResult();
            git.close();
            return JGitStatus.getStatus(mer.getMergeStatus().toString());
        } catch (GitAPIException | IOException e) {
            logger.error("Pull error for the " + project.getName() + " project: " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    /**
     * Pulls the list of projects from the upstream and shows the status in {@link ProgressListener}
     * @param projects - list of projects to pull
     * @param progressListener - instance of {@link OperationProgressListener}
     * @return <code>true</code> if pull operation works well and <code>false</code> otherwise
     */
    public boolean pull(List<Project> projects, OperationProgressListener progressListener) {
        if (projects == null || projects.size() == 0 || progressListener == null) {
            logger.error("Error during pull! Projects: " + projects + "; progressListener: " + progressListener);
            return false;
        }
        long step = 100 / projects.size();
        AtomicLong progress = new AtomicLong(0);
        progressListener.onStart("Pull operation started");
        Runnable pullTask = () -> {
            projects.parallelStream()
                    .filter(project -> project.isCloned())
                    .forEach(project -> pullProject(project, progressListener, progress, step));
            progressListener.onFinish("Pull process was finished");
        };
        Thread pullThread = new Thread(pullTask, "Pull thread");
        pullThread.start();
        return true;
    }

    private JGitStatus pullProject(Project project, ProgressListener progressListener, AtomicLong progress, long delta) {
        progressListener.onStart(project);
        JGitStatus pullResult = pull(project);
        progress.addAndGet(delta);
        if (pullResult == JGitStatus.FAILED || pullResult == JGitStatus.CONFLICTING) {
            progressListener.onError(progress.get(), project, pullResult);
        } else {
            progressListener.onSuccess(progress.get(), project, pullResult);
        }
        return pullResult;
    }

    /**
     * Commit of all the projects in the group
     *
     * @param projects       projects for commit
     * @param message        a message for commit. The commit message can not be {null}.
     * @param setAll         if set to true the commit command automatically stages files that have been
     *                       modified and deleted, but new files not known by the repository are not affected.
     * @param nameCommitter  the name committer for this commit.
     * @param emailCommitter the email committer for this commit.
     * @param nameAuthor     the name author for this commit.
     * @param emailAuthor    the email author for this commit.
     * @param progressListener Listener for obtaining data on the process of performing the operation.
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * Projects that failed to commit will be displayed in the UI console.
     *
     * @return map with projects and theirs statuses
     */
    public Map<Project, JGitStatus> commit (List<Project> projects, String message, boolean setAll,
                              String nameCommitter, String emailCommitter,
                              String nameAuthor, String emailAuthor,
                              ProgressListener progressListener) {
        Map<Project, JGitStatus> statuses = new HashMap<>();
        if (projects == null || message == null || projects.isEmpty() || message.isEmpty()) {
            throw new IllegalArgumentException("Incorrect data: projects is " + projects + ", message is " + message);
        }
        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if (pr == null) {
                statuses.put(pr, JGitStatus.FAILED);
                continue;
            }
            if (!pr.isCloned()) {
                statuses.put(pr, JGitStatus.FAILED);
                String errMessage = pr.getName() + ERROR_MSG_NOT_CLONED;
                logger.debug(errMessage);
                continue;
            }
            if(commitProject(pr, message, setAll, nameCommitter, emailCommitter,
                      nameAuthor, emailAuthor).equals(JGitStatus.FAILED)) {
                statuses.put(pr, JGitStatus.FAILED);
                String errMessage = "Failed to commit " + pr.getName() + " project";
                logger.debug(errMessage);
                continue;
            }
            progressListener.onSuccess(currentProgress);
            statuses.put(pr, JGitStatus.SUCCESSFUL);
            logger.debug("Commit for the projects is " + JGitStatus.SUCCESSFUL);
        }
        progressListener.onFinish();
        return statuses;
    }

    /**
     *
     * Commit of project in the group
     *
     * @param project       a project for commit
     * @param message        a message for commit. The commit message can not be {null}.
     * @param setAll         if set to true the commit command automatically stages files that have been
     *                       modified and deleted, but new files not known by the repository are not affected.
     * @param nameCommitter  the name committer for this commit.
     * @param emailCommitter the email committer for this commit.
     * @param nameAuthor     the name author for this commit.
     * @param emailAuthor    the email author for this commit.
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * Projects that failed to commit will be displayed in the UI console.
     *
     * @return status SUCCESSFUL is if committed successfully, otherwise is FAILED.
     */
    public JGitStatus commitProject (Project project, String message, boolean setAll,
                               String nameCommitter, String emailCommitter,
                               String nameAuthor, String emailAuthor) {
        if (project == null) {
            throw new IllegalArgumentException("Incorrect data! Project is null");
        }
        try (Git git = getGit(project.getPath())){
            PersonIdent author = getPersonIdent(nameAuthor, emailAuthor);
            PersonIdent comitter = getPersonIdent(nameCommitter, emailCommitter);
            git.commit().setMessage(message).setAuthor(author).setCommitter(comitter).call();
            logger.debug("Commit for the " + project.getName() + " project is " + JGitStatus.SUCCESSFUL);
            return JGitStatus.SUCCESSFUL;
        } catch (IOException | GitAPIException e) {
            logger.error("Failed commit for the " + project.getName() + " " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    /**
     * Commit and push of all the projects in the group
     *
     * @param projects       projects for commit and push
     * @param message        a message for commit. The commit message can not be {null}.
     * @param setAll         if set to true the commit command automatically stages files that have been
     *                       modified and deleted, but new files not known by the repository are not affected.
     * @param nameCommitter  the name committer for this commit.
     * @param emailCommitter the email committer for this commit.
     * @param nameAuthor     the name author for this commit.
     * @param emailAuthor    the email author for this commit.
     * @param progressListener Listener for obtaining data on the process of performing the operation.
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * Projects that failed to commit or to push will be displayed in the console.
     *
     * @return statuses of operation
     */
    public Map<Project, JGitStatus> commitAndPush (List<Project> projects, String message, boolean setAll,
                                                   String nameCommitter, String emailCommitter,
                                                   String nameAuthor, String emailAuthor,
                                                   ProgressListener progressListener) {
        Map<Project, JGitStatus> statuses = new HashMap<>();
        if (message == null || projects == null || projects.isEmpty() || message.isEmpty()) {
            throw new IllegalArgumentException("Incorrect data: projects is " + projects + ", message is " + message);
        }
        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if (pr == null) {
                statuses.put(pr, JGitStatus.FAILED);
                continue;
            }
            if (!pr.isCloned()) {
                progressListener.onError(currentProgress);
                String errMessage = pr.getName() + ERROR_MSG_NOT_CLONED;
                statuses.put(pr, JGitStatus.FAILED);
                logger.debug(errMessage);
                continue;
            }
            if(commitAndPush(pr, message, setAll, nameCommitter, emailCommitter, nameAuthor, emailAuthor)
                    .equals(JGitStatus.FAILED)) {
                String errorMsg = "Failed to commit and push " + pr.getName() + " project";
                progressListener.onError(currentProgress);
                statuses.put(pr, JGitStatus.FAILED);
                logger.error(errorMsg);
                continue;
            }
            progressListener.onSuccess(currentProgress);
            statuses.put(pr, JGitStatus.SUCCESSFUL);
        }
        progressListener.onFinish();
        return statuses;
    }

    /**
     * Push of all the projects in the group
     *
     * @param projects         projects for push
     * @param progressListener listener for obtaining data on the process of performing the operation
     * @return <code>true</code> if the operation is completed successfully,
     *         <code>false</code> if an error occurred during execution
     * <p>
     * !Projects that failed to push will be displayed in the UI console.
     */
    public Map<Project, JGitStatus> push(List<Project> projects, ProgressListener progressListener) {
        if (projects == null || projects.isEmpty() || progressListener == null) {
            throw new IllegalArgumentException("Incorrect data: projects is " + projects);
        }
        progressListener.onStart();
        try {
            Map<Project, JGitStatus> statuses = new HashMap<>();
            for (Project project : projects) {
                if (project == null || !project.isCloned()) {
                    progressListener.onError(project);
                    statuses.put(null, JGitStatus.FAILED);
                    continue;
                }
                JGitStatus pushStatus = push(project);
                if (pushStatus.equals(JGitStatus.FAILED)) {
                    progressListener.onError(project);
                } else {
                    progressListener.onSuccess(project);
                }
                statuses.put(project, pushStatus);
            }
            return statuses;
        } finally {
            progressListener.onFinish();
        }
    }

    /**
     * Create a new branch in the local repository.
     *
     * @param project      the cloned project
     * @param nameBranch   the name of the branch
     * @param startPoint   corresponds to the start-point option; if <code>null</code>, the current HEAD will be used
     * @param force        if <code>true</code> and the branch with the given name
     *                     already exists, the start-point of an existing branch will be
     *                     set to a new start-point; if false, the existing branch will
     *                     not be changed
     * @return JGitStatus: SUCCESSFUL - if a new branch was created,
     *                     FAILED - if the branch could not be created.
     */
    public JGitStatus createBranch(Project project, String nameBranch, String startPoint, boolean force) {
        if (project == null || nameBranch == null || nameBranch.isEmpty()) {
            throw new IllegalArgumentException(
                    "Incorrect data: project is " + project + ", nameBranch is " + nameBranch);
        }
        if (!project.isCloned()) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        try (Git git = getGit(project.getPath())) {
            List<Branch> branches = getListShortNamesOfBranches(getRefs(project, null));
            if (branches.isEmpty()) {
                return JGitStatus.FAILED;
            }
            if (!force && isContaintsBranch(branches, nameBranch)) {
                logger.error(() -> "Error createing branch "
                          + nameBranch + " in project " + project.getName() +
                          ". " + JGitStatus.BRANCH_ALREADY_EXISTS);
                return JGitStatus.BRANCH_ALREADY_EXISTS;
            }

            git.branchCreate()
                    .setName(nameBranch)
                    .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
                    .setStartPoint(startPoint)
                    .setForce(force)
                    .call();

            logger.info("!New branch has been created for the " + project.getName() + " project: " + nameBranch);
            git.close();
            return JGitStatus.SUCCESSFUL;
        } catch (GitAPIException | IOException e) {
            logger.error("Failed create branch for the " + project.getName() + " : " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    /**
     * Switch to another branch (already existing).
     *
     * @param project         the cloned project
     * @param nameBranch      the name of the branch to which to switch
     * @param isRemoteBranch if value is <true> to switch to a branch for it, a new local branch
                              with the same name will be created, if <false> switch to an existing branch.
     *
     * @return JGitStatus: SUCCESSFUL - if a new branch was created,
     *                     FAILED - if the branch could not be created,
     *                     CONFLICTS - if the branch has unsaved changes that can lead to conflicts.
     */
    public JGitStatus switchTo(Project project, String nameBranch, boolean isRemoteBranch) {
        if (project == null || nameBranch == null || nameBranch.isEmpty()) {
            throw new IllegalArgumentException(
                    "Incorrect data: project is " + project + ", nameBranch is " + nameBranch);
        }
        String prefixErrorMessage = "Swith to branch for the " + project.getName() + " project: ";
        if (!project.isCloned()) {
            logger.error(prefixErrorMessage + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }

        String nameBranchWithoutAlias = nameBranch.replace(ORIGIN_PREFIX, StringUtils.EMPTY);
        List<Branch> branches = getListShortNamesOfBranches(getRefs(project, null));
        boolean isContaints = isContaintsBranch(branches, nameBranchWithoutAlias);
        if (!isContaints && !isRemoteBranch) {
            logger.error("Failed " + prefixErrorMessage + JGitStatus.BRANCH_DOES_NOT_EXIST);
            return JGitStatus.BRANCH_DOES_NOT_EXIST;
        }
        if (isContaints && isRemoteBranch) {
            logger.error("Failed " + prefixErrorMessage + JGitStatus.BRANCH_ALREADY_EXISTS);
            return JGitStatus.BRANCH_ALREADY_EXISTS;
        }
        try (Git git = getGit(project.getPath())) {
            if (isCurrentBranch(git, nameBranchWithoutAlias)) {
                return JGitStatus.BRANCH_CURRENTLY_CHECKED_OUT;
            }
            if (isConflictsBetweenTwoBranches(git.getRepository(), git.getRepository().getFullBranch(),
                    Constants.R_HEADS + nameBranchWithoutAlias)) {
                logger.warn(prefixErrorMessage + JGitStatus.CONFLICTS);
                return JGitStatus.CONFLICTS;
            }

            git.checkout().setName(nameBranchWithoutAlias)
                                    .setStartPoint(ORIGIN_PREFIX + nameBranchWithoutAlias)
                                    .setCreateBranch(isRemoteBranch)
                                    .call();
            logger.info(prefixErrorMessage + ORIGIN_PREFIX + nameBranchWithoutAlias);
            git.getRepository().close();
            git.close();

            return JGitStatus.SUCCESSFUL;
        } catch (CheckoutConflictException cce) {
            logger.info("Oops..");
        } catch (IOException | GitAPIException e) {
            logger.error("Failed " + prefixErrorMessage + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    private boolean isContaintsBranch(List<Branch> branches, String nameBranch) {
        return branches.stream()
                       .map(Branch::getBranchName)
                       .collect(Collectors.toList())
                       .contains(nameBranch);
    }

    /**
     *
     * @param project
     * @return
     */
    public Optional<String> getCurrentBranch(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Incorrect data: project is null");
        }
        String path = project.getPath();
        if (path == null) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return Optional.empty();
        }
        try (Git git = getGit(path)) {
            Repository repo = git.getRepository();
            Optional<String> branch = Optional.ofNullable(repo.getBranch());
            repo.close();
            git.close();
            return branch;
        } catch (IOException e) {
            logger.error("Error getting current branch for the " + project.getName() + " : " + e.getMessage());
        }
        return Optional.empty();
    }

    protected Git getGit(String path) throws IOException {
        return Git.open(new File(path + "/.git"));
    }

    /**
     * Removes a branch by name.
     *
     * @param project      the cloned project
     * @param nameBranch   the name of the branch for delete
     * @param force        false - a check will be performed whether the branch to be deleted is already
     *                     merged into the current branch and deletion will be refused in this case.
     * @return JGitStatus: SUCCESSFUL - if a new branch was created,
     *                     FAILED - if the branch could not be created.
     */
    public JGitStatus deleteBranch(Project project, String nameBranch, boolean force) {
        if (project == null || nameBranch == null || nameBranch.isEmpty()) {
            throw new IllegalArgumentException(
                    "Incorrect data: project is " + project + ", nameBranch is " + nameBranch);
        }
        if (!project.isCloned()) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        try (Git git = getGit(project.getPath())) {
            if (isCurrentBranch(git, nameBranch)) {
                logger.error("The current branch can not be deleted.");
                return JGitStatus.FAILED;
            }
            git.branchDelete().setBranchNames(nameBranch).setForce(force).call();
            logger.info("!Branch \"" + nameBranch + "\" deleted from the " + project.getPath());
            return JGitStatus.SUCCESSFUL;
        } catch (GitAPIException | IOException e) {
            logger.error("Error deleting branch for the " + project.getName() + " project: " + e.getMessage());
        }

        return JGitStatus.FAILED;
    }

    private boolean clone(Project project, String localPath) {
        String path = localPath + File.separator + project.getName();
        if (!clone(project.getHttp_url_to_repo(), path)) {
            PathUtilities.deletePath(Paths.get(path));
            return false;
        }
        project.setClonedStatus(true);
        project.setPathToClonedProject(path);
        return true;
    }

    public boolean clone(String linkClone, String localPath) {
        try (Git result = tryClone(linkClone, localPath)){
            result.getRepository().close();
            result.close();
           return true;
        } catch (JGitInternalException e) {
            logger.error("Cloning process of group was cancelled!");
        } catch (GitAPIException e) {
            logger.error("Clone error " + linkClone + " : " + e.getMessage());
        }
        return false;
    }

    protected Git tryClone(String linkClone, String localPath) throws GitAPIException {
        return Git.cloneRepository()
                  .setURI(linkClone)
                  .setDirectory(new File(localPath))
                  .setProgressMonitor(new EmptyProgressMonitor() {
                      @Override
                      public boolean isCancelled() {
                          return _isCloneCancelled;
                      }
                  })
                  .call();
    }

    private PersonIdent getPersonIdent(String name, String email) {
        if (name != null && email != null) {
            return new PersonIdent(name, email);
        }
        User currentUser = getUserData();
        return new PersonIdent(currentUser.getName(), currentUser.getEmail());
    }

    protected User getUserData() {
        return CurrentUser.getInstance().getCurrentUser();
    }

    private JGitStatus commitAndPush(Project project, String message, boolean setAll, String nameCommitter,
            String emailCommitter, String nameAuthor, String emailAuthor) {
        if (commitProject(project, message, setAll, nameCommitter, emailCommitter, nameAuthor, emailAuthor)
                .equals(JGitStatus.FAILED)) {
            logger.debug("Commit and Push " + JGitStatus.FAILED + " (Project: " + project.getName() + ")");
            return JGitStatus.FAILED;
        }
        return push(project);
    }

    private JGitStatus push(Project project) {
        try (Git git = getGit(project.getPath())) {
            git.push().call();
            git.close();
            logger.debug("Push " + JGitStatus.SUCCESSFUL + " (Project: " + project.getName() + ")");
            return JGitStatus.SUCCESSFUL;
        } catch (GitAPIException | IOException e) {
            logger.error("Push error for the " + project.getName() + " project: " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    private List<Ref> getRefs(Project project, ListMode mode) {
        try (Git git = getGit(project.getPath())) {
            ListBranchCommand brCommand = git.branchList();
            if (mode != null) {
                brCommand.setListMode(mode);
            }
            return brCommand.call();
        } catch (GitAPIException | IOException e) {
            logger.error("Error getting the list of remote branches of the "
                                + project.getName() + " project:" + e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<Branch> getListShortNamesOfBranches(List<Ref> listRefs) {
        if (listRefs == null || listRefs.isEmpty()) {
            return Collections.emptyList();
        }
        List<Branch> branches = new ArrayList<>();
        for (Ref ref : listRefs) {
            int length = (ref.toString().contains(Constants.R_HEADS)) ? Constants.R_HEADS.length() : Constants.R_REMOTES.length();
            if (ref.toString().contains(Constants.R_HEADS)) {
                branches.add(new Branch(ref.getName().substring(length), BranchType.LOCAL));
            } else {
                branches.add(new Branch(ref.getName().substring(length), BranchType.REMOTE));
            }
        }
        return branches;
    }

    RevWalk getRevWalk(Repository repo) {
        return new RevWalk(repo);
    }

    boolean isConflictsBetweenTwoBranches(Repository repo, String firstBranch, String secondBranch) {
        try {
            Ref firstRef = repo.exactRef(firstBranch);
            Ref secondRef = repo.exactRef(secondBranch);
            if (firstRef == null || secondRef == null) {
                return false;
            }

            RevWalk revWalk = getRevWalk(repo);
            RevCommit firstRefCommit = null;
            RevCommit secondRefCommit = null;
            try {
                firstRefCommit = revWalk.parseCommit(firstRef.getObjectId());
                secondRefCommit = revWalk.parseCommit(secondRef.getObjectId());
                revWalk.close();
            } catch (Exception e) {
                return false;
            }

            if (firstRefCommit == null || secondRefCommit == null) {
                return false;
            }

            return checkDirCacheCheck(repo, firstRefCommit.getTree(), secondRefCommit.getTree());
        } catch (RevisionSyntaxException  | IOException e) {
            logger.error("Failed finding conflicts in the repository: " + e.getMessage());
        }
        return true;
    }

    boolean checkDirCacheCheck(Repository repo, RevTree firstTree, RevTree secondTree)
            throws NoWorkTreeException, CorruptObjectException, IOException {
        DirCache dirCache = repo.lockDirCache();
        DirCacheCheckout dirCacheCheck = new DirCacheCheckout(repo, firstTree, dirCache, secondTree);
        dirCacheCheck.setFailOnConflict(true);
        dirCacheCheck.checkout();
        dirCache.unlock();
        return false;
    }


    private boolean isCurrentBranch(Git git, String nameBranch) {
        try {
            String currentBranch = git.getRepository().getFullBranch();
            String newBranch = Constants.R_HEADS + nameBranch;
            return currentBranch.equals(newBranch);
        } catch (IOException e) {
            logger.error("[is current branch] Error getting the repository: " + e.getMessage());
        }
        return false;
    }

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
    public int[] getAheadBehindIndexCounts(Project project, String branchName) {
        if (project == null || branchName == null || branchName.isEmpty()) {
            logger.error("Wrong parameters! Project: " + project + "; branchName: " + branchName);
            return new int[] {0, 0};
        }

        int commitsAheadIndex = 0;
        int commitsBehindIndex = 0;
        try (Git git = getGit(project.getPath())) {
            BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(git.getRepository(), branchName);
            if (trackingStatus != null) {
                commitsAheadIndex = trackingStatus.getAheadCount();
                commitsBehindIndex = trackingStatus.getBehindCount();
            }
        } catch (IOException e) {
            logger.error("Could not get tracking status " + e.getMessage());
        }

        int[] aheadBehind = {commitsAheadIndex, commitsBehindIndex};
        return aheadBehind;
    }
}