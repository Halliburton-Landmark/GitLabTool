package com.lgc.gitlabtool.git.ui.javafx.progressdialog;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

public class DeleteBranchProgresDialog extends ProgressDialog {

    private static final String DELETE_BRANCH_DIALOG_TITLE = "Delete branch";

    public DeleteBranchProgresDialog() {
        this(DELETE_BRANCH_DIALOG_TITLE, ApplicationState.DELETE_BRANCH, CancelButtonStatus.DEACTIVATED);
    }

    public DeleteBranchProgresDialog(String title, ApplicationState state, CancelButtonStatus cancelButtonStatus) {
        super(title, state, cancelButtonStatus);
    }

}
