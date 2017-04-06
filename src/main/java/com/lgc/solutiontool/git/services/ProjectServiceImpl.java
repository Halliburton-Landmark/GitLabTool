package com.lgc.solutiontool.git.services;

import java.util.HashMap;

import com.lgc.solutiontool.git.connections.RESTConnector;
import com.lgc.solutiontool.git.connections.Token.PrivateToken;

public class ProjectServiceImpl implements ProjectService {
    private RESTConnector _connector;

    private static String privateTokenKey;
    private static  String privateTokenValue;
    public ProjectServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object getProjects(String idGroup) {
        privateTokenValue = PrivateToken.getInstance().getPrivateTokenValue();
        privateTokenKey = PrivateToken.getInstance().getPrivateTokenKey();
        if (privateTokenValue != null) {
            //TODO valid id group
            String sendString = "/groups/" + idGroup + "/projects";
            HashMap<String, String> header = new HashMap<>();
            header.put(privateTokenKey, privateTokenValue);
            return getConnector().sendGet(sendString, null, header);
        }
        return null;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }
}