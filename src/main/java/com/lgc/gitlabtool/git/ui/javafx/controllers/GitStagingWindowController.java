package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.jgit.ChangedFileStatus;
import com.lgc.gitlabtool.git.jgit.ChangedFileType;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.AbstractStateListener;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.BackgroundService;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.javafx.GLTAlert;
import com.lgc.gitlabtool.git.ui.javafx.JavaFXUI;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ChangedFileStatusComparator;
import com.lgc.gitlabtool.git.ui.javafx.comparators.DefaultTypeComparator;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ExtensionsTypeComparator;
import com.lgc.gitlabtool.git.ui.javafx.comparators.ProjectsTypeComparator;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listcells.FilesListCell;
import com.lgc.gitlabtool.git.ui.javafx.listeners.CommitPushProgressListener;
import com.lgc.gitlabtool.git.util.PathUtilities;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GitStagingWindowController extends AbstractStateListener {

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
    private Button _exitButton;

    @FXML
    private TextArea _commitText;

    @FXML
    private ProgressIndicator _progressIndicator;

    @FXML
    private Label _progressLabel;

    private static final String STATUS_COMMIT_DIALOG_TITLE = "Committing changes status";
    private static final String STATUS_COMMIT_DIALOG_HEADER = "Committing changes info";
    private static final String STATUS_PUSH_DIALOG_TITLE = "Pushing changes status";
    private static final String STATUS_PUSH_DIALOG_HEADER = "Pushing changes info";
    private static final String COMMIT_PROMPT_TEXT = "Please enter commit message";
    private static final String DELETE_FILES_TITLE = "Delete untracked files";
    private static final String REPLACE_FILES_TITLE = "Replace files with HEAD revision";
    private static final String CONFIRMATION_OPERATION = "Are you sure? You will loose your local changes in those files.";
    private static final String REPLACE_BUTTON_NAME = "Replace with HEAD Revision";
    private static final String DELETE_BUTTON_NAME = "Delete";

    private static final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private static final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    private static final BackgroundService _backgroundService = (BackgroundService) ServiceProvider.getInstance()
            .getService(BackgroundService.class.getName());

    private final List<Integer> _selectedProjectIds = new ArrayList<>();
    private final ProjectList _projectList = ProjectList.get(null);
    private Map<Project, JGitStatus> _commitStatuses = new HashMap<>();
    private final List<ApplicationState> _stagingStates =
            Arrays.asList(ApplicationState.ADD_FILES_TO_INDEX, ApplicationState.RESET, ApplicationState.COMMIT,
                          ApplicationState.PUSH, ApplicationState.UPDATE_PROJECT_STATUSES);

    private final String replaceWithHEADIconUrl = "icons/mainmenu/revert_changes_16x16.png";
    private final String deleteIconUrl = "icons/mainmenu/remove_16x16.png";
    private final ImageView replaceWithHEADImage;
    private final ImageView deleteImage;

    {
        _stagingStates.forEach(state -> _stateService.addStateListener(state, this));

        ClassLoader loader = getClass().getClassLoader();
        Image replaceIcon = new Image(loader.getResource(replaceWithHEADIconUrl).toExternalForm());
        replaceWithHEADImage = new ImageView(replaceIcon);
        Image deleteIcon = new Image(loader.getResource(deleteIconUrl).toExternalForm());
        deleteImage = new ImageView(deleteIcon);
    }

    /**
     * Before showing window we initialize all components.
     * We set ids of projects which were selected in the project window. Also we set their changed files.
     *
     * @param projectIds the ids of projects which were selected in the project window
     * @param files      the changed files os selected projects
     */
    public void beforeShowing(List<Integer> projectIds, Collection<ChangedFile> files) {
        ObservableList<SortingType> items = FXCollections.observableArrayList
                (SortingType.PROJECTS, SortingType.EXTENSIONS, SortingType.FILE_STATUS, SortingType.DEFAULT);
        _sortingListBox.setItems(items);
        _sortingListBox.setValue(SortingType.DEFAULT);
        _commitText.setPromptText(COMMIT_PROMPT_TEXT);

        _selectedProjectIds.addAll(projectIds);
        _filterField.textProperty().addListener((observable, oldValue, newValue) -> filterUnstagedList(oldValue, newValue));

        configureListViews(files);
        updateProgressBar(false, null);
    }


    /**
     * Gets list of {@linkplain ApplicationState} which Git Staging window listenins.
     *
     * @return a list of states
     */
    public List<ApplicationState> getStagingStates() {
        return _stagingStates;
    }

    public final EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        List<ApplicationState> activeAtates = _stateService.getActiveStates();
        if (!activeAtates.isEmpty() || activeAtates.contains(_stagingStates)) {
            event.consume();
            JavaFXUI.showWarningAlertForActiveStates(activeAtates);
            return;
        }
        isDisposed();
    };

    private void updateDisableButton() {
        BooleanBinding progressProperty = _progressLabel.textProperty().isNotEmpty();
        BooleanBinding property = Bindings.size(_stagedListView.getItems()).isEqualTo(0)
                .or(_commitText.textProperty().isEmpty())
                .or(progressProperty);
        _commitButton.disableProperty().bind(property);
        _commitPushButton.disableProperty().bind(property);
        _exitButton.disableProperty().bind(progressProperty);
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
        setupListView(_unstagedListView);
        setDragAndDropToUnstagedListView();
        _unstagedListView.addEventFilter(MouseEvent.MOUSE_CLICKED,
                event -> filterEvent(event, _unstagedListView, _stagedListView));

        setupListView(_stagedListView);
        setDragAndDropToStagedListView();
        _stagedListView.addEventFilter(MouseEvent.MOUSE_CLICKED,
                event -> filterEvent(event, _stagedListView, _unstagedListView));

        updateContendListViews(files);
    }

    private ContextMenu getContextMenu(List<ChangedFile> selectedItems) {

        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> menuItems = new ArrayList<>();

        addReplaceActionToMenu(menuItems, selectedItems);
        addDeleteActionToMenu(menuItems, selectedItems);

        contextMenu.getItems().addAll(menuItems);
        return contextMenu;
    }

    // Add replace button for all files
    private void addReplaceActionToMenu(List<MenuItem> menuItems, List<ChangedFile> selectedItems) {
        Collection<ChangedFile> filesForReplace = getFilesForReplace(selectedItems);
        if (!filesForReplace.isEmpty()) {
            MenuItem replaceWithHEAD = new MenuItem(REPLACE_BUTTON_NAME);
            replaceWithHEAD.setGraphic(replaceWithHEADImage);
            replaceWithHEAD.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    boolean isYes = requesConfirmationOperation(REPLACE_FILES_TITLE,
                            "You want to replace " + filesForReplace.size() + " file(s) with HEAD revision.",
                            CONFIRMATION_OPERATION);
                    if (isYes) {
                        _gitService.replaceWithHEADRevision(filesForReplace);
                        updateChangedFiles(getProjects(filesForReplace));
                    }
                }
            });
            menuItems.add(replaceWithHEAD);
        }
    }

    // Add delete button if user selected as least one untracked file from a ListView.
    private void addDeleteActionToMenu(List<MenuItem> menuItems, List<ChangedFile> selectedItems) {
        Collection<ChangedFile> selectedUntrackedFiles = getUntrackedFiles(selectedItems);
        if (!selectedUntrackedFiles.isEmpty()) {
            MenuItem delete = new MenuItem(DELETE_BUTTON_NAME);
            delete.setGraphic(deleteImage);
            delete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    boolean isYes = requesConfirmationOperation(DELETE_FILES_TITLE,
                            "You want to delete " + selectedUntrackedFiles.size() + " untracked file(s).",
                            CONFIRMATION_OPERATION);
                    if (isYes) {
                        selectedUntrackedFiles.forEach(file -> deleteChangedFile(file));
                        updateChangedFiles(getProjects(selectedUntrackedFiles));
                    }
                }
            });
            menuItems.add(delete);
        }
    }

    private boolean requesConfirmationOperation(String title, String header, String context) {
        GLTAlert alert = new GLTAlert(title, header, context);
        alert.clearDefaultButtons();
        alert.addButtons(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return !(result.orElse(ButtonType.NO) == ButtonType.NO);
    }

    private void deleteChangedFile(ChangedFile changedFile) {
        String path = changedFile.getProject().getPath() + File.separator + changedFile.getFileName();
        PathUtilities.deletePath(path);
    }

    private Collection<ChangedFile> getUntrackedFiles(Collection<ChangedFile> files) {
        return files.stream()
                    .filter(file -> file.getStatusFile() == ChangedFileStatus.UNTRACKED)
                    .collect(Collectors.toList());
    }

    // We can replace with HEAD revision all file except new or untracked files.
    private Collection<ChangedFile> getFilesForReplace(Collection<ChangedFile> files) {
        return files.stream()
                    .filter(file -> file.getStatusFile() != ChangedFileStatus.ADDED &&
                                    file.getStatusFile() != ChangedFileStatus.UNTRACKED)
                    .collect(Collectors.toList());
    }

    private void updateContendListViews(Collection<ChangedFile> files) {
        Collection<ChangedFile> unstagedFiles = new ArrayList<>();
        Collection<ChangedFile> stagedFiles = new ArrayList<>();
        fillLists(files, stagedFiles, unstagedFiles);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _unstagedListView.setItems(FXCollections.observableArrayList(unstagedFiles));
                _stagedListView.setItems(FXCollections.observableArrayList(stagedFiles));

                setContentAndComparatorToLists();
            }
        });
    }

    private void filterEvent(MouseEvent event, ListView<ChangedFile> setFocuseList, ListView<ChangedFile> resetSelectionList) {
        changeFocuseAndSelection(setFocuseList, resetSelectionList);

        Node node = event.getPickResult().getIntersectedNode();
        while (node != null && node != setFocuseList && !(node instanceof ListCell)) {
            node = node.getParent();
        }

        if (node instanceof ListCell) {
            @SuppressWarnings("unchecked")
            ListCell<ChangedFile> cell = (ListCell<ChangedFile>) node;
            ListView<ChangedFile> lv = cell.getListView();

            if (event.getButton() == MouseButton.SECONDARY) {
                if (!cell.isEmpty()) {
                    List<ChangedFile> selectedItems = lv.getSelectionModel().getSelectedItems();
                    lv.setContextMenu(getContextMenu(selectedItems));
                } else {
                    lv.setContextMenu(null);
                }
            }
        }
    }

    // Fix bug with double selection. We can have selected items only in one list.
    private void changeFocuseAndSelection(ListView<ChangedFile> setFocuseList, ListView<ChangedFile> resetSelectionList) {
        setFocuseList.requestFocus();
        resetSelectionList.getSelectionModel().clearSelection();
    }

    private void updateProgressBar(boolean isVisible, String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String message = isVisible ? text : StringUtils.EMPTY;
                _progressLabel.setText(message);
                _progressLabel.setVisible(isVisible);
                _progressIndicator.setVisible(isVisible);

                setDisableAllElements(isVisible);
            }
        });
    }

    private void setDisableAllElements(boolean isDisable) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _sortingListBox.setDisable(isDisable);
                _filterField.setDisable(isDisable);
                _unstagedListView.setDisable(isDisable);
                _stagedListView.setDisable(isDisable);
                _commitText.setDisable(isDisable);
            }
        });
    }

    @FXML
    public void onCommitAction(ActionEvent event) {
        commitChanges(getProjects(_stagedListView.getItems()), false);
    }

    @FXML
    public void onCommitPushAction(ActionEvent event) {
        commitChanges(getProjects(_stagedListView.getItems()), true);
    }

    private List<Project> getProjects(Collection<ChangedFile> files) {
        return files.stream()
                    .map(ChangedFile::getProject)
                    .collect(Collectors.toList());
    }

    private void commitChanges(List<Project> projects, boolean isPushChanges) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                _commitStatuses = _gitService.commitChanges(projects, _commitText.getText(), isPushChanges,
                        new CommitPushProgressListener(isPushChanges ? ApplicationState.PUSH : ApplicationState.COMMIT));
                _commitText.setText(StringUtils.EMPTY);
                updateChangedFiles(projects);
                showStatusDialog(isPushChanges, projects.size());
            }
        };

        _backgroundService.runInBackgroundThread(task);
    }

    private void updateChangedFiles(List<Project> projects) {
        _projectList.updateProjectStatuses(projects);
    }

    @FXML
    public void onChangeSortingType(ActionEvent event) {
        setContentAndComparatorToLists();
    }

    @FXML
    public void onExitAction(ActionEvent event) {
        List<ApplicationState> activeAtates = _stateService.getActiveStates();
        if (!activeAtates.isEmpty() || activeAtates.contains(_stagingStates)) {
            return;
        }
        isDisposed();
        Stage stage = (Stage) _exitButton.getScene().getWindow();
        stage.close();
    }

    // move from old CommitDialog class
    private void showStatusDialog(boolean isPushChanges, int countProjects) {
        Map<Project, JGitStatus> statuses = _commitStatuses;
        if (statuses == null || statuses.isEmpty()) {
            return;
        }
        String info = "Successfully: %s project(s)\nFailed: %s project(s)";
        String dialogTitle = isPushChanges ? STATUS_PUSH_DIALOG_TITLE : STATUS_COMMIT_DIALOG_TITLE;
        String dialogHeader = isPushChanges ? STATUS_PUSH_DIALOG_HEADER : STATUS_COMMIT_DIALOG_HEADER;

        long countSuccess = statuses.entrySet().stream()
                                               .map(Map.Entry::getValue)
                                               .filter(status -> status.equals(JGitStatus.SUCCESSFUL))
                                               .count();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StatusDialog statusDialog = new StatusDialog(dialogTitle, dialogHeader);
                statusDialog.showMessage(statuses, countProjects, info, String.valueOf(countSuccess),
                        String.valueOf(countProjects - countSuccess));
                statusDialog.showAndWait();
            }
        });
    }

    private void setupListView(ListView<ChangedFile> list) {
        list.setCellFactory(p -> new FilesListCell());
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        updateDisableButton();
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
        },

        FILE_STATUS {
            @Override
            public String toString() {
                return "file status";
            }
        };

        private static Comparator<ChangedFile> getComparatorByType(SortingType type) {
            if (type == SortingType.DEFAULT) {
                return new DefaultTypeComparator();
            } else if (type == SortingType.PROJECTS) {
                return new ProjectsTypeComparator();
            } else if (type == SortingType.FILE_STATUS) {
                return new ChangedFileStatusComparator();
            } else {
                return new ExtensionsTypeComparator();
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
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Map<Project, List<ChangedFile>> map = getMapFiles(unstagedFiles);
                List<ChangedFile> addedFiles = _gitService.addUntrackedFilesToIndex(map);
                updateChangedFiles(getProjects(addedFiles));
            }
        };
        _backgroundService.runInBackgroundThread(task);
    }

    private void onResetAction(List<ChangedFile> stagedFiles) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Map<Project, List<ChangedFile>> map = getMapFiles(stagedFiles);
                List<ChangedFile> resetFiles = _gitService.resetChangedFiles(map);
                updateChangedFiles(getProjects(resetFiles));
            }
        };
        _backgroundService.runInBackgroundThread(task);
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

    @SuppressWarnings("unchecked")
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

    @Override
    public void handleEvent(ApplicationState changedState, boolean isActivate) {
        if (changedState == ApplicationState.UPDATE_PROJECT_STATUSES && !isActivate) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Collection<ChangedFile> changedFiles = getChangedFilesSelectedProjects();
                    updateContendListViews(changedFiles);
                }
            });
        }
        String text = isActivate ? changedState.toString() : StringUtils.EMPTY;
        updateProgressBar(isActivate, text);
    }
}
