package com.lgc.solutiontool.git.project.nature.projecttype;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.SerializedName;
import com.lgc.solutiontool.git.project.nature.operation.Operation;

/**
 * The class stores the minimum information required for types of projects
 *
 * @author Lyudmila Lyska
 */
abstract class ProjectTypeImpl implements ProjectType {

    @SerializedName(ID_KEY)
    private String _id;

    private String _projectIcoUrl;
    private static final String DEFAULT_PROJECT_ICON_URL = "icons/project/unknown_project.png";
    private static final Logger logger = LogManager.getLogger(ProjectTypeImpl.class);

    private transient final Set<Operation> _operations;
    private transient final Set<String> _structures;

    public ProjectTypeImpl() {
        _operations = new HashSet<>(Operation.MIN_OPERATIONS);
        _structures = new HashSet<>();
        _projectIcoUrl = DEFAULT_PROJECT_ICON_URL;
    }

    protected void setId(String id) {
        if (id == null || id.isEmpty()) {
            logger.error("Invalid id = {" + id + "}");
            throw new IllegalArgumentException("Invalid id = {" + id + "}");
        }
        _id = id;
    }

    protected void setImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            logger.error("Invalid image url = {" + imageUrl + "}");
            throw new IllegalArgumentException("Invalid image url = {" + imageUrl + "}");
        }
        _projectIcoUrl = imageUrl;
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
    public String getIconUrl(){
        return _projectIcoUrl;
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
