package com.lgc.solutiontool.git.services;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.properties.ProgramProperties;
import com.lgc.solutiontool.git.util.XMLParser;
import com.lgc.solutiontool.git.xml.Servers;


public class StorageServiceImpl implements StorageService {
    private static final String USER_HOME_PROPERTY = "user.home";
    private static final String WORKSPACE_DIRECTORY_PROPERTY = ".SolutionTool";
    private static final String SETTINGS_DIRECTORY_PROPERTY = ".settings";
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
            System.err.println(this.getClass().getName() + ".updateStorage: " + e.getMessage()); // TODO move to logger
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
            System.err.println(this.getClass().getName() + ".loadStorage: " + e.getMessage()); // TODO move to logger
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
            System.err.println(this.getClass().getName() + ".updateServers: " + e.getMessage()); // TODO move to logger
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
            System.err.println(this.getClass().getName() + ".loadServers: " + e.getMessage()); // TODO move to logger
        } finally {
            if (servers == null) {
                updateServers(new Servers());
                servers = loadServers();
            }
        }
        return servers;
    }

    private File getPropFile(String server, String username) throws IOException {
        File propFile = new File(_workingDirectory + PATH_SEPARATOR + server + PATH_SEPARATOR + username
                + PATH_SEPARATOR + PROPERTY_FILENAME);
        return getFile(propFile);
    }

    private File getServersFile() throws IOException {
        File serverFile = new File(
                _workingDirectory + PATH_SEPARATOR + SETTINGS_DIRECTORY_PROPERTY + PATH_SEPARATOR + SERVERS_FILENAME);
        return getFile(serverFile);
    }

    private File getFile(File file) throws IOException {
        File parentDirectory = file.getParentFile();

        if (file.exists()) {
            return new File(file.getCanonicalPath());
        }

        if (!parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }
        file.createNewFile();

        return new File(file.getCanonicalPath());
    }

}
