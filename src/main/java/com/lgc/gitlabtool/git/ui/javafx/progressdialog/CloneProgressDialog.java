package com.lgc.gitlabtool.git.ui.javafx.progressdialog;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.GitService;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.ProgressDialog.CancelButtonStatus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Dialog box for tracking the process of cloning a group.
 *
 * @author Lyudmila Lyska
 */
public class CloneProgressDialog extends ProgressDialog {

    private static final String CLONE_DIALOG_TITLE = "Cloning...";
    private static final String CANCEL_MESSAGE = "Cancel process is started. This may take some time. Please wait...";
    private static final String CANCEL_LABEL = "canceling";

    private static final GitService _gitService = ServiceProvider.getInstance()
            .getService(GitService.class);


    public CloneProgressDialog() {
        super(CLONE_DIALOG_TITLE, ApplicationState.CLONE, CancelButtonStatus.ACTIVATED);
    }

    @Override
    protected EventHandler<ActionEvent> onCancelAction() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                _gitService.cancelClone();

                getCancelButton().setDisable(true);
                updateProjectLabel(CANCEL_LABEL);
                addMessageToConcole(CANCEL_MESSAGE, MessageType.SIMPLE);
                updateProgressBar(0.0);
            }
        };
    }

}
