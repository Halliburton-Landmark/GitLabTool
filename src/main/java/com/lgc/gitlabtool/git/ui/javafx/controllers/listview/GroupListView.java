package com.lgc.gitlabtool.git.ui.javafx.controllers.listview;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;

/**
 * Created by Oleksandr Kozlov on 2/20/2018.
 */
public class GroupListView<T extends Group> extends ListView {

    public GroupListView() {

    }

    public GroupListView(ObservableList<T> observableList) {
        super(observableList);
    }

    public ListViewType getType() {
        return ListViewType.GROUP_LIST;
    }

}
