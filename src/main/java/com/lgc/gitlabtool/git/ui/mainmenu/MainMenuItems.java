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

    GROUP_WINDOW_EXIT(ViewKey.GROUP_WINDOW.getKey(), "File", "group_window_file_exit", "icons/mainmenu/exit.png", "Exit"),
    GROUP_WINDOW_CLONE_GROUP(ViewKey.GROUP_WINDOW.getKey(), "Git", "group_window_group_clone", "icons/mainmenu/clone.png", "Clone"),
    GROUP_WINDOW_ABOUT(ViewKey.GROUP_WINDOW.getKey(), "Help", "group_window_help_about", "icons/mainmenu/about.png", "About"),

    MAIN_EXIT(ViewKey.MAIN_WINDOW.getKey(), "File", "main_file_exit", "icons/mainmenu/exit.png", "Exit"),
    MAIN_CREATE_BRANCH(ViewKey.MAIN_WINDOW.getKey(), "Git", "main_git_create_branch", "icons/mainmenu/newbranch.png", "New branch"),
    MAIN_SWITCH_BRANCH(ViewKey.MAIN_WINDOW.getKey(), "Git", "main_git_switch_branch", "icons/mainmenu/switchbranch.png", "Switch branch"),
    MAIN_ABOUT(ViewKey.MAIN_WINDOW.getKey(), "Help", "main_help_about", "icons/mainmenu/about.png", "About");

    private final String viewKey;
    private final String menuName;
    private final String id;
    private final String icoUrl;
    private final String text;

    private MainMenuItems(final String windowId, final String menuName, final String itemId, final String icoUrl, final String itemText) {
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
}
