package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ProjectListCell extends ListCell<Project> {

    private static final GitService _gitService = (GitService) ServiceProvider.getInstance()
            .getService(GitService.class.getName());

    private static final String SHADOW_PROJECT_ICON_URL = "icons/project/shadow_project.png";
    private static final String SHADOW_PROJECT_TOOLTIP = "The project is not cloned.";
    private static final String PROJECT_WITH_CONFLICTS_ICON_URL = "icons/project/conflicts.png";
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

            HBox picItems = new HBox(LIST_CELL_SPACING, getProjectPics(item));

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().addAll(hBoxItem, picItems);
            AnchorPane.setLeftAnchor(hBoxItem, 5.0);
            AnchorPane.setRightAnchor(picItems, 5.0);

            String tooltipText = item.isCloned() ?
                    item.getName() + " " + currentBranchTextView.getText() : SHADOW_PROJECT_TOOLTIP;
            setTooltip(new Tooltip(tooltipText));
            setGraphic(anchorPane);
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

    private Node[] getProjectPics(Project item) {
        List<Node> pics = new ArrayList<>();

        if (_gitService.projectHasConflicts(item)) {
            ImageView conflictsImageView = new ImageView(getImageForConflicts());
            pics.add(conflictsImageView);
        }

        // add another pics here

        return pics.toArray(new Node[pics.size()]);
    }

    private Image getImageForConflicts() {
        Image image = new Image(getClass().getClassLoader().getResource(PROJECT_WITH_CONFLICTS_ICON_URL).toExternalForm());
        return image;
    }

    /*private Node newStatusPic(Image image) {
        
    }*/

}