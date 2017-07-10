package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.Group;
import com.lgc.gitlabtool.git.services.ClonedGroupsService;
import com.lgc.gitlabtool.git.services.LoginService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuItems;
import com.lgc.gitlabtool.git.ui.mainmenu.MainMenuManager;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * @author Yevhen Strazhko
 */
public class GroupWindowController {
    private static final Logger _logger = LogManager.getLogger(GroupWindowController.class);

    private static final String CLONE_WINDOW_TITLE = "Cloning window";
    private static final String FAILED_HEADER_MESSAGE_LOAD_GROUP = "Failed loading cloned groups. ";
    private static final String FAILED_CONTENT_MESSAGE_LOAD_GROUP
                                        = "These groups may have been moved to another folder or deleted from disc: ";

    @FXML
    private Label userId;

    @FXML
    private ListView<Group> groupList;

    private final LoginService _loginService = (LoginService) ServiceProvider.getInstance()
            .getService(LoginService.class.getName());

    private final ClonedGroupsService _clonedGroupsService = (ClonedGroupsService) ServiceProvider.getInstance()
            .getService(ClonedGroupsService.class.getName());

    @FXML
    public void initialize() {
        configureListView(groupList);
        new Thread(this::updateClonedGroups).start();

        BooleanBinding groupListBooleanBinding = groupList.getSelectionModel().selectedItemProperty().isNull();
        configureToolbarEnablers(groupListBooleanBinding);

        userId.setText(_loginService.getCurrentUser().getName());

//        ProjectService service = (ProjectService) ServiceProvider.getInstance()
//                .getService(ProjectService.class.getName());
//
//        Group group = new Group();
//        group.setId(1467925);
//        group.setName("apitest_group");
//
//        service.createProjectInGitLab(group, "test_ate", "1467925");

        configureToolbarCommands();
        configureMainMenuCommands();
    }

    @FXML
    public void onCloneGroups(ActionEvent actionEvent) {
        URL cloningGroupsWindowUrl = getClass().getClassLoader().getResource(ViewKey.CLONING_GROUPS_WINDOW.getPath());
        Image appIcon = AppIconHolder.getInstance().getAppIcoImage();

        if (cloningGroupsWindowUrl == null) {
            return;
        }

        try {
            Parent root = FXMLLoader.load(cloningGroupsWindowUrl);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle(CLONE_WINDOW_TITLE);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.setOnHidden(we -> updateClonedGroups());
            stage.getIcons().add(appIcon);

             /* Set sizing and position */
            double dialogWidth = 500;
            double dialogHeight = 400;

            ScreenUtil.adaptForMultiScreens(stage, dialogWidth, dialogHeight);

            stage.setHeight(dialogHeight);
            stage.setWidth(dialogWidth);

            stage.show();
        } catch (IOException e) {
            _logger.error("Could not load fxml resource: " + e.getMessage());
        }
    }

    public Group getSelectedGroup() {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            return selectedGroup;
        } else {
            return new Group();
        }
    }

    private void configureToolbarEnablers(BooleanBinding booleanBinding) {
        ToolbarManager.getInstance().getAllButtonsForCurrentView().stream()
                .filter(x -> x.getId().equals(ToolbarButtons.REMOVE_GROUP_BUTTON.getId())
                        || x.getId().equals(ToolbarButtons.SELECT_GROUP_BUTTON.getId()))
                .forEach(x -> x.disableProperty().bind(booleanBinding));
    }

    private void configureToolbarCommands() {
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.SELECT_GROUP_BUTTON.getId())
                .setOnAction(this::onLoadSelectedGroupspace);
        ToolbarManager.getInstance().getButtonById(ToolbarButtons.CLONE_GROUP_BUTTON.getId())
                .setOnAction(this::onCloneGroups);
    }

    private void configureMainMenuCommands(){
        MenuItem cloneGroup = MainMenuManager.getInstance().getButtonById(MainMenuItems.GROUP_WINDOW_CLONE_GROUP);
        cloneGroup.setOnAction(this::onCloneGroups);
    }

    private void updateClonedGroups() {
        List<Group> userGroups = _clonedGroupsService.loadClonedGroups();
        if (userGroups != null) {
            groupList.setItems(FXCollections.observableList(userGroups));

            showNotExistGroups(_clonedGroupsService.getNotExistGroup());
        }
    }

    private void showNotExistGroups(Collection<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return;
        }
        StringBuffer infoAboutGroups = new StringBuffer();
        groups.forEach(group -> infoAboutGroups.append(group.getName() + " (" + group.getPathToClonedGroup() + ");"));
        _logger.warn(FAILED_HEADER_MESSAGE_LOAD_GROUP + FAILED_CONTENT_MESSAGE_LOAD_GROUP + infoAboutGroups);

        String namesAndPathsGroups = infoAboutGroups.toString().replace(";", ";\n\r");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert dialog = new StatusDialog(FAILED_HEADER_MESSAGE_LOAD_GROUP, FAILED_CONTENT_MESSAGE_LOAD_GROUP, namesAndPathsGroups);
                dialog.showAndWait();
            }
        });
    }

    private void configureListView(ListView<Group> listView) {
        // config displayable string
        listView.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
            @Override
            public ListCell<Group> call(ListView<Group> p) {
                return new GroupListCell();
            }
        });
    }

    @FXML
    public void onLoadSelectedGroupspace(ActionEvent actionEvent) {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        loadGroup(selectedGroup);
    }

    private void loadGroup(Group group) {
        URL modularWindow = getClass().getClassLoader().getResource(ViewKey.MODULAR_CONTAINER.getPath());
        if (modularWindow == null) {
            _logger.error("Could not load fxml resource");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(modularWindow);
            Parent root = fxmlLoader.load();

            ModularController myControllerHandle = fxmlLoader.getController();
            myControllerHandle.loadMainWindow(group);

            Stage previousStage = (Stage) groupList.getScene().getWindow();
            previousStage.setScene(new Scene(root));

        } catch (IOException e) {
            _logger.error("Could not load fxml resource: " + e.getMessage());
        }

    }

    private class GroupListCell extends ListCell<Group> {
        final Tooltip tooltip = new Tooltip();

        @Override
        public void updateItem(Group item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setTooltip(null);
                return;
            }
            Text groupNameText = new Text(item.getName());
            groupNameText.setFont(Font.font(Font.getDefault().getFamily(), 14));

            String localPath = item.getPathToClonedGroup();
            Text localPathText = new Text(localPath);

            VBox vboxItem = new VBox(groupNameText, localPathText);
            setGraphic(vboxItem);

            tooltip.setText(item.getName() + " (" + localPath + ")");
            setTooltip(tooltip);

            setOnMouseClicked(loadGroupByDblClickHandler());
        }

        private EventHandler<? super MouseEvent> loadGroupByDblClickHandler() {
            return event -> {
                if (event.getButton() == MouseButton.PRIMARY &&
                        event.getClickCount() > 1) {
                    ListCell<?> c = (ListCell<?>) event.getSource();
                    Object rawGroup = c.getItem();
                    if (!Group.class.isInstance(rawGroup)) {
                        return;
                    }
                    Group selectedGroup = (Group) rawGroup;
                    if (selectedGroup != null) {
                        loadGroup(selectedGroup);
                    }
                }
            };
        }
    }

    public void refreshGroupsList() {
        updateClonedGroups();
        groupList.refresh();
    }
}
