package com.lgc.gitlabtool.git.ui.javafx.controllers.listcells;

import com.lgc.gitlabtool.git.jgit.stash.Stash;
import com.lgc.gitlabtool.git.jgit.stash.StashItem;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * ListCell for ListView<StashItem> in the Stash window.
 *
 * @author Lyudmila Lyska
 */
public class StashListCell extends ListCell<StashItem> {
    private final static String SINGLE_STASH_TOOLTIP = "Single Stash";
    private final static String GROUP_STASH_TOOLTIP = "Group Stash";
    private final static String END_OF_GROUP_PREFIX = "]";
    private final ClassLoader _loader = getClass().getClassLoader();

    @Override
    protected void updateItem(StashItem item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            Image fxImage = new Image(_loader.getResource(item.getIconURL()).toExternalForm());
            ImageView imageView = new ImageView(fxImage);
            String tooltip = item instanceof Stash ? SINGLE_STASH_TOOLTIP : GROUP_STASH_TOOLTIP;
            Tooltip.install(imageView, new Tooltip(tooltip));

            Text stashName = new Text(getStashItemMessage(item));
            HBox textBox = new HBox(imageView, stashName);
            textBox.setAlignment(Pos.CENTER_LEFT);

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(textBox);
            setGraphic(anchorPane);
        }
    }

    private String getStashItemMessage(StashItem item) {
        String stashMessage = item.getMessage();
        return item instanceof Stash ? " " + stashMessage : getGroupMessage(stashMessage);
    }

    private String getGroupMessage(String stashMessage) {
        int index = stashMessage.indexOf(END_OF_GROUP_PREFIX);
        return stashMessage.substring(++index, stashMessage.length());
    }

}
