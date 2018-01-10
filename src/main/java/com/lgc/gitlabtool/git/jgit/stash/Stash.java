package com.lgc.gitlabtool.git.jgit.stash;

import com.lgc.gitlabtool.git.entities.Project;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public class Stash implements StashItem {
    private String _name;
    private String _message;
    private Project _project;

    private static final String ICON_URL = "icons/stash/stash_item_20x20.png";

    /**
     *
     *
     * @param name
     * @param message
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
     *
     * @return
     */
    public String getName() {
        return _name;
    }

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
