package com.lgc.gitlabtool.git.ui.javafx.controllers;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.javafx.GLTTheme;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ActiveViewChangeListener;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ListViewMgrProvider;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ProjectListView;
import com.lgc.gitlabtool.git.ui.javafx.listeners.ThemeChangeListener;
import com.lgc.gitlabtool.git.ui.table.Commit;
import com.lgc.gitlabtool.git.ui.table.CommitHistoryTableView;
import com.lgc.gitlabtool.git.ui.table.CustomDate;
import com.lgc.gitlabtool.git.ui.table.SortedByDate;
import com.lgc.gitlabtool.git.ui.toolbar.GLToolButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;

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

    private static final ToolbarManager _toolbarManager = ToolbarManager.getInstance();

    private static final GitService _gitService = ServiceProvider.getInstance()
            .getService(GitService.class);

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
        .getService(ThemeService.class);

    private static final ListViewMgrProvider listViewMgrProvider = ListViewMgrProvider.getInstance();

    private ProjectListView projectListView;

    private ToggleButton toggleButton;

    private ThemeChangeListener themeChangeListener = new ThemeChangeListener() {
        @Override
        public void onChanged(String themeName) {
            toggleButton.getGraphic().setEffect(getLightEffect());
        }
    };

    private EventHandler<ActionEvent> toggleChangeAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (toggleButton.isSelected()) {
                historyTable.setVisible(true);
            } else {
                historyTable.setVisible(false);
            }
        }
    };

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
                if ( toggleButton.isSelected() ) {
                    historyTable.setVisible(true);
                }
            } else {
                historyTable.setVisible(false);
            }
        }
    };

    private ActiveViewChangeListener activeViewChangeListener = new ActiveViewChangeListener() {
        @Override
        public void onChanged(String activeView) {
            if (activeView.equals(ViewKey.GROUPS_WINDOW.getKey()) || activeView.equals(ViewKey.PROJECTS_WINDOW.getKey())) {
                historyTable.setVisible(false);

                if (activeView.equals(ViewKey.PROJECTS_WINDOW.getKey()) && toggleButton == null) {
                    List<Node> list = _toolbarManager.getAllButtonsForView(ViewKey.COMMON_VIEW.getKey());
                    for(Node node : list) {
                        if ((node instanceof ToggleButton) && node.getId().equals(GLToolButtons.SHOW_PROJECT_HISTORY.getId())) {
                            toggleButton = (ToggleButton) node;
                            toggleButton.setOnAction(toggleChangeAction);
                            toggleButton.setSelected(false);
                        }
                    }
                }
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

        _themeService.addThemeChangeListener(themeChangeListener);
    }

    private void configTable() {
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setFixedCellSize(35);
    }

    private Effect getLightEffect(){
        boolean isDarkTheme = _themeService.getCurrentTheme().equals(GLTTheme.DARK_THEME);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(_themeService.getLightningCoefficient());

        return isDarkTheme ? colorAdjust : null;
    }

}
