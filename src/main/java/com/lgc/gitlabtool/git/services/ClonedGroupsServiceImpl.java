package com.lgc.gitlabtool.git.services;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.entities.ClonedGroups;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.util.PathUtilities;
import com.lgc.gitlabtool.git.util.URLManager;

public class ClonedGroupsServiceImpl implements ClonedGroupsService {

    private final ClonedGroups _clonedGroupsProvider = ClonedGroups.getInstance();

    private List<Group> _notExistGroups;

    private StorageService _storageService;

    private LoginService _loginService;

    public ClonedGroupsServiceImpl(StorageService storageService, LoginService loginService) {
        setStorageService(storageService);
        setLoginService(loginService);
    }

    private void setLoginService(LoginService loginService) {
        if (loginService != null) {
            _loginService = loginService;
        }
    }

    private void setStorageService(StorageService storageService) {
        if (storageService != null) {
            _storageService = storageService;
        }
    }

    @Override
    public List<Group> getClonedGroups() {
        return _clonedGroupsProvider.getClonedGroups();
    }

    @Override
    public void addGroups(List<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return;
        }
        List<Group> clonedGroup = _clonedGroupsProvider.getClonedGroups();
        if (clonedGroup == null || clonedGroup.isEmpty()) {
            _clonedGroupsProvider.setClonedGroups(groups);
        } else {
            clonedGroup.addAll(groups);
        }
        updateClonedGroupsInXML();
    }

    @Override
    public List<Group> loadClonedGroups() {
        String username = _loginService.getCurrentUser().getUsername();
        List<Group> groups = _storageService.loadStorage(URLManager.trimServerURL(_loginService.getServerURL()),
                username);

        _notExistGroups = groups.stream()
                                .filter(group -> isNotExistsAndDirectory(group))
                                .collect(Collectors.toList());

        groups.removeAll(_notExistGroups);
        _clonedGroupsProvider.setClonedGroups(groups);
        updateClonedGroupsInXML();
        return getClonedGroups();
    }

    @Override
    public boolean removeGroups(List<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return false;
        }
        List<Group> clonedGroup = _clonedGroupsProvider.getClonedGroups();
        if (clonedGroup == null || clonedGroup.isEmpty()) {
            return false;
        }
        boolean status = clonedGroup.removeAll(groups);
        if (status) {
            updateClonedGroupsInXML();
        }
        return status;
    }

    @Override
    public Collection<Group> getNotExistGroup() {
        return Collections.unmodifiableCollection(_notExistGroups);
    }

    private void updateClonedGroupsInXML() {
        String username = _loginService.getCurrentUser().getUsername();
        _storageService.updateStorage(URLManager.trimServerURL(_loginService.getServerURL()), username);
    }

    private boolean isNotExistsAndDirectory(Group group) {
        String path = group.getPath();
        if (path == null) {
            return true;
        }
        return !PathUtilities.isExistsAndDirectory(Paths.get(path));
    }

}