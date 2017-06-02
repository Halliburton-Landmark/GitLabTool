package com.lgc.solutiontool.git.services;

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

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.properties.ProgramProperties;
import com.lgc.solutiontool.git.util.XMLParser;
import com.lgc.solutiontool.git.xml.Servers;


public class StorageServiceImpl implements StorageService {
    private static final Logger logger = LogManager.getLogger(StorageServiceImpl.class);
    
    private static final String USER_HOME_PROPERTY = "user.home";
    private static final String WORKSPACE_DIRECTORY_PROPERTY = ".SolutionTool";
    private static final String PATH_SEPARATOR = File.separator;
    private static final String PROPERTY_FILENAME = "properties.xml";
    private static final String SERVERS_FILENAME = "servers.xml";

    private final String _workingDirectory;

    public StorageServiceImpl() {
        _workingDirectory = System.getProperty(USER_HOME_PROPERTY) + PATH_SEPARATOR + WORKSPACE_DIRECTORY_PROPERTY;
    }

    @Override
    public boolean updateStorage(String server, String username) {
        try {
            File file = getPropFile(server, username);
            XMLParser.saveObject(file, ProgramProperties.getInstance());
            return true;
        } catch (IOException | JAXBException e) {
            logger.error("", e);
            return false;
        }
    }

    @Override
    public List<Group> loadStorage(String server, String username) {
        try {
            File file = getPropFile(server, username);
            List<Group> list = XMLParser.loadObject(file, ProgramProperties.class).getClonedGroups();
            return list == null ? Collections.emptyList() : list;
        } catch (IOException | JAXBException e) {
            logger.error("", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateServers(Servers servers) {
        try {
            File file = getServersFile();
            XMLParser.saveObject(file, servers);
            return true;
        } catch (IOException | JAXBException e) {
            logger.error("", e);
            return false;
        }
    }

    @Override
    public Servers loadServers() {
        Servers servers = null;
        try {
            File file = getServersFile();
            servers = XMLParser.loadObject(file, Servers.class);
        } catch (IOException | JAXBException e) {
            logger.error(e.getMessage());
            logger.warn("servers.xml file empty or does not exist. Load defaults");
        } finally {
            if (servers == null) {
                updateServers(new Servers());
                servers = loadServers();
            }
        }
        return servers;
    }

    private File getPropFile(String server, String username) throws IOException {
        Path propertyFilePath = Paths.get(_workingDirectory + PATH_SEPARATOR + server + PATH_SEPARATOR + username
                + PATH_SEPARATOR + PROPERTY_FILENAME);
        return getFile(propertyFilePath);
    }

    private File getServersFile() throws IOException {
        Path serversFilePath = Paths.get(_workingDirectory + PATH_SEPARATOR + SERVERS_FILENAME);
        return getFile(serversFilePath);
    }

    private File getFile(Path path) throws IOException {
        if (Files.exists(path)) {
            return path.toFile();
        } else {
            File file = new File(path.toUri());
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            file.createNewFile();
            return file;
        }
    }

}
