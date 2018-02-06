package com.lgc.gitlabtool.git.ui.javafx.controllers.listcells;

import com.lgc.gitlabtool.git.jgit.stash.SingleProjectStash;
import com.lgc.gitlabtool.git.jgit.stash.Stash;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * ListCell for ListView<StashItem> in the Stash window.
 *
 * @author Lyudmila Lyska
 */
public class StashListCell extends ListCell<Stash> {
    private final static String SINGLE_STASH_TOOLTIP = "Single Stash";
    private final static String GROUP_STASH_TOOLTIP = "Group Stash";
    private final static String END_OF_GROUP_PREFIX = "]";

    private static final ThemeService _themeService = ServiceProvider.getInstance().getService(ThemeService.class);

    @Override
    protected void updateItem(Stash item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            ImageView imageView = _themeService.getStyledImageView(item.getIconURL());
            String tooltip = item instanceof SingleProjectStash ? SINGLE_STASH_TOOLTIP : GROUP_STASH_TOOLTIP;
            Tooltip.install(imageView, new Tooltip(tooltip));

            Label stashName = new Label(getStashItemMessage(item));
            HBox textBox = new HBox(imageView, stashName);
            textBox.setAlignment(Pos.CENTER_LEFT);

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(textBox);
            setGraphic(anchorPane);
        }
    }

    private String getStashItemMessage(Stash item) {
        String stashMessage = item.getMessage();
        if (item instanceof SingleProjectStash) {
            return hasGroupIdentificator(stashMessage) ? getGroupMessage(stashMessage) : " " + stashMessage;
        }
        return getGroupMessage(stashMessage);
    }

    private boolean hasGroupIdentificator(String currentMessage) {
        return currentMessage.matches("\\[GS\\d+\\](.+)?");
    }

    private String getGroupMessage(String stashMessage) {
        int index = stashMessage.indexOf(END_OF_GROUP_PREFIX);
        return stashMessage.substring(++index, stashMessage.length());
    }

}
