package com.lgc.solutiontool.git.project.nature.projecttype;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
 * Implementation of type for DSG projects
 *
 * @author Lyudmila Lyska
 */
public class DSGProjectType extends ProjectTypeImpl {

    public static final String TYPE_NAME = "com.lgc.dsg";
    private static final String STRUCTURE_OF_POM_FILE = "/pom.xml";
    private static final String STRUCTURE_OF_PLUGINS_POM = "/plugins/pom.xml";

    public DSGProjectType() {
        super();
        setId(TYPE_NAME);

        Set<Operation> operations = getModifiableOperations();
        operations.addAll(Arrays.asList(Operation.values()));

        Set<String> structures = getStructures();
        structures.add(STRUCTURE_OF_POM_FILE);
        structures.add(STRUCTURE_OF_PLUGINS_POM);

    }

    @Override
    protected boolean isPathCorrespondsToType(Path path) {
        return Files.exists(path) && Files.isRegularFile(path);
    }

}
