package com.ystrazhko.git.services;

import java.util.HashMap;
import java.util.Map;

import com.ystrazhko.git.connections.RESTConnector;
import com.ystrazhko.git.util.JSONParser;

public class GroupsUserServiceImpl implements GroupsUserService {
    private RESTConnector _connector;
    private Map<String, Object> _userData;

    private static final String PRIVATE_TOKEN_KEY = "PRIVATE-TOKEN";

    public GroupsUserServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object getGroups(String userData) {
        if ((_userData = JSONParser.parseToMap(userData)) != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put(PRIVATE_TOKEN_KEY, _userData.get("private_token").toString());
            return getConnector().sendGet("/groups", null, header);
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
