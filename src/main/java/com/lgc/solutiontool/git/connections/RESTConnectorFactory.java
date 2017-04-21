package com.lgc.solutiontool.git.connections;

import com.lgc.solutiontool.git.exceptions.HTTPExceptionProvider;

public class RESTConnectorFactory {
	
	private RESTConnector restConnector;
	
	private RESTConnectorFactory() {
		restConnector = new RESTConnectorImpl(HTTPExceptionProvider.getInstance());
	}
	
	private static class RESTConnectorFactoryHolder {
		private final static RESTConnectorFactory INSTANCE = new RESTConnectorFactory();
	}
	
	public static RESTConnectorFactory getInstance() {
		return RESTConnectorFactoryHolder.INSTANCE;
	}

    public RESTConnector getRESTConnector() {
    	return restConnector;
    }
}
