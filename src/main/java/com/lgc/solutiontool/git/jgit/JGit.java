package com.lgc.solutiontool.git.jgit;

import com.lgc.solutiontool.git.connections.token.CurrentUser;
import com.lgc.solutiontool.git.entities.Branch;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.util.FeedbackUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.errors.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;


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
    private static final JGit _jgit;
    private final String ERROR_MSG_NOT_CLONED = " project is not cloned. The operation is impossible";
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
     * Gets branch of project a certain type
     *
     * @param project cloned project
     * @param brType  type branch
     * @return a list of branch
     */
    public List<Branch> getBranches(Project project, BranchType brType) {
        if (project == null || brType == null) {
            throw new IllegalArgumentException("Wrong parameters for obtaining branch.");
        }
        ListMode mode = brType.equals(BranchType.LOCAL) ? null : ListMode.valueOf(brType.toString());
        return getListShortNamesOfBranches(getRefs(project, mode));
    }

    /**
     * Gets branch of project
     *
     * @param projects    cloned project
     * @param brType      type branch
     * @param onlyCommon if value is <true> return only common branch of projects, if <false> return all branch.
     * @return a list of branch
     */
    public Set<Branch> getBranches(List<Project> projects, BranchType brType, boolean onlyCommon) {
        if (projects == null || brType == null) {
            throw new IllegalArgumentException("Wrong parameters for obtaining branch.");
        }
        ListMode mode = brType.equals(BranchType.LOCAL) ? null : ListMode.valueOf(brType.toString());
        Set<Branch> branches = new HashSet<>();
        projects.stream().forEach((pr) -> {
            if (!pr.isCloned()) {
                System.err.println(pr.getName() + ERROR_MSG_NOT_CLONED);
                return;
            }
            List<Branch> shortNamesBranches = getListShortNamesOfBranches(getRefs(pr, mode));
            mergeCollections(branches, shortNamesBranches, onlyCommon);
        });
        return branches;
    }

    private <T> void mergeCollections(Collection<T> first, Collection<T> second, boolean onlyGeneral) {
        if (onlyGeneral && !first.isEmpty()) { // TODO: TEST IT (Can repository hasn't branch?)
            first.retainAll(second);
        } else {
            first.addAll(second);
        }
    }

    /**
     * Clones all projects from the group
     *
     * @param group      group for clone
     * @param localPath  localPath the path to where will clone all the projects of the group
     * @param onSuccess  method for tracking the success progress of cloning,
     *                   where <Integer> is a percentage of progress,
     *                   <Project> is a cloned project.
     * @param onError    method for tracking the errors during cloning,
     *                   where <Integer> is a percentage of progress, <String> error message.
     */
    public void clone(Group group, String localPath, BiConsumer<Integer, Project> onSuccess, BiConsumer<Integer, String> onError) {
        if (group == null || localPath == null) {
            return;
        }
        if (group.isCloned()) {
            String errorMsg = "!ERROR: The operation is impossible, the " + group.getName() + " group is cloned.";
            FeedbackUtil.sendError(onError, 100, errorMsg);
            return;
        }
        Collection<Project> projects = group.getProjects();
        if (projects == null || projects.isEmpty()) {
            String errorMsg = "Cloning error. " + group.getName() + " group doesn't have projects.";
            FeedbackUtil.sendError(onError, 100, errorMsg);
            return;
        }
        String groupPath = localPath + File.separator + group.getName();

        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project project : projects) {
            currentProgress += aStepInProgress;
            if (!clone(project, groupPath)) {
                String errorMsg = "Cloning error of the " + project.getName() + " project";
                FeedbackUtil.sendError(onError, currentProgress, errorMsg);
                continue;
            }
            FeedbackUtil.sendSuccess(onSuccess, currentProgress, project);
        }
        group.setClonedStatus(true);
        group.setPathToClonedGroup(groupPath);
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
            System.err.println(project.getName() + ERROR_MSG_NOT_CLONED);
            return Optional.empty();
        }
        String path = project.getPathToClonedProject();
        if (path == null) {
            return Optional.empty();
        }
        try (Git git = Git.open(new File(path))) {
            Repository repository = git.getRepository();
            Status status = git.status().call();
            if (status != null) {
                // debug code
                System.out.println();
                System.out.println("Conflicting: " + status.getConflicting());
                System.out.println("Changed: " + status.getChanged());
                System.out.println("Added: " + status.getAdded());
                System.out.println("Ignored Not In Index: " + status.getIgnoredNotInIndex());
                System.out.println("Conflicting Stage State: " + status.getConflictingStageState());
                System.out.println("Missing: " + status.getMissing());
                System.out.println("Modified: " + status.getModified());
                System.out.println("Untracked: " + status.getUntracked());
                System.out.println("Untracked Folders: " + status.getUntrackedFolders());

                repository.close();
                return Optional.of(status);
            }
        } catch (IOException | NoWorkTreeException | GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Adds untracked files for commit
     *
     * @param files names of files that need to add
     * @param project the cloned project
     */
    public void addUntrackedFileForCommit(Collection<String> files, Project project) {
        if (files == null || project == null) {
            return;
        }
        if (!project.isCloned()) {
            System.err.println(project.getName() + ERROR_MSG_NOT_CLONED);
            return;
        }
        Optional<Git> opGit = getGitForRepository(project.getPathToClonedProject());
        if (opGit.isPresent()) {
            files.forEach((nameFile) -> {
                try {
                    opGit.get().add().addFilepattern(nameFile).call();
                } catch (GitAPIException e) {
                    System.err.println("!ERROR: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Makes pull of the project
     *
     * @param  project the cloned project
     * @return JGitStatus pull result
     */
    public JGitStatus pull (Project project) {
        if (project == null) {
            return JGitStatus.FAILED;
        }
        if (!project.isCloned()) {
            System.err.println(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        try {
            Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
            if (!optGit.isPresent()) {
                return JGitStatus.FAILED;
            }
            // check which files were changed to avoid conflicts
            if (isContinueMakePull(project)) {
                PullResult pullResult = optGit.get().pull().call();
                MergeResult mer = pullResult.getMergeResult();
                return JGitStatus.getStatus(mer.getMergeStatus().toString());
            }
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
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
     * @param onSuccess      method for tracking the success progress of cloning,
     *                       where <Integer> is a percentage of progress.
     * @param onError        method for tracking the errors during cloning,
     *                       where <Integer> is a percentage of progress, <String> error message.
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * Projects that failed to commit will be displayed in the UI console.
     *
     * @return status SUCCESSFUL is if committed successfully, otherwise is FAILED.
     */
    public JGitStatus commit (List<Project> projects, String message, boolean setAll,
                              String nameCommitter, String emailCommitter,
                              String nameAuthor, String emailAuthor,
                              Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError) {
        if (projects == null || message == null || projects.isEmpty()) {
            // TODO: add log
            return JGitStatus.FAILED;
        }
        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if (!pr.isCloned()) {
                FeedbackUtil.sendError(onError, currentProgress, pr.getName() + ERROR_MSG_NOT_CLONED);
                continue;
            }
            if(commit(pr, message, setAll, nameCommitter, emailCommitter,
                      nameAuthor, emailAuthor).equals(JGitStatus.FAILED)) {
                FeedbackUtil.sendError(onError, currentProgress, "Failed to commit " + pr.getName() + " project");
                continue;
            }
            FeedbackUtil.sendSuccess(onSuccess, currentProgress);
        }
        return JGitStatus.SUCCESSFUL;
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
     * @param onSuccess      method for tracking the success progress of cloning,
     *                       where <Integer> is a percentage of progress.
     * @param onError        method for tracking the errors during cloning,
     *                       where <Integer> is a percentage of progress, <String> error message.
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * Projects that failed to commit or to push will be displayed in the console.
     *
     * @return
     */
    public boolean commitAndPush (List<Project> projects, String message, boolean setAll,
                                  String nameCommitter, String emailCommitter,
                                  String nameAuthor, String emailAuthor,
                                  Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError) {
        if (message == null || projects == null || projects.isEmpty()) {
            // TODO: log
            return false;
        }
        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if (!pr.isCloned()) {
                FeedbackUtil.sendError(onError, currentProgress, pr.getName() + ERROR_MSG_NOT_CLONED);
                continue;
            }
            if(commitAndPush(pr, message, setAll, nameCommitter, emailCommitter, nameAuthor, emailAuthor)
                    .equals(JGitStatus.FAILED)) {
                String errorMsg = "Failed to commit and push " + pr.getName() + " project";
                FeedbackUtil.sendError(onError, currentProgress, errorMsg);
                continue;
            }
            FeedbackUtil.sendSuccess(onSuccess, currentProgress);
        }
        return true;
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
        if (projects.isEmpty() || projects == null) {
            // TODO: add log
            return false;
        }
        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if (!pr.isCloned()) {
                FeedbackUtil.sendError(onError, currentProgress, pr.getName() + ERROR_MSG_NOT_CLONED);
                continue;
            }
            if(push(pr).equals(JGitStatus.FAILED)) {
                FeedbackUtil.sendError(onError, currentProgress, "Failed to push " + pr.getName() + " project");
                continue;
            }
            FeedbackUtil.sendSuccess(onSuccess, currentProgress);
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
        if (project == null || nameBranch == null) {
            return JGitStatus.FAILED;
        }
        if (!project.isCloned()) {
            System.err.println(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (!optGit.isPresent()) {
            return JGitStatus.FAILED;
        }

        List<Branch> branches = getListShortNamesOfBranches(getRefs(project, null));
        if (!force && branches.stream().map(Branch::getBranchName).collect(Collectors.toList()).contains(nameBranch)) {
            return JGitStatus.BRANCH_ALREADY_EXISTS;
        }
        try {
            CreateBranchCommand create = optGit.get().branchCreate();
            Ref res = create.setUpstreamMode(SetupUpstreamMode.TRACK)
                            .setName(nameBranch)
                            .setStartPoint(optGit.get().getRepository().getFullBranch())
                            .setForce(force)
                            .call();
            System.out.println("!CREATE NEW BRANCH: " + res.getName());
            return JGitStatus.SUCCESSFUL;
        } catch (GitAPIException | IOException e) {
            System.err.println("!ERROR: " + e.getMessage());
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
        if (project == null || nameBranch == null) {
            return JGitStatus.FAILED;
        }
        if (!project.isCloned()) {
            System.err.println(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (!optGit.isPresent()) {
            return JGitStatus.FAILED;
        }
        String nameBranchWithoutAlias = nameBranch.replace("origin/","");
        List<Branch> branches = getListShortNamesOfBranches(getRefs(project, null));
        if (!branches.stream().map(Branch::getBranchName).collect(Collectors.toList()).contains(nameBranchWithoutAlias) && !isRemoteBranch) {
            return JGitStatus.BRANCH_DOES_NOT_EXIST;
        }
        if (branches.stream().map(Branch::getBranchName).collect(Collectors.toList()).contains(nameBranchWithoutAlias) && isRemoteBranch) {
            return JGitStatus.BRANCH_ALREADY_EXISTS;
        }
        Git git = optGit.get();
        try {
            if (isCurrentBranch(git, nameBranchWithoutAlias)) {
                return JGitStatus.BRANCH_CURRENTLY_CHECKED_OUT;
            }
            if (isConflictsBetweenTwoBranches(git.getRepository(), git.getRepository().getFullBranch(),
                    Constants.R_HEADS + nameBranchWithoutAlias)) {
                return JGitStatus.CONFLICTS;
            }

            Ref ref = git.checkout()
                         .setName(nameBranchWithoutAlias)
                         .setStartPoint("origin/" + nameBranchWithoutAlias)
                         .setCreateBranch(isRemoteBranch)
                         .call();
            System.out.println("!Switch to branch: " + ref.getName());
            return JGitStatus.SUCCESSFUL;
        } catch (IOException | GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    /**
     *
     * @param project
     * @return
     */
    public Optional<String> getCurrentBranch(Project project) {
        if (project == null) {
            return Optional.empty();
        }
        if (!project.isCloned()) {
            System.err.println(project.getName() + ERROR_MSG_NOT_CLONED);
            return Optional.empty();
        }
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (!optGit.isPresent()) {
            return Optional.empty();
        }
        try {
            Repository repo = optGit.get().getRepository();
            return Optional.ofNullable(repo.getBranch());
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return Optional.empty();
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
        if (project == null || nameBranch == null) {
            return JGitStatus.FAILED;
        }
        if (!project.isCloned()) {
            System.err.println(project.getName() + ERROR_MSG_NOT_CLONED);
            return JGitStatus.FAILED;
        }
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (!optGit.isPresent() || nameBranch == null) {
            return JGitStatus.FAILED;
        }
        Git git = optGit.get();
        if (isCurrentBranch(git, nameBranch)) {
            System.err.println("!ERROR: The current branch can not be deleted.");
            return JGitStatus.FAILED;
        }
        try {
            git.branchDelete().setBranchNames(nameBranch).setForce(force).call();
            System.out.println("!Branch \"" + nameBranch + "\" deleted from the " + project.getPathToClonedProject());
            return JGitStatus.SUCCESSFUL;
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    private boolean clone(Project project, String localPath) {
        String path = localPath + File.separator + project.getName();
        if (clone(project.getHttp_url_to_repo(), path)) {
            project.setClonedStatus(true);
            project.setPathToClonedProject(path);
            return true;
        }
        return false;
    }

    private boolean clone(String linkClone, String localPath) {
        try {
            Git.cloneRepository().setURI(linkClone).setDirectory(new File(localPath)).call();
            return true;
        } catch (InvalidRemoteException | TransportException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    private JGitStatus commit (Project project, String message, boolean setAll,
                               String nameCommitter, String emailCommitter,
                               String nameAuthor, String emailAuthor) {
        if (project == null) {
            return JGitStatus.FAILED;
        }
        Optional<Git> opGit = getGitForRepository(project.getPathToClonedProject());
        if (!opGit.isPresent()) {
            return JGitStatus.FAILED;
        }
        Git git = opGit.get();
        PersonIdent author = getPersonIdent(nameAuthor, emailAuthor);
        PersonIdent comitter = getPersonIdent(nameCommitter, emailCommitter);
        try {
            git.commit().setAll(setAll)
                        .setMessage(message)
                        .setAuthor(author)
                        .setCommitter(comitter)
                        .call();
            return JGitStatus.SUCCESSFUL;
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    private PersonIdent getPersonIdent(String name, String email) {
        if (name != null || email != null) { // TODO valid data
            return new PersonIdent(name, email);
        }
        User currentUser = CurrentUser.getInstance().getCurrentUser();
        return new PersonIdent(currentUser.getName(), currentUser.getEmail());
    }

    private JGitStatus commitAndPush(Project project, String message, boolean setAll,
                                     String nameCommitter, String emailCommitter,
                                     String nameAuthor, String emailAuthor) {
        try {
            if(commit(project, message, setAll, nameCommitter, emailCommitter,
                      nameAuthor, emailAuthor).equals(JGitStatus.FAILED)) {
                return JGitStatus.FAILED;
            }
            return push(project);
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    private JGitStatus push(Project project) {
        if (project == null) {
            return JGitStatus.FAILED;
        }
        Optional<Git> opGit = getGitForRepository(project.getPathToClonedProject());
        if (opGit.isPresent()) {
            try {
                opGit.get().push().call();
                return JGitStatus.SUCCESSFUL;
            } catch (GitAPIException e) {
                System.err.println("!ERROR: " + e.getMessage());
            }
        }
        return JGitStatus.FAILED;
    }

    Optional<Git> getGitForRepository(String path) {
        if (path != null) {
            try {
                return Optional.ofNullable(Git.open(new File(path + "/.git")));
            } catch (IOException e) {
                System.err.println("!ERROR: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    // Check if we will have conflicts after the pull command
    private boolean isContinueMakePull(Project project) {
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (!optGit.isPresent()) {
            return false;
        }
        Optional<List<DiffEntry>> optListDiffs = getListModifyFilesInLocalRepository(optGit.get());
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
            System.err.println("!ERROR: " + e.getMessage());
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
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (optGit.isPresent()) {
            try {
                ListBranchCommand brCommand = optGit.get().branchList();
                if (mode != null) {
                    brCommand.setListMode(mode);
                }
                return brCommand.call();
            } catch (GitAPIException e) {
                System.err.println("!ERROR: " + e.getMessage());
            }
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
            } else if (ref.toString().contains(Constants.R_REMOTES)) {
                branches.add(new Branch(ref.getName().substring(length), BranchType.REMOTE));
            }
        }
        return branches;
    }

    private boolean isConflictsBetweenTwoBranches(Repository repo, String firstBranch, String secondBranch) {
        if (repo == null) {
            return false;
        }
        try {
            Ref firstRef = repo.exactRef(firstBranch);
            Ref secondRef = repo.exactRef(secondBranch);
            if (firstRef == null || secondRef == null) {
                return false;
            }

            RevWalk revWalk = new RevWalk(repo);
            AnyObjectId headId = firstRef.getObjectId();

            RevCommit firstRefCommit = revWalk.parseCommit(headId);
            RevCommit secondRefCommit = revWalk.parseCommit(secondRef.getObjectId());
            revWalk.close();

            if (firstRefCommit == null || secondRefCommit == null) {
                return false;
            }

            RevTree headTree = firstRefCommit.getTree();
            DirCache dirCache = repo.lockDirCache();

            DirCacheCheckout dirCacheCheck = new DirCacheCheckout(repo, headTree, dirCache, secondRefCommit.getTree());
            dirCacheCheck.setFailOnConflict(true);
            dirCacheCheck.checkout();
            dirCache.unlock();
            return false;
        } catch (RevisionSyntaxException | IncorrectObjectTypeException | AmbiguousObjectException
                | MissingObjectException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return true;
    }

    private boolean isCurrentBranch(Git git, String nameBranch) {
        try {
            String currentBranch = git.getRepository().getFullBranch();
            String newBranch = Constants.R_HEADS + nameBranch;
            if (currentBranch.equals(newBranch)) {
                return true;
            }
        } catch (IOException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    private boolean isRepositoryChanged(Project pr) {
        if (pr == null) {
            return false;
        }
        Optional<Git> optGit = getGitForRepository(pr.getPathToClonedProject());
        if (!optGit.isPresent()) {
            return false;
        }
        try {
            List<DiffEntry> diffEntries = optGit.get().diff().call();
            return !diffEntries.isEmpty();
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    private Collection<Project> getChangedProjects(Group group) {
        Collection<Project> projects = group.getProjects();
        if (projects == null) {
            return Collections.emptyList();
        }
        Collection<Project> modifProjects = new ArrayList<>();
        for (Project project : projects) {
            if (isRepositoryChanged(project)) {
                modifProjects.add(project);
            }
        }
        return modifProjects;
    }

}