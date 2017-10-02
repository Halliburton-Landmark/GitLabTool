package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Dialog box for tracking the process of cloning a group.
 *
 * @author Lyudmila Lyska
 */
public class CloneProgressDialog extends ProgressDialog {

    private static final String CANCEL_DIALOG_TITLE = "Clonning dialog";
    private static final String CANCEL_MESSAGE = "Cancel process is started. This may take some time. Please wait...";
    private static final String CANCEL_LABEL = "canceling";

    public CloneProgressDialog() {
        super(CANCEL_DIALOG_TITLE, ApplicationState.CLONE, CancelButtonStatus.ACTIVATED);
    }

    @Override
    protected EventHandler<ActionEvent> onCancelAction() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                JGit.getInstance().cancelClone();

                getCancelButton().setDisable(true);
                updateProjectLabel(CANCEL_LABEL);
                addMessageToConcole(CANCEL_MESSAGE, MessageType.SIMPLE);
                updateProgressBar(0.0);
            }
        };
    }

}
