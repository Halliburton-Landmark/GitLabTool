package com.lgc.gitlabtool.git.ui.javafx.controllers;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.listeners.stateListeners.ProjectSelectionChangeListener;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.selection.ListViewKey;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.ui.table.Commit;
import com.lgc.gitlabtool.git.ui.table.CommitHistoryTableView;
import javafx.collections.FXCollections;
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

    ProjectSelectionChangeListener projectSelectionChangeListener = new ProjectSelectionChangeListener() {

        @Override
        public void onChanged(List<Project> projects) {
            Project project = projects.get(0);
            List<Project> projectsList = SelectionsProvider.getInstance().getSelectionItems(ListViewKey.MAIN_WINDOW_PROJECTS.getKey());
            if ( !projectsList.isEmpty() ) {
                String nameBranch = _gitService.getCurrentBranchName(project);
                List<Commit> commits = _gitService.getAllCommits(project, nameBranch);
                ObservableList<Commit> data = FXCollections.observableArrayList();
                data.addAll(commits);
                historyTable.setItems(data);
            } else {
                clearTableContent();
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

        clearTableContent();

        initListeners();
    }

    private void initListeners() {
        SelectionsProvider.getInstance().addProjectSelectionChangeListener(projectSelectionChangeListener);
    }

    private void clearTableContent() {
        ObservableList<Commit> data = FXCollections.observableArrayList();
        data.add(new Commit());
        historyTable.setItems(data);
    }

    private void configTable() {
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

}
