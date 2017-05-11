package com.lgc.solutiontool.git.ui.selection;

import com.lgc.solutiontool.git.entities.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionsProvider {

    private static SelectionsProvider _instance;

    private final Map<String, List> _listItemsMap;

    public static SelectionsProvider getInstance() {
        if (_instance == null) {
            _instance = new SelectionsProvider();
        }
        return _instance;
    }

    public List getSelectionItems(String listViewId) {
        return _listItemsMap.get(listViewId);
    }

    public void setSelectionItems(String listViewId, List items) {
        if (_listItemsMap.containsKey(listViewId)) {
            _listItemsMap.get(listViewId).clear();
            _listItemsMap.get(listViewId).addAll(items);
        } else {
            _listItemsMap.put(listViewId, items);
        }
    }


    private SelectionsProvider() {
        _listItemsMap = new HashMap<>();
        _listItemsMap.put(ListViewKey.MAIN_WINDOW_PROJECTS.getKey(), new ArrayList<Project>());

    }
}