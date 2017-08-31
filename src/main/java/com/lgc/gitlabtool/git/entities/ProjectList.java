package com.lgc.gitlabtool.git.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;

public class ProjectList {

    private static ProjectList _instance;

    private final ProjectService _projectService = (ProjectService) ServiceProvider.getInstance()
            .getService(ProjectService.class.getName());

    private List<Project> _projects = new ArrayList<>();

    //TODO: write javadoc for it
    private static boolean _isLockCreating = false;
    private static Group _currentGroup;

    //TODO: write javadoc for it
    public static ProjectList get(Group group) {
        if (!_isLockCreating) {
            _instance = new ProjectList(group);
        }
        return _instance;
    }

    private ProjectList(Group group) {
        if (group != null) {
            _currentGroup = group;
            setProjects((List<Project>) _projectService.loadProjects(_currentGroup));
        }
    }

    public static void setLockCreating(boolean isLockCreating) {
        _isLockCreating = isLockCreating;
    }

    //TODO: write javadoc for it
    public List<Project> getProjects() {
        return Collections.unmodifiableList(_projects);
    }

    //TODO: write javadoc for it
    public void refreshLoadProjects() {
        if (_currentGroup != null) {
            _projects = loadProjects();
        }
    }

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
                continue;
            }
            newList.add(pr.get());
        }

        return Collections.unmodifiableList(newList);
    }

    public static List<Integer> getIdsProjects(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyList();
        }
        return projects.parallelStream()
                       .map(pr -> pr.getId())
                       .collect(Collectors.toList());
    }

    private List<Project> loadProjects() {
        return (List<Project>) _projectService.loadProjects(_currentGroup);
    }

    private void setProjects(List<Project> projects) {
        _projects = projects;
    }
}
