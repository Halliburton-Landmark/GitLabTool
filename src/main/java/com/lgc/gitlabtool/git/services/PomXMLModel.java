package com.lgc.gitlabtool.git.services;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * The class contains a model of a pom.xml file. When you create an object, the data is read from a file. The class can
 * return the pom.xml model and write the modified model to a file.
 *
 * @author Lyudmila Lyska
 */
public class PomXMLModel {

    private Model _originalModel;
    private static final String FILE_NAME = "pom.xml";

    /**
     * When creating an object, we read data from a file.
     *
     * @param pomXmlPath path to a pom.xml file of a cloned project
     */
    public PomXMLModel(String pomXmlPath) {
        if (pomXmlPath == null) {
            System.err.println("!ERROR: Could not create file model, passed invalid data.");
            return;
        }
        Path path = Paths.get(pomXmlPath);
        if (isCorrectPath(path)) {
            updateModelFile(path);
        } else {
            System.err.println("!ERROR: Could not create file model. Invalid file path.");
        }
    }

    /**
     * Gets model of a xml file
     *
     * @return model
     */
    public Model getModelFile() {
        return _originalModel;
    }

    /**
     * Writes the model in the pom.xml
     */
    public void writeToFile() {
        if (_originalModel == null) {
            System.err.println("!ERROR: The file can not be written to. The model is not defined.");
            return;
        }
        try (Writer fileWriter = new FileWriter(_originalModel.getPomFile())) {
            MavenXpp3Writer pomWriter = new MavenXpp3Writer();
            pomWriter.write(fileWriter, _originalModel);
        } catch (Exception e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
    }

    private void updateModelFile(Path path) {
        try (FileReader reader = new FileReader(path.toFile());) {
            MavenXpp3Reader mavenreader = new MavenXpp3Reader();
            Model model = mavenreader.read(reader);
            if (model != null) {
                model.setPomFile(path.toFile());
                _originalModel = model;
                return;
            }
        } catch (IOException | XmlPullParserException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        _originalModel = null;
    }

    private boolean isCorrectPath(Path path) {
        boolean exists = Files.exists(path);
        boolean isFile = Files.isReadable(path);
        boolean isExtensionCorrectly = FILE_NAME.equals(path.getFileName().toString()) ? true : false;
        return exists && isFile && isExtensionCorrectly;
    }

}
