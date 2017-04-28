package com.lgc.solutiontool.git.connections;

import com.lgc.solutiontool.git.exceptions.HTTPExceptionProvider;

public class RESTConnectorFactory {
	
	private static RESTConnectorFactory instance;
	
	private RESTConnector restConnector;
	
	private RESTConnectorFactory() {
		restConnector = new RESTConnectorImpl(HTTPExceptionProvider.getInstance());
	}
	
	public static RESTConnectorFactory getInstance() {
		if (instance == null) {
			instance = new RESTConnectorFactory();
		}
		return instance;
	}

    public RESTConnector getRESTConnector() {
    	return restConnector;
    }
}
