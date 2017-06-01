package com.lgc.solutiontool.git.services;

import java.util.List;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.util.URLManager;

/**
 *
 * @author Lyudmila Lyska
 */
public class ClonedGroupsServiceImpl implements ClonedGroupsService {

    private final ClonedGroups _clonedGroupsProvider = ClonedGroups.getInstance();

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
        _clonedGroupsProvider.setClonedGroups(groups);
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
    public void updateClonedGroupsInXML() {
        String username = _loginService.getCurrentUser().getUsername();
        _storageService.updateStorage(URLManager.trimServerURL(_loginService.getServerURL()), username);
    }
}