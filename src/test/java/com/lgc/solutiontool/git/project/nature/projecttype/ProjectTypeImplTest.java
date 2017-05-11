package com.lgc.solutiontool.git.project.nature.projecttype;

import java.nio.file.Path;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

public class ProjectTypeImplTest {

    private ProjectTypeImpl _projectType = new ProjectTypeImpl() {};
    private final String _pomFile = "/pom.xml";

    @Test
    public void projectTypeStructureTest() {
        Set<String> structures = _projectType.getStructures();
        Assert.assertNotNull(structures);
        Assert.assertTrue(structures.isEmpty());

        structures.add(_pomFile);
        Assert.assertFalse(structures.isEmpty());
        Assert.assertEquals(structures.size(), 1);
        Assert.assertTrue(structures.contains(_pomFile));
    }

    @Test
    public void projectTypeIdCorrectDataTest() {
        String id = "my_type";

        Assert.assertNull(_projectType.getId());
        _projectType.setId(id);
        Assert.assertNotNull(_projectType.getId());
        Assert.assertEquals(_projectType.getId(), id);
    }

    @Test(expected=IllegalArgumentException.class)
    public void projectTypeIdIfNullTest() {
        _projectType.setId(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void projectTypeIdIfEmptyTest() {
        _projectType.setId("");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void projectTypeAvailableOperationsTest() {
        Set<Operation> operations = _projectType.getAvailableOperations();
        Assert.assertNotNull(operations);
        Assert.assertFalse(operations.isEmpty());
        Assert.assertEquals(operations.size(), Operation.MIN_OPERATIONS.size());
        Assert.assertTrue(operations.containsAll(Operation.MIN_OPERATIONS));

        operations.add(Operation.CHANGE_POM_FILE);
    }

    @Test
    public void projectTypeOperationsTest() {
        Set<Operation> operations = _projectType.getModifiableOperations();
        Assert.assertNotNull(operations);
        Assert.assertFalse(operations.isEmpty());
        Assert.assertEquals(operations.size(), Operation.MIN_OPERATIONS.size());
        Assert.assertTrue(operations.containsAll(Operation.MIN_OPERATIONS));

        int oldSize = operations.size();
        operations.add(Operation.CHANGE_POM_FILE);
        Assert.assertTrue(oldSize < operations.size());
    }

    @Test
    public void projectTypeHasOperationTest() {
        Assert.assertTrue(_projectType.hasOperation(Operation.REPLACEMENT_TEXT_IN_FILE));
        Assert.assertFalse(_projectType.hasOperation(null));
    }

    @Test
    public void isProjectCorrespondsTypeCorrectDataTest() {
        String path = "path/";
        _projectType = new ProjectTypeImpl() {
            @Override
            protected boolean isPathCorrespondsToType(Path path) {
                return true;
            }
        };

        Assert.assertTrue(_projectType.isProjectCorrespondsType(path));
        _projectType.getStructures().add(null);
        Assert.assertTrue(_projectType.isProjectCorrespondsType(path));
    }

    @Test
    public void isProjectCorrespondsTypeIncorrectDataTest() {
        _projectType.isProjectCorrespondsType(null);
        _projectType.isProjectCorrespondsType("");
    }

}
