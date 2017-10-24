package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

public class SwitchBranchProgressDialog extends ProgressDialog {

    public SwitchBranchProgressDialog() {
        super("Switch operation", ApplicationState.SWITCH_BRANCH, CancelButtonStatus.DEACTIVATED);
    }
}
