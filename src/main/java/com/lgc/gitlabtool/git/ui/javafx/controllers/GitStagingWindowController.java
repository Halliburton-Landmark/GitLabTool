package com.lgc.gitlabtool.git.ui.javafx.controllers;

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
import com.lgc.gitlabtool.git.jgit.ChangedFileType;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorDefaultType;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorExtensionsType;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ComparatorProjectsType;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listcells.FilesListCell;

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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class GitStagingWindowController {

    @FXML
    private TextField _filterField;

    @FXML
    private ChoiceBox<SortingType> _sortingListBox;

    @FXML
    private ListView<ChangedFile> _unstagedListView;

    @FXML
    private ListView<ChangedFile> _stagedListView;

    @FXML
    private Button _commitButton;

    @FXML
    private Button _commitPushButton;

    @FXML
    private Button _cancelButton;

    private final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    //private final ProjectService _projectService = (ProjectService) ServiceProvider.getInstance()
            //.getService(ProjectService.class.getName());

    private final List<Integer> _selectedProjectIds = new ArrayList<>();
    private final ProjectList _projectList = ProjectList.get(null);

    public void beforeShowing(List<Integer> projectIds, Collection<ChangedFile> files) {
        ObservableList<SortingType> items = FXCollections.observableArrayList
                (SortingType.PROJECTS,SortingType.EXTENSIONS, SortingType.DEFAULT);
        _sortingListBox.setItems(items);
        _sortingListBox.setValue(SortingType.DEFAULT);

        _selectedProjectIds.addAll(projectIds);
        _filterField.textProperty().addListener((observable, oldValue, newValue) -> filterUnstagedList(oldValue, newValue));

        configureListViews(files);
    }

    private void fillLists(Collection<ChangedFile> all, Collection<ChangedFile> staged, Collection<ChangedFile> unstaged) {
        for (ChangedFile changedFile : all) {
            if (changedFile.getTypeFile() == ChangedFileType.STAGED) {
                staged.add(changedFile);
                continue;
            }
            unstaged.add(changedFile);
        }
    }

    private void configureListViews(Collection<ChangedFile> files) {
        Collection<ChangedFile> unstagedFiles = new ArrayList<>();
        Collection<ChangedFile> stagedFiles = new ArrayList<>();
        fillLists(files, stagedFiles, unstagedFiles);

        setupListView(_unstagedListView);
        setDragAndDropToUnstagedListView();
        _unstagedListView.setItems(FXCollections.observableArrayList(unstagedFiles));
        _unstagedListView.addEventFilter(MouseEvent.MOUSE_PRESSED,
                event -> changeFocuseAndSelection(_unstagedListView, _stagedListView));

        setupListView(_stagedListView);
        setDragAndDropToStagedListView();
        _stagedListView.setItems(FXCollections.observableArrayList(stagedFiles));
        _stagedListView.addEventFilter(MouseEvent.MOUSE_PRESSED,
                event -> changeFocuseAndSelection(_stagedListView, _unstagedListView));

        setContentAndComparatorToLists();
    }

    // Fix bug with double selection. We can have selected items only in one list.
    private void changeFocuseAndSelection(ListView<ChangedFile> setFocuseList, ListView<ChangedFile> resetSelectionList) {
        setFocuseList.requestFocus();
        resetSelectionList.getSelectionModel().clearSelection();
    }

    @FXML
    public void onChangeSortingType(ActionEvent event) {
        setContentAndComparatorToLists();
    }

    private void setupListView(ListView<ChangedFile> list) {
        list.setCellFactory(p -> new FilesListCell());
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        list.getSelectionModel().getSelectedItems().addListener(new FilesListChangeListener());
    }

    private void setContentAndComparatorToLists() {
        setContentAndComparator(_unstagedListView);
        setContentAndComparator(_stagedListView);
    }

    private void setContentAndComparator(ListView<ChangedFile> list) {
        SortedList<ChangedFile> sortedList = new SortedList<>(list.getItems());
        Comparator<ChangedFile> comparator = SortingType.getComparatorByType(_sortingListBox.getValue());
        sortedList.setComparator(comparator);
        list.setItems(sortedList);
    }

    private void filterUnstagedList(String oldValue, String newValue) {
        ObservableList<ChangedFile> items = FXCollections.observableArrayList(new ArrayList<>());
        Collection<ChangedFile> unstagedItems = getFilesInUnstagedList();

        if (_filterField == null || _filterField.getText().equals(StringUtils.EMPTY)) {
            items.addAll(unstagedItems);
        } else {
            String searchText = _filterField.getText();
            List<ChangedFile> foundFiles = unstagedItems.stream()
                                                        .filter(file -> StringUtils.containsIgnoreCase(file.getFileName(), searchText))
                                                        .collect(Collectors.toList());
            items.addAll(foundFiles);
        }
        _unstagedListView.setItems(items);
        setContentAndComparator(_unstagedListView);
    }

    private void removeItemsFromList(ListView<ChangedFile> fromList, List<ChangedFile> items) {
        ObservableList<ChangedFile> fromListItems = FXCollections.observableArrayList(fromList.getItems());
        fromListItems.removeAll(items);
        fromList.setItems(fromListItems);

        setContentAndComparator(fromList);
    }

    private void addItemsToList(ListView<ChangedFile> toList, List<ChangedFile> items) {
        ObservableList<ChangedFile> toListItems = FXCollections.observableArrayList(toList.getItems());
        toListItems.addAll(items);
        toList.setItems(toListItems);

        setContentAndComparator(toList);
    }

    private Collection<ChangedFile> getFilesInUnstagedList() {
        Collection<ChangedFile> unstagedFiles = getChangedFilesSelectedProjects();
        List<ChangedFile> stagedItems = _stagedListView.getItems();
        if (!stagedItems.isEmpty()) {
            unstagedFiles.removeAll(stagedItems);
        }
        return unstagedFiles;
    }

    private List<Project> getSelectedProjects() {
        return _projectList.getProjectsByIds(_selectedProjectIds);
    }

    private Collection<ChangedFile> getChangedFiles(Project project) {
        return _gitService.getChangedFiles(project);
    }

    private Collection<ChangedFile> getChangedFilesSelectedProjects() {
        Collection<Project> selectedProjects = getSelectedProjects();
        Collection<ChangedFile> files = new ArrayList<>();
        selectedProjects.forEach(project -> files.addAll(getChangedFiles(project)));
        return files;
    }


    /******************** Methods for changing selection event in lists ********************/

    /**
    * Listener for handling changing selection events in ListViews.
    * If change selection item in any list we will update states and actions
    * for select all, move up/down, delete and apply buttons.
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
        setDisableApplyButton();
    }

    private void setDisableApplyButton() {
        boolean isDisable = _stagedListView.getItems().isEmpty();
        _commitButton.setDisable(isDisable);
        _commitPushButton.setDisable(isDisable);
    }

    private List<ChangedFile> getSelectedItems(ListView<ChangedFile> list) {
        return list.getSelectionModel().getSelectedItems();
    }


    /******************************************************************************************************/
    /**
     * Type for sorting files in ListViews.
     *
     * @author Lyudmila Lyska
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

    /************************************************ DRAG AND DROP ************************************************/

    private static final DataFormat _dataFormatUnstagedList = new DataFormat("subUnstagedListFiles");
    private static final DataFormat _dataFormatStagedList = new DataFormat("subStagedListFiles");

    private void setDragAndDropToUnstagedListView() {
        _unstagedListView.setOnDragDetected(evn -> setDragDetectedActionToList(_unstagedListView, _dataFormatUnstagedList));
        setMoveOnDragOverAction(_stagedListView);
        _stagedListView.setOnDragDropped(event -> {
            List<ChangedFile> files = getMovedFiles(event, _dataFormatUnstagedList);
            if (files != null) {
                // here logic for add files to index
                onAddToIndexAction(files);
                event.setDropCompleted(true);
            }
        });
    }

    private void onAddToIndexAction(List<ChangedFile> unstagedFiles) {
        Map<Project, List<ChangedFile>> map = getMapFiles(unstagedFiles);
        List<ChangedFile> addedFiles = _gitService.addUntrackedFilesToIndex(map);
        addRemoveFiles(_stagedListView, _unstagedListView, addedFiles);
    }

    private void onResetAction(List<ChangedFile> stagedFiles) {
        Map<Project, List<ChangedFile>> map = getMapFiles(stagedFiles);
        List<ChangedFile> resetFiles = _gitService.resetChangedFiles(map);
        addRemoveFiles(_unstagedListView, _stagedListView, resetFiles);
    }

    private Map<Project, List<ChangedFile>> getMapFiles(List<ChangedFile> list) {
        Map<Project, List<ChangedFile>> map = new HashMap<>();
        for (ChangedFile changedList : list) {
            List<ChangedFile> files = map.get(changedList.getProject());
            if (files == null) {
                files = new ArrayList<>();
            }
            files.add(changedList);
            map.put(changedList.getProject(), files);
        }
        return map;
    }

    private void setDragAndDropToStagedListView() {
        _stagedListView.setOnDragDetected(event -> setDragDetectedActionToList(_stagedListView, _dataFormatStagedList));
        setMoveOnDragOverAction(_unstagedListView);
        _unstagedListView.setOnDragDropped(event -> {
            List<ChangedFile> files = getMovedFiles(event, _dataFormatStagedList);
            if (files != null) {
                // here logic for remove files from index
                onResetAction(files);
                event.setDropCompleted(true);
            }
        });
    }

    private List<ChangedFile> getMovedFiles(DragEvent event, DataFormat dataFormat) {
        return (List<ChangedFile>) event.getDragboard().getContent(dataFormat);
    }

    private void setDragDetectedActionToList(ListView<ChangedFile> list, DataFormat dataFormat) {
        Dragboard dragBoard = list.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.put(dataFormat, new ArrayList<ChangedFile>(getSelectedItems(list)));
        dragBoard.setContent(content);
    }

    private void setMoveOnDragOverAction(ListView<ChangedFile> list) {
        list.setOnDragOver(event -> event.acceptTransferModes(TransferMode.MOVE));
    }

    private void addRemoveFiles(ListView<ChangedFile> toList, ListView<ChangedFile> fromList, List<ChangedFile> files) {
        addItemsToList(toList, files);
        removeItemsFromList(fromList, files);
    }
}
