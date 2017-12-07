package com.lgc.gitlabtool.git.ui.mainmenu;

import com.lgc.gitlabtool.git.ui.ViewKey;

/**
 * Enum-helper which contains data for creating menu items
 * Uses for {@link MainMenuManager)
 * For adding a new menu item to main menu you need to add a new Enum item.
 * <p>
 * Note: Follow the naming rules for id - view_menu_menuItemId
 * (it necessary for searching menuItems)
 *
 * @author Pavlo Pidhorniy
 */
public enum MainMenuItems {

    GENERAL_EXIT(MainmenuConstants.ALL_WINDOW_KEY, "File", "main_file_exit", "icons/mainmenu/exit_16x16.png", "Exit"),

    GROUP_WINDOW_CLONE_GROUP(MainmenuConstants.GROUPS_WINDOW_KEY, "Git", "group_window_group_clone", "icons/mainmenu/clone_16x16.png", "Clone"),

    MAIN_CLONE_PROJECT(MainmenuConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_clone_project", "icons/mainmenu/clone_16x16.png", "Clone"),
    MAIN_CREATE_BRANCH(MainmenuConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_create_branch", "icons/mainmenu/newbranch_16x16.png", "New branch"),
    MAIN_CHECKOUT_BRANCH(MainmenuConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_checkout_branch", "icons/mainmenu/checkoutbranch_16x16.png", "Checkout"),
    MAIN_STAGING(MainmenuConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_staging", "icons/mainmenu/staging_16x16.png", "Staging"),
    MAIN_PUSH(MainmenuConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_push", "icons/mainmenu/push_16x16.png", "Push"),
    MAIN_PULL(MainmenuConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_pull", "icons/mainmenu/pull_16x16.png", "Pull"),
    MAIN_REVERT(MainmenuConstants.PROJECTS_WINDOW_KEY, "Git", "main_git_revert", "icons/mainmenu/revert_changes_16x16.png", "Revert"),

    GENERAL_USER_GUIDE(MainmenuConstants.ALL_WINDOW_KEY, "Help", "all_window_help_user_guide", "icons/mainmenu/user_guide_16x16.png", "User guide"),
    GENERAL_ABOUT(MainmenuConstants.ALL_WINDOW_KEY, "Help", "all_window_help_about", "icons/mainmenu/about_16x16.png", "About");

    private final String viewKey;
    private final String menuName;
    private final String id;
    private final String icoUrl;
    private final String text;

    MainMenuItems(final String windowId, final String menuName, final String itemId, final String icoUrl, final String itemText) {
        this.viewKey = windowId;
        this.menuName = menuName;
        this.id = itemId;
        this.icoUrl = icoUrl;
        this.text = itemText;
    }

    /**
     * Gets the menu item's id
     *
     * @return menu item's id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the menu item's text
     *
     * @return menu item's text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the view's key in which menu item placed
     *
     * @return view's key
     */
    public String getViewKey() {
        return viewKey;
    }

    /**
     * Gets the url of icon for menu item
     *
     * @return icon's url for menu item
     */
    public String getIconUrl() {
        return icoUrl;
    }

    /**
     * Gets the name of menu in which menu item placed
     *
     * @return menu's name
     */
    public String getMenuName() {
        return menuName;
    }

    static class MainmenuConstants{
        final static String GROUPS_WINDOW_KEY = ViewKey.GROUPS_WINDOW.getKey();
        final static String PROJECTS_WINDOW_KEY = ViewKey.PROJECTS_WINDOW.getKey();
        final static String ALL_WINDOW_KEY = ViewKey.ALL_WINDOWS.getKey();
    }
}
