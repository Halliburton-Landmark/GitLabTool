package com.lgc.solutiontool.git.project.nature.projecttype;

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

    protected Set<String> getStructures() {
        return _structures;
    }

    @Override
    public Set<Operation> getAvailableOperations() {
        return Collections.unmodifiableSet(_operations);
    }

    protected Set<Operation> getModifiableOperations() {
        return _operations;
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public boolean hasOperation(Operation operation) {
        return _operations.contains(operation);
    }

}
