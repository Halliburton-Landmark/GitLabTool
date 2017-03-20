package com.ystrazhko.git.services;

import java.util.HashMap;

import com.ystrazhko.git.connections.RESTConnector;

public class ProjectServiceImpl implements ProjectService {
    private RESTConnector _connector;

    public ProjectServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object getProjects(String idGroup) {
        //TODO valid id group
        String sendString = "/groups/" + idGroup + "/projects";
        HashMap<String, String> header = new HashMap<>();
        header.put(GroupsUserServiceImpl.PRIVATE_TOKEN_KEY, GroupsUserServiceImpl.PRIVATE_TOKEN_VALUE);
        return getConnector().sendGet(sendString, null, header);
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }
}
