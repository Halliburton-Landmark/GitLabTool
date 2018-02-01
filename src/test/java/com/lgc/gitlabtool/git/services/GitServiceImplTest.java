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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.jgit.ChangedFileStatus;
import com.lgc.gitlabtool.git.jgit.ChangedFileType;
import com.lgc.gitlabtool.git.jgit.ChangedFilesUtils;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.jgit.stash.GroupStash;
import com.lgc.gitlabtool.git.jgit.stash.SingleProjectStash;
import com.lgc.gitlabtool.git.jgit.stash.Stash;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

public class GitServiceImplTest {

    private GitService _gitService;
    private Project _stubProject;

    private StateService _stateService;
    private ConsoleService _consoleService;
    private JGit _jGit;
    private ChangedFilesUtils _changedFilesUtilsMock;

    @Before
    public void init() {
        _stateService = Mockito.mock(StateService.class);
        _consoleService = Mockito.mock(ConsoleService.class);
        _jGit = Mockito.mock(JGit.class);
        _changedFilesUtilsMock = Mockito.mock(ChangedFilesUtils.class);
        _gitService = new GitServiceImpl(_stateService, _consoleService, _jGit, _changedFilesUtilsMock);
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
        when(_jGit.checkoutBranch(_stubProject, branchName, isRemote)).thenReturn(JGitStatus.SUCCESSFUL);

        Map<Project, JGitStatus> statuses = _gitService.checkoutBranch(list, branchName, isRemote, progressListener);

        assertEquals(1, statuses.size());
    }

    @Test
    public void testCheckoutBranchOffState() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        list.add(_stubProject);
        String branchName = "foo";
        boolean isRemote = false;
        ProgressListener progressListener = null;
        when(_jGit.checkoutBranch(_stubProject, branchName, isRemote)).thenReturn(JGitStatus.SUCCESSFUL);
        when(_stateService.isActiveState(ApplicationState.CHECKOUT_BRANCH)).thenReturn(true);

        _gitService.checkoutBranch(list, branchName, isRemote, progressListener);

        verify(_stateService, times(1)).stateON(ApplicationState.CHECKOUT_BRANCH);
        verify(_stateService, times(1)).stateOFF(ApplicationState.CHECKOUT_BRANCH);
    }

    @Test(expected = NullPointerException.class)
    public void testCheckoutBranchSwitchingOffStateAfterException() {
        List<Project> list = new ArrayList<>();
        _stubProject = Mockito.mock(Project.class);
        list.add(_stubProject);
        String branchName = "foo";
        boolean isRemote = false;
        ProgressListener progressListener = null;
        when(_jGit.checkoutBranch(_stubProject, branchName, isRemote)).thenThrow(NullPointerException.class);
        when(_stateService.isActiveState(ApplicationState.CHECKOUT_BRANCH)).thenReturn(true);

        _gitService.checkoutBranch(list, branchName, isRemote, progressListener);

        verify(_stateService, times(1)).stateON(ApplicationState.CHECKOUT_BRANCH);
        verify(_stateService, times(1)).stateOFF(ApplicationState.CHECKOUT_BRANCH);

    }

    @Test
    public void getChangedFilesWrongParameters() {
        List<ChangedFile> resultWithNullParam = _gitService.getChangedFiles(null);
        List<ChangedFile> resultNotClonedParam = _gitService.getChangedFiles(new Project());

        assertTrue(resultWithNullParam.isEmpty());
        assertTrue(resultNotClonedParam.isEmpty());
    }

    @Test
    public void getChangedFilesIncorrectStatusFromJGit() {
        Project clonedProject = getClonedProject();
        when(_jGit.getStatusProject(clonedProject)).thenReturn(getIncorrectStatus());

        List<ChangedFile> changedFiles = _gitService.getChangedFiles(clonedProject);

        assertTrue(changedFiles.isEmpty());
    }

    @Test
    public void getChangedFilesUntrackedFiles() {
        Project project = getClonedProject();
        Set<String> files = getFiles();
        ProjectStatus status = new ProjectStatus(false, 0, 0, "branch", null, files, files,
                new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        project.setProjectStatus(status);

        Collection<ChangedFile> changedFiles = _gitService.getChangedFiles(project);

        assertEquals(changedFiles.size(), files.size()*2);
    }

    @Test
    public void addUntrackedFileForCommitIncorrectData() {
        List<ChangedFile> resultWithNullParam = _gitService.addUntrackedFilesToIndex(null);
        List<ChangedFile> resultEmptyMapParam = _gitService.addUntrackedFilesToIndex(Collections.emptyMap());

        assertTrue(resultWithNullParam.isEmpty());
        assertTrue(resultEmptyMapParam.isEmpty());
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
        // data for the test
        Project project = getClonedProject();
        String currentBranch = "test_branch";
        int[] aheadBehindIndex = new int[] {0, 1};
        int expectedBehindIndex = aheadBehindIndex[1];
        int expectedAheadIndex = aheadBehindIndex[0];
        boolean hasChanges = true;
        Set<String> files = getFiles();
        Status gitStatusMock = Mockito.mock(Status.class);
        // set the returned data for mock of git status
        when(gitStatusMock.getConflicting()).thenReturn(files);
        when(gitStatusMock.getUntracked()).thenReturn(files);
        when(gitStatusMock.hasUncommittedChanges()).thenReturn(hasChanges);
        // mock for JGit methods
        when(_jGit.getStatusProject(project)).thenReturn(Optional.of(gitStatusMock));
        when(_jGit.getCurrentBranch(project)).thenReturn(Optional.of(currentBranch));
        when(_jGit.getAheadBehindIndexCounts(project, currentBranch)).thenReturn(aheadBehindIndex);

        ProjectStatus status = _gitService.getProjectStatus(project);

        assertEquals(expectedBehindIndex, status.getBehindIndex());
        assertEquals(expectedAheadIndex, status.getAheadIndex());
        assertEquals(currentBranch, status.getCurrentBranch());
        assertEquals(files, status.getConflictedFiles());
        assertEquals(files, status.getUntrackedFiles());
        assertEquals(hasChanges, status.hasChanges());
    }

    @Test
    public void resetChangedFilesWrongParameters() {
        List<ChangedFile> filesNullParam = _gitService.resetChangedFiles(null);
        List<ChangedFile> filesEmptyMapParam = _gitService.resetChangedFiles(new HashMap<>());

        assertTrue(filesNullParam.isEmpty());
        assertTrue(filesEmptyMapParam.isEmpty());
    }

    @Test
    public void resetChangedFilesIncorrectDataInMap() {
        Map<Project, List<ChangedFile>> changedFile = new HashMap<>();
        changedFile.put(null, getChangedFiles());
        changedFile.put(new Project(), getChangedFiles());
        changedFile.put(getClonedProject(), new ArrayList<>());

        List<ChangedFile> resetFiles = _gitService.resetChangedFiles(changedFile);

        assertTrue(resetFiles.isEmpty());
    }

    @Test
    public void resetChangedFilesSuccessfully() {
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

    @Test
    public void testGetTrackingBranchCorrectValue() {
        _stubProject = Mockito.mock(Project.class);
        when(_jGit.getTrackingBranch(_stubProject)).thenReturn(JGitStatus.SUCCESSFUL.toString());

        String result = _gitService.getTrackingBranch(_stubProject);

        assertEquals(result, JGitStatus.SUCCESSFUL.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceWithHEADRevisionNullParameter() {
        _gitService.replaceWithHEADRevision(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceWithHEADRevisionEmptyListParameter() {
        _gitService.replaceWithHEADRevision(Collections.emptyList());
    }

    @Test
    public void replaceWithHEADRevisionSuccessfully() {
        Collection<ChangedFile> changedFiles = getChangedFiles();

        _gitService.replaceWithHEADRevision(changedFiles);
    }

    @Test
    public void replaceWithHEADRevisionNullCheck() {
        Collection<ChangedFile> changedFiles = getChangedFiles(); // correct files
        changedFiles.addAll(Arrays.asList(null, null, null));

        _gitService.replaceWithHEADRevision(changedFiles); // we check that we won't get NPE
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStashNullList() {
        _gitService.createStash(null, "test", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStashEmptyList() {
        _gitService.createStash(new ArrayList<>(), "test", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStashNullStashMessageList() {
        _gitService.createStash(getProjects(), null, true);
    }

    @Test
    public void createStashFailedResultForOneProject() {
       Project clonedProject = getClonedProject();
       List<Project> projects = Arrays.asList(clonedProject);
       String stashMessage = "test message";
       when(_jGit.stashCreate(clonedProject, stashMessage, true)).thenReturn(false);

       Map<Project, Boolean> result = _gitService.createStash(projects, stashMessage, true);

       assertFalse(result.get(clonedProject));
    }

    @Test
    public void createStashFailedResultForSomeProject() {
       Project firstProject = getClonedProject();
       Project secondProject = new Project();
       List<Project> projects = Arrays.asList(firstProject, secondProject, null);
       String stashMessage = "test message";
       when(_jGit.stashCreate(any(Project.class), eq(stashMessage), eq(true))).thenReturn(false);

       Map<Project, Boolean> result = _gitService.createStash(projects, stashMessage, true);

       assertFalse(result.get(firstProject));
       assertFalse(result.get(secondProject));
       assertEquals(result.size(), projects.size()-1);
    }

    @Test
    public void createStashSuccessffulResultForOneProject() {
       Project clonedProject = getClonedProject();
       List<Project> projects = Arrays.asList(clonedProject);
       String stashMessage = "test message";
       when(_jGit.stashCreate(clonedProject, stashMessage, true)).thenReturn(true);

       Map<Project, Boolean> result = _gitService.createStash(projects, stashMessage, true);

       assertTrue(result.get(clonedProject));
    }

    @Test
    public void createStashSuccessffulResultForSomeProject() {
       List<Project> projects = getProjects();
       when(_jGit.stashCreate(any(Project.class), any(String.class), eq(true))).thenReturn(true);

       Map<Project, Boolean> result = _gitService.createStash(projects, "test message", true);

       assertEquals(projects.size()-1, result.size());
       assertEquals(getCountSuccessForList(projects), getCountSuccessForMap(result));
    }

    @Test
    public void getStashListNullList() {
        assertTrue(_gitService.getStashList(null).isEmpty());
    }

    @Test
    public void getStashListEmptyList() {
        assertTrue(_gitService.getStashList(new ArrayList<>()).isEmpty());
    }

    @Test
    public void getStashListFailedResult() {
        List<Project> projects = getProjects();
        when(_jGit.getStashes(any(Project.class))).thenReturn(new ArrayList<>());

        List<Stash> result = _gitService.getStashList(projects);

        assertTrue(result.isEmpty());
    }

    @Test
    public void getStashListSuccessffulResult() {
        List<Project> projects = getProjects();
        List<SingleProjectStash> stashes = getStashesWithoutGroup();
        when(_jGit.getStashes(any(Project.class))).thenReturn(stashes);

        List<Stash> result = _gitService.getStashList(projects);

        assertFalse(result.isEmpty());
    }

    @Test
    public void applyStashNullStashItem() {
        StashApplyListener progressListener = new StashApplyListener();

        _gitService.applyStashes(null, progressListener);

        Assert.assertFalse(progressListener.isSuccessfully());
    }

    @Test
    public void applyStashFailedResult() {
        StashApplyListener progressListener = new StashApplyListener();
        Mockito.doNothing().when(_jGit);

        _gitService.applyStashes(new SingleProjectStash("test", "test", getClonedProject()), progressListener);

        assertFalse(progressListener.isSuccessfully());
    }

    @Test
    public void applyStashSucccessffulResult() {
        StashApplyListener progressListener = new StashApplyListener();
        GroupStash groupStash = new GroupStash("test message");
        groupStash.addStash(new SingleProjectStash("test1", "test message", getClonedProject()));
        groupStash.addStash(new SingleProjectStash("test2", "test message", getClonedProject("new path")));
        groupStash.addStash(null);
        groupStash.addStash(new SingleProjectStash(null, null, null));

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation){
                Object[] args = invocation.getArguments();
                if (args[1] instanceof StashApplyListener) {
                    StashApplyListener listener = (StashApplyListener) args[1];
                    listener.onSuccess();
                }
                return null;
             }
         }).when(_jGit).stashApply(any(SingleProjectStash.class), eq(progressListener));

        _gitService.applyStashes(groupStash, progressListener);

         assertTrue(progressListener.isSuccessfully());
    }


    /*********************************************************************************************************/

    class StashApplyListener implements ProgressListener {

        boolean resultOperation = false;

        @Override
        public void onSuccess(Object... t) {
            resultOperation = true;
        }

        @Override
        public void onStart(Object... t) {}

        @Override
        public void onFinish(Object... t) {}

        @Override
        public void onError(Object... t) {}

        public boolean isSuccessfully() {
            return resultOperation;
        }
    }

    private int getCountSuccessForMap(Map<Project, Boolean> result) {
        return (int) result.entrySet().stream().filter(entry -> entry.getValue()).count();
    }

    private int getCountSuccessForList(List<Project> result) {
        return (int) result.stream().filter(project -> project != null && project.isCloned()).count();
    }

    private List<SingleProjectStash> getStashesWithoutGroup() {
        SingleProjectStash firstStash = new SingleProjectStash("first", "[GS0112181412] test", getClonedProject());
        SingleProjectStash secondStash = new SingleProjectStash("second", "[GS0112181412] test", getClonedProject());
        SingleProjectStash thirdStash = new SingleProjectStash("second", "test 3", new Project());
        SingleProjectStash fourthStash = new SingleProjectStash("second", "test 4", getClonedProject("new path 2"));
        return Arrays.asList(firstStash, secondStash, thirdStash, fourthStash);
    }

    private List<Project> getProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(getClonedProject("new path"));
        projects.add(new Project());
        projects.add(getClonedProject());
        projects.add(null);
        return projects;
    }

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

    private Project getClonedProject(String path) {
        Project project = new Project();
        project.setClonedStatus(true);
        project.setProjectStatus(new ProjectStatus());
        project.setPathToClonedProject(path);
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
