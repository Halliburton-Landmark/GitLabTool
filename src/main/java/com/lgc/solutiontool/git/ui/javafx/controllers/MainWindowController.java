package com.lgc.solutiontool.git.ui.javafx.controllers;


import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ProjectService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.ui.toolbar.ToolbarManager;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.List;

public class MainWindowController {
    private static final String HEDER_GROUP_TITLE = "Current group: ";

    private Group _selectedGroup;

    private LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());

    @FXML
    private ListView projectsList;

    @FXML
    private Label leftLabel;

    @FXML
    private Label userId;

    public void beforeShowing() {
        String username = _loginService.getCurrentUser().getName();
        userId.setText(username);

        String currentGroupname = getSelectedGroup().getName();
        leftLabel.setText(HEDER_GROUP_TITLE + currentGroupname);

        configureListView(projectsList);

        BooleanBinding booleanBinding = projectsList.getSelectionModel().selectedItemProperty().isNull();
        ToolbarManager.getInstance().getAllButtonsForCurrentView().forEach(x -> x.disableProperty().bind(booleanBinding));

        //TODO: Additional thread should be placed to services
        new Thread(this::updateProjectList).start();

        configureToolbarCommands();
    }

    public Group getSelectedGroup() {
        return _selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        this._selectedGroup = selectedGroup;
    }

    private void configureToolbarCommands() {
    }

    private void updateProjectList() {
        List<Project> groupProjects = (List<Project>) _selectedGroup.getProjects();
        ObservableList<Project> projectsObservableList = FXCollections.observableList(groupProjects);
        projectsList.setItems(projectsObservableList);
    }

    private void configureListView(ListView listView) {
        //config displayable string
        listView.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>() {
            @Override
            public ListCell<Project> call(ListView<Project> p) {

                return new ListCell<Project>() {
                    @Override
                    protected void updateItem(Project item, boolean bln) {
                        super.updateItem(item, bln);
                        if (item != null) {
                            String itemText = item.getName();
                            setText(itemText);
                        }
                    }
                };
            }
        });

        //setup selection
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            while (node != null && node != listView && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            if (node instanceof ListCell) {
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                lv.requestFocus();

                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });
    }
}
