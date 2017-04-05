package com.ystrazhko.git.entities;

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
public class Properties {
    private static Properties _instance;
    private static final String MOCK_SERVERNAME = "gitlab.com";

    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<Integer, String> clonedProjects;

    private StorageService _storageService =
            (StorageService) ServiceProvider.getInstance().getService(StorageService.class.getName());

    private LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());


    public static Properties getInstance() {
        if (_instance == null) {
            _instance = new Properties();
        }

        return _instance;
    }

    private Properties() {

    }

    public void setClonedGroups(List<Group> clonedGroups, String localParentPath) {
        clonedProjects = clonedGroups.stream().collect(
                Collectors.toMap(x -> x.getId(), x -> localParentPath + "\\" + x.getName()));

        String username = _loginService.getCurrentUser().getUsername();
        _storageService.updateStorage(MOCK_SERVERNAME, username);
    }

}
