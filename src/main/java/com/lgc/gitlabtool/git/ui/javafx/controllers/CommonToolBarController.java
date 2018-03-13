package com.lgc.gitlabtool.git.ui.javafx.controllers;

import com.lgc.gitlabtool.git.ui.ViewKey;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ActiveViewChangeListener;
import com.lgc.gitlabtool.git.ui.javafx.controllers.listview.ListViewMgrProvider;
import com.lgc.gitlabtool.git.ui.toolbar.GLToolButtons;
import com.lgc.gitlabtool.git.ui.toolbar.ToolbarManager;
import com.lgc.gitlabtool.git.util.ThemeUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * This class represents toolbar controller for additional buttons.
 *
 * Created by Oleksandr Kozlov on 10.03.2018.
 */
public class CommonToolBarController {

    @FXML
    private ToolBar toolbar;

    private List<Node> toolbarItems;

    private static final ToolbarManager _toolbarManager = ToolbarManager.getInstance();

    private static final ListViewMgrProvider listViewMgrProvider = ListViewMgrProvider.getInstance();

    private ActiveViewChangeListener activeViewChangeListener = new ActiveViewChangeListener() {
        @Override
        public void onChanged(String activeView) {
            if (activeView.equals(ViewKey.GROUPS_WINDOW.getKey())) {
                _toolbarManager.getItemById(ViewKey.COMMON_VIEW.getKey(),GLToolButtons.SHOW_PROJECT_HISTORY.getId()).setVisible(false);
            } else if (activeView.equals(ViewKey.PROJECTS_WINDOW.getKey())) {
                _toolbarManager.getItemById(ViewKey.COMMON_VIEW.getKey(),GLToolButtons.SHOW_PROJECT_HISTORY.getId()).setVisible(true);
            }
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
        for(Node item : toolbarItems) {
            item.setVisible(false);
            if (item instanceof ToggleButton) {
                ((ToggleButton)item).getGraphic().setEffect(ThemeUtil.getLightEffect());
            } else if (item instanceof Button) {
                ((Button)item).getGraphic().setEffect(ThemeUtil.getLightEffect());
            }
            buttonBar.getChildren().add(item);
        }

        toolbar.getItems().clear();
        toolbar.getItems().addAll(buttonBar);
    }

}
