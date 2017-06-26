package com.lgc.gitlabtool.git.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides possibility to get the project version and project name from property file
 * that is filled by Maven according to the settings in pom.xml
 *
 * @author Igor Khlaponin
 * @email IKhlaponin@luxoft.com
 */
public class ProjectPropertiesUtil {

    private static final Logger _logger = LogManager.getLogger(ProjectPropertiesUtil.class);
    private static final String PROJECT_PROPERTY_FILE_NAME = "pom.properties";
    /** project version key in pom.properties file */
    private static final String PROJECT_VERSION_KEY = "gitlabtool.version";
    /** project name key in pom.properties file */
    private static final String PROJECT_NAME_KEY = "gitlabtool.name";
    private static final String UNDEFINED_VALUE = "undefined";

    /**
     * Returns the value from the property file by its key
     *
     * @param propertyFileName - name of the property file
     * @param key - the key
     * @return the value from the property file or <code>UNDEFINED_VALUE</code> if such key does not exist
     */
    static String getProperty(String propertyFileName, String key) {
        Properties props = new Properties();
        try (InputStream fis = ProjectPropertiesUtil.class.getClassLoader().getResourceAsStream(propertyFileName)) {
            props.load(fis);
        } catch (Exception e) {
            _logger.error("Error getting properties: " + e.getMessage());
            return UNDEFINED_VALUE;
        }
        return props.getProperty(key) == null ? UNDEFINED_VALUE : props.getProperty(key);
    }

    public static String getProjectVersion() {
        return getProperty(PROJECT_PROPERTY_FILE_NAME, PROJECT_VERSION_KEY);
    }

    public static String getProjectName() {
        return getProperty(PROJECT_PROPERTY_FILE_NAME, PROJECT_NAME_KEY);
    }

}
