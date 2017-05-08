package com.lgc.solutiontool.git.project.nature.projecttype;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
 *
 * @author Lyudmila Lyska
 */
abstract class ProjectTypeImpl implements ProjectType {

    private String _id;
    private final Set<Operation> _operations;
    private final Set<String> _structures;

    public ProjectTypeImpl() {
        Operation[] minOperations = Operation.MIN_OPERATIONS.toArray(new Operation[]{});
        _operations = new HashSet<>();
        _operations.addAll(Arrays.asList(minOperations));

        _structures = new HashSet<>();
    }

    protected void setId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("The type id has already been set and cannot be changed");
        }
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
