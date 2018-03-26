package com.lgc.gitlabtool.git.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.lgc.gitlabtool.git.entities.ClonedGroups;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.User;
import com.lgc.gitlabtool.git.util.PathUtilities;
import com.lgc.gitlabtool.git.util.URLManager;

public class ClonedGroupsServiceImplTest {

    private ClonedGroupsService _clonedGroupsService;
    private StorageService _storageService;
    private LoginService _loginService;
    private ClonedGroups _clonedGroupsProvider;
    private PathUtilities _pathUtilities;
    private URLManager _urlManager;

    @Before
    public void init() {
        _storageService = mock(StorageService.class);
        _loginService = mock(LoginService.class);
        _clonedGroupsProvider = mock(ClonedGroups.class);
        _pathUtilities = mock(PathUtilities.class);
        _urlManager = mock(URLManager.class);

        _clonedGroupsService = new ClonedGroupsServiceImpl(_storageService,
                _loginService, _pathUtilities, _clonedGroupsProvider, _urlManager);
    }

    @After
    public void clear() {
        _storageService = null;
        _loginService = null;
        _pathUtilities = null;
        _clonedGroupsProvider = null;
        _clonedGroupsService = null;
        _urlManager = null;
    }

    @Test
    public void getClonedGroupsSuccess() {
        List<Group> clonedGroups = new ArrayList<>(Arrays.asList(new Group(), new Group()));
        when(_clonedGroupsProvider.getClonedGroups()).thenReturn(clonedGroups);

        List<Group> result = _clonedGroupsService.getClonedGroups();

        assertFalse(result.isEmpty());
        assertEquals(result.size(), clonedGroups.size());
    }

    @Test
    public void addGroupsNullList() {
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());
        _clonedGroupsProvider.setClonedGroups(new ArrayList<>());

        _clonedGroupsService.addGroups(null);

        assertTrue(_clonedGroupsService.getClonedGroups().isEmpty());
    }

    @Test
    public void addGroupsEmptyList() {
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());
        _clonedGroupsProvider.setClonedGroups(new ArrayList<>());

        _clonedGroupsService.addGroups(new ArrayList<>());

        assertTrue(_clonedGroupsService.getClonedGroups().isEmpty());
    }

    @Test
    public void addGroupsNullClonedList() {
        List<Group> clonedGroups = new ArrayList<>(Arrays.asList(new Group(), new Group()));
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        when(_loginService.getCurrentUser()).thenReturn(user);
        when(_loginService.getServerURL()).thenReturn("url");
        when(_urlManager.trimServerURL("url")).thenReturn("server");
        when(_storageService.updateStorage(anyString(), anyString())).thenReturn(true);
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());

        _clonedGroupsService.addGroups(clonedGroups);
        List<Group> result = _clonedGroupsService.getClonedGroups();

        assertFalse(result.isEmpty());
        assertEquals(result.size(), clonedGroups.size());
    }

    @Test
    public void addGroupsEmptyClonedList() {
        List<Group> clonedGroups = new ArrayList<>(Arrays.asList(new Group(), new Group()));
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());
        _clonedGroupsProvider.setClonedGroups(new ArrayList<>());
        updateClonedGroupsInXMLMock(true);

        _clonedGroupsService.addGroups(clonedGroups);
        List<Group> result = _clonedGroupsService.getClonedGroups();

        assertFalse(result.isEmpty());
        assertEquals(result.size(), clonedGroups.size());
    }

    @Test
    public void addGroupsClonedListIsNotEmpty() {
        List<Group> clonedGroups = new ArrayList<>(Arrays.asList(new Group(), new Group()));
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        updateClonedGroupsInXMLMock(true);
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());
        _clonedGroupsProvider.setClonedGroups(new ArrayList<>(clonedGroups));

        _clonedGroupsService.addGroups(new ArrayList<>(clonedGroups));
        List<Group> result = _clonedGroupsService.getClonedGroups();

        assertFalse(result.isEmpty());
        assertEquals(result.size(), clonedGroups.size()*2);
    }

    @Test
    public void loadGroupEmptyList() {
        String userName = "username";
        String serverName = "server";
        updateClonedGroupsInXMLMock(true);
        when(_storageService.loadStorage(serverName, userName)).thenReturn(new ArrayList<>());
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());

        List<Group> result = _clonedGroupsService.loadClonedGroups();

        assertTrue(result.isEmpty());
    }

    @Test
    public void loadGroupGroupsAreExist() {
        Group clonedGroup = new Group();
        clonedGroup.setPath(".");
        List<Group> clonedGroups = new ArrayList<>(Arrays.asList(new Group(), new Group(), clonedGroup));
        String userName = "username";
        String serverName = "server";
        updateClonedGroupsInXMLMock(true);
        when(_storageService.loadStorage(serverName, userName)).thenReturn(new ArrayList<>(clonedGroups));
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());
        when(_pathUtilities.isExistsAndDirectory(Mockito.any(Path.class))).thenReturn(false);

        List<Group> result = _clonedGroupsService.loadClonedGroups();

        assertTrue(result.isEmpty());
        assertNotEquals(clonedGroups.size(), result.size());
    }


    @Test
    public void loadGroupGroupsAreNotExist() {
        List<Group> clonedGroups = getClonedGroups(3);
        String userName = "username";
        String serverName = "server";
        updateClonedGroupsInXMLMock(true);
        when(_storageService.loadStorage(serverName, userName)).thenReturn(new ArrayList<>(clonedGroups));
        when(_pathUtilities.isExistsAndDirectory(Mockito.any(Path.class))).thenReturn(true);
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());

        List<Group> result = _clonedGroupsService.loadClonedGroups();

        assertFalse(result.isEmpty());
        assertEquals(clonedGroups.size(), result.size());
    }

    @Test
    public void removeGroupsNullList() {
        boolean result = _clonedGroupsService.removeGroups(null);

        assertFalse(result);
    }

    @Test
    public void removeGroupsEmptyList() {
        boolean result = _clonedGroupsService.removeGroups(new ArrayList<>());

        assertFalse(result);
    }


    @Test
    public void removeGroupsNullClonedGroup() {
        List<Group> clonedGroups = getClonedGroups(3);
        when(_clonedGroupsProvider.getClonedGroups()).thenReturn(null);

        boolean result = _clonedGroupsService.removeGroups(clonedGroups);

        assertFalse(result);
    }

    @Test
    public void removeGroupsEmptyClonedGroup() {
        List<Group> clonedGroups = getClonedGroups(3);
        when(_clonedGroupsProvider.getClonedGroups()).thenReturn(new ArrayList<>());

        boolean result = _clonedGroupsService.removeGroups(clonedGroups);

        assertFalse(result);
    }

    @Test
    public void removeGroupsFailed() {
        List<Group> clonedGroups = getClonedGroups(3);
        when(_clonedGroupsProvider.getClonedGroups()).thenReturn(new ArrayList<>(Arrays.asList(new Group())));

        boolean result = _clonedGroupsService.removeGroups(clonedGroups);

        assertFalse(result);
    }

    @Test
    public void removeGroupsSuccess() {
        Group groupFotDelete = new Group();
        List<Group> clonedGroups = new ArrayList<>(Arrays.asList(groupFotDelete));
        when(_clonedGroupsProvider.getClonedGroups()).thenReturn(clonedGroups);
        updateClonedGroupsInXMLMock(true);

        boolean result = _clonedGroupsService.removeGroups(new ArrayList<>(Arrays.asList(groupFotDelete)));

        assertTrue(result);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void getNotExistGroupSuccess() {
        Collection<Group> groups = _clonedGroupsService.getNotExistGroup();

        assertTrue(groups.isEmpty());

        groups.add(new Group()); // we cannot change unmodifiable list
    }

    //***************************************************************************************************

    private List<Group> getClonedGroups(int count) {
        List<Group> clonedGroups = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Group group = new Group();
            group.setPath(".");
            group.setClonedStatus(true);
            clonedGroups.add(group);
        }
        return clonedGroups;
    }

    private void updateClonedGroupsInXMLMock(boolean isSuccessful) {
        User user = mock(User.class);
        String userName = "username";
        String serverName = "server";
        when(user.getUsername()).thenReturn(userName);
        when(_loginService.getServerURL()).thenReturn("url");
        when(_loginService.getCurrentUser()).thenReturn(user);
        when(_urlManager.trimServerURL("url")).thenReturn(serverName);
        when(_storageService.updateStorage(serverName, userName)).thenReturn(isSuccessful);
    }

}
