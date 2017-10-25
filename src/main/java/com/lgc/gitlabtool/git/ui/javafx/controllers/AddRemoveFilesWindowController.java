package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

public class AddRemoveFilesWindowController {

    @FXML
    private TextField _filterField;

    @FXML
    private ChoiceBox<SortingType> _sortingByChoice;

    @FXML
    private ListView<String> _unstagedFilesListView;

    @FXML
    private ListView<String> _stagedFilesListView;

    //data for view
    private List<Integer> _selectedProjectIds = new ArrayList<>();
    private final ProjectList _projectList = ProjectList.get(null);

    public void beforeShowing(List<Integer> projectIds) {
        _selectedProjectIds = projectIds;

        _filterField.setText("" + _selectedProjectIds.size());

        ObservableList<SortingType> items = FXCollections.observableArrayList(SortingType.PROJECTS,
                                                                              SortingType.TYPE_FILES,
                                                                              SortingType.DEFAULT);
        _sortingByChoice.setItems(items);
        _sortingByChoice.setValue(SortingType.DEFAULT);

        configureListViews();
    }

    private void configureListViews() {

        _unstagedFilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _stagedFilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }

    private List<Project> getSelectedProjects() {
        return _projectList.getProjectsByIds(_selectedProjectIds);
    }

    /**
     * Type for sorting files in ListViews.
     *
     * @author Lyudmila Lyska
     *
     */
    enum SortingType {
        PROJECTS {
            @Override
            public String toString() {
                return "projects";
            }
        },

        TYPE_FILES {
            @Override
            public String toString() {
                return "files type";
            }
        },

        DEFAULT {
            @Override
            public String toString() {
                return "default";
            }
        };
    }

}
