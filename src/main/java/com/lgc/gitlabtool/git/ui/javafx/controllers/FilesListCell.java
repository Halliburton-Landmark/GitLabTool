package com.lgc.gitlabtool.git.ui.javafx.controllers;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.ChangedFile;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FilesListCell extends ListCell<ChangedFile> {

    @Override
    protected void updateItem(ChangedFile item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            Text fileNameText = new Text(item.getFileName());
            Text projectNameText = new Text(getProjectNamme(item.getProject()));
            projectNameText.setFill(Color.DARKCYAN);
            projectNameText.setFont(new Font(10));

            HBox textBox = new HBox(fileNameText, projectNameText);
            textBox.setAlignment(Pos.CENTER_LEFT);
            setGraphic(textBox);
        }
    }

   private String getProjectNamme(Project project) {
        if (project == null) {
            return " ";
        }
        return " [" + project.getName() + "]";
    }
}
