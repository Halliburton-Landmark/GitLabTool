package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorDefaultType;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorExtensionsType;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorProjectsType;
import com.lgc.gitlabtool.git.util.PathUtilities;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AddRemoveFilesWindowController {

    @FXML
    private TextField _filterField;

    @FXML
    private ChoiceBox<SortingType> _sortingListBox;

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

    @FXML
    private Button _addCommitButton;

    @FXML
    private Button _exitButton;

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
                                                                              SortingType.EXTENSIONS,
                                                                              SortingType.DEFAULT);
        _sortingListBox.setItems(items);
        _sortingListBox.setValue(SortingType.DEFAULT);

        _selectButton.setGraphic(new ImageView(_imageSelectButton));

        _moveUpDownButton.setOnAction(this::moveBetweenLists);
        _addButton.setOnAction(this::onAddFilesAction);
        _removeButton.setOnAction(this::onRemoveAction);
        _exitButton.setOnAction(this::onExitAction);

        configureListViews();
    }

    private void configureListViews() {
        List<Project> items = getSelectedProjects();
        Project project = items.get(0);
        Collection<ChangedFile> files = getUnstagedFiles(project);

        _unstagedFilesListView.setCellFactory(p -> new FilesListCell());
        _unstagedFilesListView.setItems(FXCollections.observableArrayList(files));
        _unstagedFilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _unstagedFilesListView.getSelectionModel().getSelectedItems().addListener(new FilesListChangeListener());
        _unstagedFilesListView.addEventFilter(MouseEvent.MOUSE_PRESSED,
                event -> changeFocuseAndSelection(_unstagedFilesListView, _stagedFilesListView));

        _stagedFilesListView.setCellFactory(p -> new FilesListCell());
        _stagedFilesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        _stagedFilesListView.getSelectionModel().getSelectedItems().addListener(new FilesListChangeListener());
        _stagedFilesListView.addEventFilter(MouseEvent.MOUSE_PRESSED,
                event -> changeFocuseAndSelection(_stagedFilesListView, _unstagedFilesListView));
        setContentAndComparatorToLists();
    }

    private void changeFocuseAndSelection(ListView<ChangedFile> setFocuseList, ListView<ChangedFile> resetSelectionList) {
        setFocuseList.requestFocus();
        resetSelectionList.getSelectionModel().clearSelection();
    }

    @SuppressWarnings("unchecked")
    private void onAddFilesAction(ActionEvent event) {
        List<ChangedFile> unstagedList = _stagedFilesListView.getItems();
        Map<Project, List<ChangedFile>> map = new HashedMap();
        for (ChangedFile changedList : unstagedList) {
            List<ChangedFile> files = map.get(changedList.getProject());
            if (files == null) {
                files = new ArrayList<>();
            }
            files.add(changedList);
            map.put(changedList.getProject(), files);
        }
        List<ChangedFile> addedFiles = _gitService.addUntrackedFileForCommit(map);
        removeItemsFromList(_stagedFilesListView, addedFiles);
    }

    private void onRemoveAction(ActionEvent event) {
        List<ChangedFile> unstagedList = getSelectedItems(_unstagedFilesListView);
        if (!unstagedList.isEmpty()) {
            List<ChangedFile> removedFiles = removeSelectedFiles(unstagedList);
            removeItemsFromList(_unstagedFilesListView, removedFiles);
        } else {
            List<ChangedFile> removedFiles = removeSelectedFiles(getSelectedItems(_stagedFilesListView));
            removeItemsFromList(_stagedFilesListView, removedFiles);
        }
        onChangedSelectionAction();
    }

    private void setContentAndComparatorToLists() {
        SortedList<ChangedFile> unstagedFiles = new SortedList<>(_unstagedFilesListView.getItems());
        SortedList<ChangedFile> stagedFiles = new SortedList<>(_stagedFilesListView.getItems());

        Comparator<ChangedFile> comparator = SortingType.getComparatorByType(_sortingListBox.getValue());
        unstagedFiles.setComparator(comparator);
        stagedFiles.setComparator(comparator);

        _unstagedFilesListView.setItems(unstagedFiles);
        _stagedFilesListView.setItems(stagedFiles);
    }

    private void removeItemsFromList(ListView<ChangedFile> fromList, List<ChangedFile> removeFiles) {
        ObservableList<ChangedFile> fromListItems = FXCollections.observableArrayList(fromList.getItems());
        fromListItems.removeAll(removeFiles);
        fromList.setItems(fromListItems);

        setContentAndComparatorToLists();
        onChangedSelectionAction();
    }

    private List<ChangedFile> removeSelectedFiles(List<ChangedFile> files) {
        List<ChangedFile> removedFiles = new ArrayList<>();
        for (ChangedFile file : files) {
            String projectPath = file.getProject().getPath();
            String fileName = file.getFileName();
            boolean result = PathUtilities.deletePath(projectPath + File.separatorChar + fileName);
            if (result) {
                removedFiles.add(file);
            }
        }
        return removedFiles;
    }

    private List<ChangedFile> getSelectedItems(ListView<ChangedFile> list) {
        return list.getSelectionModel().getSelectedItems();
    }

    private void moveBetweenLists(ActionEvent event) {
        boolean isUnstagedFiles = hasListSelectionItems(_unstagedFilesListView);
        if (isUnstagedFiles) {
            moveBetweenLists(_unstagedFilesListView, _stagedFilesListView);
        } else {
            moveBetweenLists(_stagedFilesListView, _unstagedFilesListView);
        }
    }

    private void moveBetweenLists(ListView<ChangedFile> fromList, ListView<ChangedFile> toList) {
        List<ChangedFile> selectedFiles = getSelectedItems(fromList);
        if (selectedFiles != null) {
            ObservableList<ChangedFile> toListItems = FXCollections.observableArrayList(selectedFiles);
            toListItems.addAll(toList.getItems());
            toList.setItems(toListItems);

            removeItemsFromList(fromList, selectedFiles);
        }
    }

    private void onExitAction(ActionEvent event) {
        Stage stage = (Stage) _exitButton.getScene().getWindow();
        stage.close();
    }

    /**
     *
     */
    public void onSelectAll() {
        if (_unstagedFilesListView == null || _unstagedFilesListView.getItems().isEmpty()) {
            return;
        }
        _unstagedFilesListView.getSelectionModel().selectAll();
        _unstagedFilesListView.requestFocus();
    }

    private void onDeselectAll() {
        if (_unstagedFilesListView == null || _unstagedFilesListView.getItems().isEmpty()) {
            return;
        }
        _unstagedFilesListView.getSelectionModel().clearSelection();
        _unstagedFilesListView.requestFocus();
    }

    private Collection<ChangedFile> getUnstagedFiles(Project project) {
        return _gitService.getChangedFiles(project);
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
        setDisableRemoveButton();
        setDisableAddButton();
    }

    private void setDisableAddButton() {
        _addButton.setDisable(_stagedFilesListView.getItems().isEmpty());
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

    private boolean areFilesSelected(ListView<?> listView) {
        if (_unstagedFilesListView == null || _unstagedFilesListView.getItems().isEmpty()) {
            return false;
        }
        return _unstagedFilesListView.getItems().size() == getSelectedItems(_unstagedFilesListView).size();
    }

    private void updateMoveUpDownButton() {
        boolean isUnstagedFilesSelected = hasListSelectionItems(_unstagedFilesListView);
        boolean isStagedFlesSelected = hasListSelectionItems(_stagedFilesListView);
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

    private void setDisableRemoveButton() {
        boolean isUnstagedFilesSelected = hasListSelectionItems(_unstagedFilesListView);
        boolean isStagedFlesSelected = hasListSelectionItems(_stagedFilesListView);
        if (isUnstagedFilesSelected || isStagedFlesSelected) {
            _removeButton.setDisable(false);
        } else {
            _removeButton.setDisable(true);
        }
    }

    private boolean hasListSelectionItems(ListView<ChangedFile> list) {
        return !getSelectedItems(list).isEmpty();
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

        EXTENSIONS {
            @Override
            public String toString() {
                return "extensions";
            }
        },

        DEFAULT {
            @Override
            public String toString() {
                return "default";
            }
        };

        private static Comparator<ChangedFile> getComparatorByType(SortingType type) {
            if (type == SortingType.DEFAULT) {
                return new ComparatorDefaultType();
            } else if (type == SortingType.PROJECTS) {
                return new ComparatorProjectsType();
            } else {
                return new ComparatorExtensionsType();
            }
        }
    }

}
