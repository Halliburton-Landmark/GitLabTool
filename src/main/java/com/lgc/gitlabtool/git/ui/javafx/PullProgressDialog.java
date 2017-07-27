package com.lgc.gitlabtool.git.ui.javafx;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class PullProgressDialog extends ProgressDialog {

    public PullProgressDialog() {
        super("Pull operation", ApplicationState.PULL);
    }

    @Override
    EventHandler<ActionEvent> onCancelAction() {
        return event -> {
            //do nothing
        };
    }

    
}
