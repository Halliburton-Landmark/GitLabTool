package com.lgc.solutiontool.git.ui.javafx;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import com.lgc.solutiontool.git.ui.icon.AppIconHolder;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * CommitDialog class allows to create a window for committing changes.
 *
 * @author Pavlo Pidhorniy
 */
public class CommitDialog extends Dialog {
    private static final Image _appIcon = AppIconHolder.getInstance().getAppIcoImage();

    private ButtonType commitButton;
    private ButtonType commitAndPushButton;

    private TextArea _textArea;

    public CommitDialog() {
        super();

        commitButton = new ButtonType("Commit");
        commitAndPushButton = new ButtonType("Commit and push");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(commitButton, commitAndPushButton, cancel);

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

    public ButtonType getCommitButton() {
        return commitButton;
    }

    public ButtonType getCommitAndPushButton() {
        return commitAndPushButton;
    }

    public String getCommitMessage(){
        if (_textArea == null || _textArea.getText() == null) {
            return StringUtils.EMPTY;
        }

        return _textArea.getText();
    }

}
