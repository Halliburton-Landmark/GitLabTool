package com.lgc.gitlabtool.git.jgit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.StashApplyCommand;
import org.eclipse.jgit.api.StashCreateCommand;
import org.eclipse.jgit.api.StashDropCommand;
import org.eclipse.jgit.api.StashListCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.attributes.AttributesNodeProvider;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.BaseRepositoryBuilder;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.ReflogReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.User;
import com.lgc.gitlabtool.git.jgit.stash.SingleProjectStash;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.BackgroundService;
import com.lgc.gitlabtool.git.services.BackgroundServiceImpl;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.ProgressDialog;

/**
 * Tests for the JGit class.
 *
 * @author Lyudmila Lyska
 */
public class JGitTest {

    private static final String NAME_BRANCH = "test_name";
    private static final String NAME_TRACKING_BRANCH = "test_tracking_branch";
    private static final String CORRECT_PATH = "/path";
    private static final String fileName = "test";

    @Test(expected = IllegalArgumentException.class)
    public void cloneGroupIncorrectDataExceptionGroupTest() {
        getJGitMock(null).clone(null, CORRECT_PATH, getEmptyOperationProgressListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cloneGroupIncorrectDataExceptionPathTest() {
        getJGitMock(null).clone(new ArrayList<>(), null, getEmptyOperationProgressListener());
    }


    @Test
    public void gitcloneRepositoryCorrectDataTest() {
        Repository repo = getRepo("_");
        Git gitMock = new Git (getRepository()) {
            @Override
            public Repository getRepository() {
                return repo;
            }
            @Override
            public void close() {
                //Do nothing
            }
        };
        JGit jgit = new JGit(getBackgroundServiceMock()) {
            @Override
            protected Git tryClone(String linkClone, String localPath)
                    throws InvalidRemoteException, TransportException, GitAPIException {
                return gitMock;
            }
        };

        Assert.assertTrue(jgit.clone(getCorrectProject(2), CORRECT_PATH, getEmptyOperationProgressListener()));
    }

    @Test
    public void gitcloneRepositoryCancelExceptionTest() {
        JGit git = new JGit(getBackgroundServiceMock()) {
            @Override
            protected Git tryClone(String linkClone, String localPath) throws JGitInternalException {
                JGitInternalException cancelException = mock(JGitInternalException.class);
                throw cancelException;
            }
        };
        Assert.assertTrue(git.clone(getProjects(2), CORRECT_PATH, getEmptyOperationProgressListener()));

        git = new JGit(getBackgroundServiceMock()) {
            @Override
            protected Git tryClone(String linkClone, String localPath) throws GitAPIException {
                throw getGitAPIException();
            }
        };
        Assert.assertTrue(git.clone(getProjects(2), CORRECT_PATH, getEmptyOperationProgressListener()));
    }

    @Test
    public void gitStatusCorrectDataTest() {
        Git gitMock = getGitMock();
        StatusCommand statusCommandMock = new StatusCommand(getRepository()) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                return mock(Status.class);
            }
        };
        when(gitMock.status()).thenReturn(statusCommandMock);

        Assert.assertTrue(getJGitMock(gitMock).getStatusProject(getProject(true)).isPresent());
    }

    @Test
    public void gitStatusIncorrectDataTest() {
        StatusCommand statusCommandMock = new StatusCommand(getRepository()) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                return null;
            }
        };
        Git gitMock = getGitMock();
        when(gitMock.status()).thenReturn(statusCommandMock);
        Assert.assertFalse(getJGitMock(gitMock).getStatusProject(null).isPresent());
        Assert.assertFalse(getJGitMock(gitMock).getStatusProject(getProject(false)).isPresent());
        Assert.assertFalse(getJGitMock(gitMock).getStatusProject(getProject(true)).isPresent());

        statusCommandMock = new StatusCommand(getRepository()) {
            @Override
            public Status call() throws GitAPIException, NoWorkTreeException {
                throw getGitAPIException();
            }
        };
        when(gitMock.status()).thenReturn(statusCommandMock);
        Assert.assertFalse(getJGitMock(gitMock).getStatusProject(getProject(true)).isPresent());

        getProject(false).setClonedStatus(true);
        Assert.assertFalse(getJGitMock(gitMock).getStatusProject(getProject(false)).isPresent());
        getProject(false).setClonedStatus(false);
    }

    @Test
    public void addUntrackedFileForCommitCorrectDataTest() {
        Git gitMock = getGitMock();
        AddCommand addCommandMock = new AddCommand(getRepository()) {
            @Override
            public DirCache call() throws GitAPIException {
                return getDirCache();
            }
        };
        when(gitMock.add()).thenReturn(addCommandMock);

        List<String> files = new ArrayList<>();
        files.add("0");
        files.add(null);

        List<String> addedFiles = getJGitMock(gitMock).addUntrackedFilesToIndex(files, getProject(true));
        Assert.assertFalse(addedFiles.isEmpty());
        Assert.assertEquals(files.get(0), addedFiles.get(0));
    }

    @Test
    public void addUntrackedFileToIndexIncorrectTest() throws NoFilepatternException, GitAPIException {
        Git gitMock = getGitMock();
        AddCommand addCommandMock = mock(AddCommand.class);
        when(addCommandMock.addFilepattern(fileName)).thenReturn(addCommandMock);
        when(addCommandMock.call()).thenThrow(getGitAPIException());
        when(gitMock.add()).thenReturn(addCommandMock);

        Assert.assertFalse(getJGitMock(null).addUntrackedFileToIndex(fileName, getProject(true)));
        Assert.assertFalse(getJGitMock(gitMock).addUntrackedFileToIndex(fileName, getProject(true)));
    }

    @Test
    public void addUntrackedFileToIndexCorrectTest() throws NoFilepatternException, GitAPIException {
        Git gitMock = getGitMock();
        AddCommand addCommandMock = mock(AddCommand.class);
        when(addCommandMock.addFilepattern(fileName)).thenReturn(addCommandMock);
        when(addCommandMock.call()).thenReturn(getDirCache());
        when(gitMock.add()).thenReturn(addCommandMock);

        Assert.assertTrue(getJGitMock(gitMock).addUntrackedFileToIndex(fileName, getProject(true)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addUntrackedFileToIndexFileIncorrectException() {
        getJGitMock(null).addUntrackedFileToIndex(null, getProject(true));
    }
    @Test(expected = IllegalArgumentException.class)
    public void addUntrackedFileToIndexProjectIncorrectException() {
        getJGitMock(null).addUntrackedFileToIndex("", null);
    }

    @Test
    public void addUntrackedFileForCommitIncorrectDataTest() {
        Git gitMock = getGitMock();
        AddCommand addCommandMock = new AddCommand(getRepository()) {
            @Override
            public DirCache call() throws GitAPIException, NoFilepatternException {
                throw getGitAPIException();
            }
        };
        when(gitMock.add()).thenReturn(addCommandMock);
        Assert.assertTrue(getJGitMock(gitMock).addUntrackedFilesToIndex(new ArrayList<>(), getProject(true)).isEmpty());
        Assert.assertTrue(getJGitMock(null).addUntrackedFilesToIndex(new ArrayList<>(), getProject(false)).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addUntrackedFileForCommitProjectIsNullTest() {
        getJGitMock(null).addUntrackedFileToIndex(null, getProject(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addUntrackedFileForCommitCollectionIsNullTest() {
        getJGitMock(null).addUntrackedFilesToIndex(new ArrayList<>(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pullProjectIsNullTest() {
        getJGitMock(null).pull(null);
    }

    @Test
    public void pullIncorrectDataTest() {
        Assert.assertEquals(getJGitMock(null).pull(getProject(false)), JGitStatus.FAILED);
        Assert.assertEquals(getJGitMock(null).pull(getProject(true)), JGitStatus.FAILED);

        Git gitMock = getGitMock();
        JGit jGitMock = new JGit(mock(BackgroundServiceImpl.class));
        Assert.assertEquals(jGitMock.pull(getProject(true)), JGitStatus.FAILED);

        PullCommand pullCommandMock = new PullCommand(getRepository()) {
            @Override
            public PullResult call() throws GitAPIException, WrongRepositoryStateException,
                    InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException,
                    RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException {
                throw getGitAPIException();
            }
        };
        when(gitMock.pull()).thenReturn(pullCommandMock);
        Assert.assertEquals(getJGitMock(gitMock).pull(getProject(true)), JGitStatus.FAILED);
    }

    @Test
    public void pullCorrectDataTest() {
        Git gitMock = getGitMock();
        PullResult pullResultMock = mock(PullResult.class);
        PullCommand pullCommandMock = new PullCommand(getRepository()) {
            @Override
            public PullResult call() throws GitAPIException, WrongRepositoryStateException,
                    InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException,
                    RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException {
                return pullResultMock;
            }
        };
        when(gitMock.pull()).thenReturn(pullCommandMock);

        MergeResult mergeMock = new MergeResult(new ArrayList<>()) {
            @Override
            public MergeStatus getMergeStatus() {
                return MergeStatus.FAST_FORWARD;
            }
        };
        when(pullResultMock.getMergeResult()).thenReturn(mergeMock);
        Assert.assertEquals(getJGitMock(gitMock).pull(getProject(true)), JGitStatus.FAST_FORWARD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitMessageIsNullTest() {
        getJGitMock(null).commit(getProjects(), null, false, null, null, null, null, new EmptyListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitMessageIsEmptyTest() {
        getJGitMock(null).commit(getProjects(), "", false, null, null, null, null, new EmptyListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitProjectsIsEmptyTest() {
        getJGitMock(null).commit(new ArrayList<>(), "__", false, null, null, null, null, new EmptyListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitProjectsIsNullTest() {
        getJGitMock(null).commit(null, "__", false, null, null, null, null, new EmptyListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitProjectIsNullTest() {
        getJGitMock(null).commitProject(null, "_", false, null, null, null, null);
    }

    @Test
    public void commitProjectIncorrectDataTest() {
        JGitStatus result = getJGitMock(null).commitProject(getProject(true), "_", false, null, null, null, null);
        Assert.assertEquals(result, JGitStatus.FAILED);

        Git gitMock = getGitMock();
        CommitCommand commitCommand = new CommitCommand(getRepository()) {
            @Override
            public RevCommit call() throws GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
                    ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
                throw getGitAPIException();
            }
        };
        when(gitMock.commit()).thenReturn(commitCommand);

        result = getJGitMock(gitMock).commitProject(getProject(true), "_", false, null, null, null, null);
        Assert.assertEquals(result, JGitStatus.FAILED);
    }

    @Test
    public void commitProjectCorrectDataTest() {
        Git gitMock = getGitMock();
        CommitCommand commitCommand = new CommitCommand(getRepository()) {
            @Override
            public RevCommit call() throws GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
                    ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
                return mock(RevCommit.class);
            }
        };
        when(gitMock.commit()).thenReturn(commitCommand);
        JGitStatus result = getJGitMock(gitMock).commitProject(getProject(true), "_", false, "Lyuda", "l@gmail.com",
                "Lyuda", "l@gmail.com");
        Assert.assertEquals(result, JGitStatus.SUCCESSFUL);
    }

    @Test
    public void commitAllProjectsCorrectDataTest() {
        Git gitMock = getGitMock();
        CommitCommand commitCommand = new CommitCommand(getRepository()) {
            @Override
            public RevCommit call() throws GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
                    ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
                return mock(RevCommit.class);
            }
        };
        when(gitMock.commit()).thenReturn(commitCommand);
        Map<Project, JGitStatus> result = getJGitMock(gitMock).commit(getProjects(), "_", false, "Lyuda", "l@gmail.com", "Lyuda",
                "l@gmail.com", new EmptyListener());

        Assert.assertEquals(getCountCorrectProject(getProjects()), getCountCorrectStatuses(result));
    }

    @Test
    public void commitAllProjectsIncorrectDataTest() {
        Map<Project, JGitStatus> result = getJGitMock(null).commit(getProjects(), "_", false, null, null, null, null, new EmptyListener());
        Assert.assertEquals(result.size(), getCountIncorrectStatuses(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pushProjectsIsNullTest() {
        getJGitMock(null).push(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pushProjectsIsEmptyTest() {
        getJGitMock(null).push(new ArrayList<>(), null);
    }

    @Test
    public void pushIncorrectDataTest() {
        Map<Project, JGitStatus> statuses = getJGitMock(null).push(getProjects(), new EmptyListener());
        Assert.assertEquals(statuses.size(), getCountIncorrectStatuses(statuses));

        Git gitMock = getGitMock();
        PushCommand pushCommandMock = new PushCommand(getRepository()) {
            @Override
            public Iterable<PushResult> call() throws GitAPIException, InvalidRemoteException, TransportException {
                throw getGitAPIException();
            }
        };
        when(gitMock.push()).thenReturn(pushCommandMock);
        Map<Project, JGitStatus> results = getJGitMock(gitMock).push(getProjects(), new EmptyListener());
        Assert.assertEquals(results.size(), getCountIncorrectStatuses(results));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pushDataWithNullListenerTest() {
        getJGitMock(null).push(getProjects(), null);
    }

    @Test
    public void pushCorrectDataTest() {
        Git gitMock = getGitMock();
        PushCommand pushCommandMock = new PushCommand(getRepository()) {
            @Override
            public Iterable<PushResult> call() throws GitAPIException, InvalidRemoteException, TransportException {
                return Arrays.asList(mock(PushResult.class));
            }
        };
        when(gitMock.push()).thenReturn(pushCommandMock);
        Map<Project, JGitStatus> statuses = getJGitMock(gitMock).push(getProjects(),  new EmptyListener());
        Assert.assertEquals(getCountCorrectStatuses(statuses), getCountCorrectProject(getProjects()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitAndPushProjectsIsNullTest() {
        getJGitMock(null).commitAndPush(null, "_", false, null, null, null, null, new EmptyListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitAndPushMessageIsNullTest() {
        getJGitMock(null).commitAndPush(getProjects(), null, false, null, null, null, null, new EmptyListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitAndPushProjectsIsEmptyTest() {
        getJGitMock(null).commitAndPush(new ArrayList<>(), "_", false, null, null, null, null, new EmptyListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void commitAndPushMessageIsEmptyTest() {
        getJGitMock(null).commitAndPush(getProjects(), "", false, null, null, null, null, new EmptyListener());
    }

    @Test
    public void commitAndPushIncorrectDataTest() {
        Map<Project, JGitStatus> result = getJGitMock(null).commitAndPush(getProjects(), "__", false, null, null, null, null, new EmptyListener());
        Assert.assertEquals(result.size(), getCountIncorrectStatuses(result));
    }

    @Test
    public void commitAndPushCorrectDataTest() {
        Git gitMock = getGitMock();
        CommitCommand commitCommand = new CommitCommand(getRepository()) {
            @Override
            public RevCommit call() throws GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
                    ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
                return mock(RevCommit.class);
            }
        };
        when(gitMock.commit()).thenReturn(commitCommand);

        PushCommand pushCommandMock = new PushCommand(getRepository()) {
            @Override
            public Iterable<PushResult> call() throws GitAPIException, InvalidRemoteException, TransportException {
                return Arrays.asList(mock(PushResult.class));
            }
        };
        when(gitMock.push()).thenReturn(pushCommandMock);
        Map<Project, JGitStatus> result = getJGitMock(gitMock).commitAndPush(getProjects(), "__", false, "Lyuda", "l@gmail.com",
                "Lyuda", "l@gmail.com", new EmptyListener());

        Assert.assertEquals(getCountCorrectProject(getProjects()), getCountCorrectStatuses(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBranchProjectsIsNullTest() {
        getJGitMock(null).createBranch(null, "__", null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBranchNameBranchIsNullTest() {
        getJGitMock(null).createBranch(new Project(), null, null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBranchNameBranchIsEmptyTest() {
        getJGitMock(null).createBranch(new Project(), "", null, false);
    }

    @Test
    public void createBranchIncorrectDataTest() {
        Assert.assertEquals(getJGitMock(null).createBranch(new Project(), "__", null, false), JGitStatus.FAILED);
        Assert.assertEquals(getJGitMock(null).createBranch(getProject(true), "__", null, false), JGitStatus.FAILED);

        Git gitMock = getGitMock();
        ListBranchCommand listCommandMock = new ListBranchCommand(getRepository()) {
            @Override
            public List<Ref> call() throws GitAPIException {
                throw getGitAPIException();
            }

        };
        when(gitMock.branchList()).thenReturn(listCommandMock);
        Assert.assertEquals(getJGitMock(gitMock).createBranch(getProject(true), NAME_BRANCH, null, false), JGitStatus.FAILED);

        Ref refMock = mock(Ref.class);
        listCommandMock = getListCommandMock(refMock);
        when(refMock.getName()).thenReturn(Constants.R_REMOTES + NAME_BRANCH);
        when(gitMock.branchList()).thenReturn(listCommandMock);
        System.err.println("NAME MY BRANCH " + refMock.getName());
        Assert.assertEquals(getJGitMock(gitMock).createBranch(getProject(true), NAME_BRANCH, null, false),
                JGitStatus.BRANCH_ALREADY_EXISTS);

        CreateBranchCommand createBranchCommandMock = new CreateBranchCommand(getRepository()) {
            @Override
            public Ref call()
                    throws GitAPIException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException {
                throw getGitAPIException();
            }
        };
        when(refMock.toString()).thenReturn(Constants.R_HEADS);
        when(refMock.getName()).thenReturn(Constants.R_HEADS + "Test");
        when(gitMock.branchCreate()).thenReturn(createBranchCommandMock);
        Assert.assertEquals(getJGitMock(gitMock).createBranch(getProject(true), NAME_BRANCH, null, false), JGitStatus.FAILED);
    }

    @Test
    public void createBranchCorrectDataTest() {
        Ref refMock = mock(Ref.class);
        Git gitMock = getGitMock();
        ListBranchCommand listCommandMock = getListCommandMock(refMock);
        when(gitMock.branchList()).thenReturn(listCommandMock);
        CreateBranchCommand createBranchCommandMock = new CreateBranchCommand(getRepository()) {
            @Override
            public Ref call()
                    throws GitAPIException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException {
                return refMock;
            }
        };
        when(refMock.toString()).thenReturn(Constants.R_HEADS);
        when(refMock.getName()).thenReturn(Constants.R_HEADS + "Test");
        when(gitMock.branchCreate()).thenReturn(createBranchCommandMock);
        Assert.assertEquals(getJGitMock(gitMock).createBranch(getProject(true), NAME_BRANCH, null, true),
                JGitStatus.SUCCESSFUL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBranchesProjectNullTest() {
        getJGitMock(null).getBranches(null, BranchType.LOCAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBranchesBranchTypeIsNullTest() {
        getJGitMock(null).getBranches(new Project(), null);
    }

    @Test
    public void getBranchesCorrectData() {
        Ref refMock = mock(Ref.class);
        Git gitMock = getGitMock();
        ListBranchCommand listCommandMock = getListCommandMock(refMock);
        when(refMock.toString()).thenReturn(Constants.R_HEADS);
        when(refMock.getName()).thenReturn(Constants.R_HEADS + "Test");
        when(gitMock.branchList()).thenReturn(listCommandMock);

        Assert.assertFalse(getJGitMock(gitMock).getBranches(getProject(true), BranchType.LOCAL).isEmpty());
        Assert.assertFalse(getJGitMock(gitMock).getBranches(getProject(true), BranchType.REMOTE).isEmpty());
    }

    @Test
    public void getBranchesIncorrectData() {
        Git gitMock = getGitMock();
        Assert.assertTrue(getJGitMock(null).getBranches(getProject(true), BranchType.REMOTE).isEmpty());
        Assert.assertTrue(getJGitMock(gitMock).getBranches(getProject(false), BranchType.REMOTE).isEmpty());

    }

    @Test(expected = IllegalArgumentException.class)
    public void getBranchesWithParametersProjectNullTest() {
        getJGitMock(null).getBranches(null, BranchType.LOCAL, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBranchesWithParametersBranchTypeIsNullTest() {
        getJGitMock(null).getBranches(new ArrayList<>(), null, false);
    }

    @Test
    public void getBranchesWithParametersCorrectData() {
        Assert.assertTrue(getJGitMock(null).getBranches(new ArrayList<>(), BranchType.LOCAL, false).isEmpty());
        Assert.assertTrue(getJGitMock(null).getBranches(new ArrayList<>(), BranchType.REMOTE, false).isEmpty());

        Ref refMock = mock(Ref.class);
        Git gitMock = getGitMock();
        ListBranchCommand listCommandMock = getListCommandMock(refMock);
        when(refMock.toString()).thenReturn(Constants.R_HEADS);
        when(refMock.getName()).thenReturn(Constants.R_HEADS + "Test");
        when(gitMock.branchList()).thenReturn(listCommandMock);

        Assert.assertFalse(getJGitMock(gitMock).getBranches(getProjects(), BranchType.LOCAL, true).isEmpty());
        Assert.assertFalse(getJGitMock(gitMock).getBranches(getProjects(), BranchType.ALL, false).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCurrentBranchProjectIsNullTest() {
        getJGitMock(null).getCurrentBranch(null);
    }

    @Test
    public void getCurrentBranchIncorrectData() {
        Assert.assertFalse(getJGitMock(null).getCurrentBranch(getProject(false)).isPresent());
        Assert.assertFalse(getJGitMock(null).getCurrentBranch(getProject(true)).isPresent());

        Git gitMock = getGitMock();
        Repository repoMock = getRepo(null);
        when(gitMock.getRepository()).thenReturn(repoMock);
        Assert.assertFalse(getJGitMock(gitMock).getCurrentBranch(getProject(true)).isPresent());
    }

    @Test
    public void getCurrentBranchCorrectData() {
        Git gitMock = getGitMock();
        Repository repoMock = getRepo(NAME_BRANCH);
        when(gitMock.getRepository()).thenReturn(repoMock);
        Assert.assertTrue(getJGitMock(gitMock).getCurrentBranch(getProject(true)).isPresent());
    }

    @Test
    public void getTrackingBranchTest() {
        Git gitMock = getGitMock();
        Repository repoMock = getRepo(NAME_BRANCH);
        when(gitMock.getRepository()).thenReturn(repoMock);
        Assert.assertTrue(getJGitMock(gitMock).getTrackingBranch(getProject(true)) != null);
        Assert.assertFalse(getJGitMock(gitMock).getTrackingBranch(getProject(true)).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkoutBranchProjectIsNullTest() {
        getJGitMock(null).checkoutBranch(null, "__", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkoutBranchNameIsNullTest() {
        getJGitMock(null).checkoutBranch(getProject(false), null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkoutBranchNameIsEmptyTest() {
        getJGitMock(null).checkoutBranch(getProject(true), "", false);
    }

    @Test
    public void checkoutBranchIncorrectDataTest() {
        Assert.assertEquals(getJGitMock(null).checkoutBranch(getProject(false), NAME_BRANCH, false), JGitStatus.FAILED);
        //Assert.assertEquals(getJGitMock(null).switchTo(getProject(true), NAME_BRANCH, false), JGitStatus.FAILED);

        Ref refMock = mock(Ref.class);
        Git gitMock = getGitMock();
        ListBranchCommand listCommandMock = getListCommandMock(refMock);
        when(refMock.toString()).thenReturn(Constants.R_HEADS);
        when(refMock.getName()).thenReturn(Constants.R_HEADS + "Test");
        when(gitMock.branchList()).thenReturn(listCommandMock);

        Repository repoMock = getRepo(NAME_BRANCH);
        when(gitMock.getRepository()).thenReturn(repoMock);
        Assert.assertEquals(getJGitMock(gitMock).checkoutBranch(getProject(true), NAME_BRANCH, false),
                JGitStatus.BRANCH_DOES_NOT_EXIST);
        Assert.assertEquals(getJGitMock(gitMock).checkoutBranch(getProject(true), NAME_BRANCH, true),
                JGitStatus.BRANCH_CURRENTLY_CHECKED_OUT);

        listCommandMock = getListCommandMock(refMock);
        when(refMock.getName()).thenReturn(Constants.R_HEADS + NAME_BRANCH);
        Assert.assertEquals(getJGitMock(gitMock).checkoutBranch(getProject(true), NAME_BRANCH, true),
                JGitStatus.BRANCH_ALREADY_EXISTS);

        when(refMock.getName()).thenReturn(Constants.R_HEADS + NAME_BRANCH);

        JGit git = new JGit(getBackgroundServiceMock()) {

            @Override
            protected Git getGit(String path) throws IOException {
                return gitMock;
            }

            @Override
            protected boolean isConflictsBetweenTwoBranches(Repository repo, String firstBranch, String secondBranch) {
                return true;
            }
        };
        Assert.assertEquals(git.checkoutBranch(getProject(true), NAME_BRANCH + "2", true), JGitStatus.CONFLICTS);

        git = new JGit(getBackgroundServiceMock()) {
            @Override
            protected Git getGit(String path) throws IOException {
                return gitMock;
            }

            @Override
            protected boolean isConflictsBetweenTwoBranches(Repository repo, String firstBranch, String secondBranch) {
                return false;
            }
        };

        CheckoutCommand checkoutCommandMock = new CheckoutCommand(getRepository()) {
            @Override
            public Ref call() throws GitAPIException, RefAlreadyExistsException, RefNotFoundException,
                    InvalidRefNameException, CheckoutConflictException {
                throw getGitAPIException();
            }
        };
        when(gitMock.checkout()).thenReturn(checkoutCommandMock);
        Assert.assertEquals(git.checkoutBranch(getProject(true), NAME_BRANCH + "2", true), JGitStatus.FAILED);
    }

    @Test
    public void checkoutBranchCorrectDataTest() {
        Git gitMock = getGitMock();
        JGit git = new JGit(getBackgroundServiceMock()) {
            @Override
            protected Git getGit(String path) throws IOException {
                return gitMock;
            }

            @Override
            protected boolean isConflictsBetweenTwoBranches(Repository repo, String firstBranch, String secondBranch) {
                return false;
            }
        };
        Repository repoMock = getRepo(NAME_BRANCH);
        when(gitMock.getRepository()).thenReturn(repoMock);

        Ref refMock = mock(Ref.class);
        when(refMock.getName()).thenReturn(Constants.R_HEADS + NAME_BRANCH);
        when(refMock.toString()).thenReturn(Constants.R_HEADS);

        ListBranchCommand listCommandMock = getListCommandMock(refMock);
        when(gitMock.branchList()).thenReturn(listCommandMock);

        CheckoutCommand checkoutCommandMock = new CheckoutCommand(getRepository()) {
            @Override
            public Ref call() throws GitAPIException, RefAlreadyExistsException, RefNotFoundException,
                    InvalidRefNameException, CheckoutConflictException {
                return refMock;
            }
        };
        when(gitMock.checkout()).thenReturn(checkoutCommandMock);
        Assert.assertEquals(git.checkoutBranch(getProject(true), NAME_BRANCH + "2", true), JGitStatus.SUCCESSFUL);
    }

    @Test
    public void isConflictsBetweenTwoBranchesInorrectDataTest() {
        Repository repoMock = getRepo(null);
        Assert.assertFalse(getJGitMock(null).isConflictsBetweenTwoBranches(repoMock, "", ""));

        repoMock = getRepo(NAME_BRANCH);
        RevWalk revWalkMockException = new RevWalk(getRepository()) {
            @Override
            public void close() {}

            @Override
            public RevCommit parseCommit(AnyObjectId id)
                    throws MissingObjectException, IncorrectObjectTypeException, IOException {
                throw mock(IOException.class);
            }
        };
        JGit gitException = new JGit(getBackgroundServiceMock()) {
            @Override
            RevWalk getRevWalk(Repository repo) {
                return revWalkMockException;
            }
        };
        Assert.assertFalse(gitException.isConflictsBetweenTwoBranches(repoMock, "", ""));

        RevWalk revWalkMockNull = new RevWalk(getRepository()) {
            @Override
            public void close() {}

            @Override
            public RevCommit parseCommit(AnyObjectId id)
                    throws MissingObjectException, IncorrectObjectTypeException, IOException {
                return null;
            }
        };
        JGit gitNull = new JGit(getBackgroundServiceMock()) {
            @Override
            RevWalk getRevWalk(Repository repo) {
                return revWalkMockNull;
            }
        };
        Assert.assertFalse(gitNull.isConflictsBetweenTwoBranches(repoMock, "", ""));

        RevCommit revCommitMock = mock(RevCommit.class);
        RevWalk revWalkMock = new RevWalk(getRepository()) {

            @Override
            public void close() {}

            @Override
            public RevCommit parseCommit(AnyObjectId id)
                    throws MissingObjectException, IncorrectObjectTypeException, IOException {
                return revCommitMock;
            }
        };
        JGit git = new JGit(getBackgroundServiceMock()) {
            @Override
            RevWalk getRevWalk(Repository repo) {
                return revWalkMock;
            }

            @Override
            boolean checkDirCacheCheck(Repository repo, RevTree firstTree, RevTree secondTree)
                    throws NoWorkTreeException, CorruptObjectException, IOException {
                return true;
            }
        };
        Assert.assertTrue(git.isConflictsBetweenTwoBranches(repoMock, "", ""));

        git = new JGit(getBackgroundServiceMock()) {
            @Override
            RevWalk getRevWalk(Repository repo) {
                return revWalkMock;
            }

            @Override
            boolean checkDirCacheCheck(Repository repo, RevTree firstTree, RevTree secondTree)
                    throws NoWorkTreeException, CorruptObjectException, IOException {
                throw mock(IOException.class);
            }
        };
        Assert.assertTrue(git.isConflictsBetweenTwoBranches(repoMock, "", ""));
    }

    @Test
    public void isConflictsBetweenTwoBranchesCorrectDataTest() {
        Repository repoMock = getRepo(NAME_BRANCH);

        RevCommit revCommitMock = mock(RevCommit.class);
        RevWalk revWalkMock = new RevWalk(getRepository()) {
            @Override
            public void close() {}

            @Override
            public RevCommit parseCommit(AnyObjectId id)
                    throws MissingObjectException, IncorrectObjectTypeException, IOException {
                return revCommitMock;
            }
        };
        JGit git = new JGit(getBackgroundServiceMock()) {
            @Override
            RevWalk getRevWalk(Repository repo) {
                return revWalkMock;
            }

            @Override
            boolean checkDirCacheCheck(Repository repo, RevTree firstTree, RevTree secondTree)
                    throws NoWorkTreeException, CorruptObjectException, IOException {
                return false;
            }
        };
        Assert.assertFalse(git.isConflictsBetweenTwoBranches(repoMock, "", ""));
    }

    @Test
    public void addDeletedFileIncorrectParametersTest() throws NoFilepatternException, GitAPIException {
        Assert.assertFalse(getJGitMock(getGitMock()).addDeletedFile(null, getProject(true), true));
        Assert.assertFalse(getJGitMock(getGitMock()).addDeletedFile(fileName, getProject(false), true));
        Assert.assertFalse(getJGitMock(getGitMock()).addDeletedFile(fileName, null, false));
        Assert.assertFalse(getJGitMock(null).addDeletedFile(fileName, getProject(true), false));

        Git gitMock = getGitMock();
        RmCommand rmCommandMock = mock(RmCommand.class);
        when(rmCommandMock.setCached(true)).thenReturn(rmCommandMock);
        when(rmCommandMock.addFilepattern(fileName)).thenReturn(rmCommandMock);
        when(rmCommandMock.call()).thenThrow(getGitAPIException());
        when(gitMock.rm()).thenReturn(rmCommandMock);

        Assert.assertFalse(getJGitMock(gitMock).addDeletedFile(fileName, getProject(true), true));
    }

    @Test
    public void addDeletedFileCorrectTest() throws NoFilepatternException, GitAPIException {
        Git gitMock = getGitMock();
        RmCommand rmCommandMock = mock(RmCommand.class);

        when(rmCommandMock.setCached(true)).thenReturn(rmCommandMock);
        when(rmCommandMock.addFilepattern(fileName)).thenReturn(rmCommandMock);
        when(rmCommandMock.call()).thenReturn(getDirCache());
        when(gitMock.rm()).thenReturn(rmCommandMock);

        Assert.assertTrue(getJGitMock(gitMock).addDeletedFile(fileName, getProject(true), true));
    }

    @Test
    public void addDeletedFilesIncorrectParametersTest() throws NoFilepatternException, GitAPIException {
        List<String> files = new ArrayList<>(Arrays.asList("xyz", "abc"));
        Assert.assertTrue(getJGitMock(getGitMock()).addDeletedFiles(null, getProject(true), true).isEmpty());
        Assert.assertTrue(getJGitMock(getGitMock()).addDeletedFiles(new ArrayList<>(), getProject(true), true).isEmpty());
        Assert.assertTrue(getJGitMock(getGitMock()).addDeletedFiles(files, null, false).isEmpty());
        Assert.assertTrue(getJGitMock(getGitMock()).addDeletedFiles(files, getProject(false), false).isEmpty());
        Assert.assertTrue(getJGitMock(null).addDeletedFiles(files, getProject(true), false).isEmpty());

        Git gitMock = getGitMock();
        RmCommand rmCommandMock = mock(RmCommand.class);
        when(rmCommandMock.setCached(true)).thenReturn(rmCommandMock);
        when(rmCommandMock.addFilepattern(Mockito.anyString())).thenReturn(rmCommandMock);
        when(rmCommandMock.call()).thenThrow(getGitAPIException());
        when(gitMock.rm()).thenReturn(rmCommandMock);

        Assert.assertTrue(getJGitMock(gitMock).addDeletedFiles(files, getProject(true), true).isEmpty());
    }

    @Test
    public void addDeletedFilesCorrectTest() throws NoFilepatternException, GitAPIException {
        List<String> files = new ArrayList<>(Arrays.asList("xyz", "abc"));

        Git gitMock = getGitMock();
        RmCommand rmCommandMock = mock(RmCommand.class);
        when(rmCommandMock.setCached(true)).thenReturn(rmCommandMock);
        when(rmCommandMock.addFilepattern(Mockito.anyString())).thenReturn(rmCommandMock);
        when(rmCommandMock.call()).thenReturn(getDirCache());
        when(gitMock.rm()).thenReturn(rmCommandMock);

        Assert.assertFalse(getJGitMock(gitMock).addDeletedFiles(files, getProject(true), true).isEmpty());
    }

    @Test
    public void resetChangedFilesIncorrectParametersTest() throws CheckoutConflictException, GitAPIException {
        List<String> files = new ArrayList<>(Arrays.asList("xyz", "abc"));
        Assert.assertTrue(getJGitMock(getGitMock()).resetChangedFiles(null, getProject(true)).isEmpty());
        Assert.assertTrue(getJGitMock(getGitMock()).resetChangedFiles(new ArrayList<>(), getProject(true)).isEmpty());
        Assert.assertTrue(getJGitMock(getGitMock()).resetChangedFiles(files, null).isEmpty());
        Assert.assertTrue(getJGitMock(getGitMock()).resetChangedFiles(files, getProject(false)).isEmpty());
        Assert.assertTrue(getJGitMock(null).resetChangedFiles(files, getProject(true)).isEmpty());

        Git gitMock = getGitMock();
        ResetCommand resetCommandMock = mock(ResetCommand.class);
        when(resetCommandMock.setRef(Constants.HEAD)).thenReturn(resetCommandMock);
        when(resetCommandMock.addPath(Mockito.anyString())).thenReturn(resetCommandMock);
        when(resetCommandMock.call()).thenThrow(getGitAPIException());
        when(gitMock.reset()).thenReturn(resetCommandMock);
        Assert.assertTrue(getJGitMock(gitMock).resetChangedFiles(files, getProject(true)).isEmpty());
    }

    @Test
    public void resetChangedFilesCorrectTest() throws CheckoutConflictException, GitAPIException {
        List<String> files = new ArrayList<>(Arrays.asList("xyz", "abc"));
        Git gitMock = getGitMock();
        ResetCommand resetCommandMock = mock(ResetCommand.class);
        when(resetCommandMock.setRef(Constants.HEAD)).thenReturn(resetCommandMock);
        when(resetCommandMock.addPath(Mockito.anyString())).thenReturn(resetCommandMock);
        when(gitMock.reset()).thenReturn(resetCommandMock);
        when(resetCommandMock.call()).thenReturn(mock(Ref.class));
        Assert.assertFalse(getJGitMock(gitMock).resetChangedFiles(files, getProject(true)).isEmpty());
    }

    private final List<String> correctFiles = Arrays.asList("test", "564.txt");

    @Test
    public void replaceWithHEADRevisionWrongParameters() {
        Project clonedProject = getProject(true);

        Assert.assertFalse(getJGitMock(null).replaceFilesWithHEADRevision(null, correctFiles));
        Assert.assertFalse(getJGitMock(null).replaceFilesWithHEADRevision(new Project(), correctFiles));
        Assert.assertFalse(getJGitMock(null).replaceFilesWithHEADRevision(clonedProject, null));
        Assert.assertFalse(getJGitMock(null).replaceFilesWithHEADRevision(clonedProject, new ArrayList<>()));
    }

    @Test
    public void replaceWithHEADRevisionGitDoesntExist() {
        Project clonedProject = getProject(true);

        boolean isSuccessful = getJGitMock(null).replaceFilesWithHEADRevision(clonedProject, correctFiles);

        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void replaceWithHEADRevisionFailedReplaced() throws GitAPIException {
        String correctFileName = "test";
        Project clonedProject = getProject(true);
        Git gitMock = getGitMock();
        CheckoutCommand checkCommandMock = mock(CheckoutCommand.class);
        // mock CheckoutCommand and Git methods
        Mockito.when(checkCommandMock.addPath(correctFileName)).thenReturn(checkCommandMock);
        Mockito.when(checkCommandMock.call()).thenThrow(getGitAPIException());
        Mockito.when(gitMock.checkout()).thenReturn(checkCommandMock);

        boolean isSuccessful = getJGitMock(gitMock).replaceFilesWithHEADRevision(clonedProject, correctFiles);

        Assert.assertFalse(isSuccessful);
    }

    @Test
    public void replaceWithHEADRevisionSuccessfullyReplaced() throws GitAPIException {
        String correctFileName = "test";
        Project clonedProject = getProject(true);
        Git gitMock = getGitMock();
        CheckoutCommand checkCommandMock = mock(CheckoutCommand.class);
        // mock CheckoutCommand and Git methods
        Mockito.when(checkCommandMock.addPath(correctFileName)).thenReturn(checkCommandMock);

        Mockito.when(checkCommandMock.call()).thenReturn(mock(Ref.class));
        Mockito.when(gitMock.checkout()).thenReturn(checkCommandMock);

        boolean isSuccessful = getJGitMock(gitMock).replaceFilesWithHEADRevision(clonedProject, correctFiles);

        Assert.assertTrue(isSuccessful);
    }

    @Test
    public void stashCreateIncorrectParameters() {
        String correctMessage = "test";

        Assert.assertFalse(getJGitMock(null).stashCreate(null, correctMessage, true));
        Assert.assertFalse(getJGitMock(null).stashCreate(getProject(false), correctMessage, true));
        Assert.assertFalse(getJGitMock(null).stashCreate(getProject(true), null, true));
        Assert.assertFalse(getJGitMock(null).stashCreate(getProject(true), "", true));
    }

    @Test
    public void stashCreateIncorrectGit() {
        String correctMessage = "test";
        Project correctProject = getProject(true);

        boolean resultOperation = getJGitMock(null).stashCreate(correctProject, correctMessage, true);

        Assert.assertFalse(resultOperation);
    }

    @Test
    public void stashCreateFailedResult() throws GitAPIException {
        String correctMessage = "test";
        Project correctProject = getProject(true);
        // mock git operations
        Git gitMock = getGitMock();
        StashCreateCommand stashCreateMock = getStashCreateWithSettings(correctMessage, false);
        when(gitMock.stashCreate()).thenReturn(stashCreateMock);

        boolean resultOperation = getJGitMock(gitMock).stashCreate(correctProject, correctMessage, true);

        Assert.assertFalse(resultOperation);
    }

    @Test
    public void stashCreateSuccessfulResult() throws GitAPIException  {
        String correctMessage = "test";
        Project correctProject = getProject(true);
        // mock git operations
        Git gitMock = getGitMock();
        StashCreateCommand stashCreateMock = getStashCreateWithSettings(correctMessage, true);
        when(gitMock.stashCreate()).thenReturn(stashCreateMock);

        boolean resultOperation = getJGitMock(gitMock).stashCreate(correctProject, correctMessage, true);

        Assert.assertTrue(resultOperation);
    }

    @Test
    public void getStashesIncorrectParameters() {
        Assert.assertTrue(getJGitMock(null).getStashes(null).isEmpty());
        Assert.assertTrue(getJGitMock(null).getStashes(getProject(false)).isEmpty());
    }

    @Test
    public void getStashesIncorrectGit() {
        List<SingleProjectStash> result = getJGitMock(null).getStashes(getProject(true));

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getStashesFailedResult() throws InvalidRefNameException, GitAPIException {
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setStashListToGitMock(gitMock, false);

        List<SingleProjectStash> result = getJGitMock(gitMock).getStashes(project);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getStashesSuccessfulResult() throws InvalidRefNameException, GitAPIException {
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setStashListToGitMock(gitMock, true);

        List<SingleProjectStash> result = getJGitMock(gitMock).getStashes(project);

        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void stashApplyNullStash() {
        StashApplyListener progressListener = new StashApplyListener();

        getJGitMock(null).stashApply(null, progressListener);

        Assert.assertFalse(progressListener.isSuccessfully());
    }

    @Test
    public void stashApplyNullProject() {
        StashApplyListener progressListener = new StashApplyListener();
        SingleProjectStash stash = new SingleProjectStash(null, null, null);

        getJGitMock(null).stashApply(stash, progressListener);

        Assert.assertFalse(progressListener.isSuccessfully());
    }

    @Test
    public void stashApplyNotClonedProject() {
        StashApplyListener progressListener = new StashApplyListener();
        SingleProjectStash stash = new SingleProjectStash("test name", "test message", new Project());

        getJGitMock(null).stashApply(stash, progressListener);

        Assert.assertFalse(progressListener.isSuccessfully());
    }

    @Test
    public void stashApplyStashNameNull() {
        StashApplyListener progressListener = new StashApplyListener();
        SingleProjectStash stash = new SingleProjectStash(null, "test message", getProject(true));

        getJGitMock(null).stashApply(stash, progressListener);

        Assert.assertFalse(progressListener.isSuccessfully());
    }


    @Test
    public void stashApplyEmptyStashName() {
        StashApplyListener progressListener = new StashApplyListener();
        SingleProjectStash stash = new SingleProjectStash("", "test message", getProject(true));

        getJGitMock(null).stashApply(stash, progressListener);

        Assert.assertFalse(progressListener.isSuccessfully());
    }

    @Test
    public void stashApplyIncorrectGit() {
        StashApplyListener progressListener = new StashApplyListener();
        Project project = getProject(true);
        SingleProjectStash stash = new SingleProjectStash("test name", "test message", project);

        getJGitMock(null).stashApply(stash, progressListener);

        Assert.assertFalse(progressListener.isSuccessfully());
    }

    @Test
    public void stashApplyFailedResult() throws InvalidRefNameException, GitAPIException {
        StashApplyListener progressListener = new StashApplyListener();
        Project project = getProject(true);
        String stashName = "test name";
        SingleProjectStash stash = new SingleProjectStash(stashName, "test message", project);
        Git gitMock = getGitMock();
        setStashApplyCommandToGit(gitMock, stashName, false);

        getJGitMock(gitMock).stashApply(stash, progressListener);

        Assert.assertFalse(progressListener.isSuccessfully());
    }

    @Test
    public void stashApplySuccessfulResult() throws InvalidRefNameException, GitAPIException {
        StashApplyListener progressListener = new StashApplyListener();
        Project project = getProject(true);
        String stashName = "test name";
        SingleProjectStash stash = new SingleProjectStash(stashName, "test message", project);
        Git gitMock = getGitMock();
        setStashApplyCommandToGit(gitMock, stashName, true);

        getJGitMock(gitMock).stashApply(stash, progressListener);

        Assert.assertTrue(progressListener.isSuccessfully());
    }

    @Test
    public void stashDropStashIncorrectParameters() {
        String correctName = "test";

        Assert.assertFalse(getJGitMock(null).stashDrop(null, correctName));
        Assert.assertFalse(getJGitMock(null).stashDrop(new Project(), correctName));
        Assert.assertFalse(getJGitMock(null).stashDrop(getProject(true), null));
        Assert.assertFalse(getJGitMock(null).stashDrop(getProject(true), ""));
    }

    @Test
    public void stashDropStashIncorrectGit() {
        String stashName = "test";
        Project project = getProject(true);

        boolean resultOperation = getJGitMock(null).stashDrop(project, stashName);

        Assert.assertFalse(resultOperation);
    }

    @Test
    public void stashDropStashFailedResult() throws InvalidRefNameException, GitAPIException {
        String stashName = "test";
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setStashDropCommandToGit(gitMock, false);

        boolean resultOperation = getJGitMock(gitMock).stashDrop(project, stashName);

        Assert.assertFalse(resultOperation);
    }

    @Test
    public void stashDropStashSuccessfulResult() throws InvalidRefNameException, GitAPIException {
        String stashName = "test";
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setStashDropCommandToGit(gitMock, true);

        boolean resultOperation = getJGitMock(gitMock).stashDrop(project, stashName);

        Assert.assertTrue(resultOperation);
    }

    @Test(expected=IllegalArgumentException.class)
    public void deleteLocalBranchNullProject() {
        getJGitMock(null).deleteBranch(null, "", false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void deleteLocalBranchNullBranchName() {
        getJGitMock(null).deleteBranch(getProject(true), null, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void deleteLocalBranchNameIsEmpty() {
        getJGitMock(null).deleteBranch(getProject(true), "", false);
    }

    @Test
    public void deleteLocalBranchProjectNotCloned() {
        Map<JGitStatus, String> mapResult = getJGitMock(null).deleteBranch(new Project(), "test", false);
        JGitStatus status = mapResult.entrySet().iterator().next().getKey();

        assertFalse(status.isSuccessful());
    }

    @Test
    public void deleteBranchGitFailed() {
        Map<JGitStatus, String> mapResult = getJGitMock(null).deleteBranch(getProject(true), "test", false);
        JGitStatus status = mapResult.entrySet().iterator().next().getKey();

        assertFalse(status.isSuccessful());
    }

    @Test
    public void deleteLocalBranchFailed() throws NotMergedException, CannotDeleteCurrentBranchException, GitAPIException {
        String branchName = "test";
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setDeleteBranchCommandToGit(gitMock, false);

        Map<JGitStatus, String> mapResult = getJGitMock(gitMock).deleteBranch(project, branchName, false);
        JGitStatus status = mapResult.entrySet().iterator().next().getKey();

        assertFalse(status.isSuccessful());
    }

    @Test
    public void deleteLocalBranchSuccessfulResult() throws NotMergedException, CannotDeleteCurrentBranchException, GitAPIException {
        String branchName = "test";
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setDeleteBranchCommandToGit(gitMock, true);

        Map<JGitStatus, String> mapResult = getJGitMock(gitMock).deleteBranch(project, branchName, false);
        JGitStatus status = mapResult.entrySet().iterator().next().getKey();

        assertTrue(status.isSuccessful());
    }

    @Test
    public void deleteRemoteBranchFailed() throws InvalidRemoteException, TransportException, GitAPIException {
        String branchName = "test";
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setPushRemoteToGit(gitMock, true, false);

        Map<JGitStatus, String> mapResult = getJGitMock(gitMock).deleteBranch(project, branchName, true);
        JGitStatus status = mapResult.entrySet().iterator().next().getKey();

        assertFalse(status.isSuccessful());
    }

    @Test
    public void deleteRemoteBranchFailedPushResult() throws InvalidRemoteException, TransportException, GitAPIException {
        String branchName = "test";
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setPushRemoteToGit(gitMock, false, false);

        Map<JGitStatus, String> mapResult = getJGitMock(gitMock).deleteBranch(project, branchName, true);
        JGitStatus status = mapResult.entrySet().iterator().next().getKey();

        assertFalse(status.isSuccessful());
    }

    @Test
    public void deleteRemoteBranchSuccessfulResult() throws InvalidRemoteException, TransportException, GitAPIException {
        String branchName = "test";
        Project project = getProject(true);
        Git gitMock = getGitMock();
        setPushRemoteToGit(gitMock, false, true);

        Map<JGitStatus, String> mapResult = getJGitMock(gitMock).deleteBranch(project, branchName, true);
        JGitStatus status = mapResult.entrySet().iterator().next().getKey();

        assertTrue(status.isSuccessful());
    }

    /***************************************************************************************************************/

    private void setPushRemoteToGit(Git gitMock, boolean throwException, boolean pushResultSuccess)
            throws InvalidRemoteException, TransportException, GitAPIException {
        PushCommand pushCommand = mock(PushCommand.class);
        when(pushCommand.setRefSpecs(Mockito.any(RefSpec.class))).thenReturn(pushCommand);
        when(pushCommand.setRemote(Mockito.anyString())).thenReturn(pushCommand);

        if (throwException) {
            when(pushCommand.call()).thenThrow(getGitAPIException());
        } else {
            PushResult pushResult = getPushResult(pushResultSuccess);
            when(pushCommand.call()).thenReturn(Arrays.asList(pushResult));
        }
        when(gitMock.push()).thenReturn(pushCommand);
    }

    private PushResult getPushResult(boolean isSuccessfulOperation) {
        PushResult pushResult = mock(PushResult.class);
        RemoteRefUpdate remoteRefUpdate = mock(RemoteRefUpdate.class);
        if (isSuccessfulOperation) {
            when(remoteRefUpdate.getStatus()).thenReturn(org.eclipse.jgit.transport.RemoteRefUpdate.Status.OK);
        } else {
            when(remoteRefUpdate.getStatus()).thenReturn(org.eclipse.jgit.transport.RemoteRefUpdate.Status.REJECTED_OTHER_REASON);
        }
        when(pushResult.getRemoteUpdates()).thenReturn(Arrays.asList(remoteRefUpdate));
        when(pushResult.getMessages()).thenReturn("");
        return pushResult;
    }

    private void setDeleteBranchCommandToGit(Git gitMock, boolean isSuccessfulOperation) throws NotMergedException, CannotDeleteCurrentBranchException, GitAPIException {
        DeleteBranchCommand deleteBranchCommand = mock(DeleteBranchCommand.class);
        when(deleteBranchCommand.setBranchNames(Mockito.anyString())).thenReturn(deleteBranchCommand);
        when(deleteBranchCommand.setForce(true)).thenReturn(deleteBranchCommand);

        if (isSuccessfulOperation) {
            when(deleteBranchCommand.call()).thenReturn(new ArrayList<>());
        } else {
            when(deleteBranchCommand.call()).thenThrow(getGitAPIException());
        }
        when(gitMock.branchDelete()).thenReturn(deleteBranchCommand);
    }

    private void setStashDropCommandToGit(Git gitMock, boolean isCorrectStash)
            throws InvalidRefNameException, GitAPIException {
        StashDropCommand stashDropCommand = mock(StashDropCommand.class);
        when(stashDropCommand.setStashRef(Mockito.anyInt())).thenReturn(stashDropCommand);
        if (isCorrectStash) {
            when(stashDropCommand.call()).thenReturn(mock(ObjectId.class));
        } else {
            when(stashDropCommand.call()).thenThrow(getGitAPIException());
        }
        when(gitMock.stashDrop()).thenReturn(stashDropCommand);
    }

    private void setStashApplyCommandToGit(Git gitMock, String nameStash, boolean isCorrectStash)
            throws InvalidRefNameException, GitAPIException {
        StashApplyCommand stashApplyCommand = mock(StashApplyCommand.class);
        when(stashApplyCommand.setStashRef(nameStash)).thenReturn(stashApplyCommand);
        if (isCorrectStash) {
            when(stashApplyCommand.call()).thenReturn(mock(ObjectId.class));
        } else {
            when(stashApplyCommand.call()).thenThrow(getGitAPIException());
        }
        when(gitMock.stashApply()).thenReturn(stashApplyCommand);
    }

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

    private void setStashListToGitMock(Git gitMock, boolean isCorrectStash)
            throws InvalidRefNameException, GitAPIException {
        StashListCommand stashListMock = mock(StashListCommand.class);
        if (isCorrectStash) {
            when(stashListMock.call()).thenReturn(getRevCommits());
        } else {
            when(stashListMock.call()).thenThrow(getGitAPIException());
        }
        when(gitMock.stashList()).thenReturn(stashListMock);
    }

    private Collection<RevCommit> getRevCommits() {
        return Arrays.asList(mock(RevCommit.class), mock(RevCommit.class), null);
    }

    private StashCreateCommand getStashCreateWithSettings(String stashMessage, boolean isCorrectStash)
            throws GitAPIException {
        StashCreateCommand stashCreateMock = mock(StashCreateCommand.class);
        when(stashCreateMock.setWorkingDirectoryMessage(stashMessage)).thenReturn(stashCreateMock);
        when(stashCreateMock.setIncludeUntracked(true)).thenReturn(stashCreateMock);
        if (isCorrectStash) {
            when(stashCreateMock.call()).thenReturn(mock(RevCommit.class));
        } else {
            when(stashCreateMock.call()).thenThrow(getGitAPIException());
        }
        return stashCreateMock;
    }

    private Project getProject(boolean isCorrectProject) {
        if (!isCorrectProject) {
            return new Project();
        }
        Project projectCorrect = new Project() {

            private static final long serialVersionUID = 1L;

            @Override
            protected boolean checkPath(Path pathToProject) {
                return true;
            };
        };
        projectCorrect.setPath(".path");
        projectCorrect.setClonedStatus(true);
        return projectCorrect;
    }

    private List<Project> getProjects() {
        //Please use COUNT_INCORRECT_PROJECT if you add here new incorrect value
        List<Project> listProjects = new ArrayList<>();
        listProjects.add(getProject(true));
        listProjects.add(null);
        listProjects.add(getProject(false));
        listProjects.add(new Project());
        return listProjects;
    }

    private long getCountIncorrectProject(List<Project> projects) {
        return projects.stream()
                .filter((project) -> project == null || !project.isCloned())
                .count();
    }

    private long getCountCorrectProject(List<Project> projects) {
        return (projects.size() - getCountIncorrectProject(projects));
    }

    private long getCountCorrectStatuses(Map<Project, JGitStatus> statuses){
        return  statuses.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(status -> status.equals(JGitStatus.SUCCESSFUL))
                .count();
    }

    private long getCountIncorrectStatuses(Map<Project, JGitStatus> statuses){
        return  statuses.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(status -> status.equals(JGitStatus.FAILED))
                .count();
    }

    private JGit getJGitMock(Git gitMock) {
        if (gitMock == null) {
            return new JGit(getBackgroundServiceMock()) {
                @Override
                protected Git getGit(String path) throws IOException {
                    throw mock(IOException.class);
                }
            };
        }

        JGit correctJGitMock = new JGit(getBackgroundServiceMock()) {
            @Override
            protected User getUserData() {
                User user = new User("Lyudmila", "ld@email.com");
                return user;
            }

            @Override
            protected Git getGit(String path) throws IOException {
                return gitMock;
            }

            @Override
            protected BranchConfig getBranchConfig(Config config, String branchName) {

                return new BranchConfig(config, branchName) {
                    @Override
                    public String getTrackingBranch() {
                        return NAME_TRACKING_BRANCH;
                    }
                };
            }

            @Override
            SingleProjectStash getStash(RevCommit revCommit, Project project) {
                return new SingleProjectStash("test name", "test message", project);
            }

            @Override
            int getIndexInList(Git git, String stashName) {
                return 0;
            }
        };
        return correctJGitMock;
    }

    private Git getGitMock() {
        return mock(Git.class);
    }

    private Repository getRepository() {
        return mock(Repository.class);
    }

    private Repository getRepo(String nameBranch) {
        BaseRepositoryBuilder<?, ?> buildMock = mock(BaseRepositoryBuilder.class);
        if (nameBranch == null) {
            return new Repository(buildMock) {

                @Override
                public void close() {
                    // Do nothing
                }

                @Override
                public Ref exactRef(String name) throws IOException {
                    return null;
                }

                @Override
                public String getFullBranch() throws IOException {
                    throw new IOException();
                }

                @Override
                public String getBranch() throws IOException {
                    throw new IOException();
                }

                @Override
                public void scanForRepoChanges() throws IOException {
                }

                @Override
                public void notifyIndexChanged() {
                }

                @Override
                public ReflogReader getReflogReader(String refName) throws IOException {
                    return null;
                }

                @Override
                public RefDatabase getRefDatabase() {
                    return null;
                }

                @Override
                public ObjectDatabase getObjectDatabase() {
                    return null;
                }

                @Override
                public StoredConfig getConfig() {
                    return mock(StoredConfig.class);
                }

                @Override
                public AttributesNodeProvider createAttributesNodeProvider() {
                    return null;
                }

                @Override
                public void create(boolean bare) throws IOException {

                }
            };
        }

        Ref refMock = mock(Ref.class);
        ObjectId objectIdMock = mock(ObjectId.class);
        when(refMock.getObjectId()).thenReturn(objectIdMock);
        Repository repoMock = new Repository(buildMock) {

            @Override
            public void close() {
                // Do nothing
            }

            @Override
            public Ref exactRef(String name) throws IOException {
                return refMock;
            }

            @Override
            public String getFullBranch() throws IOException {
                return Constants.R_HEADS + nameBranch;
            }

            @Override
            public String getBranch() throws IOException {
                return nameBranch;
            }

            @Override
            public void scanForRepoChanges() throws IOException {
            }

            @Override
            public void notifyIndexChanged() {
            }

            @Override
            public ReflogReader getReflogReader(String refName) throws IOException {
                return null;
            }

            @Override
            public RefDatabase getRefDatabase() {
                return null;
            }

            @Override
            public ObjectDatabase getObjectDatabase() {
                return null;
            }

            @Override
            public StoredConfig getConfig() {
                return mock(StoredConfig.class);
            }

            @Override
            public AttributesNodeProvider createAttributesNodeProvider() {
                return null;
            }

            @Override
            public void create(boolean bare) throws IOException {

            }
        };
        return repoMock;
    }

    private ListBranchCommand getListCommandMock(Ref ref) {
        ListBranchCommand listCommandMock = new ListBranchCommand(getRepository()) {
            @Override
            public List<Ref> call() throws GitAPIException {
                List<Ref> refs = new ArrayList<>();
                refs.add(ref);
                return refs;
            }
        };
        return listCommandMock;
    }

    private GitAPIException getGitAPIException() {
        return mock(GitAPIException.class);
    }

    private DirCache getDirCache() {
        return mock(DirCache.class);
    }

    private BackgroundService getBackgroundServiceMock() {
        return mock(BackgroundServiceImpl.class);
    }

    class EmptyListener implements ProgressListener {

        @Override
        public void onSuccess(Object... t) {
        }

        @Override
        public void onError(Object... t) {
        }

        @Override
        public void onStart(Object... t) {
        }

        @Override
        public void onFinish(Object... t) {
        }
    }

    private List<Project> getProjects(int count) {
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            projects.add(new Project());
        }
        return projects;
    }

    private List<Project> getCorrectProject(int countProject) {
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < countProject; i++) {
            Project pr = new Project();
            pr.setClonedStatus(true);
            pr.setPath(".");
            projects.add(pr);
        }
        return projects;
    }

    private OperationProgressListener getEmptyOperationProgressListener() {
        return new OperationProgressListener(
                Mockito.mock(ProgressDialog.class),
                ApplicationState.CLONE) {
            @Override
            public void onSuccess(Object... t) {}
            @Override
            public void onError(Object... t) {}
            @Override
            public void onStart(Object... t) {}
            @Override
            public void doOnFinishJob(Object... t) {}
        };
    }
}
