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
    private Button _moveUpDownButton;

    @FXML
    private Button _addButton;

    @FXML
    private Button _removeButton;

    private static final String SELECT_ALL_IMAGE_URL = "icons/select_all_20x20.png";
    private static final String MOVE_UP_IMAGE_URL = "icons/arrow-up20x20.png";
    private static final String MOVE_DOWN_IMAGE_URL = "icons/arrow-down20x20.png";

    private final Image _imageSelectButton;
    private final Image _imageMoveUpButton;
    private final Image _imageMoveDownButton;

    {
        _imageSelectButton = new Image(getClass().getClassLoader().getResource(SELECT_ALL_IMAGE_URL).toExternalForm());
        _imageMoveUpButton = new Image(getClass().getClassLoader().getResource(MOVE_UP_IMAGE_URL).toExternalForm());
        _imageMoveDownButton = new Image(getClass().getClassLoader().getResource(MOVE_DOWN_IMAGE_URL).toExternalForm());

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

        _moveUpDownButton.setDisable(true);
        _moveUpDownButton.setOnAction(this::moveBetweenLists);

        configureListViews();
    }

    private void moveBetweenLists(ActionEvent event) {
        boolean isUnstagedFiles = !_unstagedFilesListView.getSelectionModel().getSelectedItems().isEmpty();
        if (isUnstagedFiles) {
            moveBetweenLists(_unstagedFilesListView, _stagedFilesListView);
        } else {
            moveBetweenLists(_stagedFilesListView, _unstagedFilesListView);
        }
    }

    private void moveBetweenLists(ListView<ChangedFile> fromList, ListView<ChangedFile> toList) {
        List<ChangedFile> allFiles = fromList.getItems();
        List<ChangedFile> selectedFiles = fromList.getSelectionModel().getSelectedItems();

        if (selectedFiles != null) {
            ObservableList<ChangedFile> toListItems = FXCollections.observableArrayList(selectedFiles);
            toList.getItems().addAll(toListItems);

            allFiles.removeAll(selectedFiles);

            ObservableList<ChangedFile> fromListItems = FXCollections.observableArrayList(allFiles);
            fromList.setItems(fromListItems);
            fromList.getSelectionModel().clearSelection();

            onChangedSelectionAction();
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

    /**
     *
     */
    public void onSelectAll() {
        _unstagedFilesListView.getSelectionModel().selectAll();
        _unstagedFilesListView.requestFocus();
    }

    private void onDeselectAll() {
        _unstagedFilesListView.getSelectionModel().clearSelection();
        _unstagedFilesListView.requestFocus();
    }

    private boolean areFilesSelected(ListView<?> listView) {
        if (_unstagedFilesListView == null || _unstagedFilesListView.getItems().isEmpty()) {
            return false;
        }
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

    class FilesListChangeListener implements ListChangeListener<ChangedFile> {

        @Override
        public void onChanged(ListChangeListener.Change<? extends ChangedFile> event) {
            onChangedSelectionAction();
        }

    }

    private void onChangedSelectionAction() {
        selectAllAction();
        updateMoveUpDownButton();
    }

    private void selectAllAction() {
        boolean isSelectedAll = areFilesSelected(_unstagedFilesListView);
        _selectButton.setSelected(isSelectedAll);
        if (isSelectedAll) {
            _selectButton.setOnAction(action -> onDeselectAll());
        } else {
            _selectButton.setOnAction(action -> onSelectAll());
        }
    }

    private void updateMoveUpDownButton() {
        boolean isUnstagedFilesSelected = !_unstagedFilesListView.getSelectionModel().getSelectedItems().isEmpty();
        boolean isStagedFlesSelected = !_stagedFilesListView.getSelectionModel().getSelectedItems().isEmpty();
        if (isUnstagedFilesSelected) {
            setGraphicAndDisableMoveButton(_imageMoveDownButton, false);
        } else if (isStagedFlesSelected) {
            setGraphicAndDisableMoveButton(_imageMoveUpButton, false);
        } else {
            setGraphicAndDisableMoveButton(null, true);
        }
    }

    private void setGraphicAndDisableMoveButton(Image image, boolean isDisable) {
        _moveUpDownButton.setGraphic(new ImageView(image));
        _moveUpDownButton.setDisable(isDisable);
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
