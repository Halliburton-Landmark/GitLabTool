package com.lgc.gitlabtool.git.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.ClonedGroups;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.util.JSONParser;
import com.lgc.gitlabtool.git.util.PathUtilities;
import com.lgc.gitlabtool.git.util.XMLParser;
import com.lgc.gitlabtool.git.xml.GroupInfo;
import com.lgc.gitlabtool.git.xml.Server;
import com.lgc.gitlabtool.git.xml.Servers;


public class StorageServiceImpl implements StorageService {
    private static final Logger logger = LogManager.getLogger(StorageServiceImpl.class);

    private static final String USER_HOME_PROPERTY = "user.home";
    private static final String WORKSPACE_DIRECTORY_PROPERTY = ".GitlabTool";
    private static final String PATH_SEPARATOR = File.separator;
    private static final String CLONED_GROUPS_FILENAME = "clonedGroups.xml";
    private static final String INFO_GROUP_FILENAME = "gltGroupInfo.xml";
    private static final String SERVERS_FILENAME = "servers.xml";

    private final String _workingDirectory;

    public StorageServiceImpl() {
        _workingDirectory = System.getProperty(USER_HOME_PROPERTY) + PATH_SEPARATOR + WORKSPACE_DIRECTORY_PROPERTY + PATH_SEPARATOR;
    }

    @Override
    public boolean updateStorage(String server, String username) {
        try {
            File file = getFile(_workingDirectory + server + PATH_SEPARATOR + username, CLONED_GROUPS_FILENAME);
            return updateStorage(file, ClonedGroups.getInstance());
        } catch (IOException | JAXBException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<Group> loadStorage(String server, String username) {
        try {
            File file = getFile(_workingDirectory + server + PATH_SEPARATOR + username, CLONED_GROUPS_FILENAME);
            ClonedGroups groupsProvider = (ClonedGroups) loadStorage(file, ClonedGroups.class);
            if (groupsProvider != null) {
                List<Group> list = groupsProvider.getClonedGroups();
                return list == null ? Collections.emptyList() : list;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public Group loadGroupInfo(String pathToGroup) {
        try {
            GroupInfo info = (GroupInfo) loadStorage(getFile(pathToGroup, INFO_GROUP_FILENAME), GroupInfo.class);
            return info.getGroup();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void updateGroupInfo(Group group) {
        if (group == null) {
            logger.error("Error in the StorageServiceImpl.updateGroupInfo method. Group is null.");
            return;
        }
        try {
            updateStorage(getFile(group.getPathToClonedGroup(), INFO_GROUP_FILENAME), new GroupInfo(JSONParser.parseObjectToJson(group)));
        } catch (IOException | JAXBException e) {
            logger.error(e);
        }
    }

    private boolean updateStorage(File file, Object object) throws JAXBException {
        XMLParser.saveObject(file, object);
        return true;
    }

    private Object loadStorage(File file, Class classPars) {
        try {
            return XMLParser.loadObject(file, classPars);
        } catch (JAXBException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateServers(Servers servers) {
        try {
            File file = getFile(_workingDirectory, SERVERS_FILENAME);
            XMLParser.saveObject(file, servers);
            return true;
        } catch (IOException | JAXBException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Servers loadServers() {
        Servers servers = null;
        try {
            File file = getFile(_workingDirectory, SERVERS_FILENAME);
            servers = XMLParser.loadObject(file, Servers.class);
        } catch (IOException | JAXBException e) {
            logger.warn(SERVERS_FILENAME + " file empty or does not exist. Load defaults");
        } finally {
            if (servers == null) {
                updateServers(new Servers());
                servers = loadServers();
            }
        }
        return servers;
    }

    private File getFile(String pathDirectory, String fileName) throws IOException {
        Path path = Paths.get(pathDirectory + PATH_SEPARATOR + fileName);
        if (!PathUtilities.isExistsAndRegularFile(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
        return path.toFile();
    }

    @Override
    public boolean updateLastUserName(String serverName, String userName) {
        Servers servers = loadServers();
        servers.getServers().forEach(server -> {
            if (server.getName().contentEquals(serverName)) {
                server.setLastUserName(userName);
                server.setLastUsed(true);
            } else {
                server.setLastUsed(false);
            }
        });

        return updateServers(servers);
    }

    @Override
    public String getLastUserName(String serverName) {
        return loadServers().getServer(serverName)
                            .map(Server::getLastUserName)
                            .orElse("");
    }

    @Override
    public Server getLastUsedServer() {
        return loadServers().getServers()
                .stream()
                .filter(Server::isLastUsed)
                .findAny()
                .orElse(null);
    }
}
