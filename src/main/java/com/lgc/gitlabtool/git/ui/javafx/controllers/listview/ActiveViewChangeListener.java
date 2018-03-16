package com.lgc.gitlabtool.git.ui.javafx.controllers.listview;

/**
 * This interface represents listener that listen switching between group and projects view
 *
 * Created by Oleksandr Kozlov on 2/20/2018.
 */
public interface ActiveViewChangeListener {

    /**
     * This method invoked on group or project view activation
     *
     * @param activeView
     */
    public void onChanged(String activeView);

}
