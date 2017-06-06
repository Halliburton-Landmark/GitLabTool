package com.lgc.solutiontool.git.ui.javafx;

import org.apache.commons.lang.StringUtils;

import com.lgc.solutiontool.git.ui.icon.AppIconHolder;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
public class CommitDialog extends TextInputDialog {
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private TextArea _textArea;

    public CommitDialog() {
        super();

        ButtonType commit = new ButtonType("Commit");
        ButtonType commitAndPush = new ButtonType("Commit and push");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(commit, commitAndPush, cancel);

        Label label = new Label("Commit message:");
        _textArea = new TextArea();
        VBox.setVgrow(_textArea, Priority.ALWAYS);
        VBox expContent = new VBox();
        expContent.getChildren().addAll(label, _textArea);
        getDialogPane().setContent(expContent);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(_appIcon);

        setTitle("Switch branch confirmation");
        setHeaderText("This projects have uncommited a changes");
    }

    public String getCommitMessage(){
        if (_textArea == null || _textArea.getText() == null) {
            return StringUtils.EMPTY;
        }

        return _textArea.getText();
    }

}
