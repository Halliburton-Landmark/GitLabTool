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

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.lgc.solutiontool.git.connections.token.CurrentUser;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.entities.User;
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
     * @param message         a message for commit. The commit message can not be {null}.
     * @param setAll          if set to true the commit command automatically stages files that have been
     *                        modified and deleted, but new files not known by the repository are not affected.
     * @param nameCommitter   the name committer for this commit.
     * @param emailCommitter  the email committer for this commit.
     * @param nameAuthor      the name author for this commit.
     * @param emailAuthor     the email author for this commit.
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * @return
     */
    public boolean commit (String groupFolderPath, String message, boolean setAll,
                           String nameCommitter, String emailCommitter,
                           String nameAuthor, String emailAuthor) {
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
            if(!commit(path, message, setAll, nameCommitter, emailCommitter, nameAuthor, emailAuthor)) {
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
     * @param message         a message for commit. The commit message can not be {null}.
     * @param setAll          if set to true the commit command automatically stages files that have been
     *                        modified and deleted, but new files not known by the repository are not affected.
     * @param nameCommitter   the name committer for this commit.
     * @param emailCommitter  the email committer for this commit.
     * @param nameAuthor      the name author for this commit.
     * @param emailAuthor     the email author for this commit.
     *
     * If the passed committer or author is {null} we take the value from the current user.
     * Projects that failed to commit or to push will be displayed in the console.
     */
    public boolean commitAndPush (String groupFolderPath, String message, boolean setAll,
                                  String nameCommitter, String emailCommitter,
                                  String nameAuthor, String emailAuthor) {
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
            if(!commitAndPush(path, message, setAll, nameCommitter, emailCommitter, nameAuthor, emailAuthor)) {
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
     * @return true - if the operation is completed successfully, false - if an error occurred during execution !
     *         Projects that failed to push will be displayed in the console.
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

    private boolean commit (Path projectPath, String message, boolean setAll,
                            String nameCommitter, String emailCommitter,
                            String nameAuthor, String emailAuthor) {

        if (projectPath == null || message == null) {
            return false;
        }
        Optional<Git> opGit = getGitForRepository(projectPath.toString());
        if (!opGit.isPresent()) {
            return false;
        }
        Git git = opGit.get();
        PersonIdent author = getPersonIdent(nameAuthor, emailAuthor);
        PersonIdent comitter = getPersonIdent(nameCommitter, emailCommitter);
        try {
            CommitCommand command = git.commit();
            RevCommit commit = command.setAll(setAll)
                                      .setMessage(message)
                                      .setAuthor(author)
                                      .setCommitter(comitter)
                                      .call();
            System.err.println(commit.getId());
            return true;
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    private PersonIdent getPersonIdent(String name, String email) {
        if (name != null || email != null) { // TODO valid data
            return new PersonIdent(name, email);
        }
        User currentUser = CurrentUser.getInstance().getCurrentUser();
        return new PersonIdent(currentUser.getUsername(), currentUser.getEmail());
    }

    private boolean commitAndPush(Path projectPath, String message, boolean setAll,
                                  String nameCommitter, String emailCommitter,
                                  String nameAuthor, String emailAuthor) {
        if (projectPath == null) {
            return false;
        }
        try {
            if(!commit(projectPath, message, setAll, nameCommitter, emailCommitter, nameAuthor, emailAuthor)) {
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
            Git git = Git.open(new File(path + "/.git"));
            return Optional.ofNullable(git);
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
        } catch (IOException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }  catch (GitAPIException e) {
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

    // debug code
    private Collection<Project> getProjects(Group group) {
        return ((ProjectService) ServiceProvider.getInstance()
                .getService(ProjectService.class.getName())).getProjects(group);
    }
}
