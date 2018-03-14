package com.lgc.gitlabtool.git.preferences;

/**
 * Contains nodes names for the ApplicationPreferences
 *
 * The preferences tree will have the following structure:
 * <code>
 *      gitlab_tool_application_preferences <br>
 *          | <br>
 *          |-cloning_group_prefs <br>
 *          |-glt_theme <br>
 *          |-modular_controller_dividers <br>
 *          |-open_in_terminal_command <br>
 * </code>
 *
 *  @author Igor Khlaponin
 */
public class PreferencesNodes {

    /**
     * The root node of GitlabTool application preferences
     */
    static final String GLT_PREFERENCES_NODE = "gitlab_tool_application_preferences";
    /**
     * Contains cloning group path
     */
    public static final String CLONING_GROUP_NODE = "cloning_group_prefs";
    /**
     * Contains {@link com.lgc.gitlabtool.git.ui.javafx.controllers.ModularController} dividers positions
     */
    public static final String DIVIDER_PROPERTY_NODE = "modular_controller_dividers";
    /**
     * Contains the current theme
     */
    public static final String THEME_PREFS_NODE = "glt_theme";
    /**
     * Contains the command for terminal opening
     */
    public static final String OPEN_TERMINAL_NODE = "open_in_terminal_command";
}
