package com.lgc.gitlabtool.git.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.JGit;

public class ProjectServiceImplTest {

    private ProjectService _projectService;

    private ProjectTypeService _projectTypeService;
    private StateService _stateService;
    private RESTConnector _connector;
    private ConsoleService _consoleService;
    private GitService _gitService;
    private JGit _jGit;

    @Before
    public void init() {
        _projectTypeService = mock(ProjectTypeService.class);
        _stateService = mock(StateService.class);
        _connector = mock(RESTConnector.class);
        _consoleService = mock(ConsoleService.class);
        _jGit = mock(JGit.class);
        _gitService = mock(GitService.class);

        _projectService = new ProjectServiceImpl(_connector, _projectTypeService,
                _stateService, _consoleService, _gitService, _jGit);
    }

    @After
    public void clear() {
        _projectTypeService = null;
        _stateService = null;
        _connector = null;
        _consoleService = null;
        _gitService = null;
        _jGit = null;
        _gitService = null;
        _projectService = null;
    }

    @Test
    public void getIdsProjectsWrongParameter() {
        assertTrue(_projectService.getIdsProjects(null).isEmpty());
        assertTrue(_projectService.getIdsProjects(new ArrayList<>()).isEmpty());
    }

    @Test
    public void getIdsProjectsSuccessfully() {
        List<Project> projects = new ArrayList<>();
        int firstId = 25;
        int secondId = 305;
        projects.add(getProjectWithId(firstId));
        projects.add(getProjectWithId(secondId));

        List<Integer> result = _projectService.getIdsProjects(projects);

        assertFalse(result.isEmpty());
        assertEquals(result.size(), projects.size());
        assertTrue(result.containsAll(Arrays.asList(firstId, secondId)));
    }

    @Test
    public void getCorrectProjectsWrongParameter() {
        assertTrue(_projectService.getCorrectProjects(null).isEmpty());
        assertTrue(_projectService.getCorrectProjects(new ArrayList<>()).isEmpty());
    }

    @Test
    public void getCorrectProjectsSuccessfully() {
        List<Project> projects = new ArrayList<>();
        projects.add(new Project());
        projects.add(getCorrectProject());

        List<Project> result = _projectService.getCorrectProjects(projects);

        assertFalse(result.isEmpty());
        assertNotEquals(result.size(), projects.size());
    }

    @Test
    public void projectIsClonedAndWithoutConflictsWrongParameter() {
        assertFalse(_projectService.projectIsClonedAndWithoutConflicts(null));
        assertFalse(_projectService.projectIsClonedAndWithoutConflicts(new Project()));
    }

    @Test
    public void projectIsClonedAndWithoutConflictsWrongFailed() {
        Project project = getProjectWithConflicts();

        boolean isCorrectProjects = _projectService.projectIsClonedAndWithoutConflicts(project);

        assertFalse(isCorrectProjects);
    }

    @Test
    public void projectIsClonedAndWithoutConflictsSuccessfully() {
        Project project = getCorrectProject();

        boolean isCorrectProjects = _projectService.projectIsClonedAndWithoutConflicts(project);

        assertTrue(isCorrectProjects);
    }

    /*********************************************************************************************/

    private Project getCorrectProject() {
        Project project = new Project();
        project.setClonedStatus(true);
        ProjectStatus projectStatus = new ProjectStatus(false);
        project.setProjectStatus(projectStatus);
        return project;
    }

    private Project getProjectWithConflicts() {
        Project project = new Project();
        project.setClonedStatus(true);
        ProjectStatus projectStatus = new ProjectStatus(false, 0, 0, "", null, new HashSet<>(Arrays.asList("file")),
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        project.setProjectStatus(projectStatus);
        return project;
    }

    private Project getProjectWithId(int id) {
        Project project = mock(Project.class);
        try {
            Class<?> gotClass = Class.forName(Project.class.getName());
            Object instance = gotClass.newInstance();
            Field foundField = instance.getClass().getDeclaredField("_id");
            foundField.setAccessible(true);
            foundField.set(instance, id);
            foundField.setAccessible(false);
            project = (Project) instance;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            System.err.println("Failed setting private id field in Project.class: " + e.getMessage());
        }
        return project;
    }
}
