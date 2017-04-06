package com.lgc.solutiontool.git.services;

import com.lgc.solutiontool.git.connections.RESTConnector;
import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.properties.ProgramProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StorageServiceImpl implements StorageService {
    private static final String USER_HOME_PROPERTY = "user.home";
    private static final String WORKSPACE_DIRECTORY_PROPERTY = ".SolutionTool";
    private static final String PATH_SEPARATOR = "\\";
    private static final String PROPERTY_FILENAME = "properties.xml";

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

            JAXBContext jaxbContext = JAXBContext.newInstance(ProgramProperties.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(ProgramProperties.getInstance(), file);

            return true;
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<Group, String> loadStorage(String server, String username) {
        try {
            File file = getPropFile(server, username);
            JAXBContext jaxbContext = JAXBContext.newInstance(ProgramProperties.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return ((ProgramProperties) jaxbUnmarshaller.unmarshal(file)).getGroupPathMap();
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
            return new HashMap<Group, String>();
        }
    }

    private File getPropFile(String server, String username) throws IOException {
        File propFile = new File(_workingDirectory + PATH_SEPARATOR + server + PATH_SEPARATOR + username +
                PATH_SEPARATOR + PROPERTY_FILENAME);
        File parentDor = propFile.getParentFile();

        if (propFile.exists()) {
            return new File(propFile.getCanonicalPath());
        }

        if (!parentDor.exists()) {
            parentDor.mkdirs();
        }
        propFile.createNewFile();

        return new File(propFile.getCanonicalPath());
    }
}
