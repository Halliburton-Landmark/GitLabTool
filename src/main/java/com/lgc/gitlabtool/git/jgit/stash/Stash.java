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

    /**
     *
     *
     * @param name
     * @param message
     */
    public Stash (String name, String message, Project project) {
        setName(name);
        setMessage(message);
        setProject(_project);
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
