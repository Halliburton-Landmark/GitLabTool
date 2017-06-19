package com.lgc.gitlabtool.git.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectPropertiesUtil {

    private static final Logger logger = LogManager.getLogger(ProjectPropertiesUtil.class);
    private static final String RESOURCE_PATH = ProjectPropertiesUtil.class.getClassLoader().getResource("pom.properties").getPath();

    public static String getProperty(String propertyFileName, String key) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propertyFileName)) {
            props.load(fis);
        } catch (IOException e) {
            logger.error("", e);
            return "undefined";
        }
        return props != null ? props.getProperty(key) : "undefined";
    }

    public static boolean setProperty(String propertyFileName, String key, String value) {
        Properties props = new Properties();
        try (FileOutputStream fos = new FileOutputStream(propertyFileName)) {
            props.setProperty(key, value);
            props.store(fos, null);
            logger.debug("property saved: " + key + ":" + value);
        } catch (IOException e) {
            logger.error("", e);
            return false;
        }
        return true;
    }

    public static String getProjectVersion() {
        return getProperty(RESOURCE_PATH, "gitlabtool.version");
    }

    public static String getProjectName() {
        return getProperty(RESOURCE_PATH, "gitlabtool.name");
    }

}
