package com.lgc.gitlabtool.git.connections;

public class RESTConnectorFactory {

    private static RESTConnectorFactory _instance;

    private final RESTConnector _restConnector;

    private RESTConnectorFactory() {
        _restConnector = new RESTConnectorImpl();
    }

    public static RESTConnectorFactory getInstance() {
        if (_instance == null) {
            _instance = new RESTConnectorFactory();
        }
        return _instance;
    }

    public RESTConnector getRESTConnector() {
        return _restConnector;
    }
}
