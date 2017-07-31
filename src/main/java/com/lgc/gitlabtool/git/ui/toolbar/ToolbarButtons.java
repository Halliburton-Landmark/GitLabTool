package com.lgc.gitlabtool.git.ui.toolbar;

import com.lgc.gitlabtool.git.ui.ViewKey;

/**
 * Enum-helper which contains data for creating buttons
 * Uses for {@link ToolbarManager)
 * For adding a new button to toolbar you need to add a new Enum item.
 * <p>
 * Note: Follow the naming rules for buttonId - subgroup_comandId
 * (it necessary for dividing toolbar into subgroups)
 */
public enum ToolbarButtons {

    CLONE_GROUP_BUTTON(ViewKey.GROUP_WINDOW.getKey(), "group_cloneGroup", "icons/toolbar/clone_20x20.png", "Clone group", "Clone group"),
    REMOVE_GROUP_BUTTON(ViewKey.GROUP_WINDOW.getKey(), "group_removeGroup", "icons/toolbar/remove_20x20.png", "Remove group", "Remove selected group"),
    IMPORT_GROUP_BUTTON(ViewKey.GROUP_WINDOW.getKey(), "group_importGroup", "icons/toolbar/import_20x20.png", "Import group", "Import group from disk"),
    SELECT_GROUP_BUTTON(ViewKey.GROUP_WINDOW.getKey(), "group_selectGroup", "icons/toolbar/select_20x20.png", "Load group", "Load selected group"),

    /* Temporary unavailable buttons
    ADD_REPO_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "repo_addRepo", "", "Add repository", "Add repository"),
    REMOVE_REPO_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "repo_removeRepo", "icons/toolbar/remove_20x20.png", "Remove repository", "Remove repository"),
    EDIT_REPO_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "repo_editRepo", "icons/toolbar/edit_20x20.png", "Edit repository", "Edit repository"),
    DELETE_BRANCH_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "branch_deleteBranch", "icons/toolbar/remove_20x20.png", "Remove branch", "Remove branch");
    */
    REFRESH_PROJECTS(ViewKey.MAIN_WINDOW.getKey(), "refresh_projects", "icons/toolbar/refresh_projects_20x20.png", "Refresh projects", "Refresh projects' list"),
    CLONE_PROJECT_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "project_clone", "icons/toolbar/clone_20x20.png", "Clone", "Clone shadow project"),
    CREATE_PROJECT_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "create_project_button", "icons/toolbar/create_project_20x20.png", "Create project", "Create new project"),
    NEW_BRANCH_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "branch_newBranch", "icons/toolbar/new_branch_20x20.png", "New branch", "Create new branch"),
    SWITCH_BRANCH_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "branch_switchBranch", "icons/toolbar/switch_20x20.png", "Switch branch", "Switch to another branch"),
    COMMIT_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "commit", "icons/toolbar/commit_20x20.png", "Commit", "Show commit dialog"),
    PUSH_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "push", "icons/toolbar/push_20x20.png", "Push", "Show push dialog"),
    EDIT_PROJECT_PROPERTIES_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "edit_project_propecties", "icons/toolbar/edit_properties_20x20.png", "Edit project properties", "Edit project properties");

    private final String viewKey;
    private final String id;
    private final String text;
    private final String iconUrl;
    private final String tooltip;

    private ToolbarButtons(final String windowId, final String buttonId, final String buttonIconUrl, final String buttonText, final String buttonTooltip) {
        this.viewKey = windowId;
        this.id = buttonId;
        this.text = buttonText;
        this.iconUrl = buttonIconUrl;
        this.tooltip = buttonTooltip;
    }

    /**
     * Gets the button's id
     *
     * @return button's id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the button's text
     *
     * @return button's text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the button's url of icon
     *
     * @return button's url of icon
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * Gets the button's tooltip
     *
     * @return button's tooltip
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Gets the view's key in which button placed
     *
     * @return view's key
     */
    public String getViewKey() {
        return viewKey;
    }
}
