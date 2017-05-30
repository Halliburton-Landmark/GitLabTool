package com.lgc.solutiontool.git.ui.javafx.controllers;


import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import com.lgc.solutiontool.git.entities.Group;
import com.lgc.solutiontool.git.entities.Project;
import com.lgc.solutiontool.git.jgit.JGit;
import com.lgc.solutiontool.git.project.nature.projecttype.ProjectType;
import com.lgc.solutiontool.git.services.LoginService;
import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.ui.mainmenu.MainMenuItems;
import com.lgc.solutiontool.git.ui.mainmenu.MainMenuManager;
import com.lgc.solutiontool.git.ui.selection.ListViewKey;
import com.lgc.solutiontool.git.ui.selection.SelectionsProvider;
import com.lgc.solutiontool.git.ui.toolbar.ToolbarManager;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainWindowController {
    private static final String HEDER_GROUP_TITLE = "Current group: ";

    private Group _selectedGroup;

    private final LoginService _loginService =
            (LoginService) ServiceProvider.getInstance().getService(LoginService.class.getName());

    @FXML
    private ListView projectsList;

    @FXML
    private Label leftLabel;

    @FXML
    private Label userId;

    public void beforeShowing() {
        String username = _loginService.getCurrentUser().getName();
        userId.setText(username);

        String currentGroupname = getSelectedGroup().getName();
        leftLabel.setText(HEDER_GROUP_TITLE + currentGroupname);

        configureListView(projectsList);

        BooleanBinding booleanBinding = projectsList.getSelectionModel().selectedItemProperty().isNull();
        ToolbarManager.getInstance().getAllButtonsForCurrentView()
                .forEach(x -> x.disableProperty().bind(booleanBinding));

        MainMenuManager.getInstance().getButtonById(MainMenuItems.MAIN_SWITCH_BRANCH).disableProperty()
                .bind(booleanBinding);

        //TODO: Additional thread should be placed to services
        new Thread(this::updateProjectList).start();

        configureToolbarCommands();
    }

    public Group getSelectedGroup() {
        return _selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        this._selectedGroup = selectedGroup;
    }

    public void refreshProjectsList(){
        projectsList.refresh();
    }

    private void configureToolbarCommands() {
    }

    private void updateProjectList() {
        List<Project> groupProjects = (List<Project>) _selectedGroup.getProjects();
        ObservableList<Project> projectsObservableList = FXCollections.observableList(groupProjects);
        projectsList.setItems(projectsObservableList);
    }

    private void configureListView(ListView listView) {
        //config displayable string
        listView.setCellFactory(p -> new MainWindowController.ProjectListCell());

        //setup selection
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

        listView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                SelectionsProvider.getInstance().setSelectionItems(ListViewKey.MAIN_WINDOW_PROJECTS.getKey(),
                        listView.getSelectionModel().getSelectedItems());
            }
        });
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
}
