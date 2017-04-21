package com.lgc.solutiontool.git.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.lgc.solutiontool.git.connections.RESTConnector;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.util.JSONParser;
import com.lgc.solutiontool.git.connections.token.CurrentUser;

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