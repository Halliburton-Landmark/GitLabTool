package com.lgc.gitlabtool.git.ui.javafx.controllers;


import com.lgc.gitlabtool.git.entities.Branch;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.BranchType;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.project.nature.projecttype.ProjectType;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.icon.LocalRemoteIconHolder;
import com.lgc.gitlabtool.git.ui.selection.SelectionsProvider;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class SwitchBranchWindowController {
    private static final String TOTAL_CAPTION = "Total count: ";
    private static final String SWITCHTO_STATUS_ALERT_TITLE = "Switch branch info";
    private static final String SWITCHTO_STATUS_ALERT_HEADER = "Switch branch statuses:";
    private static final String NEW_LINE_SYMBOL = "\n";

    private GitService _gitService =
            (GitService) ServiceProvider.getInstance().getService(GitService.class.getName());

    private List<Branch> allBranches = new ArrayList<>();

    private List<Project> allSelectedProjects = new ArrayList<>();

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

        allSelectedProjects = SelectionsProvider.getInstance().getSelectionItems("mainWindow_projectsList");
        setProjectListItems(allSelectedProjects, currentProjectsListView);

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

        allBranches = getBranches(allSelectedProjects, branchType, isCommonMatching);
        branchesListView.getSelectionModel().clearSelection();
        branchesListView.setItems(FXCollections.observableArrayList(allBranches));

        searchField.setText(StringUtils.EMPTY);
        currentProjectsListView.setItems(FXCollections.observableArrayList(allSelectedProjects));
    }

    private void filterPlantList(String oldValue, String newValue) {

        List<Branch> filteredBranchList = new ArrayList<>();

        if (searchField == null || searchField.getText().equals(StringUtils.EMPTY)) {
            branchesListView.setItems(FXCollections.observableArrayList(allBranches));
            currentProjectsListView.setItems(FXCollections.observableArrayList(allSelectedProjects));
        } else {
            //filtering branches
            newValue = newValue.toUpperCase();
            for (Object branch : allBranches) {
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
        for (Object project : allSelectedProjects) {
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

        alert.showAndWait();
    }

    private class ProjectListCell extends ListCell<Project> {
        private final Integer LIST_CELL_SPACING = 5;
        private final String LEFT_BRACKET = "[";
        private final String RIGHT_BRACKET = "]";

        @Override
        protected void updateItem(Project item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                ProjectType type = item.getProjectType();

                Image fxImage = new Image(getClass().getClassLoader().getResource(type.getIconUrl()).toExternalForm());
                ImageView imageView = new ImageView(fxImage);

                Optional<String> currentBranchName = JGit.getInstance().getCurrentBranch(item);
                String currentBranch = currentBranchName.orElse(StringUtils.EMPTY);

                Text branchNameTextView = new Text(item.getName());
                Text currentBranchTextView = new Text(LEFT_BRACKET + currentBranch + RIGHT_BRACKET);
                currentBranchTextView.setFill(Color.DARKBLUE);

                HBox hBoxItem = new HBox(imageView, branchNameTextView, currentBranchTextView);
                hBoxItem.setSpacing(LIST_CELL_SPACING);

                String tooltipText = item.getName() + " " + LEFT_BRACKET + currentBranch + RIGHT_BRACKET;
                setTooltip(new Tooltip(tooltipText));
                setGraphic(hBoxItem);
            }
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
