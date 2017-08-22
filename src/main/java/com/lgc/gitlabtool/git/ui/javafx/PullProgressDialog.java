package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

public class PullProgressDialog extends ProgressDialog {

    public PullProgressDialog() {
        super("Pull operation", ApplicationState.PULL, CancelButtonStatus.DEACTIVATED);
    }

}
