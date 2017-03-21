package com.ystrazhko.git.jgit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import com.google.gson.reflect.TypeToken;
import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.entities.Project;
import com.ystrazhko.git.services.ProjectService;
import com.ystrazhko.git.services.ServiceProvider;
import com.ystrazhko.git.util.JSONParser;

/**
 * Class for work with Git:
 *
 * - create repository;
 * - clone a group, project or URL of repository;
 * - commit;
 * - push.
 *
 * @author Lyska Lyudmila
 */
public class JGit {

    private static final JGit _jgit;
    private Git _git;

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
     * @return true - group was cloned successfully, false - if failed to perform action
     */
    public boolean clone(Group group, String localPath) {
        if (group == null || localPath == null) {
            return false;
        }

        Object jsonProjects = ((ProjectService) ServiceProvider.getInstance()
                .getService(ProjectService.class.getName())).getProjects(String.valueOf(group.getId()));
        Collection<Project> projects = JSONParser.parseToCollectionObjects(jsonProjects,
                new TypeToken<List<Project>>() {
                }.getType());

        for (Project project : projects) {
            if (!clone(project.getHttp_url_to_repo(), localPath + "/" + project.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clones the project
     *
     * @param project for clone
     * @param localPath the path to where will clone the project
     * @return true - project was cloned successfully, false - if failed to perform action
     */
    public boolean clone(Project project, String localPath) {
        if (project == null || localPath == null) {
            return false;
        }

        if (!clone(project.getHttp_url_to_repo(), localPath + "/" + project.getName())) {
            return false;
        }
        return true;
    }

    /**
     * Clones the URL
     *
     * @param linkClone for clone
     * @param localPath the path to where will clone the project
     * @return true - project was cloned successfully, false - if failed to perform action
     */
    public boolean clone(String linkClone, String localPath) {
        if (linkClone == null || localPath == null) {
            return false;
        }
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

    ////////////////////////////////////////////////// TODO \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public boolean createRepository(String localPath) {
        try {
            Repository rep = new FileRepository(localPath + "/.git");
            // rep.create();
            _git = new Git(rep);
            return true;
        } catch (IOException e) {
            System.err.println("!ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean commit(String message) {
        try {
            _git.commit().setMessage(message).call();
            return true;
        } catch (NoHeadException | NoMessageException | UnmergedPathsException | ConcurrentRefUpdateException
                | WrongRepositoryStateException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    public boolean commitAndPush(String message) {
        if (!commit(message)) {
            return false;
        }
        return push();
    }

    public boolean push() {
        try {
            _git.push().call();
            return true;
        } catch (InvalidRemoteException | TransportException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }
}
