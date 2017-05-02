package com.lgc.solutiontool.git.ui;

/**
 * Enum-helper for managing viewWindow names
 *
 * @author Pavlo Pidhorniy
 */
public enum ViewKey {

    WELCOME_WINDOW("welcomeWindow", "fxml/WelcomeWindow.fxml"),
    MAIN_WINDOW("mainWindow", "fxml/MainWindow.fxml"),
    MODULAR_CONTAINER("modularContainer", "fxml/ModularContainer.fxml"),
    CLONING_GROUPS_WINDOW("cloningGroupsWindow", "fxml/CloningGroupsWindow.fxml"),
	SERVER_SELECTION_WINDOW("serverSelectionWindow", "fxml/ServerSelectionWindow.fxml");

    private final String key;
    private final String path;

    /**
     * Returns id of viewWindow (using for some ui operations: toolbar, etc)
     *
     * @return id of viewWindow
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns path of viewWindow (using for some ui operations: toolbar, etc)
     *
     * @return path of viewWindow
     */
    public String getPath() {
        return path;
    }


    private ViewKey(final String selectedKey, final String selectedPath) {
        this.key = selectedKey;
        this.path = selectedPath;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return key + " (" + path + ")";
    }
}
