package com.lgc.gitlabtool.git.ui.javafx.controllers;

import javafx.scene.control.ListCell;

public class FilesListCell extends ListCell<String> {


    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            //code here

        }
    }
}
