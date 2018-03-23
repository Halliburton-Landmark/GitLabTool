package com.lgc.gitlabtool.git.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        when(_loginService.getCurrentUser()).thenReturn(user);
        when(_loginService.getServerURL()).thenReturn("url");
        when(_urlManager.trimServerURL("url")).thenReturn("server");
        when(_storageService.updateStorage(anyString(), anyString())).thenReturn(true);
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());
        _clonedGroupsProvider.setClonedGroups(new ArrayList<>());

        _clonedGroupsService.addGroups(clonedGroups);

        List<Group> result = _clonedGroupsService.getClonedGroups();
        assertFalse(result.isEmpty());
        assertEquals(result.size(), clonedGroups.size());
    }

    @Test
    public void addGroupsClonedListIsNotEmpty() {
        List<Group> clonedGroups = new ArrayList<>(Arrays.asList(new Group(), new Group()));
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        when(_clonedGroupsProvider.getClonedGroups()).thenCallRealMethod();
        when(_loginService.getCurrentUser()).thenReturn(user);
        when(_loginService.getServerURL()).thenReturn("url");
        when(_urlManager.trimServerURL("url")).thenReturn("server");
        when(_storageService.updateStorage(anyString(), anyString())).thenReturn(true);
        doCallRealMethod().when(_clonedGroupsProvider).setClonedGroups(anyList());
        _clonedGroupsProvider.setClonedGroups(new ArrayList<>(clonedGroups));

        _clonedGroupsService.addGroups(new ArrayList<>(clonedGroups));

        List<Group> result = _clonedGroupsService.getClonedGroups();
        assertFalse(result.isEmpty());
        assertEquals(result.size(), clonedGroups.size()*2);
    }


    //***************************************************************************************************



}
