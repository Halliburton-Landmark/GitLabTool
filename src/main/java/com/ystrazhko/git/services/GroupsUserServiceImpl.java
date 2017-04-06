package com.ystrazhko.git.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;
import com.ystrazhko.git.connections.RESTConnector;
import com.ystrazhko.git.connections.Token.PrivateToken;
import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.properties.ProgramProperties;
import com.ystrazhko.git.entities.User;
import com.ystrazhko.git.jgit.JGit;
import com.ystrazhko.git.statuses.CloningStatus;
import com.ystrazhko.git.util.JSONParser;
import org.eclipse.jgit.api.errors.JGitInternalException;

public class GroupsUserServiceImpl implements GroupsUserService {
    private RESTConnector _connector;

    public static String privateTokenKey;
    public static String privateTokenValue;

    public GroupsUserServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object getGroups(User user) {
        privateTokenValue = PrivateToken.getInstance().getPrivateTokenValue();
        privateTokenKey = PrivateToken.getInstance().getPrivateTokenKey();
        if (privateTokenValue != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put(privateTokenKey, privateTokenValue);
            Object userProjects = getConnector().sendGet("/groups", null, header);

            return JSONParser.parseToCollectionObjects(userProjects, new TypeToken<List<Group>>() {
            }.getType());
        }

        return null;
    }

    @Override
    public CloningStatus cloneGroup(Group group, String destinationPath) {
        try {
            JGit.getInstance().clone(group, destinationPath);
            return CloningStatus.SUCCESSFUL;
        } catch (JGitInternalException ex) {
            return CloningStatus.FAILED;
        }

    }

    @Override
    public Group getGroupById(int idGroup) {
        //TODO valid id group
        privateTokenValue = PrivateToken.getInstance().getPrivateTokenValue();
        privateTokenKey = PrivateToken.getInstance().getPrivateTokenKey();
        if (privateTokenValue != null) {
            String sendString = "/groups/" + idGroup;
            HashMap<String, String> header = new HashMap<>();
            header.put(privateTokenKey, privateTokenValue);

            Object uparsedGroup = getConnector().sendGet(sendString, null, header);
            return JSONParser.parseToObject(uparsedGroup, Group.class);
        }
        return null;
    }

    @Override
    public Map<Group, CloningStatus> cloneGroups(List<Group> groups, String destinationPath) {
        Map<Group, CloningStatus> statusMap = new HashMap<>();
        for (Group groupItem : groups) {
            CloningStatus status = cloneGroup(groupItem, destinationPath);
            statusMap.put(groupItem, status);
        }

        List<Group> clonedGroups = statusMap.entrySet().stream()
                .filter(map -> map.getValue() == (CloningStatus.SUCCESSFUL))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        //TODO: fix issue with empty groups
        ProgramProperties.getInstance().updateClonedGroups(clonedGroups, destinationPath);
        return statusMap;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }
}
