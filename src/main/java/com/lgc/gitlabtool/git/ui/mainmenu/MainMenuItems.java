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

    WELCOME_EXIT(ViewKey.WELCOME_WINDOW.getKey(), "File", "welcome_file_exit", "Exit"),
    WELCOME_ABOUT(ViewKey.WELCOME_WINDOW.getKey(), "Help", "welcome_help_about", "About"),

    MAIN_EXIT(ViewKey.MAIN_WINDOW.getKey(), "File", "main_file_exit", "Exit"),
    MAIN_CREATE_BRANCH(ViewKey.MAIN_WINDOW.getKey(), "Git", "main_git_create_branch", "New branch"),
    MAIN_SWITCH_BRANCH(ViewKey.MAIN_WINDOW.getKey(), "Git", "main_git_switch_branch", "Switch branch"),
    MAIN_ABOUT(ViewKey.MAIN_WINDOW.getKey(), "Help", "main_help_about", "About");

    private final String viewKey;
    private final String menuName;
    private final String id;
    private final String text;

    private MainMenuItems(final String windowId, final String menuName, final String itemId, final String itemText) {
        this.viewKey = windowId;
        this.menuName = menuName;
        this.id = itemId;
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
     * Gets the name of menu in which menu item placed
     *
     * @return menu's name
     */
    public String getMenuName() {
        return menuName;
    }
}
