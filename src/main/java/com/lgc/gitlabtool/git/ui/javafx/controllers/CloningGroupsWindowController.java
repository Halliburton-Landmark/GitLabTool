package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.inject.internal.util.Objects;
import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.preferences.ApplicationPreferences;
import com.lgc.gitlabtool.git.preferences.PreferencesNodes;
import com.lgc.gitlabtool.git.services.GroupService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ProjectService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.javafx.listeners.OperationProgressListener;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.CloneProgressDialog;
import com.lgc.gitlabtool.git.util.PathUtilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("unchecked")
public class CloningGroupsWindowController {
    private static final String FOLDER_CHOOSER_DIALOG = "Destination folder";

    private final LoginService _loginService = ServiceProvider.getInstance().getService(LoginService.class);
    private final GroupService _groupsService = ServiceProvider.getInstance().getService(GroupService.class);
    private final ProjectService _projectService = ServiceProvider.getInstance().getService(ProjectService.class);
    private final ThemeService _themeService = ServiceProvider.getInstance().getService(ThemeService.class);

    @FXML
    private TextField folderPath;

    @FXML
    private ListView dataListView;

    @FXML
    private Button nextButton;

    @FXML
    private Button backButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button browseButton;

    @FXML
    private Label cloneLabel;

    @FXML
    private ToggleButton selectAllButton;

    private static final String PREF_NAME = "path_to_group";
    private static final String NEXT_BUTTON_TEXT = "Next";
    private static final String CLONE_BUTTON_TEXT = "Clone";
    private static final String GROUP_CLONE_LABEL = "Please select groups for cloning";
    private static final String PROJECTS_CLONE_LABEL = "Please select projects for cloning";
    private static final String SELECT_ALL_IMAGE_URL = "icons/select_all_20x20.png";

    private List<Group> _mainGroups;
    private Object _selectedGroup;
    private int _selectedGroupIndex = -1;

    private ApplicationPreferences getPrefs() {
        return ((ApplicationPreferences) ServiceProvider.getInstance()
                .getService(ApplicationPreferences.class)).node(PreferencesNodes.CLONING_GROUP_NODE);
    }

    @FXML
    public void initialize() {
        ImageView imageViewSelectAll = _themeService.getStyledImageView(SELECT_ALL_IMAGE_URL);
        selectAllButton.setTooltip(new Tooltip("Select all projects"));
        selectAllButton.setGraphic(imageViewSelectAll);

        Collection<Group> allGroups = _groupsService.getGroups(_loginService.getCurrentUser());
        _mainGroups = _groupsService.getOnlyMainGroups((List<Group>) allGroups);
        updatePanelForGroups();


        dataListView.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell<Object> call(ListView<Object> p) {

                return new ListCell<Object>() {
                    @Override
                    protected void updateItem(Object item, boolean bln) {
                        super.updateItem(item, bln);
                        setText(item == null ? StringUtils.EMPTY : getTextListItem(item));
                    }
                };
            }
        });
        dataListView.setOnMouseClicked((EventHandler<Event>) event -> filterForOkButton());
        folderPath.textProperty().addListener((observable, oldValue, newValue) -> filterForOkButton());
        setStyleAndDisableForIncorrectData();

        String propertyValue = getPrefs().get(PREF_NAME, StringUtils.EMPTY);
        if (propertyValue != null) {
            folderPath.setText(propertyValue);
        }
    }

    @FXML
    public void onBrowseButton() throws Exception {
        Stage stage = (Stage) browseButton.getScene().getWindow();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(FOLDER_CHOOSER_DIALOG);
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            String path = selectedDirectory.getCanonicalPath();
            getPrefs().put(PREF_NAME, path);
            folderPath.setText(path);
        }
    }

    @FXML
    public void onBackAction() {
        updatePanelForGroups();
    }

    @FXML
    public void onNextAction() {
        if (Objects.equal(cloneLabel.getText(), PROJECTS_CLONE_LABEL)) {
            ((Stage) nextButton.getScene().getWindow()).close(); // hide clone dialog
            List<Project> projects = dataListView.getSelectionModel().getSelectedItems();
            String path = folderPath.getText();
            Group group = (Group) _selectedGroup;

            CloneProgressDialog progressDialog = new CloneProgressDialog();
            OperationProgressListener progress = new OperationProgressListener(progressDialog, ApplicationState.CLONE);
            progressDialog.setStartAction(() -> _groupsService.cloneGroup(group, projects, path, progress));
            progressDialog.showDialog();
        } else {
            updatePanelForProjects();
        }
    }

    @FXML
    public void onCancelButton() throws Exception {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onSelectAllButton() {
        if (selectAllButton.isSelected()) {
            dataListView.getSelectionModel().selectAll();
        } else {
            dataListView.getSelectionModel().clearSelection();
        }
    }

    private void filterForOkButton() {
        if (isIncorrectPath()) {
            setStyleAndDisableForIncorrectData();
            return;
        } else if (dataListView.getSelectionModel().selectedItemProperty().isNull().get()) {
            folderPath.setStyle("-fx-border-color: green;");
            nextButton.setDisable(true);
        } else {
            folderPath.setStyle("-fx-border-color: green;");
            nextButton.setDisable(false);
        }
    }

    private boolean isIncorrectPath() {
        String text = folderPath.textProperty().get();
        if (text == null || text.isEmpty()) {
            return true;
        }
        Path path = Paths.get(text);
        return !PathUtilities.isExistsAndDirectory(path);
    }

    private void setStyleAndDisableForIncorrectData() {
        folderPath.setStyle("-fx-border-color: red;");
        nextButton.setDisable(true);
    }


    private void updatePanelForGroups() {
        updateSelectAllButton(false);
        updatePanelForNextStep(true, NEXT_BUTTON_TEXT, GROUP_CLONE_LABEL);
        configureGroupListView();
        filterForOkButton();
    }

    private void updatePanelForProjects() {
        updateSelectAllButton(true);
        updatePanelForNextStep(false, CLONE_BUTTON_TEXT, PROJECTS_CLONE_LABEL);
        setSelectedGroup(dataListView.getSelectionModel());
        configureProjectListView();
        filterForOkButton();
    }

    private void updatePanelForNextStep(boolean isBackDisable, String nextButtonText, String cloneLabelText) {
        backButton.setDisable(isBackDisable);
        nextButton.setText(nextButtonText);
        cloneLabel.setText(cloneLabelText);
    }

    private void clearDataListView() {
        dataListView.getSelectionModel().clearSelection();
        dataListView.setItems(FXCollections.observableList(new ArrayList<>()));
        dataListView.refresh();
    }

    private void updateSelectAllButton(boolean isVisible) {
        selectAllButton.setVisible(isVisible);
        selectAllButton.setSelected(false);
    }

    private void configureGroupListView() {
        clearDataListView();
        dataListView.setItems(FXCollections.observableList(new ArrayList<>(_mainGroups)));
        dataListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (_selectedGroupIndex != -1) {
            dataListView.getSelectionModel().select(_selectedGroupIndex);
        }
    }

    private void configureProjectListView() {
        clearDataListView();
        Collection<Project> loadedProjects = _projectService.getProjects((Group) _selectedGroup);
        ObservableList<Project> observableList = FXCollections.observableList((List<Project>) loadedProjects);
        SortedList<Project> sortedList = new SortedList<>(observableList);
        sortedList.setComparator(new Comparator<Project>(){
            @Override
            public int compare(Project arg0, Project arg1) {
                return arg0.getName().compareToIgnoreCase(arg1.getName());
            }
        });
        dataListView.setItems(sortedList);
        dataListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setSelectedGroup(MultipleSelectionModel<Object> selectionModel) {
        if (selectionModel != null) {
            _selectedGroup = selectionModel.getSelectedItem();
            _selectedGroupIndex = selectionModel.getSelectedIndex();
        }
    }

    private String getTextListItem(Object item) {
        String itemText;
        if (item instanceof Group) {
            Group group = (Group) item;
            itemText = group.getName() + " (@" + group.getFullPath() + ")";
        } else {
            itemText = ((Project) item).getName();
        }
        return itemText;
    }
}
