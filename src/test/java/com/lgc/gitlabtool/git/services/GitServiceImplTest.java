package com.lgc.gitlabtool.git.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.jgit.ChangedFileStatus;
import com.lgc.gitlabtool.git.jgit.ChangedFileType;
import com.lgc.gitlabtool.git.jgit.ChangedFilesUtils;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

public class GitServiceImplTest {

    private GitService _gitService;
    private Project _stubProject;

    private StateService _stateService;
    private JGit _jGit;
    private ChangedFilesUtils _changedFilesUtilsMock;

    @Before
    public void init() {
        _stateService = Mockito.mock(StateService.class);
        _jGit = Mockito.mock(JGit.class);
        _changedFilesUtilsMock = Mockito.mock(ChangedFilesUtils.class);
        _gitService = new GitServiceImpl(_stateService, _jGit, _changedFilesUtilsMock);
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

        ProjectStatus status = new ProjectStatus(false, 0, 0, "branch", files, files,
                new HashSet<>(), new HashSet<>(), new HashSet<>(),new HashSet<>(), new HashSet<>());
        project.setProjectStatus(status);

        Collection<ChangedFile> changedFiles = _gitService.getChangedFiles(project);
        assertEquals(changedFiles.size(), files.size()*2);
    }

    @Test
    public void addUntrackedFileForCommitIncorrectData() {
        assertTrue(_gitService.addUntrackedFilesToIndex(null).isEmpty());
        assertTrue(_gitService.addUntrackedFilesToIndex(Collections.emptyMap()).isEmpty());
    }

    @Test
    public void addUntrackedFileForCommitCorrectData() {
        Project project = getClonedProject();

        Map<Project, List<ChangedFile>> data = getFilesForProject(project);
        when(_changedFilesUtilsMock.getChangedFiles(any(), any(), any())).thenReturn(getChangedFiles());
        when(_jGit.addUntrackedFileToIndex(any(), eq(project))).thenReturn(true);
        when(_jGit.addDeletedFile(any(), eq(project), eq(true))).thenReturn(true);

        List<ChangedFile> addedFiles = _gitService.addUntrackedFilesToIndex(data);
        assertEquals(addedFiles.size(), getChangedFiles().size());
    }

    @Test
    public void getProjectStatusIncorrectData() {
        ProjectStatus status = _gitService.getProjectStatus(null);
        assertFalse(status.hasChanges());
        assertFalse(status.hasConflicts());
        assertTrue(status.getConflictedFiles().isEmpty());
        assertTrue(status.getUntrackedFiles().isEmpty());
        assertEquals(status.getAheadIndex(), 0);
        assertEquals(status.getBehindIndex(), 0);
    }

    @Test
    public void getProjectStatusCorrectData() {
        Project project = getClonedProject();
        String currentBranch = "test_branch";
        int[] aheadBehindIndex = new int[] {0, 1};
        boolean hasChanges = true;

        Status gitStatus = Mockito.mock(Status.class);
        Set<String> files = getFiles();
        when(gitStatus.getConflicting()).thenReturn(files);
        when(gitStatus.getUntracked()).thenReturn(files);
        when(gitStatus.hasUncommittedChanges()).thenReturn(hasChanges);

        when(_jGit.getStatusProject(project)).thenReturn(Optional.of(gitStatus));
        when(_jGit.getCurrentBranch(project)).thenReturn(Optional.of(currentBranch));
        when(_jGit.getAheadBehindIndexCounts(project, currentBranch)).thenReturn(aheadBehindIndex);

        ProjectStatus status = _gitService.getProjectStatus(project);
        assertEquals(aheadBehindIndex[1], status.getBehindIndex());
        assertEquals(aheadBehindIndex[0], status.getAheadIndex());
        assertEquals(currentBranch, status.getCurrentBranch());
        assertEquals(files, status.getConflictedFiles());
        assertEquals(files, status.getUntrackedFiles());
        assertEquals(hasChanges, status.hasChanges());
    }

    @Test
    public void resetChangedFilesIncorrectData() {
        Map<Project, List<ChangedFile>> changedFile = new HashMap<>();
        assertTrue(_gitService.resetChangedFiles(null).isEmpty());
        assertTrue(_gitService.resetChangedFiles(changedFile).isEmpty());

        changedFile.put(null, getChangedFiles());
        changedFile.put(new Project(), getChangedFiles());
        changedFile.put(getClonedProject(), new ArrayList<>());
        assertTrue(_gitService.resetChangedFiles(changedFile).isEmpty());
    }

    @Test
    public void resetChangedFilesCorrectData() {
        Project project = getClonedProject();
        List<String> files = new ArrayList<>(getFiles());
        Map<Project, List<ChangedFile>> data = getFilesForProject(project);
        when(_changedFilesUtilsMock.getFileNames(any())).thenReturn(files);
        when(_jGit.resetChangedFiles(any(), eq(project))).thenReturn(files);
        when(_changedFilesUtilsMock.getChangedFiles(eq(files), eq(project), any())).thenReturn(getChangedFiles(files));

        List<ChangedFile> modifiedFiles = _gitService.resetChangedFiles(data);
        assertFalse(modifiedFiles.isEmpty());
        assertEquals(modifiedFiles.size(), files.size());
    }


    /*********************************************************************************************************/

    private Optional<Status> getIncorrectStatus() {
        return Optional.empty();
    }

    private Project getClonedProject() {
        Project project = new Project();
        project.setClonedStatus(true);
        project.setProjectStatus(new ProjectStatus());
        project.setPathToClonedProject(".");
        return project;
    }

    private Set<String> getFiles() {
        return new HashSet<>(Arrays.asList("new file 1", "new file 2", "new file 3"));
    }

    private List<ChangedFile> getChangedFiles() {
        List<ChangedFile> files = getChangedFiles(getFiles());
        files.add(new ChangedFile(getClonedProject(), "test 2", ChangedFileType.UNSTAGED, ChangedFileStatus.MODIFIED));
        return files;
    }

    private List<ChangedFile> getChangedFiles(Collection<String> files) {
        List<ChangedFile> changedFiles = new ArrayList<>();
        Project project = getClonedProject();
        getFiles().forEach(fileName -> changedFiles.add(
                new ChangedFile(project, fileName, ChangedFileType.UNSTAGED, ChangedFileStatus.MODIFIED)));
        return changedFiles;
    }

    private Map<Project, List<ChangedFile>> getFilesForProject(Project project) {
        Map<Project, List<ChangedFile>> map = new HashMap<>();
        map.put(null, Collections.emptyList()); // some incorrect value
        map.put(new Project(), Collections.emptyList()); // some incorrect value
        map.put(project, getChangedFiles());
        return map;
    }
}
