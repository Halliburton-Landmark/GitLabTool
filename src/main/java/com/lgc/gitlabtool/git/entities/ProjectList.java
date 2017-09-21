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

    private final ProjectService _projectService = (ProjectService) ServiceProvider.getInstance()
            .getService(ProjectService.class.getName());

    private final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    /**
     * We lock create new instance if _isLockCreating is <true>, we return exist instance.
     * We can use one ProjectList for current group.
     */
    private static boolean _isLockCreating = false;
    private static Group _currentGroup;
    private static List<Project> _projects = new ArrayList<>();
    private static ProjectList _instance;

    /**
     * Gets instance of PrjectList.
     *
     * @param group the current group.
     *        The group can be null if ProjectList have already created and _isLockCreating is <true>.
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
     * @return a unmodifiable list of project
     */
    public List<Project> getProjects() {
        return Collections.unmodifiableList(_projects);
    }

    /**
     * Refreshes projects. Activates and deactivates ApplicationState.REFRESH_PROJECTS.
     */
    public void refreshLoadProjects() {
        if (_currentGroup != null) {
            _stateService.stateON(ApplicationState.REFRESH_PROJECTS);
            _projects = loadProjects();
            _stateService.stateOFF(ApplicationState.REFRESH_PROJECTS);
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
            if (pr.isPresent()) {
                newList.add(pr.get());
            }
        }

        return Collections.unmodifiableList(newList);
    }

    /**
     * Gets list of projects ids.
     *
     * @param projects the project list
     * @return ids list
     */
    public static List<Integer> getIdsProjects(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyList();
        }
        return projects.parallelStream()
                       .map(pr -> pr.getId())
                       .collect(Collectors.toList());
    }

    public static List<Project> getProjectsClonedAndWithoutConflicts(List<Project> projects) {
        return projects.stream().filter(project -> projectIsReadyForGitOperations(project))
                .collect(Collectors.toList());
    }

    public static boolean projectIsReadyForGitOperations(Project project) {
        ProjectStatus projectType = project.getProjectStatus();
        return project.isCloned() && !projectType.hasConflicts();
    }

    /**
     * Resets ProjectList data. After this method _isLockCreating is <false>.
     * This allows create new instance of ProjectList for another group.
     */
    public static void reset() {
        _isLockCreating = false;
        _currentGroup = null;
        _projects.clear();
    }

    private List<Project> loadProjects() {
        return (List<Project>) _projectService.loadProjects(_currentGroup);
    }

    private void setProjects(List<Project> projects) {
        _projects = projects;
    }
}
