package com.lgc.solutiontool.git.services;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.project.nature.projecttype.DSGProjectType;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;
import com.lgc.solutiontool.git.project.nature.projecttype.UnknownProjectType;

public class ProjectTypeServiceImplTest {

    private static ProjectTypeServiceImpl _service = (ProjectTypeServiceImpl) ServiceProvider.getInstance()
            .getService(ProjectTypeService.class.getName());

    private static final Project _projectCorrect;

    static {
        _projectCorrect = new Project() {
            @Override
            protected boolean checkPath(Path pathToProject) {
                return true;
            }
        };
        _projectCorrect.setPathToClonedProject("/path");
        _projectCorrect.setClonedStatus(true);
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

        ProjectType type = service.getProjectType(_projectCorrect);
        Assert.assertTrue(type instanceof DSGProjectType);
        type = _service.getProjectType(new Project());
        Assert.assertTrue(type instanceof UnknownProjectType);
        type = _service.getProjectType(_projectCorrect);
        Assert.assertTrue(type instanceof UnknownProjectType);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getProjectTypeProjectIsNullTest() {
        _service.getProjectType(null);
    }

}
