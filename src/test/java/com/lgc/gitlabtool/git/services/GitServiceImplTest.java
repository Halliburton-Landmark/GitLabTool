package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GitServiceImplTest {

    private GitService gitService;
    private Project stubProject;

    private StateService stateService;
    private JGit jGit;

    @Before
    public void init() {
        stateService = Mockito.mock(StateService.class);
        jGit = Mockito.mock(JGit.class);
        gitService = new GitServiceImpl(stateService, jGit);
    }

    @After
    public void clear() {
        gitService = null;
        jGit = null;
        gitService = null;
    }

    @Test(expected = NullPointerException.class)
    public void testCreateBranchWithNullableListOfProjects() {
        String branchName = "foo";
        String startPoint = "masterFoo";
        gitService.createBranch(null, branchName, startPoint, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBranchWithNullableBranchName() {
        List<Project> list = new ArrayList<>();
        stubProject = Mockito.mock(Project.class);
        when(stubProject.isCloned()).thenReturn(true);

        list.add(stubProject);
        String branchName = null;
        String startPoint = "masterFoo";
        when(jGit.createBranch(stubProject, branchName, startPoint, false)).thenThrow(IllegalArgumentException.class);

        gitService.createBranch(list, branchName, startPoint, false);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateBranchWithNullableStartPoint() {
        List<Project> list = new ArrayList<>();
        stubProject = Mockito.mock(Project.class);
        when(stubProject.isCloned()).thenReturn(true);
        list.add(stubProject);
        String branchName = "foo";
        String startPoint = null;

        gitService.createBranch(list, branchName, startPoint, false);
    }

    @Test
    public void testCreateBranchReturnStatement() {
        List<Project> list = new ArrayList<>();
        stubProject = Mockito.mock(Project.class);
        when(stubProject.isCloned()).thenReturn(true);
        Project anotherStubProject = Mockito.mock(Project.class);
        list.add(stubProject);
        list.add(anotherStubProject);
        String branchName = "foo";
        String startPoint = "master";
        when(jGit.createBranch(stubProject, branchName, startPoint, false)).thenReturn(JGitStatus.SUCCESSFUL);

        int expectedMapSize = 1;
        Map<Project, JGitStatus> statuses = gitService.createBranch(list, branchName, startPoint, false);

        assertEquals(expectedMapSize, statuses.size());
    }

    @Test
    public void testCreateBranchSwitchingOffState() {
        List<Project> list = new ArrayList<>();
        stubProject = Mockito.mock(Project.class);
        when(stubProject.isCloned()).thenReturn(true);
        list.add(stubProject);
        String branchName = "foo";
        String startPoint = "master";
        when(jGit.createBranch(stubProject, branchName, startPoint, false)).thenReturn(JGitStatus.SUCCESSFUL);
        when(stateService.isActiveState(ApplicationState.CREATE_BRANCH)).thenReturn(true);

        gitService.createBranch(list, branchName, startPoint, false);

        verify(stateService, times(1)).stateON(ApplicationState.CREATE_BRANCH);
        verify(stateService, times(1)).stateOFF(ApplicationState.CREATE_BRANCH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBranchSwitchingOffStateAfterException() {
        List<Project> list = new ArrayList<>();
        stubProject = Mockito.mock(Project.class);
        when(stubProject.isCloned()).thenReturn(true);
        list.add(stubProject);
        String branchName = "foo";
        String startPoint = "master";
        when(jGit.createBranch(stubProject, branchName, startPoint, false)).thenThrow(IllegalArgumentException.class);
        when(stateService.isActiveState(ApplicationState.CREATE_BRANCH)).thenReturn(true);

        gitService.createBranch(list, branchName, startPoint, false);

        verify(stateService, times(1)).stateON(ApplicationState.CREATE_BRANCH);
        verify(stateService, times(1)).stateOFF(ApplicationState.CREATE_BRANCH);
    }

    @Test
    public void testSwitchBranchWithNullableProgressListener() {
        List<Project> list = new ArrayList<>();
        stubProject = Mockito.mock(Project.class);
        list.add(stubProject);
        String branchName = "foo";
        boolean isRemote = false;
        ProgressListener progressListener = null;
        when(jGit.switchTo(stubProject, branchName, isRemote)).thenReturn(JGitStatus.SUCCESSFUL);

        Map<Project, JGitStatus> statuses = gitService.switchTo(list, branchName, isRemote, progressListener);

        assertEquals(1, statuses.size());
    }

    @Test
    public void testSwitchBranchSwitchingOffState() {
        List<Project> list = new ArrayList<>();
        stubProject = Mockito.mock(Project.class);
        list.add(stubProject);
        String branchName = "foo";
        boolean isRemote = false;
        ProgressListener progressListener = null;
        when(jGit.switchTo(stubProject, branchName, isRemote)).thenReturn(JGitStatus.SUCCESSFUL);
        when(stateService.isActiveState(ApplicationState.SWITCH_BRANCH)).thenReturn(true);

        gitService.switchTo(list, branchName, isRemote, progressListener);

        verify(stateService, times(1)).stateON(ApplicationState.SWITCH_BRANCH);
        verify(stateService, times(1)).stateOFF(ApplicationState.SWITCH_BRANCH);
    }

    @Test(expected = NullPointerException.class)
    public void testSwitchBranchSwitchingOffStateAfterException() {
        List<Project> list = new ArrayList<>();
        stubProject = Mockito.mock(Project.class);
        list.add(stubProject);
        String branchName = "foo";
        boolean isRemote = false;
        ProgressListener progressListener = null;
        when(jGit.switchTo(stubProject, branchName, isRemote)).thenThrow(NullPointerException.class);
        when(stateService.isActiveState(ApplicationState.SWITCH_BRANCH)).thenReturn(true);

        gitService.switchTo(list, branchName, isRemote, progressListener);

        verify(stateService, times(1)).stateON(ApplicationState.SWITCH_BRANCH);
        verify(stateService, times(1)).stateOFF(ApplicationState.SWITCH_BRANCH);
    }
    
    @Test
    public void testGetTrackingBranch() {
        stubProject = Mockito.mock(Project.class);
        when(jGit.getTrackingBranch(stubProject)).thenReturn(JGitStatus.SUCCESSFUL.toString());    
    }
    
}
