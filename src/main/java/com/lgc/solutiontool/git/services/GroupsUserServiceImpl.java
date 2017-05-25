package com.lgc.solutiontool.git.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.errors.JGitInternalException;

import com.google.gson.reflect.TypeToken;
import com.lgc.solutiontool.git.connections.RESTConnector;
import com.lgc.solutiontool.git.connections.token.CurrentUser;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.jgit.JGit;
import com.lgc.solutiontool.git.properties.ProgramProperties;
import com.lgc.solutiontool.git.statuses.CloningStatus;
import com.lgc.solutiontool.git.util.JSONParser;
import com.lgc.solutiontool.git.util.PathUtilities;

public class GroupsUserServiceImpl implements GroupsUserService {
    private RESTConnector _connector;

    private static String privateTokenKey;
    private static String privateTokenValue;

    public GroupsUserServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object getGroups(User user) {
        privateTokenValue = CurrentUser.getInstance().getPrivateTokenValue();
        privateTokenKey = CurrentUser.getInstance().getPrivateTokenKey();
        if (privateTokenValue != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put(privateTokenKey, privateTokenValue);
            Object userProjects = getConnector().sendGet("/groups", null, header);
            return JSONParser.parseToCollectionObjects(userProjects, new TypeToken<List<Group>>(){}.getType());
        }

        return null;
    }

    @Override
    public Group cloneGroup(Group group, String destinationPath, BiConsumer<Integer, Project> onSuccess, BiConsumer<Integer, String> onError) {
        try {
            if (group.getProjects() == null) {
                group = getGroupById(group.getId());
            }
            JGit.getInstance().clone(group, destinationPath, onSuccess, onError);
        } catch (JGitInternalException ex) {
            System.out.println("!Error: " + ex.getMessage());
        }
        return group;
    }

    @Override
    public Group getGroupById(int idGroup) {
        privateTokenValue = CurrentUser.getInstance().getPrivateTokenValue();
        privateTokenKey = CurrentUser.getInstance().getPrivateTokenKey();
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
    public Map<Group, CloningStatus> cloneGroups(List<Group> groups, String destinationPath, BiConsumer<Integer, Project> onSuccess, BiConsumer<Integer, String> onError) {
        if (groups == null || destinationPath == null) {
            return Collections.emptyMap();
        }

        // path validation
        Path path = Paths.get(destinationPath);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return Collections.emptyMap();
        }

        Map<Group, CloningStatus> statusMap = new HashMap<>();
        for (Group groupItem : groups) {
            Group clonedGroup = cloneGroup(groupItem, destinationPath, onSuccess, onError);
            statusMap.put(clonedGroup, getStatus(clonedGroup));
        }

        List<Group> clonedGroups = statusMap.entrySet().stream()
                .filter(map -> map.getValue() == (CloningStatus.SUCCESSFUL))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        ProgramProperties.getInstance().updateClonedGroups(clonedGroups);
        return statusMap;
    }

    private CloningStatus getStatus(Group group) {
        if (group.isCloned()) {
            return CloningStatus.SUCCESSFUL;
        }
        return CloningStatus.FAILED;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }

    @Override
    public Optional<Group> loadGroupFromLocalRepository(String groupPath) {
        if (groupPath == null || groupPath.isEmpty()) {
            throw new IllegalArgumentException("ERROR: Incorrect data.");
        }
        Path path = Paths.get(groupPath);
        if (!PathUtilities.isExistsAndDirectory(path)) {
            return Optional.empty();
        }
        return loadGroupFromLocalDisk(path);
    }

    private Optional<Group> loadGroupFromLocalDisk(Path groupPath) {
        Path nameGroup = groupPath.getName(groupPath.getNameCount()-1);
        Optional<Group> optFoundGroup = getGroupByName(nameGroup.toString());
        if (!optFoundGroup.isPresent()) {
            return Optional.empty();
        }
        Group foundGroup = getGroupById(optFoundGroup.get().getId());
        if (foundGroup == null) {
            return Optional.empty();
        }
        foundGroup.setPathToClonedGroup(groupPath.toString());
        foundGroup.setClonedStatus(true);

        updateProjectsInGroup(foundGroup.getProjects(), groupPath);
        return Optional.of(foundGroup);
    }

    private Optional<Group> getGroupByName(String nameGroup) {
        List<Group> groups = (List<Group>) getGroups(CurrentUser.getInstance().getCurrentUser());
        if (groups == null || groups.isEmpty()) {
            return Optional.empty();
        }
        return findGroupByName(groups, nameGroup);
    }

    private Optional<Group> findGroupByName(Collection<Group> groups, String nameGroup) {
        return groups.stream().filter(group -> group.getName().equals(nameGroup)).findFirst();
    }

    private void updateProjectsInGroup(Collection<Project> projects, Path localPathGroup) {
        if (projects == null || projects.isEmpty()) {
            return;
        }
        Collection<String> projectsName = PathUtilities.getFolders(localPathGroup);
        if (projectsName.isEmpty()) {
            return;
        }
        projects.stream()
                .filter(project -> projectsName.contains(project.getName()))
                .forEach((project) -> updateProjectStatus(project, localPathGroup.toString()));
    }

    private void updateProjectStatus(Project project, String pathGroup) {
        project.setClonedStatus(true);
        project.setPathToClonedProject(pathGroup + project.getName());
        ProjectTypeService typeService = (ProjectTypeService) ServiceProvider.getInstance().
                getService(ProjectTypeService.class.getName());
        project.setProjectType(typeService.getProjectType(project));
    }

}