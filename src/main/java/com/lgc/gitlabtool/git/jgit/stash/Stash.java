package com.lgc.gitlabtool.git.jgit.stash;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;

/**
 * Implementation of {@link StashItem} for storing info about a stash for a single project.
 *
 * It stores info about a project for which was created stash, its message and name.
 *
 * @author Lyudmila Lyska
 */
public class Stash implements StashItem {
    private String _name;
    private String _message;
    private Project _project;

    private static final String ICON_URL = "icons/stash/stash_item_20x20.png";

    /**
     * Constructor for creating object.
     *
     * @param name    the stash name
     * @param message the stash message
     * @param project the project for which was created stash
     */
    public Stash (String name, String message, Project project) {
        setName(name);
        setMessage(message);
        setProject(project);
    }

    @Override
    public String getMessage() {
        return _message;
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    /**
     * Gets name of stash (using for operation in {@link JGit}})
     *
     * @return a stash name
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets project for which was created stash.
     *
     * @return a project
     */
    public Project getProject() {
        return _project;
    }

    @Override
    public String getIconURL() {
        return ICON_URL;
    }

    private void setName(String name) {
        _name = name;
    }

    private void setMessage(String message) {
        _message = message == null ? "N/A" : message;
    }

    private void setProject(Project project) {
        _project = project;
    }


}
