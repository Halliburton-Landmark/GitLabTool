package com.ystrazhko.git.properties;

import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.services.LoginService;
import com.ystrazhko.git.services.ServiceProvider;
import com.ystrazhko.git.services.StorageService;
import com.ystrazhko.git.xml.MapAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class keeps data about properties.
 *
 * @author Pavlo Pidhorniy
 */
@XmlRootElement
public class ProgramProperties {
    private static final String MOCK_SERVERNAME = "gitlab.com";
    private static final String PATH_SEPARATOR = "\\";

    private static ProgramProperties _instance;


    /**
     * Already cloned groups (groupId, localpath)
     */

    private Map<Group, String> _groupPathMap;

    private StorageService _storageService =
            (StorageService) ServiceProvider.getInstance().getService(StorageService.class.getName());

    private LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());


    public static ProgramProperties getInstance() {
        if (_instance == null) {
            _instance = new ProgramProperties();
        }

        return _instance;
    }

    private ProgramProperties() {

    }

    @XmlJavaTypeAdapter(MapAdapter.class)
    public void setGroupPathMap(Map<Group, String> map) {
        this._groupPathMap = map;
    }

    public Map<Group, String> getGroupPathMap() {
        return _groupPathMap;
    }

    public void updateClonedGroups(List<Group> groups, String localParentPath) {
        _groupPathMap = groups.stream().collect(
                Collectors.toMap(x -> x, x -> localParentPath + PATH_SEPARATOR + x.getName()));

        String username = _loginService.getCurrentUser().getUsername();
        _storageService.updateStorage(MOCK_SERVERNAME, username);
    }

    public List<Group> getClonedGroups() {

        String username = _loginService.getCurrentUser().getUsername();
        if (_groupPathMap == null) {
            setGroupPathMap(_storageService.loadStorage(MOCK_SERVERNAME, username));
        }
        return _groupPathMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }


}
