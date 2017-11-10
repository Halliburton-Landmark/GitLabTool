package com.lgc.gitlabtool.git.ui.javafx.controllers.listcells;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.ChangedFile;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * ListCell for ListView<ChangedFile>.
 * Sets HBox which contents
 *      - a name of changed file (if file has conflicts we'll set Color.RED to text);
 *      - a name of project.
 *
 * @author Lyudmila Lyska
 */
public class FilesListCell extends ListCell<ChangedFile> {

    private static final String OPEN_BRACKETS = " [";
    private static final String CLOSE_BRACKETS = "]";
    private static final String CONFLICTING_PREFIX = " [conflicting]";
    private static final int PROJECT_FONT = 10;

    @Override
    protected void updateItem(ChangedFile item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            Text fileNameText = getFileNameText(item);
            Text projectNameText = getProjectNameText(item);
            HBox textBox = new HBox(fileNameText, projectNameText);
            textBox.setAlignment(Pos.CENTER_LEFT);
            setGraphic(textBox);
        }
    }

    private Text getFileNameText(ChangedFile item) {
        Text fileNameText = new Text();
        String fileName = item.getFileName();
        if (item.isHasConflicting()) {
            fileNameText.setText(fileName + CONFLICTING_PREFIX);
            fileNameText.setFill(Color.DARKRED);
        } else {
            fileNameText.setText(fileName);
        }
        return fileNameText;
    }

    private Text getProjectNameText(ChangedFile item) {
        Text projectNameText = new Text(getProjectNamme(item));
        projectNameText.setFill(Color.DARKCYAN);
        projectNameText.setFont(new Font(PROJECT_FONT));
        return projectNameText;
    }

    private String getProjectNamme(ChangedFile item) {
        Project project = item.getProject();
        if (project == null) {
            return StringUtils.EMPTY;
        }
        return OPEN_BRACKETS + project.getName() + CLOSE_BRACKETS;
    }
}
