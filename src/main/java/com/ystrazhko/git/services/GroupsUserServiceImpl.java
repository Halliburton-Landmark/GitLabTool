package com.ystrazhko.git.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;
import com.ystrazhko.git.connections.RESTConnector;
import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.Properties.Properties;
import com.ystrazhko.git.entities.User;
import com.ystrazhko.git.jgit.JGit;
import com.ystrazhko.git.statuses.CloningStatus;
import com.ystrazhko.git.util.JSONParser;
import org.eclipse.jgit.api.errors.JGitInternalException;

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
        String sendString = "/groups/" + idGroup;
        HashMap<String, String> header = new HashMap<>();
        header.put(GroupsUserServiceImpl.PRIVATE_TOKEN_KEY, GroupsUserServiceImpl.PRIVATE_TOKEN_VALUE);

        Object uparsedGroup = getConnector().sendGet(sendString, null, header);
        return JSONParser.parseToObject(uparsedGroup, Group.class);
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
        Properties.getInstance().updateClonedGroups(clonedGroups, destinationPath);
        return statusMap;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }
}
