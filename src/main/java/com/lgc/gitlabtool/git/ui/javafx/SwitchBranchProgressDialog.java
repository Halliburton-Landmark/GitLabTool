package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

/**
 * Realization of progress dialog for the switch branch operation.
 *
 * @author Lyudmila Lyska
 */
public class SwitchBranchProgressDialog extends ProgressDialog {

    private static final String SWITCH_BRANCH_DIALOG_TITLE = "Switch operation";

    public SwitchBranchProgressDialog() {
        super(SWITCH_BRANCH_DIALOG_TITLE, ApplicationState.SWITCH_BRANCH, CancelButtonStatus.DEACTIVATED);
    }
}
