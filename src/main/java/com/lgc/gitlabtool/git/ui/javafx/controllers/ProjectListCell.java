package com.lgc.gitlabtool.git.ui.javafx.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Status;

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
    private static final String PROJECT_WITH_CONFLICTS_ICON_URL = "icons/project/list_icons/conflicts.png";
    private static final String PROJECT_WITH_UNCOMMITTED_CHANGES_ICON_URL = "icons/project/list_icons/uncommitted_changes.png";
    private static final String COMMITS_AHEAD_INDEX_ICON_URL = "icons/project/list_icons/ahead_index.png";
    private static final String COMMITS_BEHIND_INDEX_ICON_URL = "icons/project/list_icons/behind_index.png";
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

            Text projectNameTextView = new Text(item.getName());
            Color textColor = item.isCloned() ? Color.BLACK : Color.DIMGRAY;
            projectNameTextView.setFill(textColor);
            Text currentBranchTextView = getCurrentBrantProjectText(item);
            
            String tooltipText = item.isCloned() ?
                    item.getName() + " " + currentBranchTextView.getText() : SHADOW_PROJECT_TOOLTIP;
            Tooltip.install(projectNameTextView, new Tooltip(tooltipText));

            HBox hBoxItem = new HBox(imageView, projectNameTextView, currentBranchTextView);
            hBoxItem.setSpacing(LIST_CELL_SPACING);

            HBox picItems = new HBox(LIST_CELL_SPACING, getProjectPics(item, getCurrentBranchName(item)));

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
        return item.isCloned()
               ? JGit.getInstance().getCurrentBranch(item).orElse(StringUtils.EMPTY)
               : StringUtils.EMPTY;
    }

    private Node[] getProjectPics(Project item, String branchName) {
        List<Node> pics = new ArrayList<>();
        if (!item.isCloned()) {
            return new Node[0];
        }

        Optional<Status> projectStatus = _gitService.getProjectStatus(item);
        if (projectStatus.isPresent()) {
            addPicsDependOnStatus(projectStatus.get(), pics);
        }

        pics.add(getAheadBehindCountNode(item, branchName));

        return pics.toArray(new Node[pics.size()]);
    }

    private void addPicsDependOnStatus(Status projectStatus, List<Node> pics) {
        if (projectStatus.isClean()) {
            return;
        }

        if (projectStatus.getConflicting().size() > 0) {
            Node conflictsImageView = newStatusPic(getImage(PROJECT_WITH_CONFLICTS_ICON_URL), 
                    "Project has conflicts");
            pics.add(conflictsImageView);
        }

        if (projectStatus.hasUncommittedChanges()) {
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

    private Node getAheadBehindCountNode(Project item, String branchName) {
        List<Node> items = new ArrayList<>();
        
        int[] aheadBehind = _gitService.getAheadBehindIndexCounts(item, branchName);
        try {
            if (aheadBehind[0] > 0) {
                items.add(newStatusPic(getImage(COMMITS_AHEAD_INDEX_ICON_URL), "Count of commits ahead index"));
                items.add(new Text(Integer.toString(aheadBehind[0])));
            }
            if (aheadBehind[1] > 0) {
                items.add(newStatusPic(getImage(COMMITS_BEHIND_INDEX_ICON_URL), "Count of commits behind index"));
                items.add(new Text(Integer.toString(aheadBehind[1])));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e);
        }
        HBox aheadBehindItems = new HBox(items.toArray(new Node[items.size()]));
        return aheadBehindItems;
    }
}