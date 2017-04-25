package com.lgc.solutiontool.git.ui.toolbar;

import com.lgc.solutiontool.git.ui.ViewKeys;

public enum ToolbarButtons {

    CLONE_GROUP_BUTTON(ViewKeys.WELCOME_WINDOW.getKey(), "group_cloneGroup", "icons/welcome/clone.png", "Clone group", "Clone group"),
    REMOVE_GROUP_BUTTON(ViewKeys.WELCOME_WINDOW.getKey(), "group_removeGroup", "icons/welcome/remove.png", "Remove group", "Remove selected group"),
    IMPORT_GROUP_BUTTON(ViewKeys.WELCOME_WINDOW.getKey(), "group_importGroup", "icons/welcome/import.png", "Import group", "Import group from disk"),
    SELECT_GROUP_BUTTON(ViewKeys.WELCOME_WINDOW.getKey(), "group_selectGroup", "icons/welcome/select.png", "Load group", "Load selected group"),

    ADD_REPO_BUTTON(ViewKeys.MAIN_WINDOW.getKey(), "repo_addRepo", "icons/main/add.png", "Add repository", "Add repository"),
    REMOVE_REPO_BUTTON(ViewKeys.MAIN_WINDOW.getKey(), "repo_removeRepo", "icons/main/remove.png", "Remove repository", "Remove repository"),
    EDIT_REPO_BUTTON(ViewKeys.MAIN_WINDOW.getKey(), "repo_editRepo", "icons/main/edit.png", "Edit repository", "Edit repository"),
    NEW_BRANCH_BUTTON(ViewKeys.MAIN_WINDOW.getKey(), "branch_newBranch", "icons/main/new_branch.png", "New branch", "Create new branch"),
    SWITCH_BRANCH_BUTTON(ViewKeys.MAIN_WINDOW.getKey(), "branch_switchBranch", "icons/main/switch.png", "Switch branch", "Switch to another branch");

    private final String windowKey;
    private final String id;
    private final String text;
    private final String iconUrl;
    private final String tooltip;

    private ToolbarButtons(final String windowId, final String buttonId, final String buttonIconUrl, final String buttonText, final String buttonTooltip) {
        this.windowKey = windowId;
        this.id = buttonId;
        this.text = buttonText;
        this.iconUrl = buttonIconUrl;
        this.tooltip = buttonTooltip;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getWindowKey() {
        return windowKey;
    }
}
