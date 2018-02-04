package com.lgc.gitlabtool.git.ui.table;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableView;

/**
 * Created by Oleksandr Kozlov on 03.02.2018.
 */
public class CommitHistoryTableView<S> extends TableView {

    public CommitHistoryTableView() {
        super();
    }

    public CommitHistoryTableView(ObservableList<S> items) {
        super(items);
    }

    @Override
    protected ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    @Override
    public boolean isResizable() {
        return super.isResizable();
    }

}
