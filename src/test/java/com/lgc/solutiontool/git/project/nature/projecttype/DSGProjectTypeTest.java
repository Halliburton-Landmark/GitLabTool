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

    private final DSGProjectType _dsgProjectType = new DSGProjectType() {
        @Override
        protected boolean isPathCorrespondsToType(Path path) {
            return true;
        };
    };

    private final Set<Operation> ALL_OPERATIONS = new HashSet<>(Arrays.asList(Operation.values()));


    @Test
    public void projectTypeOperationsTest() {
        Set<Operation> operations = _dsgProjectType.getModifiableOperations();
        Assert.assertNotNull(operations);
        Assert.assertFalse(operations.isEmpty());
        Assert.assertEquals(operations.size(), ALL_OPERATIONS.size());
        Assert.assertTrue(operations.containsAll(ALL_OPERATIONS));
    }

    @Test
    public void projectTypeStructureTest() {
        Set<String> structures = _dsgProjectType.getStructures();
        Assert.assertNotNull(structures);
        Assert.assertFalse(structures.isEmpty());
        Assert.assertEquals(structures.size(), 2);
    }

    @Test
    public void isProjectCorrespondsTypeCorrectDataTest() {
        String path = "path/";
        Assert.assertTrue(_dsgProjectType.isProjectCorrespondsType(path));
        _dsgProjectType.getStructures().add(null);
        Assert.assertTrue(_dsgProjectType.isProjectCorrespondsType(path));
    }

    @Test
    public void isProjectCorrespondsTypeIncorrectDataTest() {
        Assert.assertFalse(_dsgProjectType.isProjectCorrespondsType(null));
        Assert.assertFalse(_dsgProjectType.isProjectCorrespondsType(""));


        DSGProjectType dsgProjectType = new DSGProjectType() {
            @Override
            protected boolean isPathCorrespondsToType(Path path) {
                return false;
            };
        };

        Assert.assertFalse(dsgProjectType.isProjectCorrespondsType("path/"));
    }

}
