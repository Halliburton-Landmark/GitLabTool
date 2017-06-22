package com.lgc.gitlabtool.git.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProjectPropertiesUtilTest {

    private static final String EXPECTED_NAME = "Gitlab Tool";
    private static final String EXPECTED_VERSION = "0.0.1";
    private static final String TEST_RESOURCE_FILE_NAME = "pom.properties";

    @Test
    public void checkGetProjectVersion() {
        String expectedVersion = EXPECTED_VERSION;
        String actualVersion = ProjectPropertiesUtil.getProjectVersion();

        assertEquals(expectedVersion, actualVersion);
    }

    @Test
    public void checkGetProjectName() {
        String expectedProjectName = EXPECTED_NAME;
        String actualProjectName = ProjectPropertiesUtil.getProjectName();

        assertEquals(expectedProjectName, actualProjectName);
    }

    @Test
    public void checkGetRightProperty() {
        String expectedVersion = EXPECTED_VERSION;
        String expectedName = EXPECTED_NAME;

        String actualVersion = ProjectPropertiesUtil.getProperty(TEST_RESOURCE_FILE_NAME, "gitlabtool.version");
        String actualName = ProjectPropertiesUtil.getProperty(TEST_RESOURCE_FILE_NAME, "gitlabtool.name");

        assertEquals(expectedVersion, actualVersion);
        assertEquals(expectedName, actualName);
    }

    @Test
    public void checkGetWrongProperty() {
        String extpectedResult = "undefined";
        String actualResult = ProjectPropertiesUtil.getProperty(TEST_RESOURCE_FILE_NAME, "foo");

        assertEquals(extpectedResult, actualResult);
    }

}
