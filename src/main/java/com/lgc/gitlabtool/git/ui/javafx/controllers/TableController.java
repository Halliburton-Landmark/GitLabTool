package com.lgc.gitlabtool.git.ui.javafx.controllers;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ActiveViewChangeListener;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ListViewMgrProvider;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ProjectListView;
import com.lgc.gitlabtool.git.ui.table.Commit;
import com.lgc.gitlabtool.git.ui.table.CommitHistoryTableView;
import com.lgc.gitlabtool.git.ui.table.CustomDate;
import com.lgc.gitlabtool.git.ui.table.SortedByDate;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Collections;
import java.util.List;

/**
 * This class represent controller for commit history table
 *
 * Created by Oleksandr Kozlov on 03.02.2018.
 */
public class TableController {

    @FXML
    private CommitHistoryTableView<Commit> historyTable;

    @FXML
    private TableColumn<Commit, String> hashColumn;

    @FXML
    private TableColumn<Commit, String> messageColumn;

    @FXML
    private TableColumn<Commit, String> authorColumn;

    @FXML
    private TableColumn<Commit, CustomDate> authoredDateColumn;

    @FXML
    private TableColumn<Commit, String> committerColumn;

    @FXML
    private TableColumn<Commit, CustomDate> dateColumn;

    @FXML
    private TableColumn<Commit, String> projectColumn;

    private static final GitService _gitService = ServiceProvider.getInstance()
            .getService(GitService.class);

    private static final ListViewMgrProvider listViewMgrProvider = ListViewMgrProvider.getInstance();

    private ProjectListView projectListView;

    private ListChangeListener<Project> listChangeListener = new ListChangeListener<Project>() {
        @Override
        public void onChanged(Change<? extends Project> projects) {
            List<Project> projectsList = projectListView.getSelectionModel().getSelectedItems();
            if ( !projectsList.isEmpty() ) {
                ObservableList<Commit> data = FXCollections.observableArrayList();
                for(Project project : projectsList) {
                    String nameBranch = _gitService.getCurrentBranchName(project);
                    List<Commit> commits = _gitService.getAllCommits(project, nameBranch);
                    data.addAll(commits);
                }
                Collections.sort(data, new SortedByDate());
                historyTable.refresh();
                historyTable.setItems(data);
                historyTable.setVisible(true);
            } else {
                historyTable.setVisible(false);
            }
        }
    };

    private ActiveViewChangeListener activeViewChangeListener = new ActiveViewChangeListener() {
        @Override
        public void onChanged(String activeView) {
            if (activeView.equals(ViewKey.GROUPS_WINDOW.getKey())) {
                historyTable.setVisible(false);
            } else if (activeView.equals(ViewKey.PROJECTS_WINDOW.getKey())) {
                historyTable.setVisible(false);
            }
        }
    };

    @FXML
    protected void initialize() {

        PropertyValueFactory<Commit, String> hashProperty
                = new PropertyValueFactory<Commit, String>("hash");

        PropertyValueFactory<Commit, String> messageProperty
                = new PropertyValueFactory<Commit, String>("message");

        PropertyValueFactory<Commit, String> authorProperty
                = new PropertyValueFactory<Commit, String>("author");

        PropertyValueFactory<Commit, CustomDate> authoredDateProperty
                = new PropertyValueFactory<Commit, CustomDate>("authoredDate");

        PropertyValueFactory<Commit, String> committerProperty
                = new PropertyValueFactory<Commit, String>("committer");

        PropertyValueFactory<Commit, CustomDate> dateProperty
                = new PropertyValueFactory<Commit, CustomDate>("date");

        PropertyValueFactory<Commit, String> projectProperty
                = new PropertyValueFactory<Commit, String>("project");

        hashColumn.setCellValueFactory(hashProperty);
        messageColumn.setCellValueFactory(messageProperty);
        authorColumn.setCellValueFactory(authorProperty);
        authoredDateColumn.setCellValueFactory(authoredDateProperty);
        committerColumn.setCellValueFactory(committerProperty);
        dateColumn.setCellValueFactory(dateProperty);
        projectColumn.setCellValueFactory(projectProperty);

        configTable();

        initListeners();
    }

    public void initListeners() {
        projectListView = (ProjectListView) listViewMgrProvider.getFeature().getListView(ProjectListView.class);
        if (projectListView != null) {
            projectListView.getSelectionModel().getSelectedItems().addListener(listChangeListener);
        }

        listViewMgrProvider.getFeature().addChangeActiveViewListener(activeViewChangeListener);
    }

    private void configTable() {
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setFixedCellSize(35);
    }

}
