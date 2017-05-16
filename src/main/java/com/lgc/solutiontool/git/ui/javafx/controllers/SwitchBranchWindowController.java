package com.lgc.solutiontool.git.ui.javafx.controllers;


import com.lgc.solutiontool.git.entities.Branch;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.jgit.BranchType;
import com.lgc.solutiontool.git.jgit.JGit;
import com.lgc.solutiontool.git.ui.icon.LocalRemoteIconHolder;
import com.lgc.solutiontool.git.ui.selection.SelectionsProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SwitchBranchWindowController {

    @FXML
    public ListView currentProjectsListView;

    @FXML
    public Label projectsCount;

    @FXML
    public ToggleGroup branchesFilter;

    @FXML
    public ListView branchesListView;

    @FXML
    public CheckBox commonMatchingCheckBox;

    @FXML
    public TextField searchField;

    @FXML
    public Label branchesCount;

    private List<Branch> currentBranches = new ArrayList<>();
    private static final String TOTAL_CAPTION = "Total count: ";

    @FXML
    public void initialize() {
        configureProjectsListView(currentProjectsListView);
        configureBranchesListView(branchesListView);

        List<?> selectedProjects = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
        setProjectListItems(selectedProjects, currentProjectsListView);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterPlantList(oldValue, newValue));

        projectsCount.textProperty().bind(Bindings.concat(TOTAL_CAPTION,
                Bindings.size((currentProjectsListView.getItems())).asString()));
    }

    private void filterPlantList(String oldValue, String newValue) {

        ObservableList<Branch> filteredList = FXCollections.observableArrayList();
        if (searchField == null || newValue.length() < oldValue.length()) {
            branchesListView.setItems(FXCollections.observableArrayList(currentBranches));
        } else {
            newValue = newValue.toUpperCase();
            for (Object plants : branchesListView.getItems()) {
                String filterText = ((Branch) plants).getBranchName();
                if (filterText.toUpperCase().contains(newValue)) {
                    filteredList.add((Branch) plants);
                }
            }
            branchesListView.setItems(filteredList);
        }
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

    private List<Branch> getBranches(List<Project> selectedProjects, BranchType branchType, Boolean isCommonMatching) {
        Set<Branch> allBranchesWithTypes = JGit.getInstance().getBranches(selectedProjects,
                branchType, isCommonMatching);

        List<Branch> list = new ArrayList(allBranchesWithTypes);
        list.sort(Comparator.comparing(Branch::getBranchName));
        return list;
    }


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
        listView.setCellFactory(p -> new GroupListCell());

        //setup selection
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void configureBranchesListView(ListView listView) {
        //config displayable string with icon
        listView.setCellFactory(new Callback<ListView<Branch>, ListCell<Branch>>() {
            @Override
            public ListCell<Branch> call(ListView<Branch> p) {

                return new ListCell<Branch>() {
                    @Override
                    protected void updateItem(Branch item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Image fxImage = getBranchIcon(item);
                            ImageView imageView = new ImageView(fxImage);
                            setGraphic(imageView);
                            setText(item.getBranchName());
                        }
                    }
                };
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
        });
    }

    private class GroupListCell extends ListCell<Project> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("x");

        GroupListCell() {
            super();

            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(event -> getListView().getItems().remove(getItem()));
        }

        @Override
        protected void updateItem(Project item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                label.setText(item.getName());
                setGraphic(hbox);
            }
        }
    }
}
