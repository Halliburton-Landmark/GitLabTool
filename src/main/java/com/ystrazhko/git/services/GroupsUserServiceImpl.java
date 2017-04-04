package com.ystrazhko.git.services;

import java.util.HashMap;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.ystrazhko.git.connections.RESTConnector;
import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.entities.User;
import com.ystrazhko.git.util.JSONParser;

public class GroupsUserServiceImpl implements GroupsUserService {
    private RESTConnector _connector;

    public static final String PRIVATE_TOKEN_KEY = "PRIVATE-TOKEN";
    public static String PRIVATE_TOKEN_VALUE;

    public GroupsUserServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object getGroups(User user) {
        PRIVATE_TOKEN_VALUE = user.getPrivate_token();
        if (PRIVATE_TOKEN_VALUE != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put(PRIVATE_TOKEN_KEY, PRIVATE_TOKEN_VALUE);
            Object userProjects = getConnector().sendGet("/groups", null, header);

            return JSONParser.parseToCollectionObjects(userProjects, new TypeToken<List<Group>>(){}.getType());
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
