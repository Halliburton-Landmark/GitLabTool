package com.lgc.solutiontool.git.services;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.project.nature.projecttype.DSGProjectType;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;
import com.lgc.solutiontool.git.project.nature.projecttype.UnknownProjectType;


/**
 * Tests for the ProjectTypeServiceImpl service.
 *
 * @author Lyudmila Lyska
 */
public class ProjectTypeServiceImplTest {

    private Project getCorrectProject() {
        Project projectCorrect = new Project() {
            @Override
            protected boolean checkPath(Path pathToProject) {
                return true;
            }
        };
        projectCorrect.setPathToClonedProject("/path");
        projectCorrect.setClonedStatus(true);
        return projectCorrect;
    }

    @Test
    public void getProjectTypeCorrectDataTest() {
        DSGProjectType dsgType = new DSGProjectType() {
            @Override
            protected boolean isPathCorrespondsToType(Path path) {
                return true;
            }
        };
        ProjectTypeServiceImpl service = new ProjectTypeServiceImpl() {
            @Override
            protected void initProjectTypes() {
                getSetProjectTypes().add(dsgType);
            }
        };

        ProjectType type = service.getProjectType(getCorrectProject());
        Assert.assertTrue(type instanceof DSGProjectType);

        service = new ProjectTypeServiceImpl();
        type = service.getProjectType(new Project());
        Assert.assertTrue(type instanceof UnknownProjectType);
        type = service.getProjectType(getCorrectProject());
        Assert.assertTrue(type instanceof UnknownProjectType);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getProjectTypeProjectIsNullTest() {
        ProjectTypeServiceImpl service = new ProjectTypeServiceImpl();
        service.getProjectType(null);
    }

}
