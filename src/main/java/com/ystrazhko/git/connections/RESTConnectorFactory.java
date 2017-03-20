package com.ystrazhko.git.connections;

import com.ystrazhko.git.exceptions.HTTPExceptionProvider;

public class RESTConnectorFactory {

    public RESTConnector getRESTConnector() {
        return new RESTConnectorImpl(HTTPExceptionProvider.getInstance());
    }
}
