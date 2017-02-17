package com.ystrazhko.git.services;

import java.util.HashMap;
import java.util.Map;

import com.ystrazhko.git.connections.RESTConnectorFactory;

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
        _services.put(LoginServiceImpl.class.getName(),
                new LoginServiceImpl(new RESTConnectorFactory().getRESTConnector()));

    }
}
