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
public enum GLToolButtons {
    
    GENERAL_EXIT(GLToolConstants.ALL_WINDOW_KEY, "File", "main_file_exit", "icons/mainmenu/exit_16x16.png", "Exit", null),
    GENERAL_USER_GUIDE(GLToolConstants.ALL_WINDOW_KEY, "Help", "all_window_help_user_guide", "icons/mainmenu/user_guide_16x16.png", "User guide", null),
    GENERAL_ABOUT(GLToolConstants.ALL_WINDOW_KEY, "Help", "all_window_help_about", "icons/mainmenu/about_16x16.png", "About", null),
    
    GROUP_WINDOW_CLONE_GROUP(GLToolConstants.GROUPS_WINDOW_KEY, "Git", "group_window_group_clone", "icons/mainmenu/clone_16x16.png", "Clone", null),

    CLONE_GROUP_BUTTON(GLToolConstants.GROUPS_WINDOW_KEY, "group_cloneGroup", "icons/toolbar/clone_20x20.png", "Clone group", "Clone group", null),
    REMOVE_GROUP_BUTTON(GLToolConstants.GROUPS_WINDOW_KEY, "group_removeGroup", "icons/toolbar/remove_20x20.png", "Remove group", "Remove selected group", null),
    IMPORT_GROUP_BUTTON(GLToolConstants.GROUPS_WINDOW_KEY, "group_importGroup", "icons/toolbar/import_20x20.png", "Import group", "Import group from disk", null),
    SELECT_GROUP_BUTTON(GLToolConstants.GROUPS_WINDOW_KEY, "group_selectGroup", "icons/toolbar/select_20x20.png", "Load group", "Load selected group", null),

    CHANGE_GROUP_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "change_group", "icons/toolbar/change_group_20x20.png", "Change group", "Change group", null),
    CLONE_PROJECT_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "project_clone", "icons/toolbar/clone_20x20.png", "Clone", "Clone shadow project", null),
    CREATE_PROJECT_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "create_project_button", "icons/toolbar/create_project_20x20.png", "Create project", "Create new project", null),
    NEW_BRANCH_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "branch_newBranch", "icons/toolbar/new_branch_20x20.png", "New branch", "Create new branch", null),
    CHECKOUT_BRANCH_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "branch_checkoutBranch", "icons/toolbar/checkout_20x20.png", "Checkout", "Checkout to another branch", null),
    STAGING_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "git staging", "icons/toolbar/staging_20x20.png", "Staging", "Show Git Staging dialog", null),
    PUSH_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "push", "icons/toolbar/push_20x20.png", "Push", "Show push dialog", null),
    EDIT_PROJECT_PROPERTIES_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "edit_project_propecties", "icons/toolbar/edit_properties_20x20.png", "Edit project properties", "Edit project properties", null),
    PULL_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY, "pull", "icons/toolbar/pull_20x20.png", "Pull", "Pull projects", null),
    REVERT_CHANGES(GLToolConstants.PROJECTS_WINDOW_KEY, "revert", "icons/toolbar/revert_changes_20x20.png", "Revert", "Revert changes", null),
    
    MAIN_CLONE_PROJECT(GLToolConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_clone_project", "icons/mainmenu/clone_16x16.png", "Clone", null),
    MAIN_CREATE_BRANCH(GLToolConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_create_branch", "icons/mainmenu/newbranch_16x16.png", "New branch", null),
    MAIN_CHECKOUT_BRANCH(GLToolConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_checkout_branch", "icons/mainmenu/checkoutbranch_16x16.png", "Checkout", null),
    MAIN_STAGING(GLToolConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_staging", "icons/mainmenu/staging_16x16.png", "Staging", null),
    MAIN_PUSH(GLToolConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_push", "icons/mainmenu/push_16x16.png", "Push", null),
    MAIN_PULL(GLToolConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_pull", "icons/mainmenu/pull_16x16.png", "Pull", null),
    MAIN_REVERT(GLToolConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_revert", "icons/mainmenu/revert_changes_16x16.png", "Revert", null);


    private final String viewKey;
    private final String id;
    private final String text;
    private final String iconUrl;
    private final String tooltip;
    private final String menuName;

    GLToolButtons(final String windowId, final String buttonId, final String buttonIconUrl, final String buttonText, final String buttonTooltip, final String menuName) {
        this.viewKey = windowId;
        this.id = buttonId;
        this.text = buttonText;
        this.iconUrl = buttonIconUrl;
        this.tooltip = buttonTooltip;
        this.menuName = menuName;
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

    /**
     * Gets the name of menu in which menu item placed
     *
     * @return menu's name
     */
    public String getMenuName() {
        return menuName;
    }

    public static class GLToolConstants{
        public final static String GROUPS_WINDOW_KEY = ViewKey.GROUPS_WINDOW.getKey();
        public final static String PROJECTS_WINDOW_KEY = ViewKey.PROJECTS_WINDOW.getKey();
        public final static String ALL_WINDOW_KEY = ViewKey.ALL_WINDOWS.getKey();
    }
}
