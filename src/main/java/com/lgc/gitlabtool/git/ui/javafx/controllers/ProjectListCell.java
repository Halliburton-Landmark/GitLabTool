package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.entities.ProjectStatus;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ProjectListCell extends ListCell<Project> {
    private static final String SHADOW_PROJECT_ICON_URL = "icons/project/shadow_project.png";
    private static final String SHADOW_PROJECT_TOOLTIP = "The project is not cloned.";
    private static final String PROJECT_WITH_CONFLICTS_ICON_URL = "icons/project/list_icons/conflicts_16x16.png";
    private static final String PROJECT_WITH_NEW_FILES_ICON_URL = "icons/project/list_icons/new_files_16x16.png";
    private static final String PROJECT_WITH_UNCOMMITTED_CHANGES_ICON_URL = "icons/project/list_icons/uncommitted_changes_16x16.png";
    private static final String COMMITS_AHEAD_INDEX_ICON_URL = "icons/project/list_icons/ahead_index_12x12.png";
    private static final String COMMITS_BEHIND_INDEX_ICON_URL = "icons/project/list_icons/behind_index_12x12.png";
    private static final String COMMITS_AHEAD_TOOLTIP = "Count of commits ahead index";
    private static final String COMMITS_BEHIND_TOOLTIP = "Count of commits behind index";
    private final Integer LIST_CELL_SPACING = 5;
    private final Double INDEX_FONT_SIZE = 12.0;
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

            Text projectNameTextView = new Text(item.getName());
            Color textColor = item.isCloned() ? Color.BLACK : Color.DIMGRAY;
            projectNameTextView.setFill(textColor);
            Text currentBranchTextView = getCurrentBrantProjectText(item);

            String tooltipText = item.isCloned() ?
                    item.getName() + " " + currentBranchTextView.getText() : SHADOW_PROJECT_TOOLTIP;
            Tooltip.install(projectNameTextView, new Tooltip(tooltipText));

            HBox hBoxItem = new HBox(imageView, projectNameTextView, currentBranchTextView);
            hBoxItem.setSpacing(LIST_CELL_SPACING);

            HBox picItems = new HBox(LIST_CELL_SPACING, getProjectPics(item));

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().addAll(hBoxItem, picItems);
            AnchorPane.setLeftAnchor(hBoxItem, 5.0);
            AnchorPane.setRightAnchor(picItems, 5.0);

            setGraphic(anchorPane);
        }
    }

    private Image getImageForProject(Project item) {
        String url = item.isCloned() ? item.getProjectType().getIconUrl() : SHADOW_PROJECT_ICON_URL;
        return new Image(getClass().getClassLoader().getResource(url).toExternalForm());
    }

    private Text getCurrentBrantProjectText(Project item) {
        String currentBranch = getCurrentBranchName(item);
        String currentBranchFull = item.isCloned() ? LEFT_BRACKET + currentBranch + RIGHT_BRACKET : StringUtils.EMPTY;
        Text currentBranchTextView = new Text(currentBranchFull);
        currentBranchTextView.setFill(Color.DARKBLUE);

        return currentBranchTextView;
    }

    private String getCurrentBranchName(Project item) {
        ProjectStatus projectStatus = item.getProjectStatus();
        return projectStatus.getCurrentBranch();
    }

    private Node[] getProjectPics(Project item) {
        List<Node> pics = new ArrayList<>();
        if (!item.isCloned()) {
            return new Node[0];
        }

        addPicsDependOnStatus(item, pics);

        pics.add(getAheadBehindCountNode(item));

        return pics.toArray(new Node[pics.size()]);
    }

    private void addPicsDependOnStatus(Project project, List<Node> pics) {
        if (project == null || pics == null) {
            return;
        }
        ProjectStatus projectStatus = project.getProjectStatus();
        if (projectStatus.hasConflicts()) {
            Node conflictsImageView = newStatusPic(getImage(PROJECT_WITH_CONFLICTS_ICON_URL),
                    "Project has conflicts");
            pics.add(conflictsImageView);
            return;
        }

        if (projectStatus.hasNewUntrackedFiles()) {
            Node untrackedImageView = newStatusPic(getImage(PROJECT_WITH_NEW_FILES_ICON_URL),
                    "Project has new files");
            pics.add(untrackedImageView);
            return;
        }

        if (projectStatus.hasChanges()) {
            Node uncommittedChangesImage = newStatusPic(getImage(PROJECT_WITH_UNCOMMITTED_CHANGES_ICON_URL),
                    "Project has uncommitted changes");
            pics.add(uncommittedChangesImage);
        }
    }

    private Image getImage(String path) {
        Image image = new Image(getClass()
                .getClassLoader()
                .getResource(path).toExternalForm());
        return image;
    }

    private Node newStatusPic(Image image, String tooltip) {
        ImageView imageView = new ImageView(image);
        Tooltip.install(imageView, new Tooltip(tooltip));
        return imageView;
    }

    private Node getAheadBehindCountNode(Project item) {
        List<Node> items = new ArrayList<>();
        ProjectStatus projectStatus = item.getProjectStatus();
        int ahead = projectStatus.getAheadIndex();
        if (ahead > 0) {
            items.add(newStatusPic(getImage(COMMITS_AHEAD_INDEX_ICON_URL), COMMITS_AHEAD_TOOLTIP));
            Text aheadIndex = new Text(Integer.toString(ahead));
            aheadIndex.setFont(new Font(INDEX_FONT_SIZE));
            items.add(aheadIndex);
        }

        int behind = projectStatus.getBehindIndex();
        if (behind > 0) {
            items.add(newStatusPic(getImage(COMMITS_BEHIND_INDEX_ICON_URL), COMMITS_BEHIND_TOOLTIP));
            Text behindIndex = new Text(Integer.toString(behind));
            behindIndex.setFont(new Font(INDEX_FONT_SIZE));
            items.add(behindIndex);
        }

        HBox aheadBehindItems = new HBox(items.toArray(new Node[items.size()]));
        aheadBehindItems.setAlignment(Pos.CENTER);
        return aheadBehindItems;
    }
}