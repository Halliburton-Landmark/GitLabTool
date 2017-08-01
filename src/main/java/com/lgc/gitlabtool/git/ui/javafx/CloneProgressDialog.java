package com.lgc.gitlabtool.git.ui.javafx;

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

    public CloneProgressDialog() {
        super("Clonning dialog", ApplicationState.CLONE, CancelButtonStatus.ACTIVATED);
    }

    @Override
    protected EventHandler<ActionEvent> onCancelAction() {
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
