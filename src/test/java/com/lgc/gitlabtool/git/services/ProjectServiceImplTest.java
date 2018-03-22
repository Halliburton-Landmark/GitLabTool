package com.lgc.gitlabtool.git.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.lgc.gitlabtool.git.connections.HttpResponseHolder;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.project.nature.projecttype.DSGProjectType;
import com.lgc.gitlabtool.git.project.nature.projecttype.UnknownProjectType;
import com.lgc.gitlabtool.git.util.PathUtilities;

public class ProjectServiceImplTest {

    private ProjectService _projectService;

    private ProjectTypeService _projectTypeService;
    private StateService _stateService;
    private RESTConnector _connector;
    private ConsoleService _consoleService;
    private GitService _gitService;
    private JSONParserService _jsonParserService;
    private CurrentUser _currentUser;
    private PathUtilities _pathUtilities;
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
        _pathUtilities = mock(PathUtilities.class);

        _projectService = new ProjectServiceImpl(_connector, _projectTypeService, _stateService,
                _consoleService, _gitService, _jsonParserService, _currentUser, _pathUtilities, _jGit);
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
        projects.add(getClonedProject());

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
        Project project = getClonedProject();

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

    @Test(expected=IllegalArgumentException.class)
    public void loadProjectsNullGroup() {
        _projectService.loadProjects(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void loadProjectsGroupIsNotCloned() {
        _projectService.loadProjects(new Group());
    }

    @Test
    public void loadProjectsErrorFromGitLab() {
        Mockito.doNothing().when(_stateService).stateON(ApplicationState.LOAD_PROJECTS);
        Mockito.doNothing().when(_stateService).stateOFF(ApplicationState.LOAD_PROJECTS);
        _projectService = new ProjectServiceImpl(_connector, _projectTypeService, _stateService,
                _consoleService, _gitService, _jsonParserService, _currentUser, _pathUtilities, _jGit) {
            @Override
            public Collection<Project> getProjects(Group group) {
                return null;
            }
        };
        Collection<Project> result = _projectService.loadProjects(getClonedGroup());

        assertEquals(result, null);
    }

    @Test
    public void loadProjectsGroupDoesNotHaveProjects() {
        Mockito.doNothing().when(_stateService).stateON(ApplicationState.LOAD_PROJECTS);
        Mockito.doNothing().when(_stateService).stateOFF(ApplicationState.LOAD_PROJECTS);
        _projectService = new ProjectServiceImpl(_connector, _projectTypeService, _stateService,
                _consoleService, _gitService, _jsonParserService, _currentUser, _pathUtilities, _jGit) {
            @Override
            public Collection<Project> getProjects(Group group) {
                return Collections.emptyList();
            }
        };

        Collection<Project> result = _projectService.loadProjects(getClonedGroup());

        assertTrue(result.isEmpty());
    }

    @Test
    public void loadProjectsGroupDoesNotHaveClonedProjects() {
        Mockito.doNothing().when(_stateService).stateON(ApplicationState.LOAD_PROJECTS);
        Mockito.doNothing().when(_stateService).stateOFF(ApplicationState.LOAD_PROJECTS);
        when(_pathUtilities.getFolders(Mockito.any(Path.class))).thenReturn(new ArrayList<>());
        List<Project> projectsFromGitLab = new ArrayList<>(Arrays.asList(new Project(), new Project()));
        _projectService = new ProjectServiceImpl(_connector, _projectTypeService, _stateService,
                _consoleService, _gitService, _jsonParserService, _currentUser, _pathUtilities, _jGit) {
            @Override
            public Collection<Project> getProjects(Group group) {
                return projectsFromGitLab;
            }
        };

        Collection<Project> result = _projectService.loadProjects(getClonedGroup());

        assertFalse(result.isEmpty());
        assertEquals(projectsFromGitLab.size(), result.size());

        // check that project statuses weren't updated
        Project resultProject = result.iterator().next();
        assertFalse(resultProject.isCloned());
        assertTrue(resultProject.getProjectStatus().getCurrentBranch().isEmpty());
        assertEquals(resultProject.getPath(), null);
        assertEquals(resultProject.getProjectType(), null);
    }

    @Test
    public void loadProjectsGroupHasClonedProjects() {
        List<Project> projectsFromGitLab = getNotClonedProjects();
        List<String> clonedProjects = new ArrayList<>(Arrays.asList("test project"));
        when(_pathUtilities.getFolders(any(Path.class))).thenReturn(clonedProjects);
        when(_pathUtilities.isExistsAndDirectory(any(Path.class))).thenReturn(true);
        when(_projectTypeService.getProjectType(any(Project.class))).thenReturn(new DSGProjectType());
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Project project = (Project) invocation.getArguments()[0];
                project.setProjectStatus(getProjectStatus(true, false));
                return null;
            }
        }).when(_gitService).getProjectStatus(any());
        Mockito.doNothing().when(_stateService).stateON(ApplicationState.LOAD_PROJECTS);
        Mockito.doNothing().when(_stateService).stateOFF(ApplicationState.LOAD_PROJECTS);
        _projectService = new ProjectServiceImpl(_connector, _projectTypeService, _stateService,
                _consoleService, _gitService, _jsonParserService, _currentUser, _pathUtilities, _jGit) {
            @Override
            public Collection<Project> getProjects(Group group) {
                return projectsFromGitLab;
            }
        };

        Collection<Project> result = _projectService.loadProjects(getClonedGroup());

        assertFalse(result.isEmpty());
        assertEquals(projectsFromGitLab.size(), result.size());

        // check that project statuses were updated
        Project resultProject = result.iterator().next();
        assertTrue(resultProject.isCloned());
        assertTrue(resultProject.getProjectStatus().hasChanges());
        assertTrue(resultProject.getProjectType() instanceof DSGProjectType);
        assertFalse(resultProject.getProjectStatus().getCurrentBranch().isEmpty());
    }

    @Test
    public void updateProjectStatusNullProject() {
        boolean result = _projectService.updateProjectStatus(null);

        assertFalse(result);
    }

    @Test
    public void updateProjectStatusProjectIsNotCloned() {
        boolean result = _projectService.updateProjectStatus(new Project());

        assertFalse(result);
    }

    @Test
    public void updateProjectStatusesNullList() {
        boolean result = _projectService.updateProjectStatuses(null);

        assertFalse(result);
    }

    @Test
    public void updateProjectStatusesEmptyList() {
        boolean result = _projectService.updateProjectStatuses(new ArrayList<>());

        assertFalse(result);
    }

    @Test
    public void updateProjectStatusesSuccess() {
        List<Project> projects = Arrays.asList(getClonedProject(), getClonedProject(), null);
        Mockito.doNothing().when(_stateService).stateON(ApplicationState.UPDATE_PROJECT_STATUSES);
        Mockito.doNothing().when(_stateService).stateOFF(ApplicationState.UPDATE_PROJECT_STATUSES);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Project project = (Project) invocation.getArguments()[0];
                project.setProjectStatus(getProjectStatus(true, false));
                return null;
            }
        }).when(_gitService).getProjectStatus(any());

        boolean result = _projectService.updateProjectStatuses(projects);

        assertTrue(result);
        assertTrue(projects.get(0).getProjectStatus().hasChanges());
        assertFalse(projects.get(0).getProjectStatus().getCurrentBranch().isEmpty());
    }

    @Test(expected=IllegalArgumentException.class)
    public void createProjectNullGroup() {
        _projectService.createProject(null, "name", new UnknownProjectType(), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createProjectNotClonedGroup() {
        _projectService.createProject(new Group(), "name", new UnknownProjectType(), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createProjectNullName() {
        _projectService.createProject(getClonedGroup(), null, new UnknownProjectType(), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createProjectEmptyName() {
        _projectService.createProject(getClonedGroup(), "", new UnknownProjectType(), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createProjectNullProjectType() {
        _projectService.createProject(getClonedGroup(), "name", null, null);
    }

    @Test
    public void createProjectErrorFromGitLab() {
        _projectService = new ProjectServiceImpl(_connector, _projectTypeService, _stateService,
                _consoleService, _gitService, _jsonParserService, _currentUser, _pathUtilities, _jGit) {
            @Override
            public Collection<Project> getProjects(Group group) {
                return null;
            }
        };
        CreateProjectTestListener progressListener = new CreateProjectTestListener();

        _projectService.createProject(getClonedGroup(), "name", new UnknownProjectType(), progressListener);

        assertFalse(progressListener.isSuccessfulOperation());
    }

    @Test
    public void createProjectProjectExist() {
        _projectService = new ProjectServiceImpl(_connector, _projectTypeService, _stateService,
                _consoleService, _gitService, _jsonParserService, _currentUser, _pathUtilities, _jGit) {
            @Override
            public Collection<Project> getProjects(Group group) {
                return null;
            }
        };
        CreateProjectTestListener progressListener = new CreateProjectTestListener();

        _projectService.createProject(getClonedGroup(), "name", new UnknownProjectType(), progressListener);

        assertFalse(progressListener.isSuccessfulOperation());
    }

    /*********************************************************************************************/

    private Group getClonedGroup() {
        Group group = new Group();
        group.setClonedStatus(true);
        group.setPath(".");
        return group;
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

    private Project getClonedProject() {
        Project project = new Project();
        project.setClonedStatus(true);
        project.setPath(".");
        ProjectStatus projectStatus = new ProjectStatus(false);
        project.setProjectStatus(projectStatus);
        return project;
    }

    private List<Project> getNotClonedProjects() {
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Project project = new Project() {
                @Override
                protected boolean checkPath(Path pathToProject) {
                    return true;
                }
            };
            projects.add(project);
        }
        return projects;
    }

    private Project getProjectWithConflicts() {
        Project project = new Project();
        project.setClonedStatus(true);
        ProjectStatus projectStatus = getProjectStatus(false, true);
        project.setProjectStatus(projectStatus);
        return project;
    }

    private Project getProjectWithId(int id) {
        Project project = mock(Project.class);
        try {
            Class<?> gotClass = Class.forName(Project.class.getName());
            Object instance = gotClass.newInstance();
            setProjectFieldValue(instance, "_id", id);
            project = (Project) instance;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.err.println("Failed setting private id field in Project.class: " + e.getMessage());
        }
        return project;
    }

    private void setProjectFieldValue(Object instance, String fieldName, Object fieldValue) {
        try {
            Field foundField = instance.getClass().getDeclaredField(fieldName);
            foundField.setAccessible(true);
            foundField.set(instance, fieldValue);
            foundField.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            System.err.println("Failed setting private id field in Project.class: " + e.getMessage());
        }
    }

    private ProjectStatus getProjectStatus(boolean hasChanges, boolean hasConflicts) {
        return new ProjectStatus(hasChanges, 0, 0, "test_branch", null,
                hasConflicts ? new HashSet<>(Arrays.asList("conflict file")) : new HashSet<>(),
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
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


    private class CreateProjectTestListener implements ProgressListener {

        private boolean _isSuccessfulOperation;

        @Override
        public void onSuccess(Object... t) {
            _isSuccessfulOperation = true;
        }

        @Override
        public void onError(Object... t) {
            _isSuccessfulOperation = false;
        }

        @Override
        public void onStart(Object... t) {

        }

        @Override
        public void onFinish(Object... t) {

        }

        public boolean isSuccessfulOperation() {
            return _isSuccessfulOperation;
        }
    }
}
