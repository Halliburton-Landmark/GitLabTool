package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.jgit.ChangedFiles;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AddRemoveFilesWindowController {

    @FXML
    private TextField _filterField;

    @FXML
    private ChoiceBox<SortingType> _sortingByChoice;

    @FXML
    private ListView<ChangedFile> _unstagedFilesListView;

    @FXML
    private ListView<ChangedFile> _stagedFilesListView;

    @FXML
    private ToggleButton _selectButton;

    @FXML
    private Button _addUpButton;

    @FXML
    private Button _addDownButton;

    private static final String SELECT_ALL_IMAGE_URL = "icons/select_all_20x20.png";
    private static final String ADD_UP_IMAGE_URL = "icons/arrow-up20x20.png";
    private static final String ADD_DOWN_IMAGE_URL = "icons/arrow-down20x20.png";

    private final Image _imageSelectButton;
    private final Image _imageAddUpButton;
    private final Image _imageAddDownButton;

    {
        _imageSelectButton = new Image(getClass().getClassLoader().getResource(SELECT_ALL_IMAGE_URL).toExternalForm());
        _imageAddUpButton = new Image(getClass().getClassLoader().getResource(ADD_UP_IMAGE_URL).toExternalForm());
        _imageAddDownButton = new Image(getClass().getClassLoader().getResource(ADD_DOWN_IMAGE_URL).toExternalForm());

    }

    //data for view
    private List<Integer> _selectedProjectIds = new ArrayList<>();
    private final ProjectList _projectList = ProjectList.get(null);

    private final GitService _gitService = (GitService) ServiceProvider.getInstance()
                    .getService(GitService.class.getName());

    public void beforeShowing(List<Integer> projectIds) {
        _selectedProjectIds = projectIds;
        _filterField.setText("" + _selectedProjectIds.size());

        ObservableList<SortingType> items = FXCollections.observableArrayList(SortingType.PROJECTS,
                                                                              SortingType.TYPE_FILES,
                                                                              SortingType.DEFAULT);
        _sortingByChoice.setItems(items);
        _sortingByChoice.setValue(SortingType.DEFAULT);

        _selectButton.setGraphic(new ImageView(_imageSelectButton));
        _addUpButton.setGraphic(new ImageView(_imageAddUpButton));
        _addDownButton.setGraphic(new ImageView(_imageAddDownButton));

        _addUpButton.setOnAction(this::moveFilesToUpList);
        _addDownButton.setOnAction(this::moveFilesToDownList);

        configureListViews();
    }
    // bug logic
    private void moveFilesToDownList(ActionEvent event) {
        List<ChangedFile> allFiles = _unstagedFilesListView.getItems();
        List<ChangedFile> selectedFiles = _unstagedFilesListView.getSelectionModel().getSelectedItems();

        if (selectedFiles != null) {
            ObservableList<ChangedFile> stagedItems = FXCollections.observableArrayList(selectedFiles);
            _stagedFilesListView.setItems(stagedItems);

            allFiles.removeAll(selectedFiles);

            ObservableList<ChangedFile> unstagedItems = FXCollections.observableArrayList(allFiles);
            _unstagedFilesListView.setItems(unstagedItems);
            changeSelectionFiles();
        }
    }
    // bug logic
    private void moveFilesToUpList(ActionEvent event) {
        List<ChangedFile> allFiles = _stagedFilesListView.getItems();
        List<ChangedFile> selectedFiles = _stagedFilesListView.getSelectionModel().getSelectedItems();

        if (selectedFiles != null) {
            ObservableList<ChangedFile> unstagedItems = FXCollections.observableArrayList(selectedFiles);
            _unstagedFilesListView.setItems(unstagedItems);
            changeSelectionFiles();

            allFiles.removeAll(selectedFiles);

            ObservableList<ChangedFile> stagedItems = FXCollections.observableArrayList(allFiles);
            _stagedFilesListView.setItems(stagedItems);
        }
    }


    private void configureListViews() {
        List<Project> items = getSelectedProjects();
        Project project = items.get(0);
        ObservableList<ChangedFile> observableItems = FXCollections.observableArrayList(getUnstagedFiles(project));

        _unstagedFilesListView.setCellFactory(p -> new FilesListCell());
        _unstagedFilesListView.setItems(observableItems);
        _unstagedFilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _unstagedFilesListView.getSelectionModel().getSelectedItems().addListener(new FilesListChangeListener());

        _stagedFilesListView.setCellFactory(p -> new FilesListCell());
        _stagedFilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _stagedFilesListView.getSelectionModel().getSelectedItems().addListener(new FilesListChangeListener());

    }

    public void onSelectAll() {
        _unstagedFilesListView.getSelectionModel().selectAll();
        _unstagedFilesListView.requestFocus();
    }

    public void onDeselectAll() {
        _unstagedFilesListView.getSelectionModel().clearSelection();
        _unstagedFilesListView.requestFocus();
    }

    private boolean areFilesSelected(ListView<?> listView) {
        return _unstagedFilesListView.getItems().size() ==
               _unstagedFilesListView.getSelectionModel().getSelectedItems().size();
    }

    private Collection<ChangedFile> getUnstagedFiles(Project project) {
        ChangedFiles files = _gitService.getChangedFiles(project);
        return files.getUnstagedFiles();
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

    // here
    class FilesListChangeListener implements ListChangeListener<ChangedFile> {

        @Override
        public void onChanged(ListChangeListener.Change<? extends ChangedFile> event) {
            changeSelectionFiles();
        }

    }

    private void changeSelectionFiles() {
        boolean isSelectedAll = areFilesSelected(_unstagedFilesListView);
        _selectButton.setSelected(isSelectedAll);
        if (isSelectedAll) {
            _selectButton.setOnAction(action -> onDeselectAll());
        } else {
            _selectButton.setOnAction(action -> onSelectAll());
        }
    }

}
