package com.lgc.solutiontool.git.ui.selection;

public enum ListViewKey {

    MAIN_WINDOW_PROJECTS("mainWindow_projectsList");

    private final String key;

    /**
     * Returns id of viewWindow (using for some ui operations: toolbar, etc)
     *
     * @return id of viewWindow
     */
    public String getKey() {
        return key;
    }

    private ListViewKey(final String selectedKey) {
        this.key = selectedKey;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return key;
    }
}
