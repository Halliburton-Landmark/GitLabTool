package com.lgc.gitlabtool.git.ui.javafx.progressdialog;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

/**
 * Realization of progress dialog for the switch branch operation.
 *
 * @author Lyudmila Lyska
 */
public class CheckoutBranchProgressDialog extends ProgressDialog {

    private static final String CHECKOUT_BRANCH_DIALOG_TITLE = "Checkout operation";

    public CheckoutBranchProgressDialog() {
        super(CHECKOUT_BRANCH_DIALOG_TITLE, ApplicationState.CHECKOUT_BRANCH, CancelButtonStatus.DEACTIVATED);
    }
}
