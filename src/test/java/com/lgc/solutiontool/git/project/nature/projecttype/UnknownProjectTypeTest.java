package com.lgc.solutiontool.git.project.nature.projecttype;

import org.junit.Assert;
import org.junit.Test;

public class UnknownProjectTypeTest {

    private final UnknownProjectType _unknownProjectType = new UnknownProjectType();
    private final String _nameType = "unknown";

    @Test
    public void idTest() {
        Assert.assertNotNull(_unknownProjectType.getId());
        Assert.assertEquals(_unknownProjectType.getId(), _nameType);
    }

    @Test
    public void isProjectCorrespondsTypeCorrectDataTest() {
        Assert.assertTrue(_unknownProjectType.isProjectCorrespondsType("path/"));
    }

    @Test
    public void isProjectCorrespondsTypeIncorrectDataTest() {
        Assert.assertFalse(_unknownProjectType.isProjectCorrespondsType(null));
        Assert.assertFalse(_unknownProjectType.isProjectCorrespondsType(""));
    }

}
