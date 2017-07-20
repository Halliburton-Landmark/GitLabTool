package com.lgc.gitlabtool.git.ui.dialogs;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;

/**
 * The AlertWithCheckBox class subclasses the Alert class.
 *
 * It provides support for a number of pre-built dialog types that can be easily
 * shown to users to prompt for a response.
 *
 * The AlertWithCheckBox class allows to create a window with certain
 * ButtonTypes (for example: ButtonType.YES, ButtonType.NO, ButtonType.CANCEL etc).
 *
 * Also, the AlertWithCheckBox class contains a checkbox in the lower left corner.
 * The state of the checkbox button and its text we can be set in the constructor
 *
 * @author Lyudmila Lyska
 */
public class AlertWithCheckBox extends Alert {

    private CheckBox _optOut;

    public AlertWithCheckBox(AlertType type, String title, String headerText, String contentMessage,
            String checkBoxMessage, ButtonType... buttonTypes) {
        super(type);
        getDialogPane().applyCss();
        Node graphic = getDialogPane().getGraphic();
        // Create a new dialog pane that has a checkbox instead of the hide/show details button
        // Use the supplied callback for the action of the checkbox
        setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                _optOut = new CheckBox();
                _optOut.setText(checkBoxMessage);
                return _optOut;
            }
        });
        getDialogPane().getButtonTypes().addAll(buttonTypes);
        getDialogPane().setContentText(contentMessage);
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
     * Checks the checkbox is selected
     *
     * @return state button
     */
    public boolean isCheckBoxSelected() {
        return _optOut.isSelected();
    }

}
