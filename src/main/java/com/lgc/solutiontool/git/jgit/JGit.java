package com.lgc.solutiontool.git.jgit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.eclipse.jgit.errors.CheckoutConflictException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.services.ProjectService;
import com.lgc.solutiontool.git.services.ServiceProvider;

/**
 * Class for work with Git:
 *
 * [+] clone a group, project or URL of repository; - create repository; - commit; - push.
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
     * @param localPath the path to where will clone all the projects of the group
     */
    public void clone(Group group, String localPath) {
        if (group == null || localPath == null) {
            return;
        }
        for (Project project : getProjects(group)) {
            clone(project, localPath + "/" + group.getName());
        }
    }

    /**
     * Clones the project
     *
     * @param project for clone
     * @param localPath the path to where will clone the project
     */
    public void clone(Project project, String localPath) {
        if (project == null || localPath == null) {
            return;
        }
        clone(project.getHttp_url_to_repo(), localPath + "/" + project.getName());
    }

    /**
     * Clones the URL
     *
     * @param linkClone for clone
     * @param localPath the path to where will clone the project
     */
    public void clone(String linkClone, String localPath) {
        if (linkClone == null || localPath == null) {
            return;
        }
        try {
            Git.cloneRepository().setURI(linkClone).setDirectory(new File(localPath)).call();
        } catch (InvalidRemoteException | TransportException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
    }

    /**
     * Gets status project
     *
     * @param projectPath the path to where will clone the project
     * @return Optional<Status> of project
     */
    public Optional<Status> getStatusProject(String projectPath) {
        try (Git git = Git.open(new File(projectPath))) {
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
                //
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
     * @param projectPath the path to where will clone the project
     */
    public void addUntrackedFileForCommit(Collection<String> files, String projectPath) {
        if (files == null || projectPath == null) {
            return;
        }
        try {
            Git git = Git.open(new File(projectPath));
            for (String nameFile : files) {
                git.add().addFilepattern(nameFile).call();
            }
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
    }

    /**
     * Makes pull of the project
     *
     * @param projectPath the path cloned the project
     * @return JGitStatus pull result
     */
    public JGitStatus pull (String projectPath) {
        try {
            Optional<Git> optGit = getGitForRepository(projectPath);
            if (!optGit.isPresent()) {
                return JGitStatus.FAILED;
            }
            // check which files were changed to avoid conflicts
            if (isContinueMakePull(projectPath)) {
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
     * @param groupFolderPath the path to cloned of the group
     * @param message for commit
     * @return true - if the operation is completed successfully,
     * false - if an error occurred during execution
     * ! Projects that failed to commit will be displayed in the UI console.
     */
    public boolean commit (String groupFolderPath, String message) {
        List<Path> projects = getFilesInFolder(groupFolderPath);
        if (projects == Collections.EMPTY_LIST || projects.size() == 0) {
            return false;
        }

        List<Path> unsuccessfulCommit = new ArrayList<>();
        for (Path path : projects) {
            if(path == null) { // TODO path validator
                unsuccessfulCommit.add(path);
                continue;
            }
            if(!commit(path, message)) {
                unsuccessfulCommit.add(path);
            }
        }

        if (!unsuccessfulCommit.isEmpty()) {
            logUnsuccessfulOperationInfo(unsuccessfulCommit, "push");
            return false;
        }
        return true;
    }

    /**
     * Commit and push of all the projects in the group
     *
     * @param groupFolderPath the path to cloned of the group
     * @param message for commit
     * @return true - if the operation is completed successfully,
     * false - if an error occurred during execution
     * ! Projects that failed to commit or to push will be displayed in the UI console.
     */
    public boolean commitAndPush (String groupFolderPath, String message) {
        List<Path> projects = getFilesInFolder(groupFolderPath);
        if (projects == Collections.EMPTY_LIST || projects.size() == 0) {
            return false;
        }

        List<Path> unsuccessfulPush = new ArrayList<>();
        for (Path path : projects) {
            if(path == null) { // TODO path validator
                unsuccessfulPush.add(path);
                continue;
            }
            if(!commitAndPush(path, message)) {
                unsuccessfulPush.add(path);
            }
        }

        if (!unsuccessfulPush.isEmpty()) {
            logUnsuccessfulOperationInfo(unsuccessfulPush, "push");
            return false;
        }
        return true;
    }

    /**
     * Push of all the projects in the group
     *
     * @param groupFolderPath the path to cloned of the group
     * @return true - if the operation is completed successfully,
     * false - if an error occurred during execution
     * !Projects that failed to push will be displayed in the UI console.
     */
    public boolean push (String groupFolderPath) {
        List<Path> projects = getFilesInFolder(groupFolderPath);
        if (projects == Collections.EMPTY_LIST || projects.size() == 0) {
            return false;
        }

        List<Path> unsuccessfulPush = new ArrayList<>();
        for (Path path : projects) {
            if(path == null) { // TODO path validator
                unsuccessfulPush.add(path);
                continue;
            }
            if(!push(path)) {
                unsuccessfulPush.add(path);
            }
        }

        if (!unsuccessfulPush.isEmpty()) {
            logUnsuccessfulOperationInfo(unsuccessfulPush, "push");
            return false;
        }
        return true;
    }

    /**
     * Gets all the branch names that are in the local repository
     *
     * @param projectPath the path to cloned of the project
     * @return a list short names of branches
     */
    public List<String> getBranches(String projectPath) {
        return getListShortNamesOfBranches(getRefs(projectPath));
    }

    /**
     * Create a new branch in the local repository.
     *
     * @param projectPath  the path to cloned of the project
     * @param nameBranch   the name of the branch
     * @param force        if <code>true</code> and the branch with the given name
     *                     already exists, the start-point of an existing branch will be
     *                     set to a new start-point; if false, the existing branch will
     *                     not be changed
     * @return JGitStatus: SUCCESSFUL - if a new branch was created,
     *                     FAILED - if the branch could not be created.
     */
    public JGitStatus createBranch(String projectPath, String nameBranch, boolean force) {
        Optional<Git> optGit = getGitForRepository(projectPath);
        if (!optGit.isPresent() || nameBranch == null) { // TODO valid name
            return JGitStatus.FAILED;
        }
        if (!force && getBranches(projectPath).contains(nameBranch)) {
            System.err.println("!ERROR: a branch with the same name already exists");
            return JGitStatus.FAILED;
        }
        try {
            CreateBranchCommand create = optGit.get().branchCreate();
            Ref res = create.setUpstreamMode(SetupUpstreamMode.TRACK).setName(nameBranch)
                    .setStartPoint(optGit.get().getRepository().getFullBranch()).setForce(force).call();
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
     * @param projectPath  the path to cloned of the project
     * @param nameBranch   the name of the branch to which to switch
     *
     * @return JGitStatus: SUCCESSFUL - if a new branch was created,
     *                     FAILED - if the branch could not be created,
     *                     CONFLICTS - if the branch has unsaved changes that can lead to conflicts.
     */
    public JGitStatus switchTo(String projectPath, String nameBranch) {
        Optional<Git> optGit = getGitForRepository(projectPath);
        if (!optGit.isPresent() || nameBranch == null) { // TODO valid name
            return JGitStatus.FAILED;
        }
        if (!getBranches(projectPath).contains(nameBranch)) {
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
     * Removes a branch by name.
     *
     * @param projectPath  the path to cloned of the project
     * @param nameBranch   the name of the branch for delete
     * @param force        false - a check will be performed whether the branch to be deleted is already
     *                     merged into the current branch and deletion will be refused in this case.
     */
    public JGitStatus deleteBranch(String projectPath, String nameBranch, boolean force) {
        Optional<Git> optGit = getGitForRepository(projectPath);
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
            System.out.println("!Branch \"" + nameBranch + "\" deleted from the " + projectPath);
            return JGitStatus.SUCCESSFUL;
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return JGitStatus.FAILED;
    }

    private boolean commit(Path projectPath, String message) {
        if (projectPath == null) {
            return false;
        }
        try {
            Optional<Git> opGit = getGitForRepository(projectPath.toString());
            if (!opGit.isPresent()) {
                return false;
            }
            opGit.get().commit().setAll(true).setMessage(message).call();
            // TODO get a status of operation and showing to the console. Also, return a result.
            return true;
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    private boolean commitAndPush(Path projectPath, String message) {
        if (projectPath == null) {
            return false;
        }
        try {
            if(!commit(projectPath, message)) {
               return false;
            }
            if(push(projectPath)) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    private boolean push(Path projectPath) {
        if (projectPath == null) {
            return false;
        }

        Optional<Git> opGit = getGitForRepository(projectPath.toString());
        if (!opGit.isPresent()) {
            return false;
        }
        try {
            opGit.get().push().call();
            // TODO get a status of operation and showing to the console. Also, return a result.
            return true;
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    private Optional<Git> getGitForRepository(String path) {
        try {
            return Optional.ofNullable(Git.open(new File(path + "/.git")));
        } catch (IOException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return Optional.empty();
    }

    private List<Path> getFilesInFolder(String groupFolderPath){
        try {
            List<Path> foundFolders = new ArrayList<>();
            for (Path file : Files.newDirectoryStream(Paths.get(groupFolderPath))) {
                if (file != null) {
                    foundFolders.add(file);
                }
            }
            return foundFolders;
        } catch (IOException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private void logUnsuccessfulOperationInfo(List<Path> files, String nameOperation) {
        System.err.println("Unsuccessful " + nameOperation + " " + files.size() + " projects: ");
        for (Path file : files) {
            System.err.println(file);
        }
    }

    // Check if we will have conflicts after the pull command
    private boolean isContinueMakePull(String projectPath) {
        Optional<Git> optGit = getGitForRepository(projectPath);
        if (!optGit.isPresent()) {
            return false;
        }
        Optional<List<DiffEntry>> optListDiffs = getListModifyFilesInLocalRepository(optGit.get());
        if (!optListDiffs.isPresent()) {
            return false;
        }
        Optional<Status> optStatus = getStatusProject(projectPath);
        if (!optStatus.isPresent()) {
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

    private List<Ref> getRefs(String projectPath) {
        Optional<Git> optGit = getGitForRepository(projectPath);
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

            try {
                dirCacheCheck.checkout();
                return false;
            } catch (CheckoutConflictException e) {
                System.err.println("!ERROR: CONFLICTS in " + dirCacheCheck.getConflicts() + " files");
            }
            dirCache.unlock();

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

    private Collection<Project> getProjects(Group group) {
        return ((ProjectService) ServiceProvider.getInstance()
                .getService(ProjectService.class.getName())).getProjects(group);
    }
}
