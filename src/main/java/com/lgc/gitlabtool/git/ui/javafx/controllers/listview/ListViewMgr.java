package com.lgc.gitlabtool.git.ui.javafx.controllers.listview;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.ui.ViewKey;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleksandr Kozlov on 2/19/2018.
 */
public class ListViewMgr implements ComponentMgr<ListView> {

    private ArrayList<ListView> listViews = new ArrayList<>();

    private ArrayList<ActiveViewChangeListener> activeViewChangeListener = new ArrayList<>();

    private String activeView;

    public ListViewMgr() {

    }

    @Override
    public void add(ListView control) {
        if (!listViews.contains(control)) {
            listViews.add(control);
        }
    }

    @Override
    public void remove(ListView control) {
        if (listViews.contains(control)) {
            listViews.remove(control);
        }
    }

    public void setActiveView(String viewKey) {
        activeView = viewKey;
        notifyChangeActiveViewEvent(activeView);
    }

    @Override
    public int getComponentCount() {
        return listViews.size();
    }

    @Override
    public List<ListView> getComponents() {
        return listViews;
    }

    public ProjectListView getProjectListView() {
        ListView lv = null;
        for(ListView listView : listViews) {
            if (listView instanceof ProjectListView) {
                lv = listView;
            }
        }
        return (ProjectListView)lv;
    }

    public void addChangeActiveViewListener(ActiveViewChangeListener listener) {
        activeViewChangeListener.add(listener);
    }

    public void removeChangeActiveViewListener(ActiveViewChangeListener listener) {
        activeViewChangeListener.remove(listener);
    }

    public void notifyChangeActiveViewEvent(String activeView) {
        for(ActiveViewChangeListener listener : activeViewChangeListener) {
            listener.onChanged(activeView);
        }
    }

}
