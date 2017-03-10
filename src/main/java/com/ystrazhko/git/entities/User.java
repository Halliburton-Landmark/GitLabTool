package com.ystrazhko.git.entities;

public class User {
    private final String name;
    private final String username;
    private final int id;
    private final String state;
    private final String avatar_url;
    private final String created_at;
    private final boolean is_admin;
    private final String bio;
    private final String skype;
    private final String linkedin;
    private final String twitter;
    private final String website_url;
    private final String email;
    private final int color_scheme_id;
    private final int projects_limit;
    private final String current_sign_in_at;
    private final String[] identities;
    private final boolean can_create_group;
    private final boolean can_create_project;
    private final boolean two_factor_enabled;
    private final String private_token;

    public User(String name, String username, int id, String state, String avatar_url, String created_at,
            boolean is_admin, String bio, String skype, String linkedin, String twitter, String website_url,
            String email, int color_scheme_id, int projects_limit, String current_sign_in_at, String[] identities,
            boolean can_create_group, boolean can_create_project, boolean two_factor_enabled, String private_token) {
        super();
        this.name = name;
        this.username = username;
        this.id = id;
        this.state = state;
        this.avatar_url = avatar_url;
        this.created_at = created_at;
        this.is_admin = is_admin;
        this.bio = bio;
        this.skype = skype;
        this.linkedin = linkedin;
        this.twitter = twitter;
        this.website_url = website_url;
        this.email = email;
        this.color_scheme_id = color_scheme_id;
        this.projects_limit = projects_limit;
        this.current_sign_in_at = current_sign_in_at;
        this.identities = identities;
        this.can_create_group = can_create_group;
        this.can_create_project = can_create_project;
        this.two_factor_enabled = two_factor_enabled;
        this.private_token = private_token;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public String getBio() {
        return bio;
    }

    public String getSkype() {
        return skype;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public String getEmail() {
        return email;
    }

    public int getColor_scheme_id() {
        return color_scheme_id;
    }

    public int getProjects_limit() {
        return projects_limit;
    }

    public String getCurrent_sign_in_at() {
        return current_sign_in_at;
    }

    public String[] getIdentities() {
        return identities;
    }

    public boolean isCan_create_group() {
        return can_create_group;
    }

    public boolean isCan_create_project() {
        return can_create_project;
    }

    public boolean isTwo_factor_enabled() {
        return two_factor_enabled;
    }

    public String getPrivate_token() {
        return private_token;
    }

}
