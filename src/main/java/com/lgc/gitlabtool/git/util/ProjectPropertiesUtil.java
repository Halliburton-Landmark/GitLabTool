package com.lgc.gitlabtool.git.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectPropertiesUtil {

    private static final Logger logger = LogManager.getLogger(ProjectPropertiesUtil.class);
    private static final String PROJECT_PROPERTY_FILE_NAME = "pom.properties";

    static String getProperty(String propertyFileName, String key) {
        Properties props = new Properties();
        try (InputStream fis = ProjectPropertiesUtil.class.getClassLoader().getResourceAsStream(propertyFileName)) {
            props.load(fis);
        } catch (Exception e) {
            logger.error("", e);
            return "undefined";
        }
        return props.getProperty(key) == null ? "undefined" : props.getProperty(key);
    }

    public static String getProjectVersion() {
        return getProperty(PROJECT_PROPERTY_FILE_NAME, "gitlabtool.version");
    }

    public static String getProjectName() {
        return getProperty(PROJECT_PROPERTY_FILE_NAME, "gitlabtool.name");
    }

}
