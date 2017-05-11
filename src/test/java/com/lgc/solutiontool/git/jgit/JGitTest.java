package com.lgc.solutiontool.git.jgit;

import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.util.JSONParser;


public class JGitTest {

    private final JGit _jGit = JGit.getInstance();
    private static final Project _projectCorrect;
    private static final Project _projectIncorrect = new Project();
    // mocks
    private static JGit _jGitMock;
    private static final Git _gitMock = mock(Git.class);
    private static final GitAPIException _gitExceptionMock = mock(GitAPIException.class);
    private static final Repository _repositoryMock = mock(Repository.class);
    private static final DirCache _dirCachMock = mock(DirCache.class);

    static {
        _projectCorrect = new Project() {
            @Override
            protected boolean checkPath(Path pathToProject) {
                return true;
            };
        };
        _projectCorrect.setPathToClonedProject(".path");
        _projectCorrect.setClonedStatus(true);

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

    @Test(expected = IllegalArgumentException.class)
    public void cloneGroupIncorrectDataExceptionTest() {
        _jGit.clone(null, null, null, null);
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
    public void gitStatusCorrectDataTest() { // TODO
        _jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.ofNullable(_gitMock);
            }
        };
        StatusCommand statusCommandMock = new StatusCommand(_repositoryMock) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                return mock(Status.class);
            }
        };
        Mockito.when(_gitMock.status()).thenReturn(statusCommandMock);

        Assert.assertTrue(_jGitMock.getStatusProject(_projectCorrect).isPresent());
    }

    @Test
    public void gitStatusIncorrectDataTest() {
        _jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.ofNullable(_gitMock);
            }
        };

        StatusCommand statusCommandMock = new StatusCommand(_repositoryMock) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                return null;
            }
        };
        Mockito.when(_gitMock.status()).thenReturn(statusCommandMock);

        Assert.assertFalse(_jGitMock.getStatusProject(null).isPresent());
        Assert.assertFalse(_jGitMock.getStatusProject(_projectIncorrect).isPresent());
        Assert.assertFalse(_jGitMock.getStatusProject(_projectCorrect).isPresent());

        statusCommandMock = new StatusCommand(_repositoryMock) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(_gitMock.status()).thenReturn(statusCommandMock);
        Assert.assertFalse(_jGitMock.getStatusProject(_projectCorrect).isPresent());
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
        Assert.assertTrue(_jGitMock.addUntrackedFileForCommit(files, _projectCorrect));
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

        _jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.empty();
            }
        };
        Assert.assertFalse(_jGitMock.addUntrackedFileForCommit(new ArrayList<>(), _projectCorrect));

        _jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.of(_gitMock);
            }
        };
        List<String> files = new ArrayList<>();
        files.add("0");
        files.add(null);
        Assert.assertTrue(_jGitMock.addUntrackedFileForCommit(files, _projectCorrect));
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

        _jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.empty();
            }
        };
        Assert.assertEquals(_jGitMock.pull(_projectCorrect), JGitStatus.FAILED);

        _jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.of(_gitMock);
            }

            @Override
            protected boolean isContinueMakePull(Project project) {
                return false;
            }
        };
        Assert.assertEquals(_jGitMock.pull(_projectCorrect), JGitStatus.FAILED);

        _jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.of(_gitMock);
            }

            @Override
            protected boolean isContinueMakePull(Project project) {
                return true;
            }
        };

        PullCommand pullCommandMock =  new PullCommand(_repositoryMock) {
            @Override
            public PullResult call() throws GitAPIException, WrongRepositoryStateException,
                    InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException,
                    RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException {
                throw _gitExceptionMock;
            }
        };
        Mockito.when(_gitMock.pull()).thenReturn(pullCommandMock);
        Assert.assertEquals(_jGitMock.pull(_projectCorrect), JGitStatus.FAILED);
    }

    @Test
    public void pullCorrectDataTest() {
        _jGitMock = new JGit() {
            @Override
            protected Optional<Git> getGitForRepository(String path) {
                return Optional.of(_gitMock);
            }

            @Override
            protected boolean isContinueMakePull(Project project) {
                return true;
            }
        };

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
        Assert.assertEquals(_jGitMock.pull(_projectCorrect), JGitStatus.FAST_FORWARD);

    }

    @Test(expected=IllegalArgumentException.class)
    public void commitGroupIsNullTest() {
        _jGit.commit(null, "__", false, null, null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void commitMessageIsNullTest() {
        _jGit.commit(new Group(), null, false, null, null, null, null, null, null);
    }
}
