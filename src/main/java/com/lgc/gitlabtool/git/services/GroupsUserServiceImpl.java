package com.lgc.gitlabtool.git.services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.JGitInternalException;

import com.google.gson.reflect.TypeToken;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.User;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.statuses.CloningStatus;
import com.lgc.gitlabtool.git.util.JSONParser;
import com.lgc.gitlabtool.git.util.PathUtilities;

public class GroupsUserServiceImpl implements GroupsUserService {

    private static final Logger logger = LogManager.getLogger(GroupsUserServiceImpl.class);

    private RESTConnector _connector;

    private static String privateTokenKey;
    private static String privateTokenValue;

    private static final String GROUP_ALREADY_LOADED_MESSAGE = "The group with this path is already loaded.";
    private static final String GROUP_DOESNT_EXIST_MESSAGE = "This group does not exist.";
    private static final String ERROR_GETTING_GROUP_MESSAGE = "Error getting group from GitLab.";
    private static final String GROUP_DOESNT_HAVE_PROJECTS_MESSAGE = "The group has no projects.";
    private static final String INCCORECT_DATA_MESSAGE = "ERROR: Incorrect data.";
    private static final String PREFIX_SUCCESSFUL_LOAD = " uploaded.";

    private static ClonedGroupsService _clonedGroupsService;

    public GroupsUserServiceImpl(RESTConnector connector, ClonedGroupsService clonedGroupsService) {
        setConnector(connector);
        setClonedGroupsService(clonedGroupsService);
    }

    @Override
    public Object getGroups(User user) {
        privateTokenValue = CurrentUser.getInstance().getPrivateTokenValue();
        privateTokenKey = CurrentUser.getInstance().getPrivateTokenKey();
        if (privateTokenValue != null) {
            HashMap<String, String> header = new HashMap<>();
            header.put(privateTokenKey, privateTokenValue);
            Object userProjects = getConnector().sendGet("/groups", null, header);
            return JSONParser.parseToCollectionObjects(userProjects, new TypeToken<List<Group>>() {
            }.getType());
        }

        return null;
    }

    private Group cloneGroup(Group group, String destinationPath, ProgressListener progressListener) {
        try {
            if (group.getProjects() == null) {
                group = getGroupById(group.getId());
            }
            JGit.getInstance().clone(group, destinationPath, progressListener);
        } catch (JGitInternalException ex) {
            logger.error(ex.getStackTrace());
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
    public Map<Group, CloningStatus> cloneGroups(List<Group> groups, String destinationPath,
            ProgressListener progressListener) {

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
            // TODO pass onStart here
            Group clonedGroup = cloneGroup(groupItem, destinationPath, progressListener);
            statusMap.put(clonedGroup, getStatus(clonedGroup));
        }

        List<Group> clonedGroups = statusMap.entrySet().stream()
                .filter(map -> map.getValue() == (CloningStatus.SUCCESSFUL)).map(Map.Entry::getKey)
                .collect(Collectors.toList());

        _clonedGroupsService.addGroups(clonedGroups);
        return statusMap;
    }

    @Override
    public Map<Optional<Group>, String> importGroup(String groupPath) {
        if (groupPath == null || groupPath.isEmpty()) {
            throw new IllegalArgumentException(INCCORECT_DATA_MESSAGE);
        }
        Path path = Paths.get(groupPath);
        if (!PathUtilities.isExistsAndDirectory(path)) {
            Map<Optional<Group>, String> result = new HashMap<>();
            result.put(Optional.empty(), PathUtilities.PATH_NOT_EXISTS_OR_NOT_DIRECTORY);
            return result;
        }
        return importGroup(path);
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

    private void setClonedGroupsService(ClonedGroupsService clonedGroupsService) {
        if (clonedGroupsService != null) {
            _clonedGroupsService = clonedGroupsService;
        }
    }

    private Map<Optional<Group>, String> importGroup(Path groupPath) {
        Map<Optional<Group>, String> result = new HashMap<>();
        String nameGroup = groupPath.getName(groupPath.getNameCount() - 1).toString();
        if (checkGroupIsLoaded(groupPath.toAbsolutePath().toString())) {
            logger.debug(GROUP_ALREADY_LOADED_MESSAGE);
            result.put(Optional.empty(), GROUP_ALREADY_LOADED_MESSAGE);
            return result;
        }
        Optional<Group> optFoundGroup = getGroupByName(nameGroup);
        if (!optFoundGroup.isPresent()) {
            logger.debug(GROUP_DOESNT_EXIST_MESSAGE);
            result.put(Optional.empty(), GROUP_DOESNT_EXIST_MESSAGE);
            return result;
        }
        Group foundGroup = getGroupById(optFoundGroup.get().getId());
        if (foundGroup == null) {
            logger.debug(ERROR_GETTING_GROUP_MESSAGE);
            result.put(Optional.empty(), ERROR_GETTING_GROUP_MESSAGE);
            return result;
        }
        foundGroup.setPathToClonedGroup(groupPath.toString());
        foundGroup.setClonedStatus(true);
        return updateProjectsInGroup(foundGroup, groupPath);
    }

    @Override
    public Map<Boolean, String> removeGroup(Group removeGroup, boolean isRemoveFromLocalDisk) {
        Map<Boolean, String> result = new HashMap<>();
        logger.info("Deleting group is started ...");
        boolean isRemoved = _clonedGroupsService.removeGroups(Arrays.asList(removeGroup));
        if (!isRemoved) {
            logger.warn("Failed deleting of group from the workspace.");
            result.put(false, "Failed deleting of group from the workspace.");
            return result;
        }
        if (isRemoveFromLocalDisk) {
            logger.error("Deleting group from local disk is started ...");
            return deleteGroupFromDirectory(removeGroup.getPathToClonedGroup());
        }
        logger.info("Successful deleting of group from the workspace.");
        result.put(isRemoved, "Successful deleting of group from the workspace.");
        return result;
    }

    private Map<Boolean, String> deleteGroupFromDirectory(String pathToClonedGroup) {
        Map<Boolean, String> result = new HashMap<>();
        if (pathToClonedGroup == null) {
            logger.warn("Error removing. The path to the cloned group is not specified.");
            result.put(false, "Error removing. The path to the cloned group is not specified.");
            return result;
        }
        Path path = Paths.get(pathToClonedGroup);
        if (!PathUtilities.isExistsAndDirectory(path)) {
            logger.warn("Error removing. The specified path does not exist or is not a directory.");
            result.put(false, "Error removing. The specified path does not exist or is not a directory.");
            return result;
        }

        boolean deleteResult = PathUtilities.deletePath(path);
        if (deleteResult) {
            String successMessage = "The group was successfully deleted from " + path.toString();
            logger.info(successMessage);
            result.put(true, successMessage);
            return result;
        } else {
            String errorMessage = "Error removing folder " + path.toString();
            result.put(false, errorMessage);
            logger.error(errorMessage);
        }
        return result;
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

    private Optional<Group> findGroupByPath(Collection<Group> groups, String groupPath) {
        return groups.stream().filter(group -> group.getPathToClonedGroup().equals(groupPath)).findFirst();
    }

    private Map<Optional<Group>, String> updateProjectsInGroup(Group group, Path localPathGroup) {
        Map<Optional<Group>, String> result = new HashMap<>();
        Collection<Project> projects = group.getProjects();
        if (projects == null || projects.isEmpty()) {
            logger.debug(GROUP_DOESNT_HAVE_PROJECTS_MESSAGE);
            result.put(Optional.empty(), GROUP_DOESNT_HAVE_PROJECTS_MESSAGE);
            return result;
        }
        Collection<String> projectsName = PathUtilities.getFolders(localPathGroup);
        if (projectsName.isEmpty()) {
            logger.debug(group.getName() + " " + PREFIX_SUCCESSFUL_LOAD);
            result.put(Optional.of(group), group.getName() + PREFIX_SUCCESSFUL_LOAD);
            return result;
        }
        projects.stream().filter(project -> projectsName.contains(project.getName()))
                .forEach((project) -> updateProjectStatus(project, localPathGroup.toString()));
        logger.debug(group.getName() + " " + PREFIX_SUCCESSFUL_LOAD);
        result.put(Optional.of(group), group.getName() + PREFIX_SUCCESSFUL_LOAD);
        return result;
    }

    private void updateProjectStatus(Project project, String pathGroup) {
        project.setClonedStatus(true);
        project.setPathToClonedProject(pathGroup + File.separator + project.getName());
        ProjectTypeService typeService = (ProjectTypeService) ServiceProvider.getInstance()
                .getService(ProjectTypeService.class.getName());
        project.setProjectType(typeService.getProjectType(project));
    }

    private boolean checkGroupIsLoaded(String localPathGroup) {
        List<Group> loadedGroups = getLoadedGroups();
        return loadedGroups == null ? false : findGroupByPath(loadedGroups, localPathGroup).isPresent();
    }

    private List<Group> getLoadedGroups() {
        return _clonedGroupsService.loadClonedGroups();
    }
}