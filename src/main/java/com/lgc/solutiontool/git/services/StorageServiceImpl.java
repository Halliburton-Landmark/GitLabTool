package com.lgc.solutiontool.git.services;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.properties.ProgramProperties;

public class StorageServiceImpl implements StorageService {
    private static final String USER_HOME_PROPERTY = "user.home";
    private static final String WORKSPACE_DIRECTORY_PROPERTY = ".SolutionTool";
    private static final String PATH_SEPARATOR = File.separator;
    private static final String PROPERTY_FILENAME = "properties.xml";

    private final String _workingDirectory;


    public StorageServiceImpl() {
        _workingDirectory = System.getProperty(USER_HOME_PROPERTY) + PATH_SEPARATOR + WORKSPACE_DIRECTORY_PROPERTY;
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
            return false;
        }
    }

    @Override
    public List<Group> loadStorage(String server, String username) {
        try {
            File file = getPropFile(server, username);
            JAXBContext jaxbContext = JAXBContext.newInstance(ProgramProperties.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Object unmarshallObj = jaxbUnmarshaller.unmarshal(file);
            List<Group> list = ((ProgramProperties) unmarshallObj).getClonedGroups();
            return list == null ? Collections.emptyList() : list;
        } catch (IOException | JAXBException e) {
            return Collections.emptyList();
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
