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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Created by Oleksandr Kozlov on 03.02.2018.
 */
public class TableController {

    @FXML
    private CommitHistoryTableView<Commit> historyTable;

    @FXML
    private TableColumn<Commit, Integer> idColumn;

    @FXML
    private TableColumn<Commit, String> messageColumn;

    @FXML
    private TableColumn<Commit, String> authorColumn;

    @FXML
    private TableColumn<Commit, String> authoredDateColumn;

    @FXML
    private TableColumn<Commit, String> committerColumn;

    @FXML
    private TableColumn<Commit, String> committedDateColumn;

    private static final GitService _gitService = ServiceProvider.getInstance()
            .getService(GitService.class);

    private static final ListViewMgrProvider listViewMgrProvider = ListViewMgrProvider.getInstance();

    private ProjectListView projectListView;

    private ListChangeListener<Project> listChangeListener = new ListChangeListener<Project>() {
        @Override
        public void onChanged(Change<? extends Project> projects) {
            List<Project> projectsList = projectListView.getSelectionModel().getSelectedItems();
            Project project = projects.getList().get(0);
            if ( !projectsList.isEmpty() ) {
                String nameBranch = _gitService.getCurrentBranchName(project);
                List<Commit> commits = _gitService.getAllCommits(project, nameBranch);
                ObservableList<Commit> data = FXCollections.observableArrayList();
                data.addAll(commits);
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

        PropertyValueFactory<Commit, Integer> idProperty
                = new PropertyValueFactory<Commit, Integer>("id");

        PropertyValueFactory<Commit, String> messageProperty
                = new PropertyValueFactory<Commit, String>("message");

        PropertyValueFactory<Commit, String> authorProperty
                = new PropertyValueFactory<Commit, String>("author");

        PropertyValueFactory<Commit, String> authoredDateProperty
                = new PropertyValueFactory<Commit, String>("authoredDate");

        PropertyValueFactory<Commit, String> committerProperty
                = new PropertyValueFactory<Commit, String>("committer");

        PropertyValueFactory<Commit, String> committedDateProperty
                = new PropertyValueFactory<Commit, String>("committedDate");

        idColumn.setCellValueFactory(idProperty);
        messageColumn.setCellValueFactory(messageProperty);
        authorColumn.setCellValueFactory(authorProperty);
        authoredDateColumn.setCellValueFactory(authoredDateProperty);
        committerColumn.setCellValueFactory(committerProperty);
        committedDateColumn.setCellValueFactory(committedDateProperty);

        configTable();

        initListeners();
    }

    public void initListeners() {
        projectListView = (ProjectListView) listViewMgrProvider.getFeature().getListView(ProjectListView.class);
        projectListView.getSelectionModel().getSelectedItems().addListener(listChangeListener);

        listViewMgrProvider.getFeature().addChangeActiveViewListener(activeViewChangeListener);
    }

    private void configTable() {
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

}
