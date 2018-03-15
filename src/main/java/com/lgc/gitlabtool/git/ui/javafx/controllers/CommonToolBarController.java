package com.lgc.gitlabtool.git.ui.javafx.controllers;

import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ActiveViewChangeListener;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ListViewMgrProvider;
import com.lgc.gitlabtool.git.ui.toolbar.GLToolButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;

/**
 * This class represents toolbar controller for additional buttons.
 *
 * Created by Oleksandr Kozlov on 10.03.2018.
 */
public class CommonToolBarController {

    @FXML
    private ToolBar viewToolbar;

    private List<Node> toolbarItems;

    private static final ToolbarManager _toolbarManager = ToolbarManager.getInstance();

    private static final ListViewMgrProvider listViewMgrProvider = ListViewMgrProvider.getInstance();

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
        .getService(ThemeService.class);

    private static final String toolbarHBoxId = "viewToolbarHBox";

    private ActiveViewChangeListener activeViewChangeListener = new ActiveViewChangeListener() {
        @Override
        public void onChanged(String activeView) {
            if (activeView.equals(ViewKey.GROUPS_WINDOW.getKey())) {
                _toolbarManager.getItemById(ViewKey.COMMON_VIEW.getKey(),GLToolButtons.SHOW_PROJECT_HISTORY.getId()).setVisible(false);
            } else if (activeView.equals(ViewKey.PROJECTS_WINDOW.getKey())) {
                _toolbarManager.getItemById(ViewKey.COMMON_VIEW.getKey(),GLToolButtons.SHOW_PROJECT_HISTORY.getId()).setVisible(true);
            }
            viewToolbar.setVisible(checkItemsVisibility());
        }
    };

    @FXML
    protected void initialize() {
        initToolbar();

        initListeners();
    }

    private void initListeners() {
        listViewMgrProvider.getFeature().addChangeActiveViewListener(activeViewChangeListener);
    }

    private void initToolbar() {
        toolbarItems = _toolbarManager.createToolbarItems(ViewKey.COMMON_VIEW.getKey());

        HBox buttonBar = new HBox();
        buttonBar.setPrefHeight(20);
        buttonBar.setId(toolbarHBoxId);
        for(Node item : toolbarItems) {
            if (item instanceof ToggleButton) {
                ((ToggleButton)item).getGraphic().setEffect(_themeService.getLightEffect());
            } else if (item instanceof Button) {
                ((Button)item).getGraphic().setEffect(_themeService.getLightEffect());
            }
            buttonBar.getChildren().add(item);
        }

        viewToolbar.getItems().clear();
        viewToolbar.getItems().addAll(buttonBar);
    }

    private boolean checkItemsVisibility() {
        Optional<Node> optional = toolbarItems.stream().filter(item -> item.isVisible()).findFirst();
        return optional.isPresent();
    }

}
