package com.lgc.gitlabtool.git.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ProjectPropertiesUtilTest {

    private static final String TEST_SOURCE_PATH = "src/main/resources"; // do not work with File.separator
    private static final String TEST_RESOURCE_FILE_NAME = "test.properties";
    private static final Logger logger = LogManager.getLogger(ProjectPropertiesUtilTest.class);

    @Before
    public void setUp() {
        Properties props = new Properties();
        try (FileOutputStream fos = new FileOutputStream(TEST_SOURCE_PATH + File.separator + TEST_RESOURCE_FILE_NAME)) {
            props.setProperty("gitlabtool.version", "0.0.1");
            props.setProperty("gitlabtool.name", "Gitlab Tool");
            props.store(fos, null);
        } catch (IOException e) {
            logger.error("Error in @Before test method", e);
        }
    }

    @After
    public void cleanUp() {
        try {
            File file = new File(TEST_SOURCE_PATH + File.separator + TEST_RESOURCE_FILE_NAME);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error("Error in @After test method", e);
        }
    }

    @Test
    public void checkGetProjectVersion() {
        String expectedVersion = "0.0.1";
        String actualVersion = ProjectPropertiesUtil.getProjectVersion();

        assertEquals(expectedVersion, actualVersion);
    }

    @Test
    public void checkGetProjectName() {
        String expectedProjectName = "gitlabtool";
        String actualProjectName = ProjectPropertiesUtil.getProjectName();

        assertEquals(expectedProjectName, actualProjectName);
    }

    @Test @Ignore // TODO: have a problem with resources in jar. I'll think about it later
    public void checkGetRightProperty() {
        String expectedVersion = "0.0.1";
        String expectedName = "Gitlab Tool";

        String actualVersion = ProjectPropertiesUtil.getProperty(TEST_RESOURCE_FILE_NAME, "gitlabtool.version");
        String actualName = ProjectPropertiesUtil.getProperty(TEST_RESOURCE_FILE_NAME, "gitlabtool.name");

        assertEquals(expectedVersion, actualVersion);
        assertEquals(expectedName, actualName);
    }

    @Test @Ignore // TODO: have a problem with resources in jar. I'll think about it later
    public void checkGetWrongProperty() {
        String extpectedResult = "undefined";
        String actualResult = ProjectPropertiesUtil.getProperty(TEST_RESOURCE_FILE_NAME, "foo");

        assertEquals(extpectedResult, actualResult);
    }

}
