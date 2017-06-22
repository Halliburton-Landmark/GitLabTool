package com.lgc.gitlabtool.git.ui.javafx.controllers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.icon.LocalRemoteIconHolder;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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
public class SwitchBranchWindowController {
    private static final String TOTAL_CAPTION = "Total count: ";
    private static final String SWITCHTO_STATUS_ALERT_TITLE = "Switch branch info";
    private static final String SWITCHTO_STATUS_ALERT_HEADER = "Switch branch statuses:";
    private static final String NEW_LINE_SYMBOL = "\n";

    private final GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

    private List<Branch> _allBranches = new ArrayList<>();

    private List<Project> _selectedClonedProjects = new ArrayList<>();

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

    @FXML
    public void initialize() {
        configureProjectsListView(currentProjectsListView);
        configureBranchesListView(branchesListView);

        List<Project> selectedProjects = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
        _selectedClonedProjects = selectedProjects.stream()
                                                  .filter((item)-> item.isCloned())
                                                  .collect(Collectors.toList());
        setProjectListItems(_selectedClonedProjects, currentProjectsListView);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterPlantList(oldValue, newValue));

        BooleanBinding branchListBooleanBinding = branchesListView.getSelectionModel().selectedItemProperty().isNull();
        switchButton.disableProperty().bind(branchListBooleanBinding);

        onUpdateList();
    }

    /*
    Buttons
    */
    public void onSwitchButton() {
        List<Project> selectedProjects = currentProjectsListView.getItems();
        Branch selectedBranch = (Branch) branchesListView.getSelectionModel().getSelectedItem();

        Map<Project, JGitStatus> switchStatuses = _gitService.switchTo(selectedProjects, selectedBranch);

        String dialogMessage = switchStatuses.entrySet().stream()
                .map(x -> x.getKey().getName() + "  -  " + x.getValue())
                .collect(Collectors.joining(NEW_LINE_SYMBOL));
        switchToStatusDialog(dialogMessage);

        currentProjectsListView.refresh();
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

        _allBranches = getBranches(_selectedClonedProjects, branchType, isCommonMatching);
        branchesListView.getSelectionModel().clearSelection();
        branchesListView.setItems(FXCollections.observableArrayList(_allBranches));

        searchField.setText(StringUtils.EMPTY);
        currentProjectsListView.setItems(FXCollections.observableArrayList(_selectedClonedProjects));
    }

    private void filterPlantList(String oldValue, String newValue) {

        List<Branch> filteredBranchList = new ArrayList<>();

        if (searchField == null || searchField.getText().equals(StringUtils.EMPTY)) {
            branchesListView.setItems(FXCollections.observableArrayList(_allBranches));
            currentProjectsListView.setItems(FXCollections.observableArrayList(_selectedClonedProjects));
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
        for (Object project : _selectedClonedProjects) {
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

    private void switchToStatusDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(SWITCHTO_STATUS_ALERT_TITLE);
        alert.setHeaderText(SWITCHTO_STATUS_ALERT_HEADER);
        alert.setContentText(content);

        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(appIcon);

        /* Set sizing and position */
        double dialogWidth = 300;
        double dialogHeight = 150;

        ScreenUtil.adaptForMultiScreens(stage, dialogWidth, dialogHeight);

        alert.showAndWait();
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
}
