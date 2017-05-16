package com.lgc.solutiontool.git.ui.javafx.controllers;


import com.lgc.solutiontool.git.entities.Branch;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.jgit.BranchType;
import com.lgc.solutiontool.git.jgit.JGit;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;
import com.lgc.solutiontool.git.services.ProjectTypeService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.ui.icon.LocalRemoteIconHolder;
import com.lgc.solutiontool.git.ui.icon.ProjectNatureIconHolder;
import com.lgc.solutiontool.git.ui.selection.SelectionsProvider;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SwitchBranchWindowController {
    private static final String TOTAL_CAPTION = "Total count: ";
    private static final String SELECTED_CAPTION = "Selected count: ";

    private ProjectTypeService _projectTypeService =
            (ProjectTypeService) ServiceProvider.getInstance().getService(ProjectTypeService.class.getName());

    private List<Branch> currentBranches = new ArrayList<>();

    @FXML
    private ListView currentProjectsListView;

    @FXML
    private Label allProjectsCount;

    @FXML
    private ToggleGroup branchesFilter;

    @FXML
    private ListView branchesListView;

    @FXML
    private CheckBox commonMatchingCheckBox;

    @FXML
    private TextField searchField;

    @FXML
    private Label branchesCount;

    @FXML
    private Label selectedProjectsCount;

    @FXML
    public void initialize() {
        configureProjectsListView(currentProjectsListView);
        configureBranchesListView(branchesListView);

        List<?> selectedProjects = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
        setProjectListItems(selectedProjects, currentProjectsListView);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterPlantList(oldValue, newValue));

        selectedProjectsCount.textProperty().bind(Bindings.concat(SELECTED_CAPTION,
                Bindings.size((currentProjectsListView.getSelectionModel().getSelectedItems())).asString()));

        allProjectsCount.textProperty().bind(Bindings.concat(TOTAL_CAPTION,
                Bindings.size((currentProjectsListView.getItems())).asString()));
    }

    /*
    Buttons
    */
    public void onApplyButton() {
        List<Project> selectedProjects = currentProjectsListView.getSelectionModel().getSelectedItems();
        Branch selectedBranch = (Branch) branchesListView.getSelectionModel().getSelectedItem();
        String selectedBranchName = selectedBranch.getBranchName();
        boolean isRemote = selectedBranch.getBranchType().equals(BranchType.REMOTE);

        for (Project project : selectedProjects) {
            JGit.getInstance().switchTo(project, selectedBranchName, isRemote);
        }

        onUpdateList();
    }

    public void onCancel(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void onUpdateList() {
        List<Project> selectedProjects = currentProjectsListView.getSelectionModel().getSelectedItems();

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

        currentBranches = getBranches(selectedProjects, branchType, isCommonMatching);
        branchesListView.setItems(FXCollections.observableArrayList(currentBranches));
        branchesCount.textProperty().bind(Bindings.concat(TOTAL_CAPTION,
                Bindings.size((branchesListView.getItems())).asString()));
    }

    private void filterPlantList(String oldValue, String newValue) {

        ObservableList<Branch> filteredList = FXCollections.observableArrayList();
        if (searchField == null || newValue.length() < oldValue.length()) {
            branchesListView.setItems(FXCollections.observableArrayList(currentBranches));
        } else {
            newValue = newValue.toUpperCase();
            for (Object branch : branchesListView.getItems()) {
                String filterText = ((Branch) branch).getBranchName();
                if (filterText.toUpperCase().contains(newValue)) {
                    filteredList.add((Branch) branch);
                }
            }
            branchesListView.getItems().clear();
            branchesListView.setItems(filteredList);
        }
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

        //setup selection
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //setup selection
        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();
            while (node != null && node != listView && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            if (node instanceof ListCell) {
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                lv.requestFocus();

                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });

    }

    private void configureBranchesListView(ListView listView) {
        //config displayable string with icon
        listView.setCellFactory(p -> new BranchListCell());
    }

    private class ProjectListCell extends ListCell<Project> {
        private static final String DS_PROJECT_TYPE = "com.lgc.dsg";

        @Override
        protected void updateItem(Project item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                Image fxImage = getProjectIcon(item);
                ImageView imageView = new ImageView(fxImage);

                setGraphic(imageView);
                setText(item.getName());
            }
        }

        private Image getProjectIcon(Project item) {
            ProjectType type = _projectTypeService.getProjectType(item);
            Image projectIcon = ProjectNatureIconHolder.getInstance().getUnknownProjectIcoImage();

            if (type.getId().equals(DS_PROJECT_TYPE)) {
                projectIcon = ProjectNatureIconHolder.getInstance().getDsProjectIcoImage();
            }
            return projectIcon;
        }
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
