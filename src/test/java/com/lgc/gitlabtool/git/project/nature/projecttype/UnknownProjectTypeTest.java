package com.lgc.gitlabtool.git.project.nature.projecttype;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the UnknownProjectType class.
 *
 * @author Lyudmila Lyska
 */
public class UnknownProjectTypeTest {

    private final String _nameType = "unknown";

    @Test
    public void idTest() {
        UnknownProjectType unknownProjectType = new UnknownProjectType();
        Assert.assertNotNull(unknownProjectType.getId());
        Assert.assertEquals(unknownProjectType.getId(), _nameType);
    }

    @Test
    public void isProjectCorrespondsTypeCorrectDataTest() {
        UnknownProjectType unknownProjectType = new UnknownProjectType();
        Assert.assertTrue(unknownProjectType.isProjectCorrespondsType("path/"));
    }

    @Test
    public void isProjectCorrespondsTypeIncorrectDataTest() {
        UnknownProjectType unknownProjectType = new UnknownProjectType();
        Assert.assertFalse(unknownProjectType.isProjectCorrespondsType(null));
        Assert.assertFalse(unknownProjectType.isProjectCorrespondsType(""));
    }

}
