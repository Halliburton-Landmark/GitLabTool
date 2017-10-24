package com.lgc.gitlabtool.git.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;

/**
 * Keeps data about projects of current group in the main window.
 * Allows reloading projects and getting them or their ids.
 *
 * @author Lyudmila Lyska
 */
public class ProjectList {

    private final ProjectService _projectService = (ProjectService) ServiceProvider.getInstance()
            .getService(ProjectService.class.getName());

    private static final ConsoleService _consoleService = (ConsoleService) ServiceProvider.getInstance()
            .getService(ConsoleService.class.getName());

    private static final String COULD_NOT_SUBMIT_OPERATION_MESSAGE = "Operation could not be submitted for %s project. "
            + "It is not cloned or has conflicts";

    /**
     * We lock create new instance if _isLockCreating is <code>true</code>, we return exist instance.
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
            _projects = loadProjects();
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

    /**
     * Gets a filtered projects list which doesn't have shadow projects and projects with conflicts.
     *
     * @param  projects the list which need to filter
     * @return filtered list
     */
    public static List<Project> getCorrectProjects(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyList();
        }
        return projects.stream()
                       .filter(project -> projectIsClonedAndWithoutConflicts(project))
                       .collect(Collectors.toList());
    }

    /**
     * Checks that project is cloned and doesn't have conflicts.
     *
     * @param project the project for checking
     * @return <code>true</code> if project ready for operation,
     *         <code>false</code> otherwise. Also, in this case we add message to IU console and log.
     */
    public static boolean projectIsClonedAndWithoutConflicts(Project project) {
        if (project == null) {
            return false;
        }
        ProjectStatus projectStatus = project.getProjectStatus();
        boolean result = project.isCloned() && !projectStatus.hasConflicts();
        if (!result) {
            _consoleService.addMessage(String.format(COULD_NOT_SUBMIT_OPERATION_MESSAGE, project.getName()),
                    MessageType.ERROR);
        }
        return result;
    }

    /**
     * Gets cloned projects
     * @return projects
     */
    public List<Project> getClonedProjects() {
        return _projects.stream().filter(Project::isCloned).collect(Collectors.toList());
    }

    /**
     * Resets ProjectList data. After this method _isLockCreating is <code>false</code>.
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
