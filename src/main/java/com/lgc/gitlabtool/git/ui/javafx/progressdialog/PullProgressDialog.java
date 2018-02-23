package com.lgc.gitlabtool.git.ui.javafx.progressdialog;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.ProgressDialog.CancelButtonStatus;

public class PullProgressDialog extends ProgressDialog {

    public PullProgressDialog() {
        super("Pull operation", ApplicationState.PULL, CancelButtonStatus.DEACTIVATED);
    }

}
