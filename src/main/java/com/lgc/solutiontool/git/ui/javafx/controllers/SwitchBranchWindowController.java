package com.lgc.solutiontool.git.ui.javafx.controllers;


import com.lgc.solutiontool.git.entities.Branch;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.jgit.BranchType;
import com.lgc.solutiontool.git.jgit.JGit;
import com.lgc.solutiontool.git.jgit.JGitStatus;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;
import com.lgc.solutiontool.git.ui.icon.AppIconHolder;
import com.lgc.solutiontool.git.ui.icon.LocalRemoteIconHolder;
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
    private static final String SELECTED_CAPTION = "Selected count: ";
    private static final String SWITCHTO_STATUS_ALERT_TITLE = "Switch branch info";
    private static final String SWITCHTO_STATUS_ALERT_HEADER = "Switch branch statuses:";
    private static final String NEW_LINE_SYMBOL = "\n";

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

        Map<Project, JGitStatus> switchStatuses = new HashMap<>();
        for (Project project : selectedProjects) {
            JGitStatus status = JGit.getInstance().switchTo(project, selectedBranchName, isRemote);
            switchStatuses.put(project, status);
        }
        String dialogMessage = switchStatuses.entrySet().stream()
                .map(x -> x.getKey().getName() + "  -  " + x.getValue())
                .collect(Collectors.joining(NEW_LINE_SYMBOL));
        switchToStatusDialog(dialogMessage);

        currentProjectsListView.refresh();
        onUpdateList();
    }

    public void onClose(ActionEvent actionEvent) {
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
