package com.lgc.gitlabtool.git.services;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.google.inject.internal.util.Objects;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.User;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.util.JSONParser;
import com.lgc.gitlabtool.git.util.PathUtilities;

public class GroupsServiceImpl implements GroupService {

    private static final Logger logger = LogManager.getLogger(GroupsServiceImpl.class);

    private RESTConnector _connector;

    private static String privateTokenKey;
    private static String privateTokenValue;

    private static final String GROUP_ALREADY_LOADED_MESSAGE = "The group with this path is already loaded.";
    private static final String GROUP_DOESNT_EXIST_MESSAGE = "This group does not exist.";
    private static final String ERROR_GETTING_GROUP_MESSAGE = "Error getting group from GitLab.";

    private static ClonedGroupsService _clonedGroupsService;
    private static ProjectService _projectService;
    private static StateService _stateService;
    private static ConsoleService _consoleService;
    private static final String SEPARATOR = File.separator;

    private static JGit _jGit;

    public GroupsServiceImpl(RESTConnector connector,
                                 ClonedGroupsService clonedGroupsService,
                                 ProjectService projectService,
                                 StateService stateService,
                                 ConsoleService consoleService,
                                 JGit jGit) {
        setConnector(connector);
        setClonedGroupsService(clonedGroupsService);
        setProjectService(projectService);
        setStateService(stateService);
        setConsoleService(consoleService);
        setJGit(jGit);
    }

    @Override
    public Collection<Group> getGroups(User user) {
        privateTokenValue = CurrentUser.getInstance().getOAuth2TokenValue();
        privateTokenKey = CurrentUser.getInstance().getPrivateTokenKey();
        if (privateTokenValue != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put(privateTokenKey, privateTokenValue);
            Object userProjects = getConnector().sendGet("/groups", null, header).getBody();
            Collection<Group> parsedGroups = JSONParser.parseToCollectionObjects(
                    userProjects, new TypeToken<List<Group>>() {}.getType());
            setGroupsTheirSubGroups((List<Group>)parsedGroups);
            return parsedGroups;
        }

        return null;
    }

    @Override
    public void cloneGroups(List<Group> groups, String destinationPath, OperationProgressListener progressListener) {
        if (groups == null || destinationPath == null) {
            throw new IllegalArgumentException("Invalid parameters.");
        }
        // we must call StateService::stateOFF for this state in the ProgressListener::onFinish method
        _stateService.stateON(ApplicationState.CLONE);

        Path path = Paths.get(destinationPath);
        if (!PathUtilities.isExistsAndDirectory(path)) {
            String errorMessage = path.toAbsolutePath() + " path is not exist or it is not a directory.";
            logger.error(errorMessage);
            progressListener.onError(null, errorMessage);
            progressListener.onFinish(null, false);
            return;
        }
        groups.forEach((group) -> cloneGroup(group, destinationPath, progressListener));
    }

    @Override
    public Group importGroup(String groupPath) {
        _consoleService.addMessage("Started import of group from: " + groupPath, MessageType.SIMPLE);
        if (groupPath == null || groupPath.isEmpty()) {
            return null;
        }
        Path path = Paths.get(groupPath);
        if (!PathUtilities.isExistsAndDirectory(path)) {
            _consoleService.addMessage("Path doesn't exist or it is not directory", MessageType.ERROR);
            return null;
        }
        return importGroup(path);
    }

    @Override
    public Map<Boolean, String> removeGroup(Group removeGroup, boolean isRemoveFromLocalDisk) {
        Map<Boolean, String> result = new HashMap<>();
        _consoleService.addMessage("Deleting group is started ...", MessageType.SIMPLE);
        boolean isRemoved = _clonedGroupsService.removeGroups(Arrays.asList(removeGroup));
        String message;
        if (!isRemoved) {
            message = "Failed deleting of group from the workspace.";
            _consoleService.addMessage(message, MessageType.ERROR);
            result.put(false, message);
            return result;
        }
        if (isRemoveFromLocalDisk) {
            return deleteGroupFromDirectory(removeGroup.getPath());
        }
        message = "Successful deleting of group from the workspace.";
        _consoleService.addMessage(message, MessageType.SUCCESS);
        result.put(isRemoved, message);
        return result;
    }

    @Override
    public List<Group> getOnlyMainGroups(List<Group> groups) {
        return groups.stream()
                     .filter(group -> group.getParentId() == null)
                     .collect(Collectors.toList());
    }

    @Override
    public Group reloadGroup(Group group) {
        Optional<Group> optLoadedGroup = getGroupById(group.getId());
        if (optLoadedGroup.isPresent()) {
            Group loadedGroup = optLoadedGroup.get();
            loadedGroup.setPath(group.getPath());
            loadedGroup.setClonedStatus(true);
            return loadedGroup;
        }
        _consoleService.addMessage("Failed reload group", MessageType.ERROR);
        return group;
    }

    private Optional<Group> getGroupById(int idGroup) {
        Collection<Group> groups = getGroups(CurrentUser.getInstance().getCurrentUser());
        if (groups == null || groups.isEmpty()) {
            return null;
        }
        return findGroupById(groups, idGroup);
    }

    private Optional<Group> findGroupById(Collection<Group> groups, int idGroup) {
        return groups.stream()
                     .filter(group -> group.getId() == idGroup)
                     .findFirst();
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }

    private void setClonedGroupsService(ClonedGroupsService clonedGroupsService) {
        if (clonedGroupsService != null) {
            _clonedGroupsService = clonedGroupsService;
        }
    }

    private void setProjectService(ProjectService projectService) {
        if (projectService != null) {
            _projectService = projectService;
        }
    }

    private void setStateService(StateService stateService) {
        if (stateService != null) {
            _stateService = stateService;
        }
    }

    private void setConsoleService(ConsoleService consoleService) {
        if (consoleService != null) {
            _consoleService = consoleService;
        }
    }

    private void setJGit(JGit jGit) {
        if (jGit != null) {
            _jGit = jGit;
        }
    }

    private Group importGroup(Path groupPath) {
        String nameGroup = groupPath.getName(groupPath.getNameCount() - 1).toString();
        if (checkGroupIsLoaded(groupPath.toAbsolutePath().toString())) {
            _consoleService.addMessage(GROUP_ALREADY_LOADED_MESSAGE, MessageType.SIMPLE);
            return null;
        }
        Optional<Group> optFoundGroup = getGroupByName(nameGroup);
        if (!optFoundGroup.isPresent()) {
            _consoleService.addMessage(GROUP_DOESNT_EXIST_MESSAGE, MessageType.ERROR);
            return null;
        }
        Group foundGroup = optFoundGroup.get();
        foundGroup.setPath(groupPath.toString());
        foundGroup.setClonedStatus(true);
        _clonedGroupsService.addGroups(Arrays.asList(foundGroup));
        return foundGroup;
    }

    private Map<Boolean, String> deleteGroupFromDirectory(String pathToClonedGroup) {
        Map<Boolean, String> result = new HashMap<>();
        String message;
        if (pathToClonedGroup == null) {
            message = "Error removing. The path to the cloned group is not specified.";
            _consoleService.addMessage(message, MessageType.ERROR);
            result.put(false, message);
            return result;
        }
        Path path = Paths.get(pathToClonedGroup);
        if (!PathUtilities.isExistsAndDirectory(path)) {
            message = "Error removing. The specified path does not exist or is not a directory.";
            _consoleService.addMessage(message, MessageType.ERROR);
            result.put(false, message);
            return result;
        }

        boolean deleteResult = PathUtilities.deletePath(path);
        if (deleteResult) {
            message = "The group was successfully deleted from " + path.toString();
            _consoleService.addMessage(message, MessageType.SUCCESS);
            result.put(true, message);
            return result;
        } else {
            message = "Error removing folder " + path.toString();
            _consoleService.addMessage(message, MessageType.ERROR);
            result.put(false, message);
            return result;
        }
    }

    private Optional<Group> getGroupByName(String nameGroup) {
        Collection<Group> groups = getGroups(CurrentUser.getInstance().getCurrentUser());
        if (groups == null || groups.isEmpty()) {
            return Optional.empty();
        }
        return findGroupByName(groups, nameGroup);
    }

    private Optional<Group> findGroupByName(Collection<Group> groups, String nameGroup) {
        return groups.stream()
                     .filter(group -> group.getName().equals(nameGroup))
                     .findFirst();
    }

    private Optional<Group> findGroupByPath(Collection<Group> groups, String groupPath) {
        return groups.stream()
                     .filter(group -> group.getPath().equals(groupPath))
                     .findFirst();
    }

    private boolean checkGroupIsLoaded(String localPathGroup) {
        List<Group> loadedGroups = getLoadedGroups();
        return loadedGroups == null ? false : findGroupByPath(loadedGroups, localPathGroup).isPresent();
    }

    private List<Group> getLoadedGroups() {
        return _clonedGroupsService.loadClonedGroups();
    }

    private void setGroupsTheirSubGroups(List<Group> groupsFromGitLab) {
        for (Group group : groupsFromGitLab) {
            Integer parentId = group.getParentId();
            if (parentId != null) {
                Optional<Group> optGroup = foundGroupByParentId(groupsFromGitLab, parentId);
                if (optGroup.isPresent()) {
                    optGroup.get().addSubGroup(group);
                }
            }
        }
    }

    private Optional<Group> foundGroupByParentId(List<Group> groups, Integer parentId) {
        return groups.stream()
                     .filter(group -> Objects.equal(group.getId(), parentId))
                     .findFirst();
    }

    private void cloneGroup(Group cloneGroup, String destinationPath, OperationProgressListener progressListener) {
        Collection<Project> allProjects = _projectService.getProjects(cloneGroup);
        if (allProjects.isEmpty()) {
            progressListener.onFinish("Clone is finished. Could not get projects for the " + cloneGroup.getFullPath());
            return;
        }
        String pathMainGroup = destinationPath + File.separator + cloneGroup.getName();
        PathUtilities.createPath(Paths.get(pathMainGroup), true);
        _jGit.clone(allProjects, destinationPath, progressListener);
        cloneGroup.setClonedStatus(true);
        cloneGroup.setPath(pathMainGroup);
        _clonedGroupsService.addGroups(Arrays.asList(cloneGroup));
    }
}