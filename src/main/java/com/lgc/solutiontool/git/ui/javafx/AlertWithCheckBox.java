package com.lgc.solutiontool.git.ui.javafx;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;

/**
 *
 * @author Lyudmila Lyska
 */
public class AlertWithCheckBox extends Alert {

    private CheckBox _optOut;

    public AlertWithCheckBox(AlertType type, String title, String headerText, String message,
            String optOutMessage, ButtonType... buttonTypes) {
        super(type);
        getDialogPane().applyCss();
        Node graphic = getDialogPane().getGraphic();
        // Create a new dialog pane that has a checkbox instead of the hide/show details button
        // Use the supplied callback for the action of the checkbox
        setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                _optOut = new CheckBox();
                _optOut.setText(optOutMessage);
                return _optOut;
            }
        });
        getDialogPane().getButtonTypes().addAll(buttonTypes);
        getDialogPane().setContentText(message);
        // Fool the dialog into thinking there is some expandable content
        // a Group won't take up any space if it has no children
        getDialogPane().setExpandableContent(new Group());
        getDialogPane().setExpanded(true);
        // Reset the dialog graphic using the default style
        getDialogPane().setGraphic(graphic);
        getDialogPane().setMinHeight(150);

        setTitle(title);
        setHeaderText(headerText);
    }

    /**
     *
     * @return
     */
    public boolean isSelected() {
        return _optOut.isSelected();
    }

}
