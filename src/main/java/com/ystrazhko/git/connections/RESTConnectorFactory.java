package com.ystrazhko.git.connections;

public class RESTConnectorFactory {

    public RESTConnector getRESTConnector() {
        return new RESTConnectorImpl();
    }
}
