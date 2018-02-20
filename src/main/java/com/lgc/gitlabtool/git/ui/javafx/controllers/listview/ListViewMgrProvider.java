package com.lgc.gitlabtool.git.ui.javafx.controllers.listview;

/**
 * Provides an instance of {@link ListViewMgr}
 *
 * Created by Oleksandr Kozlov on 2/19/2018.
 */
public class ListViewMgrProvider {

    private static ListViewMgrProvider provider;

    private static ListViewMgr listViewMgr;

    private ListViewMgrProvider() {}

    public static ListViewMgrProvider getInstance() {
        if (provider == null) {
            provider = new ListViewMgrProvider();
            createFeature();
        }
        return provider;
    }

    private static ListViewMgr createFeature() {
        listViewMgr = new ListViewMgr();
        return listViewMgr;
    }

    public ListViewMgr getFeature() {
        return listViewMgr;
    }

}
