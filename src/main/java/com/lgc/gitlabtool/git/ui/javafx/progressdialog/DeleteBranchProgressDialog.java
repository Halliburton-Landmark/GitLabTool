package com.lgc.gitlabtool.git.ui.javafx.progressdialog;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

public class DeleteBranchProgressDialog extends ProgressDialog {

    private static final String DELETE_BRANCH_DIALOG_TITLE = "Delete branch";

    public DeleteBranchProgressDialog() {
        this(DELETE_BRANCH_DIALOG_TITLE, ApplicationState.DELETE_BRANCH, CancelButtonStatus.DEACTIVATED);
    }

    public DeleteBranchProgressDialog(String title, ApplicationState state, CancelButtonStatus cancelButtonStatus) {
        super(title, state, cancelButtonStatus);
    }

}
