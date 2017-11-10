package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.CommitDialog;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorDefaultType;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorExtensionsType;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorProjectsType;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listcells.FilesListCell;
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

public class StageRemoveNewFilesWindowController {

    private static final String MOVE_UP_IMAGE_URL = "icons/arrow-up20x20.png";
    private static final String MOVE_DOWN_IMAGE_URL = "icons/arrow-down20x20.png";

    private final Image _imageMoveUpButton;
    private final Image _imageMoveDownButton;

    private final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private final ProjectService _projectService = (ProjectService) ServiceProvider.getInstance()
            .getService(ProjectService.class.getName());

    private final List<Integer> _selectedProjectIds = new ArrayList<>();
    private final ProjectList _projectList = ProjectList.get(null);

    {
        ClassLoader loader = getClass().getClassLoader();
        _imageMoveUpButton = new Image(loader.getResource(MOVE_UP_IMAGE_URL).toExternalForm());
        _imageMoveDownButton = new Image(loader.getResource(MOVE_DOWN_IMAGE_URL).toExternalForm());
    }

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
    private Button _applyButton;

    @FXML
    private Button _deleteButton;

    @FXML
    private Button _applyCommitButton;

    @FXML
    private Button _exitButton;

    public void beforeShowing(List<Integer> projectIds) {
        ObservableList<SortingType> items = FXCollections.observableArrayList
                (SortingType.PROJECTS,SortingType.EXTENSIONS, SortingType.DEFAULT);
        _sortingListBox.setItems(items);
        _sortingListBox.setValue(SortingType.DEFAULT);

        _selectedProjectIds.addAll(projectIds);
        _filterField.textProperty().addListener((observable, oldValue, newValue) -> filterUnstagedList(oldValue, newValue));

        configureListViews();
    }

    private void configureListViews() {
        ObservableList<ChangedFile> items = FXCollections.observableArrayList(getUnstagedFilesSelectedProjects());

        _unstagedFilesListView.setItems(items);
        setupListView(_unstagedFilesListView);
        _unstagedFilesListView.addEventFilter(MouseEvent.MOUSE_PRESSED,
                event -> changeFocuseAndSelection(_unstagedFilesListView, _stagedFilesListView));

        setupListView(_stagedFilesListView);
        _stagedFilesListView.addEventFilter(MouseEvent.MOUSE_PRESSED,
                event -> changeFocuseAndSelection(_stagedFilesListView, _unstagedFilesListView));

        setContentAndComparatorToLists();
    }

    private void setupListView(ListView<ChangedFile> list) {
        list.setCellFactory(p -> new FilesListCell());
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        list.getSelectionModel().getSelectedItems().addListener(new FilesListChangeListener());
    }

    // Fix bug with double selection. We can have selected items only in one list.
    private void changeFocuseAndSelection(ListView<ChangedFile> setFocuseList, ListView<ChangedFile> resetSelectionList) {
        setFocuseList.requestFocus();
        resetSelectionList.getSelectionModel().clearSelection();
    }

    private void filterUnstagedList(String oldValue, String newValue) {
        ObservableList<ChangedFile> items = FXCollections.observableArrayList(new ArrayList<>());
        Collection<ChangedFile> unstagedItems = getFilesInUnstagedList();

        if (_filterField == null || _filterField.getText().equals(StringUtils.EMPTY)) {
            items.addAll(unstagedItems);
        } else {
            String searchText = _filterField.getText();
            List<ChangedFile> foundFiles = unstagedItems.stream()
                                                        .filter(file -> file.getFileName().contains(searchText))
                                                        .collect(Collectors.toList());
            if (!foundFiles.isEmpty()) {
                items.addAll(foundFiles);
            }
        }
        _unstagedFilesListView.setItems(items);
        setContentAndComparator(_unstagedFilesListView);
    }

    @FXML
    public List<ChangedFile> onApplyAction(ActionEvent event) {
        List<ChangedFile> stagedList = _stagedFilesListView.getItems();
        Map<Project, List<ChangedFile>> map = new HashMap<>();
        for (ChangedFile changedList : stagedList) {
            List<ChangedFile> files = map.get(changedList.getProject());
            if (files == null) {
                files = new ArrayList<>();
            }
            files.add(changedList);
            map.put(changedList.getProject(), files);
        }
        List<ChangedFile> addedFiles = _gitService.addUntrackedFileForCommit(map);
        removeItemsFromList(_stagedFilesListView, addedFiles);
        return addedFiles;
    }

    @FXML
    public void onApplyCommitAction(ActionEvent event) {
        List<ChangedFile> addedFiles =  onApplyAction(event);
        List<Project> changedProjects = addedFiles.stream()
                                                  .map(files -> files.getProject())
                                                  .collect(Collectors.toList());
        // If we add files which have conflicts to index we should update project statuses
        _projectService.updateProjectStatuses(changedProjects);

        CommitDialog dialog = new CommitDialog();
        dialog.commitChanges(changedProjects);
    }

    @FXML
    public void onDeleteAction(ActionEvent event) {
        List<ChangedFile> unstagedList = getSelectedItems(_unstagedFilesListView);
        if (!unstagedList.isEmpty()) {
            List<ChangedFile> removedFiles = deleteSelectedFiles(unstagedList);
            removeItemsFromList(_unstagedFilesListView, removedFiles);
        } else {
            List<ChangedFile> removedFiles = deleteSelectedFiles(getSelectedItems(_stagedFilesListView));
            removeItemsFromList(_stagedFilesListView, removedFiles);
        }
        onChangedSelectionAction();
    }

    @FXML
    public void onChangeSortingType(ActionEvent event) {
        setContentAndComparatorToLists();
    }

    private Collection<ChangedFile> getFilesInUnstagedList() {
        Collection<ChangedFile> unstagedFiles = getUnstagedFilesSelectedProjects();
        List<ChangedFile> stagedItems = _stagedFilesListView.getItems();
        if (!stagedItems.isEmpty()) {
            unstagedFiles.removeAll(stagedItems);
        }
        return unstagedFiles;
    }

    private void setContentAndComparatorToLists() {
        setContentAndComparator(_unstagedFilesListView);
        setContentAndComparator(_stagedFilesListView);
    }

    private void setContentAndComparator(ListView<ChangedFile> list) {
        SortedList<ChangedFile> sortedList = new SortedList<>(list.getItems());
        Comparator<ChangedFile> comparator = SortingType.getComparatorByType(_sortingListBox.getValue());
        sortedList.setComparator(comparator);
        list.setItems(sortedList);
    }

    private void removeItemsFromList(ListView<ChangedFile> fromList, List<ChangedFile> removeFiles) {
        ObservableList<ChangedFile> fromListItems = FXCollections.observableArrayList(fromList.getItems());
        fromListItems.removeAll(removeFiles);
        fromList.setItems(fromListItems);

        setContentAndComparatorToLists();
        onChangedSelectionAction();
    }

    private List<ChangedFile> deleteSelectedFiles(List<ChangedFile> files) {
        return files.stream().filter(this::deleteFile)
                             .collect(Collectors.toList());
    }

    private boolean deleteFile(ChangedFile file) {
        String projectPath = file.getProject().getPath();
        String fileName = file.getFileName();
        return PathUtilities.deletePath(projectPath + File.separatorChar + fileName);
    }

    private List<ChangedFile> getSelectedItems(ListView<ChangedFile> list) {
        return list.getSelectionModel().getSelectedItems();
    }

    @FXML
    public void moveBetweenLists(ActionEvent event) {
        boolean hasSelectedFiles = hasSelectedItems(_unstagedFilesListView);
        if (hasSelectedFiles) {
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

    @FXML
    public void onExitAction(ActionEvent event) {
        Stage stage = (Stage) _exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onSelectAll() {
        if (_unstagedFilesListView != null && !_unstagedFilesListView.getItems().isEmpty()) {
            _unstagedFilesListView.getSelectionModel().selectAll();
            _unstagedFilesListView.requestFocus();
        }
    }

    private void onDeselectAll() {
        if (_unstagedFilesListView != null && !_unstagedFilesListView.getItems().isEmpty()) {
            _unstagedFilesListView.getSelectionModel().clearSelection();
            _unstagedFilesListView.requestFocus();
        }
    }

    private List<Project> getSelectedProjects() {
        return _projectList.getProjectsByIds(_selectedProjectIds);
    }

    private Collection<ChangedFile> getUnstagedFiles(Project project) {
        return _gitService.getChangedFiles(project);
    }

    private Collection<ChangedFile> getUnstagedFilesSelectedProjects() {
        Collection<Project> selectedProjects = getSelectedProjects();
        Collection<ChangedFile> files = new ArrayList<>();
        selectedProjects.forEach(project -> files.addAll(getUnstagedFiles(project)));
        return files;
    }


    /******************** Methods for changing selection event in lists ********************/

    /**
    *
    *
    * @author Lyudmila Lyska
    */
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
        setDisableApplyButton();
    }

    private void setDisableApplyButton() {
        boolean isDisable = _stagedFilesListView.getItems().isEmpty();
        _applyButton.setDisable(isDisable);
        _applyCommitButton.setDisable(isDisable);
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
        if (hasSelectedItems(_unstagedFilesListView)) {
            setGraphicAndDisableMoveButton(_imageMoveDownButton, false);
        } else if (hasSelectedItems(_stagedFilesListView)) {
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
        boolean hasSelection = hasSelectedItems(_unstagedFilesListView) || hasSelectedItems(_stagedFilesListView);
        _deleteButton.setDisable(!hasSelection);
    }

    private boolean hasSelectedItems(ListView<ChangedFile> list) {
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
                return "A-Z";
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
