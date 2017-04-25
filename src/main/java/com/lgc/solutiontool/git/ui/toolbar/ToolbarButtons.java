package com.lgc.solutiontool.git.ui.toolbar;

import com.lgc.solutiontool.git.ui.ViewKey;

/**
 * Enum-helper which contains data for creating buttons
 * Uses for {@link ToolbarManager)
 * <p>
 * Note: Follow the naming rules for buttonId - subgroup_comandId
 * (it necessary for dividing toolbar into subgroups)
 */
public enum ToolbarButtons {

    CLONE_GROUP_BUTTON(ViewKey.WELCOME_WINDOW.getKey(), "group_cloneGroup", "icons/welcome/clone.png", "Clone group", "Clone group"),
    REMOVE_GROUP_BUTTON(ViewKey.WELCOME_WINDOW.getKey(), "group_removeGroup", "icons/welcome/remove.png", "Remove group", "Remove selected group"),
    IMPORT_GROUP_BUTTON(ViewKey.WELCOME_WINDOW.getKey(), "group_importGroup", "icons/welcome/import.png", "Import group", "Import group from disk"),
    SELECT_GROUP_BUTTON(ViewKey.WELCOME_WINDOW.getKey(), "group_selectGroup", "icons/welcome/select.png", "Load group", "Load selected group"),

    ADD_REPO_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "repo_addRepo", "icons/main/add.png", "Add repository", "Add repository"),
    REMOVE_REPO_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "repo_removeRepo", "icons/main/remove.png", "Remove repository", "Remove repository"),
    EDIT_REPO_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "repo_editRepo", "icons/main/edit.png", "Edit repository", "Edit repository"),
    NEW_BRANCH_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "branch_newBranch", "icons/main/new_branch.png", "New branch", "Create new branch"),
    SWITCH_BRANCH_BUTTON(ViewKey.MAIN_WINDOW.getKey(), "branch_switchBranch", "icons/main/switch.png", "Switch branch", "Switch to another branch");

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
