package com.lgc.solutiontool.git.services;

import java.util.List;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.util.URLManager;

/**
 *
 * @author Lyudmila Lyska
 */
public class ProgramPropertiesServiceImpl implements ProgramPropertiesService {

    private final ClonedGroupsProvider _programProperties = ClonedGroupsProvider.getInstance();

    private StorageService _storageService;

    private LoginService _loginService;

    public ProgramPropertiesServiceImpl(StorageService storageService, LoginService loginService) {
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
    public void setClonedGroups(List<Group> groups) {
        _programProperties.setClonedGroups(groups);
    }

    @Override
    public List<Group> getClonedGroups() {
        return _programProperties.getClonedGroups();
    }

    @Override
    public void updateClonedGroups(List<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return;
        }
        List<Group> clonedGroup = _programProperties.getClonedGroups();
        if (clonedGroup == null || clonedGroup.isEmpty()) {
            setClonedGroups(groups);
        } else {
            clonedGroup.addAll(groups);
        }

        String username = _loginService.getCurrentUser().getUsername();
        _storageService.updateStorage(URLManager.trimServerURL(_loginService.getServerURL()), username);
    }

    @Override
    public List<Group> loadClonedGroups() {
        String username = _loginService.getCurrentUser().getUsername();
        List<Group> groups = _storageService.loadStorage(URLManager.trimServerURL(_loginService.getServerURL()),
                username);
        setClonedGroups(groups);
        return getClonedGroups();
    }
}