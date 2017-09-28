package com.lgc.gitlabtool.git.ui.javafx.controllers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectList;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.listeners.stateListeners.StateListener;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.icon.LocalRemoteIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.ChangesCheckDialog;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

@SuppressWarnings("unchecked")
public class SwitchBranchWindowController implements StateListener {

    private final String ID = SwitchBranchWindowController.class.getName();

    private static final String TOTAL_CAPTION = "Total count: ";
    private static final String SWITCHTO_STATUS_ALERT_TITLE = "Switch branch info";
    private static final String SWITCHTO_STATUS_ALERT_HEADER = "Switch branch statuses:";

    private static final GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

    private List<Branch> _allBranches = new ArrayList<>();

    private List<Integer> _selectedProjectsIds = new ArrayList<>();
    private ProjectList _projectList;

    @FXML
    private ListView currentProjectsListView;

    @FXML
    private Label projectsCountLabel;

    @FXML
    private ToggleGroup branchesFilter;

    @FXML
    private ListView branchesListView;

    @FXML
    private CheckBox commonMatchingCheckBox;

    @FXML
    private TextField searchField;

    @FXML
    private Label branchesCountLabel;

    @FXML
    private Button switchButton;

    private static final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    {
        _stateService.addStateListener(ApplicationState.REFRESH_PROJECTS, this);
    }

    @FXML
    public void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterPlantList(oldValue, newValue));

        BooleanBinding branchListBooleanBinding = branchesListView.getSelectionModel().selectedItemProperty().isNull();
        switchButton.disableProperty().bind(branchListBooleanBinding);
    }

    public void beforeShowing(List<Project> projects) {
        _projectList = ProjectList.get(null);
        _selectedProjectsIds = ProjectList.getIdsProjects(projects);
        setProjectListItems(getProjectsByIds(), currentProjectsListView);

        configureProjectsListView(currentProjectsListView);
        configureBranchesListView(branchesListView);

        onUpdateList();
    }

    /*
    Buttons
    */
    public void onSwitchButton() {
        List<Project> selectedProjects = currentProjectsListView.getItems();
        Branch selectedBranch = (Branch) branchesListView.getSelectionModel().getSelectedItem();

        List<Project> changedProjects = _gitService.getProjectsWithChanges(selectedProjects);

        if (changedProjects.isEmpty()) {
            switchBranch(selectedProjects, selectedBranch);
        } else {
            launchSwitchBranchConfirmation(changedProjects, selectedProjects, selectedBranch);
        }
    }

    private List<Project> getProjectsByIds() {
        return _projectList.getProjectsByIds(_selectedProjectsIds);
    }

    private void switchBranch(List<Project> selectedProjects, Object selectedBranch) {
        Map<Project, JGitStatus> switchStatuses = _gitService.switchTo(selectedProjects, (Branch) selectedBranch);

        String dialogMessage = "%s projects were switched successfully";

        switchToStatusDialog(switchStatuses, selectedProjects.size(), dialogMessage);
        currentProjectsListView.refresh();
    }

    private void launchSwitchBranchConfirmation(List<Project> changedProjects,
                                                List<Project> selectedProjects, Branch selectedBranch) {

        ChangesCheckDialog alert = new ChangesCheckDialog();
        alert.launchConfirmationDialog(changedProjects, selectedProjects, selectedBranch, this::switchBranch);
    }

    public void onClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void onUpdateList() {
        RadioButton selecteRB = (RadioButton) branchesFilter.getSelectedToggle();
        String branchTypeText = selecteRB.getText();

        Boolean isCommonMatching = commonMatchingCheckBox.isSelected();

        BranchType branchType;
        switch (branchTypeText) {
            case "Remote":
                branchType = BranchType.REMOTE;
                break;
            case "Local":
                branchType = BranchType.LOCAL;
                break;
            case "Remote + Local":
                branchType = BranchType.ALL;
                break;
            default:
                branchType = BranchType.LOCAL;
        }

        _allBranches = getBranches(getProjectsByIds(), branchType, isCommonMatching);
        branchesListView.getSelectionModel().clearSelection();
        branchesListView.setItems(FXCollections.observableArrayList(_allBranches));

        searchField.setText(StringUtils.EMPTY);
        currentProjectsListView.setItems(FXCollections.observableArrayList(getProjectsByIds()));
    }

    private void filterPlantList(String oldValue, String newValue) {

        List<Branch> filteredBranchList = new ArrayList<>();

        if (searchField == null || searchField.getText().equals(StringUtils.EMPTY)) {
            branchesListView.setItems(FXCollections.observableArrayList(_allBranches));
            currentProjectsListView.setItems(FXCollections.observableArrayList(getProjectsByIds()));
        } else {
            //filtering branches
            newValue = newValue.toUpperCase();
            for (Object branch : _allBranches) {
                String filterText = ((Branch) branch).getBranchName();
                if (filterText.toUpperCase().contains(newValue)) {
                    filteredBranchList.add((Branch) branch);
                }
            }
            branchesListView.getItems().clear();
            branchesListView.setItems(FXCollections.observableArrayList(filteredBranchList));

            filteringProjectsListView(filteredBranchList);

        }
    }

    private void filteringProjectsListView(List<Branch> branches) {
        List<Project> filteredProjectList = new ArrayList<>();

        //filtering projects
        for (Object project : getProjectsByIds()) {
            if (_gitService.containsBranches((Project) project, branches, false)) {
                filteredProjectList.add((Project) project);
            }
        }
        currentProjectsListView.getItems().clear();
        currentProjectsListView.setItems(FXCollections.observableArrayList(filteredProjectList));
    }

    private List<Branch> getBranches(List<Project> selectedProjects, BranchType branchType, Boolean isCommonMatching) {
        Set<Branch> allBranchesWithTypes = JGit.getInstance().getBranches(selectedProjects,
                branchType, isCommonMatching);

        List<Branch> list = new ArrayList(allBranchesWithTypes);
        Collections.sort(list, (o1, o2) -> {

            String type1 = o1.getBranchType().name();
            String type2 = o2.getBranchType().name();
            int sComp = type1.compareTo(type2);

            if (sComp != 0) {
                return sComp;
            } else {
                String name1 = o1.getBranchName();
                String name2 = o2.getBranchName();
                return name1.compareTo(name2);
            }
        });

        return list;
    }

    private void setProjectListItems(List items, ListView<Project> listView) {
        if (items == null || items.isEmpty()) {
            return;
        }

        if (items.get(0) instanceof Project) {
            listView.setItems(FXCollections.observableArrayList(items));
        }
    }

    private void configureProjectsListView(ListView listView) {
        //config displayable string
        listView.setCellFactory(p -> new ProjectListCell());

        listView.itemsProperty().addListener((observable, oldValue, newValue) ->
                projectsCountLabel.textProperty().bind(Bindings.concat(TOTAL_CAPTION,
                        Bindings.size((listView.getItems())).asString())));

        //disabling selection
        listView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldvalue, newValue) -> Platform.runLater(() -> listView.getSelectionModel().select(-1)));
    }

    private void configureBranchesListView(ListView listView) {
        //config displayable string with icon
        listView.setCellFactory(p -> new BranchListCell());


        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ArrayList<Branch> selectedValue = new ArrayList<>();
                selectedValue.add((Branch) newValue);
                filteringProjectsListView(selectedValue);
            }
        });

        listView.itemsProperty().addListener((observable, oldValue, newValue) ->
                branchesCountLabel.textProperty().bind(Bindings.concat(TOTAL_CAPTION,
                        Bindings.size((listView.getItems())).asString())));

    }

    private void switchToStatusDialog(Map<Project, JGitStatus> statuses, int countOfProjects, String content) {
        StatusDialog statusDialog = new StatusDialog(SWITCHTO_STATUS_ALERT_TITLE, SWITCHTO_STATUS_ALERT_HEADER);
        statusDialog.showMessage(statuses, countOfProjects, content);
        statusDialog.showAndWait();
    }

    private class BranchListCell extends ListCell<Branch> {

        @Override
        protected void updateItem(Branch item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                Image fxImage = getBranchIcon(item);
                ImageView imageView = new ImageView(fxImage);

                setGraphic(imageView);
                setText(item.getBranchName());
            }
        }

        private Image getBranchIcon(Branch item) {

            BranchType type = item.getBranchType();
            Image branchIcon;
            if (type == BranchType.LOCAL) {
                branchIcon = LocalRemoteIconHolder.getInstance().getLocalBranchIcoImage();
            } else {
                branchIcon = LocalRemoteIconHolder.getInstance().getRemoteBranchIcoImage();
            }
            return branchIcon;
        }
    }

    @Override
    public void handleEvent(ApplicationState changedState, boolean isActivate) {
        if (!isActivate) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String textSearch = searchField.getText();
                        Branch branch = (Branch) branchesListView.getSelectionModel().getSelectedItem();
                        if (textSearch != null && !textSearch.isEmpty() && branch == null) {
                            filterPlantList(null, textSearch);
                        } else if (branch != null) {
                            filteringProjectsListView(Arrays.asList(branch));
                        } else {
                            currentProjectsListView.setItems(FXCollections.observableArrayList(getProjectsByIds()));
                        }
                    }
                });
            });
            executor.shutdown();
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ID == null) ? 0 : ID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SwitchBranchWindowController other = (SwitchBranchWindowController) obj;
        if (ID == null) {
            if (other.ID != null) {
                return false;
            }
        } else if (!ID.equals(other.ID)) {
            return false;
        }
        return true;
    }

}
