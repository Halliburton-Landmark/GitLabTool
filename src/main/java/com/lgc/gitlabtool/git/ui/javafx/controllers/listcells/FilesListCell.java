package com.lgc.gitlabtool.git.ui.javafx.controllers.listcells;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.ChangedFile;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

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

    private static final ThemeService _themeService = ServiceProvider.getInstance().getService(ThemeService.class);

    @Override
    protected void updateItem(ChangedFile item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            ImageView imageView = _themeService.getStyledImageView(getUrlForFile(item));
            Tooltip.install(imageView, new Tooltip(item.getStatusFile().toString()));

            Label fileNameText = getFileNameText(item);
            Label projectNameText = getProjectNameText(item);

            HBox textBox = new HBox(imageView, fileNameText, projectNameText);
            textBox.setAlignment(Pos.CENTER_LEFT);
            setGraphic(textBox);
        }
    }

    private Label getFileNameText(ChangedFile item) {
        Label fileNameText = new Label(" " + item.getFileName());
        if (item.hasConflicting()) {
            fileNameText.setId("file-list-cell-conflicting-files");
        }
        return fileNameText;
    }

    private Label getProjectNameText(ChangedFile item) {
        Label projectNameText = new Label(getProjectName(item));
        projectNameText.setId("file-list-cell-project-name");
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

    private String getUrlForFile(ChangedFile item) {
        switch (item.getStatusFile()) {
        case CHANGED:
            return CHANGED_ICON_URL;
        case ADDED:
            return ADDED_ICON_URL;
        case CONFLICTING:
            return CONFLICTING_ICON_URL;
        case MODIFIED:
            return MODIFIED_ICON_URL;
        case UNTRACKED:
            return UNTRACKED_ICON_URL;
        case REMOVED:
        case MISSING:
            return REMOVED_ICON_URL;
        default:
            throw new IllegalStateException("Unknown status");
        }
    }

}
