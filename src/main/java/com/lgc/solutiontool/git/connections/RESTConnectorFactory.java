package com.lgc.solutiontool.git.connections;

import com.lgc.solutiontool.git.exceptions.HTTPExceptionProvider;

public class RESTConnectorFactory {

    public RESTConnector getRESTConnector() {
        return new RESTConnectorImpl(HTTPExceptionProvider.getInstance());
    }
}
