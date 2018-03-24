package com.lgc.gitlabtool.git.ui.javafx.handlers;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.javafx.JavaFXUI;
import com.lgc.gitlabtool.git.ui.javafx.controllers.StashWindowController;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import java.util.List;

/**
 * Created by ??? on 24.03.2018.
 */
public class StashEventHandler implements EventHandler<WindowEvent> {

    private final StashWindowController _stashWindowController;

    private static final StateService _stateService = ServiceProvider.getInstance()
            .getService(StateService.class);

    public StashEventHandler(StashWindowController stashWindowController) {
        _stashWindowController = stashWindowController;
    }

    @Override
    public void handle(WindowEvent event) {
        List<ApplicationState> activeStates = _stateService.getActiveStates();
        if (!activeStates.isEmpty() && activeStates.contains(ApplicationState.STASH)) {
            event.consume();
            JavaFXUI.showWarningAlertForActiveStates(activeStates);
            return;
        }
        _stashWindowController.dispose();
    }

}