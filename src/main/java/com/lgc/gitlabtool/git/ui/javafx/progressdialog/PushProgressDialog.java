package com.lgc.gitlabtool.git.ui.javafx.progressdialog;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.ui.javafx.progressdialog.ProgressDialog;

public class PushProgressDialog extends ProgressDialog {

    private static final String PUSH_PROGRESS_DIALOG_TITLE = "Push";

    public PushProgressDialog() {
        this(PUSH_PROGRESS_DIALOG_TITLE, ApplicationState.PUSH, CancelButtonStatus.DEACTIVATED);
    }

    public PushProgressDialog(String title, ApplicationState state, CancelButtonStatus cancelButtonStatus) {
        super(title, state, cancelButtonStatus);
    }

}
