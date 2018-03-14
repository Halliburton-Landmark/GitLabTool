package com.lgc.gitlabtool.git.ui.toolbar;

import com.lgc.gitlabtool.git.ui.ViewKey;

/**
 * Enum-helper which contains data for creating buttons
 * Uses for {@link ToolbarManager)
 * For adding a new button to toolbar you need to add a new Enum item.
 * <p>
 * Note: Follow the naming rules for buttonId - subgroup_comandId
 * (it necessary for dividing toolbar into subgroups)
 *
 * @author Pavlo Pidhorniy
 * @author Oleksandr Kozlov
 */
public enum GLToolButtons {

    GENERAL_EXIT(GLToolConstants.ALL_WINDOW_KEY,
            "main_file_exit",
            "icons/mainmenu/exit_16x16.png",
            "Exit",
            null,
            MainMenuInfo.FILE,
            GLToolConstants.ICON_SIZE_16, false),

    GROUP_WINDOW_CLONE_GROUP(GLToolConstants.GROUPS_WINDOW_KEY,
            "group_window_group_clone",
            "icons/mainmenu/clone_16x16.png",
            "Clone",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16, false),

    GENERAL_USER_GUIDE(GLToolConstants.ALL_WINDOW_KEY,
            "all_window_help_user_guide",
            "icons/mainmenu/user_guide_16x16.png",
            "User guide",
            null,
            MainMenuInfo.HELP,
            GLToolConstants.ICON_SIZE_16, false),

    GENERAL_ABOUT(GLToolConstants.ALL_WINDOW_KEY,
            "all_window_help_about",
            "icons/mainmenu/about_16x16.png",
            "About",
            null,
            MainMenuInfo.HELP,
            GLToolConstants.ICON_SIZE_16, false),

    CLONE_GROUP_BUTTON(GLToolConstants.GROUPS_WINDOW_KEY,
            "group_cloneGroup",
            "icons/toolbar/clone_20x20.png",
            "Clone group",
            "Clone group",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    REMOVE_GROUP_BUTTON(GLToolConstants.GROUPS_WINDOW_KEY,
            "group_removeGroup",
            "icons/toolbar/remove_20x20.png",
            "Remove group",
            "Remove selected group",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    REMOVE_GROUP(GLToolConstants.GROUPS_WINDOW_KEY,
            "group_removeGroup",
            "icons/mainmenu/remove_16x16.png",
            "Remove group",
            "Remove selected group",
            null,
            GLToolConstants.ICON_SIZE_16, false),

    IMPORT_GROUP_BUTTON(GLToolConstants.GROUPS_WINDOW_KEY,
            "group_importGroup",
            "icons/toolbar/import_20x20.png",
            "Import group",
            "Import group from disk",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    SELECT_GROUP_BUTTON(GLToolConstants.GROUPS_WINDOW_KEY,
            "group_selectGroup",
            "icons/toolbar/select_20x20.png",
            "Load group",
            "Load selected group",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    LOAD_GROUP(GLToolConstants.GROUPS_WINDOW_KEY,
            "load_selected_group",
            "icons/mainmenu/load_group_16x16.png",
            "Load group",
            "Load selected group",
            null,
            GLToolConstants.ICON_SIZE_16, false),

    CHANGE_GROUP_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "change_group",
            "icons/toolbar/change_group_20x20.png",
            "Change group",
            "Change group",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    CLONE_PROJECT_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "project_clone",
            "icons/toolbar/clone_20x20.png",
            "Clone",
            "Clone shadow project",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    CREATE_PROJECT_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "create_project_button",
            "icons/toolbar/create_project_20x20.png",
            "Create project",
            "Create new project",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    NEW_BRANCH_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "branch_newBranch",
            "icons/toolbar/new_branch_20x20.png",
            "New branch",
            "Create new branch",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    CHECKOUT_BRANCH_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "branch_checkoutBranch",
            "icons/toolbar/checkout_20x20.png",
            "Checkout",
            "Checkout to another branch",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    BRANCHES_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "branches",
            "icons/toolbar/branches_20x20.png",
            "Branches",
            "Operations with branches",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    STAGING_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "git staging",
            "icons/toolbar/staging_20x20.png",
            "Staging",
            "Show Git Staging dialog",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    PUSH_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "push",
            "icons/toolbar/push_20x20.png",
            "Push",
            "Show push dialog",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    EDIT_PROJECT_PROPERTIES_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "edit_project_propecties",
            "icons/toolbar/edit_properties_20x20.png",
            "Edit project properties",
            "Edit project properties",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    MAIN_EDIT_PROJECT_PROPERTIES(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_edit_project_propecties",
            "icons/mainmenu/edit_properties_16x16.png",
            "Edit project properties",
            "Edit project properties",
            null,
            GLToolConstants.ICON_SIZE_16, false),

    PULL_BUTTON(GLToolConstants.PROJECTS_WINDOW_KEY,
            "pull",
            "icons/toolbar/pull_20x20.png",
            "Pull",
            "Pull projects",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    REVERT_CHANGES(GLToolConstants.PROJECTS_WINDOW_KEY,
            "revert",
            "icons/toolbar/revert_changes_20x20.png",
            "Revert",
            "Revert changes",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    STASH(GLToolConstants.PROJECTS_WINDOW_KEY,
            "stash",
            "icons/toolbar/stash_20x20.png",
            "Stash",
            "Stash",
            null,
            GLToolConstants.ICON_SIZE_20, false),

    OPEN_FOLDER(GLToolConstants.GROUPS_WINDOW_KEY,
            "open_folder",
            "icons/mainmenu/folder_16x16.png",
            "Show in folder",
            "Show in system explorer",
            null,
            GLToolConstants.ICON_SIZE_16),

    OPEN_IN_TERMINAL(GLToolConstants.PROJECTS_WINDOW_KEY,
            "open_in_terminal",
            "icons/mb3/open_terminal_16x16.png",
            "Open in Terminal",
            "Open in Terminal",
            null,
            GLToolConstants.ICON_SIZE_16),

    MAIN_CLONE_PROJECT(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_clone_project",
            "icons/mainmenu/clone_16x16.png",
            "Clone",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16, false),

    MAIN_CREATE_BRANCH(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_create_branch",
            "icons/mainmenu/newbranch_16x16.png",
            "New branch",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16, false),

    MAIN_CHECKOUT_BRANCH(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_checkout_branch",
            "icons/mainmenu/checkoutbranch_16x16.png",
            "Checkout",
    MAIN_BRANCHES(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_branches",
            "icons/mainmenu/branches_16x16.png",
            "Branches",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16, false),

    MAIN_STAGING(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_staging",
            "icons/mainmenu/staging_16x16.png",
            "Staging",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16, false),

    MAIN_PUSH(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_push",
            "icons/mainmenu/push_16x16.png",
            "Push",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16, false),

    MAIN_PULL(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_pull",
            "icons/mainmenu/pull_16x16.png",
            "Pull",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16, false),

    MAIN_REVERT(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_revert",
            "icons/mainmenu/revert_changes_16x16.png",
            "Revert",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16, false),

    MAIN_STASH(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_git_stash",
            "icons/mainmenu/stash_16x16.png",
            "Stash",
            null,
            MainMenuInfo.GIT,
            GLToolConstants.ICON_SIZE_16),

    MAIN_CREATE_PROJECT(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_project_create_project",
            "icons/mainmenu/create_project_16x16.png",
            "Create project",
            "Create new project",
            MainMenuInfo.PROJECT,
            GLToolConstants.ICON_SIZE_16),

    MAIN_EDIT_PROJECT_PROPERTIES(GLToolConstants.PROJECTS_WINDOW_KEY,
            "main_project_edit_project_propecties",
            "icons/mainmenu/edit_properties_16x16.png",
            "Edit project properties",
            "Edit project properties",
            MainMenuInfo.PROJECT,
            GLToolConstants.ICON_SIZE_16, false),

    SHOW_PROJECT_HISTORY(GLToolConstants.COMMON_VIEW_KEY,
            "history",
            "icons/history_20x20.png",
            null,
            "Show history",
            null,
            GLToolConstants.ICON_SIZE_20, true);

    private final String viewKey;
    private final String id;
    private final String text;
    private final String iconUrl;
    private final String tooltip;
    private final MainMenuInfo mainMenuInfo;
    private final int iconSize;
    private final boolean isToggled;

    GLToolButtons(final String windowId, final String buttonId, final String buttonIconUrl, final String buttonText, final String buttonTooltip, final MainMenuInfo mainMenuInfo, final int iconSize, boolean isToggled) {
        this.viewKey = windowId;
        this.id = buttonId;
        this.text = buttonText;
        this.iconUrl = buttonIconUrl;
        this.tooltip = buttonTooltip;
        this.mainMenuInfo = mainMenuInfo;
        this.iconSize = iconSize;
        this.isToggled = isToggled;
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
    public MainMenuInfo getMainMenuInfo() {
        return mainMenuInfo;
    }

    /**
     * Get icon size for current menu item or button
     *
     * @return icon size
     */
    public int getIconSize() {
        return iconSize;
    }

    /**
     * Check if is toggled button type
     *
     * @return true if is toggled button type, otherwise false
     */
    public boolean isToggled() {
        return isToggled;
    }

    public static class GLToolConstants {
        public final static String GROUPS_WINDOW_KEY = ViewKey.GROUPS_WINDOW.getKey();
        public final static String PROJECTS_WINDOW_KEY = ViewKey.PROJECTS_WINDOW.getKey();
        public final static String ALL_WINDOW_KEY = ViewKey.ALL_WINDOWS.getKey();
        public final static String COMMON_VIEW_KEY = ViewKey.COMMON_VIEW.getKey();

        public final static int ICON_SIZE_16 = 16;
        public final static int ICON_SIZE_20 = 20;
    }

    public enum MainMenuInfo {
        FILE("File", 1),
        PROJECT("Project", 2),
        GIT("Git", 3),
        THEMES("Themes", 4),
        HELP("Help", 5);

        private final String name;
        private final int order;

        MainMenuInfo(String name, int order) {
            this.name = name;
            this.order = order;
        }

        public String getName() {
            return name;
        }

        public int getOrder() {
            return order;
        }
    }
}
