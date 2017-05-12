package com.lgc.solutiontool.git.jgit;

import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.util.JSONParser;

/**
 * Tests for the JGit class.
 *
 * @author Lyudmila Lyska
 */
public class JGitTest {

    private final JGit _jGit = JGit.getInstance();
    private static final Project _projectCorrect;
    private static final Project _projectIncorrect = new Project();
    private static final List<Project> _listProjects;
    // mocks
    private static final JGit _correctJGitMock;
    private static final JGit _emptyJGitMock;

    private static final Git _gitMock = mock(Git.class);
    private static final GitAPIException _gitExceptionMock = mock(GitAPIException.class);
    private static final Repository _repositoryMock = mock(Repository.class);
    private static final DirCache _dirCachMock = mock(DirCache.class);

    private static final String NAME_BRANCH = "test_name";

    static {
        _projectCorrect = new Project() {
            @Override
            protected boolean checkPath(Path pathToProject) {
                return true;
            };
        };
        _projectCorrect.setPathToClonedProject(".path");
        _projectCorrect.setClonedStatus(true);

        _listProjects = new ArrayList<>();
        _listProjects.add(_projectCorrect);
        _listProjects.add(null);
        _listProjects.add(_projectCorrect);
        _listProjects.add(new Project());

        _correctJGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.ofNullable(_gitMock);
            }

            @Override
            protected boolean isContinueMakePull(Project project) {
                return true;
            }

            @Override
            protected User getUserData() {
                User user = new User("Lyudmila", "ld@email.com");
                return user;
            }
        };
        _emptyJGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.empty();
            }
        };
    }

    private final String CORRECT_PATH = "/path";
    private final Group _groupWithProjects = JSONParser.parseToObject("{\"id\":1468081,\"name\":\"STG\","
            + "\"projects\":[{\"id\":3010543, \"name\":\"stg\", \"description\":\"\",\"default_branch\":\"master\",\"public\":false,"
            + "\"visibility_level\":0,\"ssh_url_to_repo\":\"git@gitlab.com:SolutionToolGitLab/stg.git\","
            + "\"http_url_to_repo\":\"https://gitlab.com/SolutionToolGitLab/stg.git\","
            + "\"web_url\":\"https://gitlab.com/SolutionToolGitLab/stg\"}]}", Group.class);

    @Test(expected = IllegalArgumentException.class)
    public void cloneGroupIncorrectDataExceptionGroupTest() {
        _jGit.clone(null, CORRECT_PATH, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cloneGroupIncorrectDataExceptionPathTest() {
        _jGit.clone(new Group(), null, null, null);
    }

    @Test
    public void cloneGroupIncorrectDataExceptionTest() {
        Group group = new Group();
        group.setClonedStatus(true);
        Assert.assertFalse(_jGit.clone(group, ".", null, null));
    }

    @Test
    public void cloneGroupProjectsIsNullTest() {
        Group group = new Group();
        //projects is null, the clone method return false
        Assert.assertFalse(_jGit.clone(group, CORRECT_PATH, null, null));
    }

    @Test
    public void cloneGroupProjectsIsEmptyTest() {
        Group group = JSONParser.parseToObject("{\"id\":1468081,\"name\":\"STG\", \"projects\":[]}", Group.class);

        // projects is empty, the clone method return false
        Assert.assertFalse(_jGit.clone(group, CORRECT_PATH, null, null));
        Assert.assertFalse(_jGit.clone(group, CORRECT_PATH, (progress, project) -> {}, (progress, message) -> {}));
    }

    @Test
    public void gitcloneRepositoryCorrectDataTest() {
        JGit git = new JGit() {
            @Override
            protected boolean cloneRepository(String linkClone, String localPath)
                    throws InvalidRemoteException, TransportException, GitAPIException {
                return true;
            }
        };
        Assert.assertTrue(
                git.clone(_groupWithProjects, CORRECT_PATH, (progress, project) -> {}, (progress, message) -> {}));
    }

    @Test
    public void gitcloneRepositoryIncorrectDataTest() {
        JGit git = new JGit() {
            @Override
            protected boolean cloneRepository(String linkClone, String localPath) throws GitAPIException {
                throw _gitExceptionMock;
            }
        };
        Assert.assertTrue(
                git.clone(_groupWithProjects, CORRECT_PATH, (progress, project) -> {}, (progress, message) -> {}));
    }

    @Test
    public void gitStatusCorrectDataTest() {
        StatusCommand statusCommandMock = new StatusCommand(_repositoryMock) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                return mock(Status.class);
            }
        };
        Mockito.when(_gitMock.status()).thenReturn(statusCommandMock);

        Assert.assertTrue(_correctJGitMock.getStatusProject(_projectCorrect).isPresent());
    }

    @Test
    public void gitStatusIncorrectDataTest() {
        StatusCommand statusCommandMock = new StatusCommand(_repositoryMock) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                return null;
            }
        };
        Mockito.when(_gitMock.status()).thenReturn(statusCommandMock);
        Assert.assertFalse(_correctJGitMock.getStatusProject(null).isPresent());
        Assert.assertFalse(_correctJGitMock.getStatusProject(_projectIncorrect).isPresent());
        Assert.assertFalse(_correctJGitMock.getStatusProject(_projectCorrect).isPresent());

        statusCommandMock = new StatusCommand(_repositoryMock) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(_gitMock.status()).thenReturn(statusCommandMock);
        Assert.assertFalse(_correctJGitMock.getStatusProject(_projectCorrect).isPresent());

        _projectIncorrect.setClonedStatus(true);
        Assert.assertFalse(_correctJGitMock.getStatusProject(_projectIncorrect).isPresent());
        _projectIncorrect.setClonedStatus(false);
    }

    @Test
    public void addUntrackedFileForCommitCorrectDataTest() {
        AddCommand addCommandMock = new AddCommand(_repositoryMock) {
            @Override
            public DirCache call() throws GitAPIException {
                return _dirCachMock;
            }
        };
        Mockito.when(_gitMock.add()).thenReturn(addCommandMock);

        List<String> files = new ArrayList<>();
        files.add("0");
        files.add(null);
        Assert.assertTrue(_correctJGitMock.addUntrackedFileForCommit(files, _projectCorrect));
    }

    @Test
    public void addUntrackedFileForCommitIncorrectDataTest() {
        AddCommand addCommandMock = new AddCommand(_repositoryMock) {
            @Override
            public DirCache call() throws GitAPIException, NoFilepatternException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(_gitMock.add()).thenReturn(addCommandMock);
        Assert.assertTrue(_emptyJGitMock.addUntrackedFileForCommit(new ArrayList<>(), _projectCorrect));

        List<String> files = new ArrayList<>();
        files.add("0");
        files.add(null);
        Assert.assertTrue(_correctJGitMock.addUntrackedFileForCommit(files, _projectCorrect));
    }

    @Test(expected=IllegalArgumentException.class)
    public void addUntrackedFileForCommitProjectIsNullTest() {
        _jGit.addUntrackedFileForCommit(null, _projectCorrect);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addUntrackedFileForCommitCollectionIsNullTest() {
        _jGit.addUntrackedFileForCommit(new ArrayList<>(), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void pullProjectIsNullTest() {
        _jGit.pull(null);
    }

    @Test
    public void pullIncorrectDataTest() {
        Assert.assertEquals(_jGit.pull(_projectIncorrect), JGitStatus.FAILED);
        Assert.assertEquals(_emptyJGitMock.pull(_projectCorrect), JGitStatus.FAILED);

        JGit jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.of(_gitMock);
            }

            @Override
            protected boolean isContinueMakePull(Project project) {
                return false;
            }
        };
        Assert.assertEquals(jGitMock.pull(_projectCorrect), JGitStatus.FAILED);

        PullCommand pullCommandMock =  new PullCommand(_repositoryMock) {
            @Override
            public PullResult call() throws GitAPIException, WrongRepositoryStateException,
                    InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException,
                    RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(_gitMock.pull()).thenReturn(pullCommandMock);
        Assert.assertEquals(_correctJGitMock.pull(_projectCorrect), JGitStatus.FAILED);
    }

    @Test
    public void pullCorrectDataTest() {
        PullResult pullResultMock = mock(PullResult.class);
        PullCommand pullCommandMock =  new PullCommand(_repositoryMock) {
            @Override
            public PullResult call() throws GitAPIException, WrongRepositoryStateException,
                    InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException,
                    RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException {
                return pullResultMock;
            }
        };
        Mockito.when(_gitMock.pull()).thenReturn(pullCommandMock);

        MergeResult mergeMock = new MergeResult(new ArrayList<>()) {
            @Override
            public MergeStatus getMergeStatus() {
                return MergeStatus.FAST_FORWARD;
            }
        };
        Mockito.when(pullResultMock.getMergeResult()).thenReturn(mergeMock);
        Assert.assertEquals(_correctJGitMock.pull(_projectCorrect), JGitStatus.FAST_FORWARD);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitMessageIsNullTest() {
        _jGit.commit(_listProjects, null, false, null, null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitMessageIsEmptyTest() {
        _jGit.commit(_listProjects, "", false, null, null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitProjectsIsEmptyTest() {
        _jGit.commit(new ArrayList<>(), "__", false, null, null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitProjectsIsNullTest() {
        _jGit.commit(null, "__", false, null, null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitProjectIsNullTest() {
        _jGit.commitProject(null, "_", false, null, null, null, null);
    }

    @Test
    public void commitProjectIncorrectDataTest() {
        JGitStatus result = _emptyJGitMock.commitProject(_projectCorrect, "_", false, null, null, null, null);
        Assert.assertEquals(result, JGitStatus.FAILED);

        CommitCommand commitCommand = new CommitCommand(_repositoryMock) {
            @Override
            public RevCommit call() throws GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
                    ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(_gitMock.commit()).thenReturn(commitCommand);

        result = _correctJGitMock.commitProject(_projectCorrect, "_", false, null, null, null, null);
        Assert.assertEquals(result, JGitStatus.FAILED);
    }

    @Test
    public void commitProjectCorrectDataTest() {
        CommitCommand commitCommand = new CommitCommand(_repositoryMock) {
            @Override
            public RevCommit call() throws GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
                    ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
                return mock(RevCommit.class);
            }
        };
        Mockito.when(_gitMock.commit()).thenReturn(commitCommand);
        JGitStatus result = _correctJGitMock.commitProject(
                _projectCorrect, "_", false, "Lyuda", "l@gmail.com", "Lyuda", "l@gmail.com");
        Assert.assertEquals(result, JGitStatus.SUCCESSFUL);
    }

    @Test
    public void commitAllProjectsCorrectDataTest() {
        CommitCommand commitCommand = new CommitCommand(_repositoryMock) {
            @Override
            public RevCommit call() throws GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
                    ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
                return mock(RevCommit.class);
            }
        };
        Mockito.when(_gitMock.commit()).thenReturn(commitCommand);
        JGitStatus result = _correctJGitMock.commit(
                _listProjects, "_", false, "Lyuda", "l@gmail.com", "Lyuda", "l@gmail.com", null, null);
        Assert.assertEquals(result, JGitStatus.SUCCESSFUL);
    }

    @Test
    public void commitAllProjectsIncorrectDataTest() {
        JGitStatus result = _emptyJGitMock.commit(_listProjects, "_", false, null, null, null, null, null, null);
        Assert.assertEquals(result, JGitStatus.SUCCESSFUL);
    }

    @Test(expected=IllegalArgumentException.class)
    public void pushProjectsIsNullTest() {
        _jGit.push(null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void pushProjectsIsEmptyTest() {
        _jGit.push(new ArrayList<>(), null, null);
    }

    @Test
    public void pushIncorrectDataTest() {
        Assert.assertTrue(_emptyJGitMock.push(_listProjects, null, null));

        PushCommand pushCommandMock = new PushCommand(_repositoryMock) {
            @Override
            public Iterable<PushResult> call() throws GitAPIException, InvalidRemoteException, TransportException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(_gitMock.push()).thenReturn(pushCommandMock);
        Assert.assertTrue(_correctJGitMock.push(_listProjects, null, null));
    }

    @Test
    public void pushCorrectDataTest() {
        PushCommand pushCommandMock = new PushCommand(_repositoryMock) {
            @Override
            public Iterable<PushResult> call() throws GitAPIException, InvalidRemoteException, TransportException {
                return Arrays.asList(mock(PushResult.class));
            }
        };
        Mockito.when(_gitMock.push()).thenReturn(pushCommandMock);
        Assert.assertTrue(_correctJGitMock.push(_listProjects, (progress) -> {}, (progress, message) -> {}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitAndPushProjectsIsNullTest() {
        _jGit.commitAndPush(null, "_", false, null, null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitAndPushMessageIsNullTest() {
        _jGit.commitAndPush(_listProjects, null, false, null, null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitAndPushProjectsIsEmptyTest() {
        _jGit.commitAndPush(new ArrayList<>(), "_", false, null, null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitAndPushMessageIsEmptyTest() {
        _jGit.commitAndPush(_listProjects, "", false, null, null, null, null, null, null);
    }

    @Test
    public void commitAndPushIncorrectDataTest() {
        Assert.assertTrue(_emptyJGitMock.commitAndPush(
                _listProjects, "__", false, null, null, null, null, null, null));
    }

    @Test
    public void commitAndPushCorrectDataTest() {
        CommitCommand commitCommand = new CommitCommand(_repositoryMock) {
            @Override
            public RevCommit call() throws GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
                    ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
                return mock(RevCommit.class);
            }
        };
        Mockito.when(_gitMock.commit()).thenReturn(commitCommand);

        PushCommand pushCommandMock = new PushCommand(_repositoryMock) {
            @Override
            public Iterable<PushResult> call() throws GitAPIException, InvalidRemoteException, TransportException {
                return Arrays.asList(mock(PushResult.class));
            }
        };
        Mockito.when(_gitMock.push()).thenReturn(pushCommandMock);
        Assert.assertTrue(_correctJGitMock.commitAndPush(_listProjects, "__", false,
                "Lyuda", "l@gmail.com", "Lyuda", "l@gmail.com", (progress) -> {}, (progress, message) -> {}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void createBranchProjectsIsNullTest() {
        _jGit.createBranch(null, "__", false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createBranchNameBranchIsNullTest() {
        _jGit.createBranch(new Project(), null, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createBranchNameBranchIsEmptyTest() {
        _jGit.createBranch(new Project(), "", false);
    }

    @Test
    public void createBranchIncorrectDataTest() {
        Assert.assertEquals(_jGit.createBranch(new Project(), "__", false), JGitStatus.FAILED);
        Assert.assertEquals(_emptyJGitMock.createBranch(_projectCorrect, "__", false), JGitStatus.FAILED);

        ListBranchCommand listCommandMock = new ListBranchCommand(_repositoryMock) {
            @Override
            public List<Ref> call() throws GitAPIException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(_gitMock.branchList()).thenReturn(listCommandMock);
        Assert.assertEquals(_correctJGitMock.createBranch(_projectCorrect, NAME_BRANCH, false), JGitStatus.FAILED);

        Ref refMock = mock(Ref.class);
        listCommandMock = new ListBranchCommand(_repositoryMock) {
            @Override
            public List<Ref> call() throws GitAPIException {
                List<Ref> refs = new ArrayList<>();
                refs.add(refMock);
                return refs;
            }
        };
        Mockito.when(refMock.getName()).thenReturn(Constants.R_REMOTES+NAME_BRANCH);
        Mockito.when(_gitMock.branchList()).thenReturn(listCommandMock);
        Assert.assertEquals(_correctJGitMock.createBranch(_projectCorrect, NAME_BRANCH, false), JGitStatus.FAILED);

        CreateBranchCommand createBranchCommandMock = new CreateBranchCommand(_repositoryMock) {
            @Override
            public Ref call()
                    throws GitAPIException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(refMock.toString()).thenReturn(Constants.R_HEADS);
        Mockito.when(refMock.getName()).thenReturn(Constants.R_HEADS+"Test");
        Mockito.when(_gitMock.branchCreate()).thenReturn(createBranchCommandMock);
        Assert.assertEquals(_correctJGitMock.createBranch(_projectCorrect, NAME_BRANCH, false), JGitStatus.FAILED);
    }

    @Test
    public void createBranchCorrectDataTest() {
        Ref refMock = mock(Ref.class);
        ListBranchCommand listCommandMock = new ListBranchCommand(_repositoryMock) {
            @Override
            public List<Ref> call() throws GitAPIException {
                List<Ref> refs = new ArrayList<>();
                refs.add(refMock);
                return refs;
            }
        };
        Mockito.when(_gitMock.branchList()).thenReturn(listCommandMock);
        CreateBranchCommand createBranchCommandMock = new CreateBranchCommand(_repositoryMock) {
            @Override
            public Ref call()
                    throws GitAPIException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException {
                return refMock;
            }
        };
        Mockito.when(refMock.toString()).thenReturn(Constants.R_HEADS);
        Mockito.when(refMock.getName()).thenReturn(Constants.R_HEADS+"Test");
        Mockito.when(_gitMock.branchCreate()).thenReturn(createBranchCommandMock);
        Assert.assertEquals(_correctJGitMock.createBranch(_projectCorrect, NAME_BRANCH, true), JGitStatus.SUCCESSFUL);
    }
}
