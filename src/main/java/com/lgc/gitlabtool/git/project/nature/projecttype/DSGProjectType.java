package com.lgc.gitlabtool.git.project.nature.projecttype;

import java.nio.file.Path;
import java.util.Arrays;

import com.lgc.gitlabtool.git.project.nature.operation.Operation;
import com.lgc.gitlabtool.git.util.PathUtilities;

/**
 * Implementation of type for DSG projects
 *
 * @author Lyudmila Lyska
 */
public class DSGProjectType extends ProjectTypeImpl {

    public static final String TYPE_NAME = "com.lgc.dsg";
    private static final String STRUCTURE_OF_POM_FILE = "pom.xml";
    private static final String STRUCTURE_OF_PLUGINS_POM = "plugins/pom.xml";
    private static final String DS_PROJECT_ICON_URL = "icons/project/dsg_project.png";

    public DSGProjectType() {
        super();
        setId(TYPE_NAME);
        setImageUrl(DS_PROJECT_ICON_URL);
        addOperations(Arrays.asList(Operation.values()));

        addStructure(STRUCTURE_OF_POM_FILE);
        addStructure(STRUCTURE_OF_PLUGINS_POM);

    }

    @Override
    protected boolean isPathCorrespondsToType(Path path) {
        return PathUtilities.isExistsAndRegularFile(path);
    }

}
