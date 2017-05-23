package com.lgc.solutiontool.git.project.nature.projecttype;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
 * Tests for the DSGProjectType class.
 *
 * @author Lyudmila Lyska
 */
public class DSGProjectTypeTest {

    private DSGProjectType getDSGType() {
        return new DSGProjectType() {
            @Override
            protected boolean isPathCorrespondsToType(Path path) {
                return true;
            };
        };
    }


    @Test
    public void projectTypeOperationsTest() {
        final Set<Operation> ALL_OPERATIONS = new HashSet<>(Arrays.asList(Operation.values()));

        DSGProjectType dsgProjectType = getDSGType();
        Set<Operation> operations = dsgProjectType.getAvailableOperations();
        Assert.assertNotNull(operations);
        Assert.assertFalse(operations.isEmpty());
        Assert.assertEquals(operations.size(), ALL_OPERATIONS.size());
        Assert.assertTrue(operations.containsAll(ALL_OPERATIONS));
    }

    @Test
    public void projectTypeStructureTest() {
        DSGProjectType dsgProjectType = getDSGType();
        Set<String> structures = dsgProjectType.getStructures();
        Assert.assertNotNull(structures);
        Assert.assertFalse(structures.isEmpty());
        Assert.assertEquals(structures.size(), 2);


        int oldSize = dsgProjectType.getStructures().size();
        dsgProjectType.addStructure(null);
        dsgProjectType.addStructure("");
        Assert.assertEquals(oldSize, dsgProjectType.getStructures().size());
    }

    @Test
    public void isProjectCorrespondsTypeCorrectDataTest() {
        DSGProjectType dsgProjectType = getDSGType();
        String path = "path/";
        Assert.assertTrue(dsgProjectType.isProjectCorrespondsType(path));
    }

    @Test
    public void isProjectCorrespondsTypeIncorrectDataTest() {
        DSGProjectType dsgProjectType = getDSGType();
        Assert.assertFalse(dsgProjectType.isProjectCorrespondsType(null));
        Assert.assertFalse(dsgProjectType.isProjectCorrespondsType(""));

        dsgProjectType = new DSGProjectType() {
            @Override
            protected boolean isPathCorrespondsToType(Path path) {
                return false;
            };
        };

        Assert.assertFalse(dsgProjectType.isProjectCorrespondsType("path/"));
    }

}
