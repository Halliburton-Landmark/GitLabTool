package com.lgc.solutiontool.git.services;

import java.util.HashMap;
import java.util.Map;

import com.lgc.solutiontool.git.connections.RESTConnectorFactory;

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
        _services = new HashMap<>();
        _services.put(LoginService.class.getName(),
                new LoginServiceImpl(RESTConnectorFactory.getInstance().getRESTConnector()));
        _services.put(GroupsUserService.class.getName(),
                new GroupsUserServiceImpl(RESTConnectorFactory.getInstance().getRESTConnector()));
        _services.put(ProjectService.class.getName(),
                new ProjectServiceImpl(RESTConnectorFactory.getInstance().getRESTConnector()));
        _services.put(StorageService.class.getName(),
                new StorageServiceImpl(RESTConnectorFactory.getInstance().getRESTConnector()));
        _services.put(ReplacementService.class.getName(), new ReplacementServiceImpl());
        _services.put(PomXMLService.class.getName(), new PomXMLServiceImpl());
        _services.put(ProjectTypeService.class.getName(), new ProjectTypeServiceImpl());
    }
}