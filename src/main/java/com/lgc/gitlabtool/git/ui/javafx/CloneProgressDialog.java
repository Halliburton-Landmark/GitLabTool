package com.lgc.gitlabtool.git.ui.javafx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.icon.AppIconHolder;
import com.lgc.gitlabtool.git.ui.javafx.dto.DialogDTO;
import com.lgc.gitlabtool.git.util.ScreenUtil;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Dialog box for tracking the process of cloning a group.
 * <p>
 * Use {@link #showDialog()} method to show this dialog instead of the standard showAndWait() and show() methods
 *
 * @author Lyudmila Lyska
 */
public class CloneProgressDialog extends ProgressDialog {

    public CloneProgressDialog() {
        super("Clonning dialog", ApplicationState.CLONE, false);
    }

    @Override
    EventHandler<ActionEvent> onCancelAction() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                getCancelButton().setDisable(true);
                addMessageToConcole("Starting cancel process of cloning...", OperationMessageStatus.SIMPLE);
                JGit.getInstance().cancelClone();
                updateProgressBar(0.0);
            }
        };
    }

}
