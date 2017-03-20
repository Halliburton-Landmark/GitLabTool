package com.ystrazhko.git.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Class keeps data about project.
 *
 * You cannot change the name field to the class, otherwise JSONRarser can't parse from json string to object this
 * class.
 *
 * @author Lyska Lyudmila
 *
 */
public class Project {
    private int id;
    private String description;
    private String default_branch;

    @SerializedName("public")
    private boolean _public; /// ????? _public -> public

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
    private Object namespace; // TODO Object -> namespace {}
    // TODO permissions ??? { project_access {}, group_access {} }
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


    public Project(int id, String description, String default_branch, boolean _public, int visibility_level,
            String ssh_url_to_repo, String http_url_to_repo, String web_url, String[] tag_list, Object owner,
            String name, String name_with_namespace, String path, String path_with_namespace, boolean issues_enabled,
            int open_issues_count, boolean merge_requests_enabled, boolean builds_enabled, boolean wiki_enabled,
            boolean snippets_enabled, boolean container_registry_enabled, String created_at, String last_activity_at,
            int creator_id, Object namespace, boolean archived, String avatar_url, boolean shared_runners_enabled,
            int forks_count, int star_count, String runners_token, boolean public_builds, String[] shared_with_groups,
            boolean only_allow_merge_if_build_succeeds, boolean only_allow_merge_if_all_discussions_are_resolved,
            boolean request_access_enabled) {
        super();
        this.id = id;
        this.description = description;
        this.default_branch = default_branch;
        this._public = _public;
        this.visibility_level = visibility_level;
        this.ssh_url_to_repo = ssh_url_to_repo;
        this.http_url_to_repo = http_url_to_repo;
        this.web_url = web_url;
        this.tag_list = tag_list;
        this.owner = owner;
        this.name = name;
        this.name_with_namespace = name_with_namespace;
        this.path = path;
        this.path_with_namespace = path_with_namespace;
        this.issues_enabled = issues_enabled;
        this.open_issues_count = open_issues_count;
        this.merge_requests_enabled = merge_requests_enabled;
        this.builds_enabled = builds_enabled;
        this.wiki_enabled = wiki_enabled;
        this.snippets_enabled = snippets_enabled;
        this.container_registry_enabled = container_registry_enabled;
        this.created_at = created_at;
        this.last_activity_at = last_activity_at;
        this.creator_id = creator_id;
        this.namespace = namespace;
        this.archived = archived;
        this.avatar_url = avatar_url;
        this.shared_runners_enabled = shared_runners_enabled;
        this.forks_count = forks_count;
        this.star_count = star_count;
        this.runners_token = runners_token;
        this.public_builds = public_builds;
        this.shared_with_groups = shared_with_groups;
        this.only_allow_merge_if_build_succeeds = only_allow_merge_if_build_succeeds;
        this.only_allow_merge_if_all_discussions_are_resolved = only_allow_merge_if_all_discussions_are_resolved;
        this.request_access_enabled = request_access_enabled;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getDefault_branch() {
        return default_branch;
    }


    public void setDefault_branch(String default_branch) {
        this.default_branch = default_branch;
    }


    public boolean isPublic() {
        return _public;
    }


    public void setPublic(boolean _public) {
        this._public = _public;
    }


    public int getVisibility_level() {
        return visibility_level;
    }


    public void setVisibility_level(int visibility_level) {
        this.visibility_level = visibility_level;
    }


    public String getSsh_url_to_repo() {
        return ssh_url_to_repo;
    }


    public void setSsh_url_to_repo(String ssh_url_to_repo) {
        this.ssh_url_to_repo = ssh_url_to_repo;
    }


    public String getHttp_url_to_repo() {
        return http_url_to_repo;
    }


    public void setHttp_url_to_repo(String http_url_to_repo) {
        this.http_url_to_repo = http_url_to_repo;
    }


    public String getWeb_url() {
        return web_url;
    }


    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }


    public String[] getTag_list() {
        return tag_list;
    }


    public void setTag_list(String[] tag_list) {
        this.tag_list = tag_list;
    }


    public Object getOwner() {
        return owner;
    }


    public void setOwner(String[] owner) {
        this.owner = owner;
    }


    public String getName_with_namespace() {
        return name_with_namespace;
    }


    public void setName_with_namespace(String name_with_namespace) {
        this.name_with_namespace = name_with_namespace;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getPath_with_namespace() {
        return path_with_namespace;
    }


    public void setPath_with_namespace(String path_with_namespace) {
        this.path_with_namespace = path_with_namespace;
    }


    public boolean isIssues_enabled() {
        return issues_enabled;
    }


    public void setIssues_enabled(boolean issues_enabled) {
        this.issues_enabled = issues_enabled;
    }


    public int getOpen_issues_count() {
        return open_issues_count;
    }


    public void setOpen_issues_count(int open_issues_count) {
        this.open_issues_count = open_issues_count;
    }


    public boolean isMerge_requests_enabled() {
        return merge_requests_enabled;
    }


    public void setMerge_requests_enabled(boolean merge_requests_enabled) {
        this.merge_requests_enabled = merge_requests_enabled;
    }


    public boolean isBuilds_enabled() {
        return builds_enabled;
    }


    public void setBuilds_enabled(boolean builds_enabled) {
        this.builds_enabled = builds_enabled;
    }


    public boolean isWiki_enabled() {
        return wiki_enabled;
    }


    public void setWiki_enabled(boolean wiki_enabled) {
        this.wiki_enabled = wiki_enabled;
    }


    public boolean isSnippets_enabled() {
        return snippets_enabled;
    }


    public void setSnippets_enabled(boolean snippets_enabled) {
        this.snippets_enabled = snippets_enabled;
    }


    public boolean isContainer_registry_enabled() {
        return container_registry_enabled;
    }


    public void setContainer_registry_enabled(boolean container_registry_enabled) {
        this.container_registry_enabled = container_registry_enabled;
    }


    public String getCreated_at() {
        return created_at;
    }


    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }


    public String getLast_activity_at() {
        return last_activity_at;
    }


    public void setLast_activity_at(String last_activity_at) {
        this.last_activity_at = last_activity_at;
    }


    public int getCreator_id() {
        return creator_id;
    }


    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }


    public Object getNamespace() {
        return namespace;
    }


    public void setNamespace(String[] namespace) {
        this.namespace = namespace;
    }


    public boolean isArchived() {
        return archived;
    }


    public void setArchived(boolean archived) {
        this.archived = archived;
    }


    public String getAvatar_url() {
        return avatar_url;
    }


    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }


    public boolean isShared_runners_enabled() {
        return shared_runners_enabled;
    }


    public void setShared_runners_enabled(boolean shared_runners_enabled) {
        this.shared_runners_enabled = shared_runners_enabled;
    }


    public int getForks_count() {
        return forks_count;
    }


    public void setForks_count(int forks_count) {
        this.forks_count = forks_count;
    }


    public int getStar_count() {
        return star_count;
    }


    public void setStar_count(int star_count) {
        this.star_count = star_count;
    }


    public String getRunners_token() {
        return runners_token;
    }


    public void setRunners_token(String runners_token) {
        this.runners_token = runners_token;
    }


    public boolean isPublic_builds() {
        return public_builds;
    }


    public void setPublic_builds(boolean public_builds) {
        this.public_builds = public_builds;
    }


    public String[] getShared_with_groups() {
        return shared_with_groups;
    }


    public void setShared_with_groups(String[] shared_with_groups) {
        this.shared_with_groups = shared_with_groups;
    }


    public boolean isOnly_allow_merge_if_build_succeeds() {
        return only_allow_merge_if_build_succeeds;
    }


    public void setOnly_allow_merge_if_build_succeeds(boolean only_allow_merge_if_build_succeeds) {
        this.only_allow_merge_if_build_succeeds = only_allow_merge_if_build_succeeds;
    }


    public boolean isOnly_allow_merge_if_all_discussions_are_resolved() {
        return only_allow_merge_if_all_discussions_are_resolved;
    }


    public void setOnly_allow_merge_if_all_discussions_are_resolved(
            boolean only_allow_merge_if_all_discussions_are_resolved) {
        this.only_allow_merge_if_all_discussions_are_resolved = only_allow_merge_if_all_discussions_are_resolved;
    }


    public boolean isRequest_access_enabled() {
        return request_access_enabled;
    }


    public void setRequest_access_enabled(boolean request_access_enabled) {
        this.request_access_enabled = request_access_enabled;
    }

}
