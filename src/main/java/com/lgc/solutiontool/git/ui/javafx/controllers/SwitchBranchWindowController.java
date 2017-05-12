package com.lgc.solutiontool.git.ui.javafx.controllers;


import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.ui.selection.ListViewKey;
import com.lgc.solutiontool.git.ui.selection.SelectionsProvider;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.util.List;

public class SwitchBranchWindowController {

    @FXML
    public ListView currentProjectsListView;

    @FXML
    public Label projectsCount;

    @FXML
    public void initialize() {
        configureProjectsListView(currentProjectsListView);

        List<?> selectedProjects = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
        setProjectListItems(selectedProjects, currentProjectsListView);

        projectsCount.setText("Total projects: " + currentProjectsListView.getItems().size());
    }

    private void setProjectListItems(List items, ListView<Project> listView) {
        if (items == null || items.isEmpty()) {
            return;
        }

        if (items.get(0) instanceof Project) {
            currentProjectsListView.setItems(FXCollections.observableArrayList(items));
        }
    }

    private void configureProjectsListView(ListView listView) {
        //config displayable string
        listView.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>() {
            @Override
            public ListCell<Project> call(ListView<Project> p) {
                return new SwitchBranchWindowController.GroupListCell();
            }
        });

        //setup selection
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
//            Node node = evt.getPickResult().getIntersectedNode();
//
//            while (node != null && node != listView && !(node instanceof ListCell)) {
//                node = node.getParent();
//            }
//
//            if (node instanceof ListCell) {
//                evt.consume();
//
//                ListCell cell = (ListCell) node;
//                ListView lv = cell.getListView();
//
//                lv.requestFocus();
//
//                if (!cell.isEmpty()) {
//                    int index = cell.getIndex();
//                    if (cell.isSelected()) {
//                        lv.getSelectionModel().clearSelection(index);
//                    } else {
//                        lv.getSelectionModel().select(index);
//                    }
//                }
//            }
//        });

        listView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                projectsCount.setText("Total projects: " + listView.getItems().size());
                SelectionsProvider.getInstance().setSelectionItems(ListViewKey.MAIN_WINDOW_PROJECTS.getKey(),
                        listView.getSelectionModel().getSelectedItems());
            }
        });
    }

    private class GroupListCell extends ListCell<Project> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("x");

        public GroupListCell() {
            super();

            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> getListView().getItems().remove(getItem()));
        }

        @Override
        protected void updateItem(Project item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                label.setText(item.getName());
                setGraphic(hbox);
            }
        }
    }
}
