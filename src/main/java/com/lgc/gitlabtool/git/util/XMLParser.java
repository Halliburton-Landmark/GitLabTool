package com.lgc.gitlabtool.git.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * JAXB XML parser
 * 
 * @author Igor Khlaponin
 */
public class XMLParser {

    /**
     * Loads the object from the xml-file
     * 
     * @param file to store the object
     * @param clazz - Class which instance stored in the file
     * @return the instance of the class stored in the file
     * @throws JAXBException if some issues occurs during unmarshalling
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadObject(File file, Class<T> clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(file);
    }

    /**
     * Saves the object to xml-file
     * 
     * @param file to store the object
     * @param object to be stored
     * @throws JAXBException if some issues occurs during marshalling
     */
    public static void saveObject(File file, Object object) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(object, file);
    }

}
