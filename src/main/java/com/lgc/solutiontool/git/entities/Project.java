package com.lgc.solutiontool.git.entities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import com.google.gson.annotations.SerializedName;
import com.lgc.solutiontool.git.project.nature.operation.Operation;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;

/**
 * Class keeps data about project.
 *
 * You cannot change the name field to the class, otherwise
 * JSONRarser can't parse from json string to object this class.
 *
 * @author Lyska Lyudmila
 */
public class Project {
    private int id;
    private String description;
    private String default_branch;
    @SerializedName("public")
    private boolean _public;
    private int visibility_level;
    private String ssh_url_to_repo;
    private String http_url_to_repo;
    private String web_url;
    private String[] tag_list;
    private Object owner;
    private String name;
    private String name_with_namespace;
    private String path;
    private String path_with_namespace;
    private boolean issues_enabled;
    private int open_issues_count;
    private boolean merge_requests_enabled;
    private boolean builds_enabled;
    private boolean wiki_enabled;
    private boolean snippets_enabled;
    private boolean container_registry_enabled;
    private String created_at;
    private String last_activity_at;
    private int creator_id;
    private Object namespace;
    private Object permissions;
    private boolean archived;
    private String avatar_url;
    private boolean shared_runners_enabled;
    private int forks_count;
    private int star_count;
    private String runners_token;
    private boolean public_builds;
    private String[] shared_with_groups;
    private boolean only_allow_merge_if_build_succeeds;
    private boolean only_allow_merge_if_all_discussions_are_resolved;
    private boolean request_access_enabled;

    /** Path to the cloned project **/
    private String _pathToClonedProject;
    private boolean _isCloned;
    private ProjectType _type;

    /**
     * Sets a project type
     * @param type project type
     */
    public void setProjectType(ProjectType type) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid value passed");
        }
        _type = type;
    }

    /**
     * Gets a type of the project
     */
    public ProjectType getProjectType() {
        return _type;
    }

    /**
     * Gets available operations for this project
     * @return set of operations
     */
    public Set<Operation> getAvailableOperations() {
        return _type.getAvailableOperations();
    }

    /**
     * Gets status of clone
     * @return status
     */
    public boolean isCloned() {
        return _isCloned;
    }

    /**
     * Sets status the project (project is cloned or not)
     * @param status
     */
    public void setClonedStatus(boolean status) {
        _isCloned = status;
    }
    /**
     * Sets path to the cloned project
     * @param path to the project
     */
    public void setPathToClonedProject(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("ERROR: Incorrect data. Value is null.");
        }
        Path pathToProject = Paths.get(path);
        if (checkPath(pathToProject)) {
            // TODO project must have /.git() folder.
            // The implementation of the method will be in the JGit class
            _pathToClonedProject = path;
        }
    }

    protected boolean checkPath(Path pathToProject) {
        return Files.exists(pathToProject) && Files.isDirectory(pathToProject);
    }

    /**
     * Gets path to the cloned project
     * @return path to the cloned project
     */
    public String getPathToClonedProject() {
        return _pathToClonedProject;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDefault_branch() {
        return default_branch;
    }

    public boolean isPublic() {
        return _public;
    }

    public int getVisibility_level() {
        return visibility_level;
    }

    public String getSsh_url_to_repo() {
        return ssh_url_to_repo;
    }

    public String getHttp_url_to_repo() {
        return http_url_to_repo;
    }

    public String getWeb_url() {
        return web_url;
    }

    public String[] getTag_list() {
        return tag_list;
    }

    public Object getOwner() {
        return owner;
    }

    public String getName_with_namespace() {
        return name_with_namespace;
    }

    public String getPath() {
        return path;
    }

    public String getPath_with_namespace() {
        return path_with_namespace;
    }

    public boolean isIssues_enabled() {
        return issues_enabled;
    }

    public int getOpen_issues_count() {
        return open_issues_count;
    }

    public boolean isMerge_requests_enabled() {
        return merge_requests_enabled;
    }

    public boolean isBuilds_enabled() {
        return builds_enabled;
    }

    public boolean isWiki_enabled() {
        return wiki_enabled;
    }

    public boolean isSnippets_enabled() {
        return snippets_enabled;
    }

    public boolean isContainer_registry_enabled() {
        return container_registry_enabled;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getLast_activity_at() {
        return last_activity_at;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public Object getNamespace() {
        return namespace;
    }

    public boolean isArchived() {
        return archived;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public boolean isShared_runners_enabled() {
        return shared_runners_enabled;
    }

    public int getForks_count() {
        return forks_count;
    }

    public int getStar_count() {
        return star_count;
    }

    public String getRunners_token() {
        return runners_token;
    }

    public boolean isPublic_builds() {
        return public_builds;
    }

    public String[] getShared_with_groups() {
        return shared_with_groups;
    }

    public boolean isOnly_allow_merge_if_build_succeeds() {
        return only_allow_merge_if_build_succeeds;
    }

    public boolean isOnly_allow_merge_if_all_discussions_are_resolved() {
        return only_allow_merge_if_all_discussions_are_resolved;
    }

    public boolean isRequest_access_enabled() {
        return request_access_enabled;
    }

    public Object getPermissions() {
        return permissions;
    }

}
