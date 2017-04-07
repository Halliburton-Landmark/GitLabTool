package com.lgc.solutiontool.git.properties;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.services.StorageService;
import com.lgc.solutiontool.git.xml.MapAdapter;

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
     * Already cloned groups (group, localpath)
     */
    private Map<Group, String> _groupPathMap;

    private StorageService _storageService =
            (StorageService) ServiceProvider.getInstance().getService(StorageService.class.getName());

    private LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static ProgramProperties getInstance() {
        if (_instance == null) {
            _instance = new ProgramProperties();
        }

        return _instance;
    }

    private ProgramProperties() {

    }

    /**
     * Sets the map with groups and their local paths
     *
     * @param map map with groups and their local paths
     */
    @XmlJavaTypeAdapter(MapAdapter.class)
    public void setGroupPathMap(Map<Group, String> map) {
        this._groupPathMap = map;
    }

    /**
     * Gets the map with groups and their local paths
     *
     * @return map with groups and their local paths
     */
    public Map<Group, String> getGroupPathMap() {
        return _groupPathMap;
    }

    /**
     * Updates local storage using the current properties
     */
    public void updateClonedGroups(List<Group> groups, String localParentPath) {
        _groupPathMap = groups.stream().collect(
                Collectors.toMap(x -> x, x -> localParentPath + PATH_SEPARATOR + x.getName()));

        String username = _loginService.getCurrentUser().getUsername();
        _storageService.updateStorage(MOCK_SERVERNAME, username);
    }

    /**
     * Gets a list with currently cloned groups
     *
     * @return list with groups
     */
    public List<Group> getClonedGroups() {
        String username = _loginService.getCurrentUser().getUsername();
        if (_groupPathMap == null) {
            setGroupPathMap(_storageService.loadStorage(MOCK_SERVERNAME, username));
        }
        return _groupPathMap.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }


}
