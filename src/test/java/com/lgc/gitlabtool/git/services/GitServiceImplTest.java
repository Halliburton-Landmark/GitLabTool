package com.lgc.gitlabtool.git.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jgit.api.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

public class GitServiceImplTest {

    private GitService _gitService;
    private Project _stubProject;

    private StateService _stateService;
    private JGit _jGit;

    @Before
    public void init() {
        _stateService = Mockito.mock(StateService.class);
        _jGit = Mockito.mock(JGit.class);
        _gitService = new GitServiceImpl(_stateService, _jGit);
    }

    @After
    public void clear() {
        _gitService = null;
        _jGit = null;
        _gitService = null;
    }

    @Test(expected = NullPointerException.class)
    public void testCreateBranchWithNullableListOfProjects() {
        String branchName = "foo";
        String startPoint = "masterFoo";
        _gitService.createBranch(null, branchName, startPoint, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBranchWithNullableBranchName() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        when(_stubProject.isCloned()).thenReturn(true);

        list.add(_stubProject);
        String branchName = null;
        String startPoint = "masterFoo";
        when(_jGit.createBranch(_stubProject, branchName, startPoint, false)).thenThrow(IllegalArgumentException.class);

        _gitService.createBranch(list, branchName, startPoint, false);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateBranchWithNullableStartPoint() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        when(_stubProject.isCloned()).thenReturn(true);
        list.add(_stubProject);
        String branchName = "foo";
        String startPoint = null;

        _gitService.createBranch(list, branchName, startPoint, false);
    }

    @Test
    public void testCreateBranchReturnStatement() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        when(_stubProject.isCloned()).thenReturn(true);
        Project anotherStubProject = Mockito.mock(Project.class);
        list.add(_stubProject);
        list.add(anotherStubProject);
        String branchName = "foo";
        String startPoint = "master";
        when(_jGit.createBranch(_stubProject, branchName, startPoint, false)).thenReturn(JGitStatus.SUCCESSFUL);

        int expectedMapSize = 1;
        Map<Project, JGitStatus> statuses = _gitService.createBranch(list, branchName, startPoint, false);

        assertEquals(expectedMapSize, statuses.size());
    }

    @Test
    public void testCreateBranchSwitchingOffState() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        when(_stubProject.isCloned()).thenReturn(true);
        list.add(_stubProject);
        String branchName = "foo";
        String startPoint = "master";
        when(_jGit.createBranch(_stubProject, branchName, startPoint, false)).thenReturn(JGitStatus.SUCCESSFUL);
        when(_stateService.isActiveState(ApplicationState.CREATE_BRANCH)).thenReturn(true);

        _gitService.createBranch(list, branchName, startPoint, false);

        verify(_stateService, times(1)).stateON(ApplicationState.CREATE_BRANCH);
        verify(_stateService, times(1)).stateOFF(ApplicationState.CREATE_BRANCH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBranchSwitchingOffStateAfterException() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        when(_stubProject.isCloned()).thenReturn(true);
        list.add(_stubProject);
        String branchName = "foo";
        String startPoint = "master";
        when(_jGit.createBranch(_stubProject, branchName, startPoint, false)).thenThrow(IllegalArgumentException.class);
        when(_stateService.isActiveState(ApplicationState.CREATE_BRANCH)).thenReturn(true);

        _gitService.createBranch(list, branchName, startPoint, false);

        verify(_stateService, times(1)).stateON(ApplicationState.CREATE_BRANCH);
        verify(_stateService, times(1)).stateOFF(ApplicationState.CREATE_BRANCH);
    }

    @Test
    public void testSwitchBranchWithNullableProgressListener() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        list.add(_stubProject);
        String branchName = "foo";
        boolean isRemote = false;
        ProgressListener progressListener = null;
        when(_jGit.switchTo(_stubProject, branchName, isRemote)).thenReturn(JGitStatus.SUCCESSFUL);

        Map<Project, JGitStatus> statuses = _gitService.switchTo(list, branchName, isRemote, progressListener);

        assertEquals(1, statuses.size());
    }

    @Test
    public void testSwitchBranchSwitchingOffState() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        list.add(_stubProject);
        String branchName = "foo";
        boolean isRemote = false;
        ProgressListener progressListener = null;
        when(_jGit.switchTo(_stubProject, branchName, isRemote)).thenReturn(JGitStatus.SUCCESSFUL);
        when(_stateService.isActiveState(ApplicationState.SWITCH_BRANCH)).thenReturn(true);

        _gitService.switchTo(list, branchName, isRemote, progressListener);

        verify(_stateService, times(1)).stateON(ApplicationState.SWITCH_BRANCH);
        verify(_stateService, times(1)).stateOFF(ApplicationState.SWITCH_BRANCH);
    }

    @Test(expected = NullPointerException.class)
    public void testSwitchBranchSwitchingOffStateAfterException() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        list.add(_stubProject);
        String branchName = "foo";
        boolean isRemote = false;
        ProgressListener progressListener = null;
        when(_jGit.switchTo(_stubProject, branchName, isRemote)).thenThrow(NullPointerException.class);
        when(_stateService.isActiveState(ApplicationState.SWITCH_BRANCH)).thenReturn(true);

        _gitService.switchTo(list, branchName, isRemote, progressListener);

        verify(_stateService, times(1)).stateON(ApplicationState.SWITCH_BRANCH);
        verify(_stateService, times(1)).stateOFF(ApplicationState.SWITCH_BRANCH);
    }

    @Test
    public void getChangedFilesIncorrectData() {
        assertTrue(_gitService.getChangedFiles(null).isEmpty());
        assertTrue(_gitService.getChangedFiles(new Project()).isEmpty());

        Project project = getClonedProject();
        when(_jGit.getStatusProject(project)).thenReturn(getIncorrectStatus());
        assertTrue(_gitService.getChangedFiles(project).isEmpty());
    }

    @Test
    public void getChangedFilesUntrackedFiles() {
        Project project = getClonedProject();
        Set<String> files = getFiles();

        Status status = Mockito.mock(Status.class);
        when(status.getConflicting()).thenReturn(files);
        when(status.getUntracked()).thenReturn(files);

        when(_jGit.getStatusProject(project)).thenReturn(Optional.of(status));

        Collection<ChangedFile> changedFiles = _gitService.getChangedFiles(project);
        assertEquals(changedFiles.size(), files.size()*2);
    }

    @Test
    public void addUntrackedFileForCommitIncorrectData() {
        assertTrue(_gitService.addUntrackedFileForCommit(null).isEmpty());
        assertTrue(_gitService.addUntrackedFileForCommit(Collections.emptyMap()).isEmpty());
    }

    @Test
    public void addUntrackedFileForCommitCorrectData() {
        Project project = getClonedProject();
        Set<String> files = getFiles();

        Map<Project, List<ChangedFile>> data = getFilesForProject(project);
        when(_jGit.addUntrackedFileForCommit(anyCollection(), eq(project))).thenReturn(new ArrayList<>(files));

        List<ChangedFile> addedFiles = _gitService.addUntrackedFileForCommit(data);
        assertEquals(addedFiles.size(), files.size());
    }

    private Optional<Status> getIncorrectStatus() {
        return Optional.empty();
    }

    private Project getClonedProject() {
        Project project = new Project();
        project.setClonedStatus(true);
        return project;
    }

    private Set<String> getFiles() {
        return new HashSet<>(Arrays.asList("new file 1", "new file 2", "new file 3"));
    }

    private List<ChangedFile> getChangedFiles() {
        List<ChangedFile> files = new ArrayList<>();
        Project project = getClonedProject();
        getFiles().forEach(fileName -> files.add(new ChangedFile(project, fileName, false)));
        return files;
    }

    private Map<Project, List<ChangedFile>> getFilesForProject(Project project) {
        Map<Project, List<ChangedFile>> map = new HashMap<>();
        map.put(null, Collections.emptyList()); // some incorrect value
        map.put(new Project(), Collections.emptyList()); // some incorrect value
        map.put(project, getChangedFiles());
        return map;
    }
}
