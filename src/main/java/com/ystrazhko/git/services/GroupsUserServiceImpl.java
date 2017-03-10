package com.ystrazhko.git.services;

import java.util.HashMap;
import java.util.Map;

import com.ystrazhko.git.connections.RESTConnector;
import com.ystrazhko.git.util.JSONParser;

public class GroupsUserServiceImpl implements GroupsUserService {
    private RESTConnector _connector;
    private Map<String, Object> _userData;

    public static final String PRIVATE_TOKEN_KEY = "PRIVATE-TOKEN";
    public static String PRIVATE_TOKEN_VALUE;

    public GroupsUserServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object getGroups(String userData) {
//        User user = null;
//        user = gson.fromJson((String) userData, User.class);
        if ((_userData = JSONParser.parseToMap(userData)) != null) {
            PRIVATE_TOKEN_VALUE = _userData.get("private_token").toString();
            HashMap<String, String> header = new HashMap<>();
            header.put(PRIVATE_TOKEN_KEY, PRIVATE_TOKEN_VALUE);
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
