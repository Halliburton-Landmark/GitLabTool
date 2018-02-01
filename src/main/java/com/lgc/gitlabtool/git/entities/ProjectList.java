package com.lgc.gitlabtool.git.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;

/**
 * Keeps data about projects of current group in the main window.
 * Allows reloading projects and getting them or their ids.
 *
 * @author Lyudmila Lyska
 */
public class ProjectList {

    private static final ProjectService _projectService = ServiceProvider.getInstance().getService(ProjectService.class);
    private static final StateService _stateService = ServiceProvider.getInstance().getService(StateService.class);

    /**
     * We lock create new instance if _isLockCreating is <code>true</code>, we return exist instance.
     * We can use one ProjectList for current group.
     */
    private static boolean _isLockCreating = false;
    private static Group _currentGroup;
    private static List<Project> _projects = new ArrayList<>();
    private static ProjectList _instance;

    /**
     * Gets instance of ProjectList.
     *
     * @param group the current group.
     *        The group can be null if ProjectList have already created and _isLockCreating is <code>true</code>.
     * @return instance
     */
    public static ProjectList get(Group group) {
        if (!_isLockCreating) {
            _isLockCreating = true;
            _currentGroup = group;
            _instance = new ProjectList();
        }
        return _instance;
    }

    private ProjectList() {
        if (_currentGroup != null) {
            setProjects((List<Project>) _projectService.loadProjects(_currentGroup));
        }
    }

    /**
     * Gets project list of current group.
     *
     * @return a unmodifiable list of project or <code>null</code> if projects weren't loaded.
     */
    public List<Project> getProjects() {
        return _projects == null ? null : Collections.unmodifiableList(_projects);
    }

    /**
     * Refreshes projects. Activates and deactivates ApplicationState.REFRESH_PROJECTS.
     */
    public void refreshLoadProjects() {
        if (_currentGroup != null) {
            List<Project> loadedProject = loadProjects();
            // If we cannot refresh projects we'll work with projects which were loaded at last time.
            if (_projects != null && loadedProject != null) {
                _projects = loadedProject;
            }
        }
    }

    /**
     * Gets projects list by ids of projects other list.
     *
     * @param ids the ids of projects list
     * @return a unmodifiable list of project
     */
    public List<Project> getProjectsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Project> newList = new ArrayList<>();
        for (Integer id : ids) {
            Optional<Project> pr = _projects.parallelStream()
                                            .filter(project -> project.getId() == id)
                                            .findFirst();
            pr.ifPresent(newList::add);
        }

        return Collections.unmodifiableList(newList);
    }

    /**
     * Gets cloned projects
     * @return projects
     */
    public List<Project> getClonedProjects() {
        return _projects.stream()
                        .filter(Project::isCloned)
                        .collect(Collectors.toList());
    }

    /**
     * Resets ProjectList data. After this method _isLockCreating is <code>false</code>.
     * This allows create new instance of ProjectList for another group.
     */
    public void reset() {
        _isLockCreating = false;
        _currentGroup = null;
        _projects = null;
    }

    /**
     * Updates projects statuses for some projects
     *
     * @param projects projects to update statuses
     */
    public void updateProjectStatuses(List<Project> projects) {
        try {
            _stateService.stateON(ApplicationState.UPDATE_PROJECT_STATUSES);
            _projects.parallelStream()
                     .filter(projects::contains)
                     .forEach(_projectService::updateProjectStatus);
        } finally {
            _stateService.stateOFF(ApplicationState.UPDATE_PROJECT_STATUSES);
        }
    }

    /**
     * Updates project statuses for all projects.
     */
    public void updateProjectStatuses() {
        _projectService.updateProjectStatuses(_projects);
    }

    private List<Project> loadProjects() {
        return (List<Project>) _projectService.loadProjects(_currentGroup);
    }

    private void setProjects(List<Project> projects) {
        _projects = projects;
    }
}
