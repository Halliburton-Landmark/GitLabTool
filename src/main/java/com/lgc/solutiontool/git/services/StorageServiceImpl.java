package com.lgc.solutiontool.git.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.lgc.solutiontool.git.connections.RESTConnector;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.properties.ProgramProperties;
import com.lgc.solutiontool.git.util.XMLParser;
import com.lgc.solutiontool.git.xml.Servers;

public class StorageServiceImpl implements StorageService {
    private static final String USER_HOME_PROPERTY = "user.home";
    private static final String WORKSPACE_DIRECTORY_PROPERTY = ".SolutionTool";
    private static final String SETTINGS_DIRECTORY_PROPERTY = ".settings";
    private static final String PATH_SEPARATOR = "\\";
    private static final String PROPERTY_FILENAME = "properties.xml";
    private static final String SERVERS_FILENAME = "servers.xml";

    private RESTConnector _connector;
    private String _workingDirectory;


    public StorageServiceImpl(RESTConnector connector) {
        setConnector(connector);
        _workingDirectory = System.getProperty(USER_HOME_PROPERTY) + PATH_SEPARATOR + WORKSPACE_DIRECTORY_PROPERTY;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }

    @Override
    public boolean updateStorage(String server, String username) {
        try {
            File file = getPropFile(server, username);
            XMLParser.saveObject(file, ProgramProperties.getInstance());
            return true;
        } catch (IOException | JAXBException e) {
            return false;
        }
    }

    @Override
    public Map<Group, String> loadStorage(String server, String username) {
        try {
            File file = getPropFile(server, username);
            return XMLParser.loadObject(file, ProgramProperties.class).getGroupPathMap();
        } catch (IOException | JAXBException e) {
            return new HashMap<>();
        }
    }

	@Override
	public boolean updateServers(Servers servers) {
		try {
			File file = getServersFile();
			XMLParser.saveObject(file, servers);
			return true;
		} catch (IOException | JAXBException e) {
            return false;
        }
	}

	@Override
	public Servers loadServers() {
		try {
			File file = getServersFile();
            return XMLParser.loadObject(file, Servers.class);
        } catch (IOException | JAXBException e) {
        	updateServers(new Servers());
            return loadServers();
        }
	}
	
    private File getPropFile(String server, String username) throws IOException {
        File propFile = new File(_workingDirectory + PATH_SEPARATOR + server + PATH_SEPARATOR + username
                + PATH_SEPARATOR + PROPERTY_FILENAME);
        return getFile(propFile);
    }
    
	private File getServersFile() throws IOException {
		File serverFile = new File(_workingDirectory + PATH_SEPARATOR + SETTINGS_DIRECTORY_PROPERTY 
				+ PATH_SEPARATOR + SERVERS_FILENAME);
		return getFile(serverFile);
	}
    
    private File getFile(File file) throws IOException {
    	File parentDor = file.getParentFile();

        if (file.exists()) {
            return new File(file.getCanonicalPath());
        }

        if (!parentDor.exists()) {
            parentDor.mkdirs();
        }
        file.createNewFile();

        return new File(file.getCanonicalPath());
    }
    
}
