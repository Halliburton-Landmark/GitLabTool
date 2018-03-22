package com.lgc.gitlabtool.git.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.lgc.gitlabtool.git.connections.HttpResponseHolder;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Group;
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
    private JSONParserService _jsonParserService;
    private CurrentUser _currentUser;
    private JGit _jGit;

    @Before
    public void init() {
        _projectTypeService = mock(ProjectTypeService.class);
        _stateService = mock(StateService.class);
        _connector = mock(RESTConnector.class);
        _consoleService = mock(ConsoleService.class);
        _jsonParserService = mock(JSONParserService.class);
        _jGit = mock(JGit.class);
        _currentUser = mock(CurrentUser.class);
        _gitService = mock(GitService.class);

        _projectService = new ProjectServiceImpl(_connector, _projectTypeService,
                _stateService, _consoleService, _gitService, _jsonParserService, _currentUser, _jGit);
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

    @Test(expected=IllegalArgumentException.class)
    public void getProjectsNullGroup() {
        _projectService.getProjects(null);
    }

    @Test
    public void getProjectsNullTokenValue() {
        when(_currentUser.getOAuth2TokenValue()).thenReturn(null);
        when(_currentUser.getPrivateTokenKey()).thenReturn(null);

        Collection<Project> result = _projectService.getProjects(new Group());

        assertTrue(result.isEmpty());
    }

    @Test
    public void getProjectsErrorFromGitLab() {
        HttpResponseHolder httpResponseHolderMock = getHttpResponseHolder(false, "1");
        when(_currentUser.getOAuth2TokenValue()).thenReturn("testTokenValue");
        when(_currentUser.getPrivateTokenKey()).thenReturn("testTokenKey");
        when(_connector.sendGet(anyString(), eq(null), anyMap())).thenReturn(httpResponseHolderMock);

        Collection<Project> result = _projectService.getProjects(getGroupWithSubGroup(5));

        assertEquals(result, null);
    }

    @Test
    public void getProjectsOnlyOnePage() {
        int countSubGroup = 5;
        Group testedGroup = getGroupWithSubGroup(countSubGroup);
        // mock of answers from the GitLab
        HttpResponseHolder httpResponseHolderMock = getHttpResponseHolder(true, "1");
        when(_currentUser.getOAuth2TokenValue()).thenReturn("testTokenValue");
        when(_currentUser.getPrivateTokenKey()).thenReturn("testTokenKey");
        when(_connector.sendGet(anyString(), eq(null), anyMap())).thenReturn(httpResponseHolderMock);
        // mock of json parser
        Collection<Object> groupProjects = Arrays.asList(new Project(), new Project()); //for each group (include subgroups)
        when(_jsonParserService.parseToCollectionObjects(anyString(), Mockito.any(Type.class))).thenReturn(groupProjects);

        Collection<Project> result = _projectService.getProjects(testedGroup);

        assertEquals(groupProjects.size() * (countSubGroup + 1), result.size());
    }

    @Test
    public void getProjectsFewPages() {
        int countSubGroup = 3;
        Group testedGroup = getGroupWithSubGroup(countSubGroup);
        // mock of answers from the GitLab
        int countProjectPages = 3;
        HttpResponseHolder httpResponseHolderMock = getHttpResponseHolder(true, "" + countProjectPages);
        when(_currentUser.getOAuth2TokenValue()).thenReturn("testTokenValue");
        when(_currentUser.getPrivateTokenKey()).thenReturn("testTokenKey");
        when(_connector.sendGet(anyString(), eq(null), anyMap())).thenReturn(httpResponseHolderMock);
        // mock of json parser
        Collection<Object> groupProjects = new ArrayList<>(Arrays.asList(new Project())); //for each group (include subgroups)
        when(_jsonParserService.parseToCollectionObjects(anyString(), Mockito.any(Type.class))).thenReturn(groupProjects);

        Collection<Project> result = _projectService.getProjects(testedGroup);

        int countGroups = countSubGroup + 1;
        int countGroupProjects = groupProjects.size() * countProjectPages;
        assertEquals(countGroupProjects * countGroups, result.size());
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

    private Group getGroupWithSubGroup(int countSubGroup) {
        Group mainGroup = new Group();
        for (int i = 0; i < countSubGroup-1; i++) {
            Group subGroup = new Group();
            if (i == 0) {
                subGroup.addSubGroup(new Group());
            }
            mainGroup.addSubGroup(subGroup);
        }
        return mainGroup;
    }

    private HttpResponseHolder getHttpResponseHolder(boolean isSuccess, String countPages) {
        HttpResponseHolder httpResponseHolderMock = mock(HttpResponseHolder.class);
        when(httpResponseHolderMock.getResponseCode()).thenReturn(isSuccess ? 200 : 101);
        when(httpResponseHolderMock.getBody()).thenReturn("test_json");
        Map<String, List<String>> prefereces = new HashMap<>();
        prefereces.put("X-Total-Pages", Arrays.asList(countPages));
        when(httpResponseHolderMock.getHeaderLines()).thenReturn(prefereces);
        return httpResponseHolderMock;
    }
}
