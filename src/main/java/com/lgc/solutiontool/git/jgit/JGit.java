package com.lgc.solutiontool.git.jgit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

import com.google.gson.reflect.TypeToken;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.services.ProjectService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.util.JSONParser;

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
        try {
            Git git = Git.open(new File(projectPath));
            Repository repository = git.getRepository();

            Status status = new Git(repository).status().call();
            if (status != null) {

                System.out.println("\nStatus " + projectPath + " repository: ");
                System.out.println("Added: " + status.getAdded());
                System.out.println("Changed: " + status.getChanged());
                System.out.println("Conflicting: " + status.getConflicting());
                System.out.println("ConflictingStageState: " + status.getConflictingStageState());
                System.out.println("IgnoredNotInIndex: " + status.getIgnoredNotInIndex());
                System.out.println("Missing: " + status.getMissing());
                System.out.println("Modified: " + status.getModified());
                System.out.println("Removed: " + status.getRemoved());
                System.out.println("Untracked: " + status.getUntracked());
                System.out.println("UntrackedFolders: " + status.getUntrackedFolders());

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
     * @return true - if the operation is completed successfully,
     * false - if an error occurred during execution
     */
    public boolean pull (String projectPath) {
        try {
            Git git = Git.open(new File(projectPath));
            if (git != null) {
                git.pull().call();
                return true; // TODO get status pull
            }
        } catch (IOException | GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    // debug code
    private Collection<Project> getProjects(Group group) {
        Object jsonProjects = ((ProjectService) ServiceProvider.getInstance()
                .getService(ProjectService.class.getName())).getProjects(String.valueOf(group.getId()));
        return JSONParser.parseToCollectionObjects(jsonProjects, new TypeToken<List<Project>>() {
        }.getType());
    }
}
