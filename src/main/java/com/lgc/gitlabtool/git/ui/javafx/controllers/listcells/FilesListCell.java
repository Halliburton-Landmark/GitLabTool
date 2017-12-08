package com.lgc.gitlabtool.git.ui.javafx.controllers.listcells;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.jgit.ChangedFileStatus;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * ListCell for ListView<ChangedFile>.
 * Sets HBox which contents:
 *        - a name of changed file (if file has conflicts we'll set Color.RED to text);
 *        - a name of project.
 *
 * @author Lyudmila Lyska
 */
public class FilesListCell extends ListCell<ChangedFile> {

    private static final String ADDED_ICON_URL = "icons/staging/added.png";
    private static final String UNTRACKED_ICON_URL = "icons/staging/untracked.png";
    private static final String MODIFIED_ICON_URL = "icons/staging/modified.png";
    private static final String REMOVED_ICON_URL = "icons/staging/missing.png";
    private static final String CONFLICTING_ICON_URL = "icons/staging/conflicted.png";
    private static final String CHANGED_ICON_URL = "icons/staging/changed.png";

    private static final String OPEN_BRACKETS = " [";
    private static final String CLOSE_BRACKETS = "]";
    private static final int PROJECT_FONT = 10;

    @Override
    protected void updateItem(ChangedFile item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            Image fxImage = getImageForFile(item);
            ImageView imageView = new ImageView(fxImage);
            Tooltip.install(imageView, new Tooltip(item.getStatusFile().toString()));

            Text fileNameText = getFileNameText(item);
            Text projectNameText = getProjectNameText(item);

            HBox textBox = new HBox(imageView, fileNameText, projectNameText);
            textBox.setAlignment(Pos.CENTER_LEFT);
            setGraphic(textBox);
        }
    }

    private Text getFileNameText(ChangedFile item) {
        Text fileNameText = new Text(" " + item.getFileName());
        if (item.hasConflicting()) {
            fileNameText.setFill(Color.DARKRED);
        }
        return fileNameText;
    }

    private Text getProjectNameText(ChangedFile item) {
        Text projectNameText = new Text(getProjectName(item));
        projectNameText.setFill(Color.DARKCYAN);
        projectNameText.setFont(new Font(PROJECT_FONT));
        return projectNameText;
    }

    private String getProjectName(ChangedFile item) {
        Project project = item.getProject();
        if (project == null) {
            return StringUtils.EMPTY;
        }
        return OPEN_BRACKETS + project.getName() + CLOSE_BRACKETS;
    }

    private Image getImageForFile(ChangedFile item) {
        String url = getUrlForFile(item);
        return new Image(getClass().getClassLoader().getResource(url).toExternalForm());
    }

    private String getUrlForFile(ChangedFile item) {
        ChangedFileStatus status = item.getStatusFile();
        if (status == ChangedFileStatus.CHANGED) {
            return CHANGED_ICON_URL;
        } else if (status == ChangedFileStatus.ADDED) {
            return ADDED_ICON_URL;
        } else if (status == ChangedFileStatus.CONFLICTING) {
            return CONFLICTING_ICON_URL;
        } else if (status == ChangedFileStatus.MODIFIED) {
            return MODIFIED_ICON_URL;
        } else if (status == ChangedFileStatus.UNTRACKED) {
            return UNTRACKED_ICON_URL;
        } else {
            return REMOVED_ICON_URL;
        }
    }

}
