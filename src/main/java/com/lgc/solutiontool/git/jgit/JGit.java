package com.lgc.solutiontool.git.jgit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.lgc.solutiontool.git.connections.token.CurrentUser;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.entities.User;

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
     * Clones all projects from the group
     *
     * @param group for clone
     * @param localPath localPath the path to where will clone all the projects of the group
     * @param onSuccess method for tracking the success progress of cloning
     * @param onError method for tracking the errors during cloning
     */
    public void clone(Group group, String localPath, Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError) {
        if (group == null || localPath == null) {
            return;
        }
        Collection<Project> projects = group.getProjects();
        if (projects == null) {
            return;
        }
        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project project : projects) {
            currentProgress += aStepInProgress;
            if (!clone(project, localPath + "/" + group.getName())) {
                if(onError != null) {
                    onError.accept(currentProgress, "Cloning error of the " + project.getName() + " project");
                    continue;
                }
            }
            if(onSuccess != null) {
                onSuccess.accept(currentProgress);
            }
        }
        group.setClonedStatus(true);
        group.setPathToClonedGroup(localPath);
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
        String path = project.getPathToClonedProject();
        try {
            Optional<Git> opGit = getGitForRepository(path);
            if (!opGit.isPresent()) {
                return;
            }
            for (String nameFile : files) {
                opGit.get().add().addFilepattern(nameFile).call();
            }
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
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
        String path = project.getPathToClonedProject();
        if (path == null) {
            return JGitStatus.FAILED;
        }
        try {
            Optional<Git> optGit = getGitForRepository(path);
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
     * @param group          the cloned group
     * @param message        a message for commit. The commit message can not be {null}.
     * @param setAll         if set to true the commit command automatically stages files that have been
     *                       modified and deleted, but new files not known by the repository are not affected.
     * @param nameCommitter  the name committer for this commit.
     * @param emailCommitter the email committer for this commit.
     * @param nameAuthor     the name author for this commit.
     * @param emailAuthor    the email author for this commit.
     * @param onSuccess      the method for tracking the success progress of cloning
     * @param onError        the method for tracking the errors during cloning
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * Projects that failed to commit will be displayed in the UI console.
     *
     * @return status SUCCESSFUL is if committed successfully, otherwise is FAILED.
     */
    public JGitStatus commit (Group group, String message, boolean setAll,
                              String nameCommitter, String emailCommitter,
                              String nameAuthor, String emailAuthor,
                              Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError) {
        if (group == null || message == null) {
            return JGitStatus.FAILED;
        }
        Collection<Project> projects = getChangedProjects(group);
        if (projects.isEmpty() || projects == null) {
            return JGitStatus.FAILED;
        }

        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if(commit(pr, message, setAll, nameCommitter, emailCommitter,
                      nameAuthor, emailAuthor).equals(JGitStatus.FAILED)) {
                if (onError != null) {
                    onError.accept(currentProgress, "Failed to commit " + pr.getName() + " project");
                    continue;
                }
            }
            if (onSuccess != null) {
                onSuccess.accept(currentProgress);
            }
        }
        return JGitStatus.SUCCESSFUL;
    }

    /**
     * Commit and push of all the projects in the group
     *
     * @param group          the cloned group
     * @param message        a message for commit. The commit message can not be {null}.
     * @param setAll         if set to true the commit command automatically stages files that have been
     *                       modified and deleted, but new files not known by the repository are not affected.
     * @param nameCommitter  the name committer for this commit.
     * @param emailCommitter the email committer for this commit.
     * @param nameAuthor     the name author for this commit.
     * @param emailAuthor    the email author for this commit.
     * @param onSuccess      the method for tracking the success progress of cloning.
     * @param onError        the method for tracking the errors during cloning.
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * Projects that failed to commit or to push will be displayed in the console.
     *
     * @return
     */
    public boolean commitAndPush (Group group, String message, boolean setAll,
                                  String nameCommitter, String emailCommitter,
                                  String nameAuthor, String emailAuthor,
                                  Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError) {
        if (group == null || message == null) {
            return false;
        }
        Collection<Project> projects = getChangedProjects(group);
        if (projects.isEmpty() || projects == null) {
            return false;
        }

        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if(commitAndPush(pr, message, setAll, nameCommitter, emailCommitter, nameAuthor, emailAuthor).equals(JGitStatus.FAILED)) {
                if (onError != null) {
                    onError.accept(currentProgress, "Failed to commit and push " + pr.getName() + " project");
                    continue;
                }
            }
            if (onSuccess != null) {
                onSuccess.accept(currentProgress);
            }
        }
        return true;
    }

    /**
     * Push of all the projects in the group
     *
     * @param group the cloned group
     * @param onSuccess the method for tracking the success progress of cloning
     * @param onError the method for tracking the errors during cloning
     * @return true  - if the operation is completed successfully,
     *         false - if an error occurred during execution
     * !Projects that failed to push will be displayed in the UI console.
     */
    public boolean push (Group group, Consumer<Integer> onSuccess, BiConsumer<Integer, String> onError) {
        if (group == null) {
            return false;
        }
        Collection<Project> projects = group.getProjects();
        if (projects.isEmpty() || projects == null) {
            return false;
        }

        int aStepInProgress = 100 / projects.size();
        int currentProgress = 0;
        for (Project pr : projects) {
            currentProgress += aStepInProgress;
            if(push(pr).equals(JGitStatus.FAILED)) {
                if (onError != null) {
                    onError.accept(currentProgress, "Failed to push " + pr.getName() + " project");
                    continue;
                }
            }
            if (onSuccess != null) {
                onSuccess.accept(currentProgress);
            }
        }
        return true;
    }

    /**
     * Gets all the branch names that are in the local repository
     *
     * @param project the cloned project
     * @return a list short names of branches
     */
    public List<String> getBranches(Project project) {
        return getListShortNamesOfBranches(getRefs(project));
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
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (!optGit.isPresent()) {
            return JGitStatus.FAILED;
        }
        if (!force && getBranches(project).contains(nameBranch)) {
            System.err.println("!ERROR: a branch with the same name already exists");
            return JGitStatus.FAILED;
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
     * @param project      the cloned project
     * @param nameBranch   the name of the branch to which to switch
     *
     * @return JGitStatus: SUCCESSFUL - if a new branch was created,
     *                     FAILED - if the branch could not be created,
     *                     CONFLICTS - if the branch has unsaved changes that can lead to conflicts.
     */
    public JGitStatus switchTo(Project project, String nameBranch) {
        if (project == null || nameBranch == null) {
            return JGitStatus.FAILED;
        }
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (!optGit.isPresent()) {
            return JGitStatus.FAILED;
        }
        if (!getBranches(project).contains(nameBranch)) {
            System.err.println("!ERROR: a branch with this name does not exist.");
            return JGitStatus.FAILED;
        }
        Git git = optGit.get();
        try {
            if (isCurrentBranch(git, nameBranch)) {
                return JGitStatus.FAILED;
            }
            if (isConflictsBetweenTwoBranches(git.getRepository(), git.getRepository().getFullBranch(),
                    Constants.R_HEADS + nameBranch)) {
                return JGitStatus.CONFLICTS;
            }
            CheckoutCommand command = git.checkout();
            Ref ref = command.setName(nameBranch).setStartPoint("origin/" + nameBranch).call();
            System.out.println("!Switch to branch: " + ref.getName()); // TODO data to the UI console
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
        String path = localPath + "/" + project.getName();
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
        return new PersonIdent(currentUser.getUsername(), currentUser.getEmail());
    }

    public JGitStatus commitAndPush(Project project, String message, boolean setAll,
                                  String nameCommitter, String emailCommitter,
                                  String nameAuthor, String emailAuthor) {
        if (project == null) {
            return JGitStatus.FAILED;
        }
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

    private Optional<Git> getGitForRepository(String path) {
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

    private List<Ref> getRefs(Project project) {
        if (project == null) {
            return Collections.emptyList();
        }
        Optional<Git> optGit = getGitForRepository(project.getPathToClonedProject());
        if (!optGit.isPresent()) {
            return Collections.emptyList();
        }
        try {
            return optGit.get().branchList().call();
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<String> getListShortNamesOfBranches(List<Ref> listRefs) {
        if (listRefs == null) {
            return Collections.emptyList();
        }
        List<String> refs = new ArrayList<>();
        for (Ref ref : listRefs) {
            refs.add(ref.getName().substring(Constants.R_HEADS.length()));
        }
        return refs;
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