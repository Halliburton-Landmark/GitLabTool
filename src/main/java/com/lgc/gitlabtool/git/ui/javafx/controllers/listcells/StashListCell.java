package com.lgc.gitlabtool.git.ui.javafx.controllers.listcells;

import com.lgc.gitlabtool.git.jgit.stash.StashItem;

import javafx.scene.control.ListCell;
import javafx.scene.text.Text;

/**
 * ListCell for ListView<StashItem>.
 *
 * @author Lyudmila Lyska
 */
public class StashListCell extends ListCell<StashItem> {


    @Override
    protected void updateItem(StashItem item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            Text stashName = new Text(item.getMessage());
            //Text projectNameText = getProjectNameText(item);
            //HBox textBox = new HBox(imageView, fileNameText, projectNameText);
            //textBox.setAlignment(Pos.CENTER_LEFT);
            setGraphic(stashName);
        }
    }

}
