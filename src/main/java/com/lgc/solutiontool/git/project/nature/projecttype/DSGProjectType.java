package com.lgc.solutiontool.git.project.nature.projecttype;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
 *
 * @author Lyudmila Lyska
 */
public class DSGProjectType extends ProjectTypeImpl {

    public DSGProjectType() {
        super();

        setId("com.lgc.dsg");

        Set<Operation> operations = getAvailableOperations();
        operations.addAll(Arrays.asList(Operation.values()));

        Set<String> structures = getStructures();
        structures.add("/pom.xml");
        structures.add("/plugins/pom.xml");

    }

    @Override
    public boolean isProjectCorrespondsType(String projectPath) {
        for (String structure : getStructures()) {
            Path path = Paths.get(projectPath + structure);
            if (!(Files.exists(path) && Files.isRegularFile(path))) {
                return false;
            }
        }
        return true;
    }

}
