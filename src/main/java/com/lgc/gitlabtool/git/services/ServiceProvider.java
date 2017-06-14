package com.lgc.gitlabtool.git.services;

import java.util.HashMap;
import java.util.Map;

import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.RESTConnectorFactory;

public class ServiceProvider {

    private static ServiceProvider _instance;

    private final Map<String, Object> _services;

    public static ServiceProvider getInstance() {
        if (_instance == null) {
            _instance = new ServiceProvider();
        }
        return _instance;
    }

    public Object getService(String serviceName) {
        return _services.get(serviceName);
    }

    private ServiceProvider() {
        RESTConnector restConnector = RESTConnectorFactory.getInstance().getRESTConnector();
        LoginService loginService = new LoginServiceImpl(restConnector);
        StorageService storageService = new StorageServiceImpl();
        ClonedGroupsService programProgertiesService = new ClonedGroupsServiceImpl(storageService, loginService);

        _services = new HashMap<>();
        _services.put(LoginService.class.getName(), loginService);
        _services.put(ClonedGroupsService.class.getName(), programProgertiesService);
        _services.put(GroupsUserService.class.getName(), new GroupsUserServiceImpl(restConnector, programProgertiesService));
        _services.put(ProjectService.class.getName(), new ProjectServiceImpl(restConnector));
        _services.put(StorageService.class.getName(), new StorageServiceImpl());
        _services.put(ReplacementService.class.getName(), new ReplacementServiceImpl());
        _services.put(PomXMLService.class.getName(), new PomXMLServiceImpl());
        _services.put(ProjectTypeService.class.getName(), new ProjectTypeServiceImpl());
        _services.put(NetworkService.class.getName(), new NetworkServiceImpl());
        _services.put(GitService.class.getName(), new GitServiceImpl());
    }
}