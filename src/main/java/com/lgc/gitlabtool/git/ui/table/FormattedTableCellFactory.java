package com.lgc.gitlabtool.git.ui.table;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.Date;

/**
 * This class represents formatting table cell
 *
 * Created by Oleksandr Kozlov on 3/2/2018.
 */
public class FormattedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    @SuppressWarnings("unchecked")
    public TableCell<S, T> call(TableColumn<S, T> p) {
        TableCell<S, T> cell = new TableCell<S, T>() {

            @Override
            public void updateItem(Object item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem((T) item, empty);
                if (item != null) {
                    if (item instanceof String) {
                        this.setText((String) item);
                    }
                    if (item instanceof Date) {
                        this.setText(item.toString());
                    }
                    this.setAlignment(Pos.CENTER_LEFT);
                }

            }
        };
        return cell;
    }
}

