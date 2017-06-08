package com.lgc.gitlabtool.git.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.util.JSONParser;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;

public class ProjectServiceImpl implements ProjectService {
    private RESTConnector _connector;

    private static String privateTokenKey;
    private static String privateTokenValue;

    public ProjectServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Collection<Project> getProjects(Group group) {
        privateTokenValue = CurrentUser.getInstance().getPrivateTokenValue();
        privateTokenKey = CurrentUser.getInstance().getPrivateTokenKey();
        if (privateTokenValue != null) {
            //TODO valid id group
            String sendString = "/groups/" + group.getId() + "/projects";
            HashMap<String, String> header = new HashMap<>();
            header.put(privateTokenKey, privateTokenValue);
            Object jsonProjects = getConnector().sendGet(sendString, null, header);

            return JSONParser.parseToCollectionObjects(jsonProjects, new TypeToken<List<Project>>() {
            }.getType());
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