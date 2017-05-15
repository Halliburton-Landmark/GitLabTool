package com.lgc.solutiontool.git.project.nature.projecttype;

import java.nio.file.Path;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
 * Tests for the ProjectTypeImpl class.
 *
 * @author Lyudmila Lyska
 */
public class ProjectTypeImplTest {

    @Test(expected=UnsupportedOperationException.class)
    public void projectTypeAddStructureToImmutableCollectionTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};
        projectType.getStructures().add("structure");
    }

    @Test
    public void projectTypeStructureTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};
        String pomFile = "/pom.xml";

        Set<String> structures = projectType.getStructures();
        Assert.assertNotNull(structures);
        Assert.assertTrue(structures.isEmpty());

        projectType.addStructure(pomFile);
        Assert.assertFalse(structures.isEmpty());
        Assert.assertEquals(structures.size(), 1);
        Assert.assertTrue(structures.contains(pomFile));
    }

    @Test
    public void projectTypeAddStructureTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};

        int oldSize = projectType.getStructures().size();
        projectType.addStructure(null);
        projectType.addStructure("");
        Assert.assertEquals(oldSize, projectType.getStructures().size());
    }

    @Test
    public void projectTypeIdCorrectDataTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};
        String id = "my_type";

        Assert.assertNull(projectType.getId());
        projectType.setId(id);
        Assert.assertNotNull(projectType.getId());
        Assert.assertEquals(projectType.getId(), id);
    }

    @Test(expected=IllegalArgumentException.class)
    public void projectTypeIdIfNullTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};
        projectType.setId(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void projectTypeIdIfEmptyTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};
        projectType.setId("");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void projectTypeAvailableOperationsTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};
        Set<Operation> operations = projectType.getAvailableOperations();
        Assert.assertNotNull(operations);
        Assert.assertFalse(operations.isEmpty());
        Assert.assertEquals(operations.size(), Operation.MIN_OPERATIONS.size());
        Assert.assertTrue(operations.containsAll(Operation.MIN_OPERATIONS));

        //Here expect the UnsupportedOperationException
        operations.add(Operation.CHANGE_POM_FILE);
    }

    @Test
    public void projectTypeHasOperationTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};
        Assert.assertTrue(projectType.hasOperation(Operation.REPLACEMENT_TEXT_IN_FILE));
        Assert.assertFalse(projectType.hasOperation(null));
    }

    @Test
    public void isProjectCorrespondsTypeCorrectDataTest() {
        String path = "path/";
        ProjectTypeImpl projectType = new ProjectTypeImpl() {
            @Override
            protected boolean isPathCorrespondsToType(Path path) {
                return true;
            }
        };
        Assert.assertTrue(projectType.isProjectCorrespondsType(path));
    }

    @Test
    public void isProjectCorrespondsTypeIncorrectDataTest() {
        ProjectTypeImpl projectType = new ProjectTypeImpl() {};
        projectType.isProjectCorrespondsType(null);
        projectType.isProjectCorrespondsType("");
    }

}
