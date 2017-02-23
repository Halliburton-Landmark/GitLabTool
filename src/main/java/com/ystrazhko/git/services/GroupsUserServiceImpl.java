package com.ystrazhko.git.services;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ystrazhko.git.connections.RESTConnector;

public class GroupsUserServiceImpl implements GroupsUserService {
    private RESTConnector _connector;
    private Map<String, Object> _userData;

    private static final String PRIVATE_TOKEN_KEY = "PRIVATE-TOKEN";

    public GroupsUserServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object getGroups(String userData) {
        if ((_userData = parseJSON(userData)) != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put(PRIVATE_TOKEN_KEY, _userData.get("private_token").toString());

            String json = (String) getConnector().sendPost("/groups", null, header, "GET");
            return json;
        }

        return null;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }

    //from json to map<String, Object>
    private Map<String, Object> parseJSON(String data) {
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return new Gson().fromJson(data, mapType);
    }
}
