package com.ystrazhko.git.jgit;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

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
 * [+] clone a group, project or URL of repository; [+]
 * - commit;
 * - push.
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
     * @return true - group was cloned successfully, false - if failed to perform action
     */
    public boolean clone(Group group, String localPath) {
        if (group == null || localPath == null) {
            return false;
        }

        for (Project project : getProjects(group)) {
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


    //debug code
    private Collection<Project> getProjects(Group group) {
        Object jsonProjects = ((ProjectService) ServiceProvider.getInstance()
                     .getService(ProjectService.class.getName())).getProjects(String.valueOf(group.getId()));
        return JSONParser.parseToCollectionObjects(jsonProjects,new TypeToken<List<Project>>() {}.getType());
    }
}
