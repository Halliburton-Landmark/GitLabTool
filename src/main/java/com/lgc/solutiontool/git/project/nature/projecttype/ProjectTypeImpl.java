package com.lgc.solutiontool.git.project.nature.projecttype;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
 * The class stores the minimum information required for types of projects
 *
 * @author Lyudmila Lyska
 */
abstract class ProjectTypeImpl implements ProjectType {

    private String _id;
    private final Set<Operation> _operations;
    private final Set<String> _structures;

    public ProjectTypeImpl() {
        _operations = new HashSet<>(Operation.MIN_OPERATIONS);
        _structures = new HashSet<>();
    }

    protected void setId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Invalid id = {" + id + "}");
        }
        _id = id;
    }

    protected void addStructure(String structure) {
        if (structure != null && !structure.isEmpty()) {
            _structures.add(structure);
        }
    }

    protected Set<String> getStructures() {
        return Collections.unmodifiableSet(_structures);
    }

    @Override
    public Set<Operation> getAvailableOperations() {
        return Collections.unmodifiableSet(_operations);
    }

    protected void addOperation(Operation operation) {
        if (operation != null) {
            _operations.add(operation);
        }
    }

    protected void addOperations(Collection<Operation> operations) {
        if (operations != null && !operations.isEmpty()) {
            _operations.addAll(operations);
        }
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public boolean hasOperation(Operation operation) {
        if (operation != null) {
            return _operations.contains(operation);
        }
        return false;
    }

    @Override
    public boolean isProjectCorrespondsType(String projectPath) {
        if (projectPath == null || projectPath.isEmpty()) {
            return false;
        }
        for (String structure : getStructures()) {
            Path path = Paths.get(projectPath + structure);
            if (!isPathCorrespondsToType(path)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isPathCorrespondsToType(Path path) {
        return Files.exists(path);
    }

}
