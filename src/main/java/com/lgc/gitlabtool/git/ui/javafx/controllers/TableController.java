package com.lgc.gitlabtool.git.ui.javafx.controllers;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.ui.table.Commit;
import com.lgc.gitlabtool.git.ui.table.CommitHistoryTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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

        ObservableList<Commit> data = FXCollections.observableArrayList();
        data.add(new Commit("1","bug 435210 concurrent modification exception in log file","Kozlov","2/2/2018","Kozlov","4/2/2018"));
        data.add(new Commit("2","refactoring / minor changes","Kozlov","3/2/2018","Kozlov","4/2/2018"));
        data.add(new Commit("3","new feature for supporting legacy code","Kozlov","3/2/2018","Kozlov","4/2/2018"));

        historyTable.setItems(data);

        initListeners();
    }

    private void initListeners() {

        // Temporary stub for testing commit history
        historyTable.setRowFactory( tv -> {
            TableRow<String> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    System.out.println("###### Clicked !!! ");
                    List<Project> projectsList = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
                    if ( !projectsList.isEmpty() ) {
                        Project project = (com.lgc.gitlabtool.git.entities.Project) projectsList.get(0);

                        String nameBranch = _gitService.getCurrentBranchName(project);
                        List<Commit> commits = _gitService.getAllCommits(project, nameBranch);
                        ObservableList<Commit> data = FXCollections.observableArrayList();
                        data.addAll(commits);
                        historyTable.setItems(data);
                    }
                }
            });
            return row;
        });
    }
}
