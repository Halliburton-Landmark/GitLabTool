package com.lgc.gitlabtool.git.ui.javafx.controllers;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;

import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ProjectListCell extends ListCell<Project> {

    private static final String SHADOW_PROJECT_ICON_URL = "icons/project/shadow_project.png";
    private static final String SHADOW_PROJECT_TOOLTIP = "The project is not cloned.";
    private final Integer LIST_CELL_SPACING = 5;
    private final String LEFT_BRACKET = "[";
    private final String RIGHT_BRACKET = "]";

    @Override
    protected void updateItem(Project item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            Image fxImage = getImageForProject(item);
            ImageView imageView = new ImageView(fxImage);

            Text branchNameTextView = new Text(item.getName());
            Color textColor = item.isCloned() ? Color.BLACK : Color.DIMGRAY;
            branchNameTextView.setFill(textColor);
            Text currentBranchTextView = getCurrentBrantProjectText(item);

            HBox hBoxItem = new HBox(imageView, branchNameTextView, currentBranchTextView);
            hBoxItem.setSpacing(LIST_CELL_SPACING);

            String tooltipText = item.isCloned() ?
                    item.getName() + " " + currentBranchTextView.getText() : SHADOW_PROJECT_TOOLTIP;
            setTooltip(new Tooltip(tooltipText));
            setGraphic(hBoxItem);
        }
    }

    private Image getImageForProject(Project item) {
        String url = item.isCloned() ? item.getProjectType().getIconUrl() : SHADOW_PROJECT_ICON_URL;
        return new Image(getClass().getClassLoader().getResource(url).toExternalForm());
    }

    private Text getCurrentBrantProjectText(Project item) {
        String currentBranch = item.isCloned()
                ? JGit.getInstance().getCurrentBranch(item).orElse(StringUtils.EMPTY)
                : StringUtils.EMPTY;
        String currentBranchFull = item.isCloned() ? LEFT_BRACKET + currentBranch + RIGHT_BRACKET : StringUtils.EMPTY;
        Text currentBranchTextView = new Text(currentBranchFull);
        currentBranchTextView.setFill(Color.DARKBLUE);

        return currentBranchTextView;
    }

}