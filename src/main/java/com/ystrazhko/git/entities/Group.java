package com.ystrazhko.git.entities;

public class Group {
    private final int id;
    private final String name;
    private final String path;
    private final String description;
    private final boolean lfs_enabled;
    private final String visibility;
    private final String avatar_url;
    private final String web_url;
    private final boolean request_access_enabled;
    private final String full_name;
    private final String full_path;
    private final String parent_id;

    public Group(int id, String name, String path, String description, boolean lfs_enabled, String visibility,
            String avatar_url, String web_url, boolean request_access_enabled, String full_name, String full_path,
            String parent_id) {

        super();
        this.id = id;
        this.name = name;
        this.path = path;
        this.description = description;
        this.lfs_enabled = lfs_enabled;
        this.visibility = visibility;
        this.avatar_url = avatar_url;
        this.web_url = web_url;
        this.request_access_enabled = request_access_enabled;
        this.full_name = full_name;
        this.full_path = full_path;
        this.parent_id = parent_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLfs_enabled() {
        return lfs_enabled;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getWeb_url() {
        return web_url;
    }

    public boolean isRequest_access_enabled() {
        return request_access_enabled;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getFull_path() {
        return full_path;
    }

    public String getParent_id() {
        return parent_id;
    }

}
