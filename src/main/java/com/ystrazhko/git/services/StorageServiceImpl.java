package com.ystrazhko.git.services;

import com.ystrazhko.git.connections.RESTConnector;
import com.ystrazhko.git.entities.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;

public class StorageServiceImpl implements StorageService {
    private RESTConnector _connector;
    private String _workingDirectory;


    public StorageServiceImpl(RESTConnector connector) {
        setConnector(connector);
        _workingDirectory = System.getProperty("user.home") + "\\.SolutionTool";
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
            String currentPath = getPropFile(server, username);
            File file = new File(currentPath);

            JAXBContext jaxbContext = JAXBContext.newInstance(Properties.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(Properties.getInstance(), file);

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getPropFile(String server, String username) throws IOException {
        File propFile = new File(_workingDirectory + "\\" + server + "\\" + username + "\\properties.xml");
        File parentDor = propFile.getParentFile();

        if (propFile.exists()) {
            return propFile.getCanonicalPath();
        }

        if (!parentDor.exists()) {
            parentDor.mkdirs();
        }
        propFile.createNewFile();

        return propFile.getCanonicalPath();
    }
}
