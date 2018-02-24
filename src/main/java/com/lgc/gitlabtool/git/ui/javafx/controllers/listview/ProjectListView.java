package com.lgc.gitlabtool.git.ui.javafx.controllers.listview;

import com.lgc.gitlabtool.git.entities.Project;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * This class represent Project view list
 *
 * Created by Oleksandr Kozlov on 2/19/2018.
 */
public class ProjectListView<T extends Project> extends ListView {

    public ProjectListView() {

    }

    public ProjectListView(ObservableList<T> observableList) {
        super(observableList);
    }

    public ListViewType getType() {
        return ListViewType.PROJECT_LIST;
    }

}
