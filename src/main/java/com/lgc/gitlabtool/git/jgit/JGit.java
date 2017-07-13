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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CreateBranchCommand;
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
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.EmptyProgressMonitor;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.User;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.util.NullCheckUtil;
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
    private static final String CANCEL_CLONE_MESSAGE = "Cloning process of group was canceled.";
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

    public JGitStatus discardChanges(Project project) {
        if (project == null) {
            logger.error(WRONG_PARAMETERS);
            throw new IllegalArgumentException(WRONG_PARAMETERS);
        }

        if (!project.isCloned()) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        try (Git git = getGit(project.getPathToClonedProject())) {
            git.reset().setMode(ResetCommand.ResetType.HARD).call();
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
        if (projects == null || localPath == null || projects.isEmpty()) {
            String errorMsg = "Cloning error. Projects or local path is null or the group doesn't have projects.";
            progressListener.onError(1.0, errorMsg);
            progressListener.onFinish(null, FINISH_CLONE_MESSAGE);
            throw new IllegalArgumentException(errorMsg);
        }
        cloneGroupInBackgroundThread(projects, progressListener, localPath);
        return true;
    }

    private void cloneGroupInBackgroundThread(Collection<Project> projects,
                                         ProgressListener progressListener,
                                         String groupPath) {
        Runnable task = () -> {
            double step = 1.0 / projects.size();
            double currentProgress = 0.0;
            for (Project project : projects) {
                if (!_isCloneCancelled) {
                    currentProgress += step;
                    progressListener.onStart(project, currentProgress);
                    if (!clone(project, groupPath)) {
                        String errorMsg = "Cloning error of the " + project.getName() + " project";
                        progressListener.onError(currentProgress, errorMsg);
                        logger.error(errorMsg);
                        continue;
                    }
                    progressListener.onSuccess(project, currentProgress);
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
        if (!project.isCloned()) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return Optional.empty();
        }
        String path = project.getPathToClonedProject();

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
    public boolean addUntrackedFileForCommit(Collection<String> files, Project project) {
        if (files == null || project == null) {
            throw new IllegalArgumentException("Incorrect data: project is " + project + ", files is " + files);
        }
        try (Git git = getGit(project.getPathToClonedProject())) {
            files.stream().forEach((file) -> {
                if (file != null) {
                    try {
                        git.add().addFilepattern(file).call();
                    } catch (GitAPIException e) {
                        logger.error("Could not add the " + file + " file");
                        logger.error("!ERROR: " + e.getMessage());
                    }
                }
            });
            git.close();
            return true;
        } catch (IOException e) {
            logger.error("Error opening repository " + project.getPathToClonedProject() + " " + e.getMessage());
        }
        return false;
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
        try (Git git = getGit(project.getPathToClonedProject())) {
            // check which files were changed to avoid conflicts
            if (isContinueMakePull(project, git)) {
                PullResult pullResult = git.pull().call();
                MergeResult mer = pullResult.getMergeResult();
                git.close();
                return JGitStatus.getStatus(mer.getMergeStatus().toString());
            }
        } catch (GitAPIException | IOException e) {
            logger.error("Pull error for the " + project.getName() + " project: " + e.getMessage());
        }
        return JGitStatus.FAILED;
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
                statuses.put(new Project(), JGitStatus.FAILED);
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
        try (Git git = getGit(project.getPathToClonedProject())){
            PersonIdent author = getPersonIdent(nameAuthor, emailAuthor);
            PersonIdent comitter = getPersonIdent(nameCommitter, emailCommitter);
            git.commit().setAll(setAll).setMessage(message).setAuthor(author).setCommitter(comitter).call();
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
                statuses.put(new Project(), JGitStatus.FAILED);
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
            logger.debug("Commit and push for projects is " + JGitStatus.SUCCESSFUL);
        }
        return statuses;
    }

    /**
     * Push of all the projects in the group
     *
     * @param projects   projects for push
     * @param onSuccess  method for tracking the success progress of cloning,
     *                   where <Integer> is a percentage of progress.
     * @param onError    method for tracking the errors during cloning,
     *                   where <Integer> is a percentage of progress, <String> error message.
     * @return true   -  if the operation is completed successfully,
     *         false  -  if an error occurred during execution
     *
     * !Projects that failed to push will be displayed in the UI console.
     */
    public boolean push (List<Project> projects, Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError) {
        if (projects == null || projects.isEmpty()) {
            throw new IllegalArgumentException("Incorrect data: projects is " + projects);
        }
        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if (pr == null) {
                continue;
            }
            if (!pr.isCloned()) {
                NullCheckUtil.acceptBiConsumer(onError, currentProgress, pr.getName() + ERROR_MSG_NOT_CLONED);
                String errMessage = pr.getName() + ERROR_MSG_NOT_CLONED;
                logger.debug(errMessage);
                continue;
            }
            if(push(pr).equals(JGitStatus.FAILED)) {
                NullCheckUtil.acceptBiConsumer(onError, currentProgress, "Failed to push " + pr.getName() + " project");
                String errMessage = "Failed to push " + pr.getName() + " project";
                logger.debug(errMessage);
                continue;
            }
            NullCheckUtil.acceptConsumer(onSuccess, currentProgress);
            logger.debug("Push for projects is " + JGitStatus.SUCCESSFUL);
        }
        return true;
    }

    /**
     * Create a new branch in the local repository.
     *
     * @param project      the cloned project
     * @param nameBranch   the name of the branch
     * @param force        if <code>true</code> and the branch with the given name
     *                     already exists, the start-point of an existing branch will be
     *                     set to a new start-point; if false, the existing branch will
     *                     not be changed
     * @return JGitStatus: SUCCESSFUL - if a new branch was created,
     *                     FAILED - if the branch could not be created.
     */
    public JGitStatus createBranch(Project project, String nameBranch, boolean force) {
        if (project == null || nameBranch == null || nameBranch.isEmpty()) {
            throw new IllegalArgumentException(
                    "Incorrect data: project is " + project + ", nameBranch is " + nameBranch);
        }
        if (!project.isCloned()) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        try (Git git = getGit(project.getPathToClonedProject())) {
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

            CreateBranchCommand create = git.branchCreate();
            Ref res = create.setUpstreamMode(SetupUpstreamMode.TRACK).setName(nameBranch).setForce(force).call();
            logger.info("!New branch has been created for the " + project.getName() + " project: " + res.getName());
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
        try (Git git = getGit(project.getPathToClonedProject())) {
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
        if (!project.isCloned()) {
            logger.debug(project.getName() + ERROR_MSG_NOT_CLONED);
            return Optional.empty();
        }
        try (Git git = getGit(project.getPathToClonedProject())) {
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
        try (Git git = getGit(project.getPathToClonedProject())) {
            if (isCurrentBranch(git, nameBranch)) {
                logger.error("The current branch can not be deleted.");
                return JGitStatus.FAILED;
            }
            git.branchDelete().setBranchNames(nameBranch).setForce(force).call();
            logger.info("!Branch \"" + nameBranch + "\" deleted from the " + project.getPathToClonedProject());
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
        try (Git git = getGit(project.getPathToClonedProject())) {
            git.push().call();
            git.close();
            logger.debug("Push " + JGitStatus.SUCCESSFUL + " (Project: " + project.getName() + ")");
            return JGitStatus.SUCCESSFUL;
        } catch (GitAPIException | IOException e) {
            logger.error("Push error for the " + project.getName() + " project: " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    // Check if we will have conflicts after the pull command
    boolean isContinueMakePull(Project project, Git git) {
        Optional<List<DiffEntry>> optListDiffs = getListModifyFilesInLocalRepository(git);
        Optional<Status> optStatus = getStatusProject(project);
        if (!optListDiffs.isPresent() || !optStatus.isPresent()) {
            return false;
        }
        return !isHaveCoincidences(optListDiffs.get(), optStatus.get().getModified());
    }

    // Get the list of modified files in the local repository
    private Optional<List<DiffEntry>> getListModifyFilesInLocalRepository(Git git) {
        try {
            Repository repo = git.getRepository();
            git.fetch().call();

            ObjectId fetchHead = repo.resolve("FETCH_HEAD^{tree}");
            ObjectReader reader = repo.newObjectReader();

            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, fetchHead);

            return Optional.ofNullable(git.diff().setNewTree(newTreeIter).call());
        } catch (IOException | GitAPIException e) {
            logger.error("Error getting modify files in local repository: ", e.getMessage());
        }
        return Optional.empty();
    }

    // Check changed files in the local repository have coincidences with modified files in working directory
    private boolean isHaveCoincidences(List<DiffEntry> diffFiles, Set<String> modifiedFiles) {
        if (diffFiles == null || modifiedFiles == null) {
            return false;
        }
        for (String file : modifiedFiles) {
            for (DiffEntry diffFile : diffFiles) {
                if (diffFile.toString().equals(file)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Ref> getRefs(Project project, ListMode mode) {
        try (Git git = getGit(project.getPathToClonedProject())) {
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
}