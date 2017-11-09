package com.lgc.gitlabtool.git.ui.javafx.listeners;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ConsoleService;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;

/**
 * Progress listener for Push operation
 */
public class PushProgressListener implements ProgressListener {

    private static final ConsoleService _consoleService = (ConsoleService) ServiceProvider.getInstance()
            .getService(ConsoleService.class.getName());

    private static final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    private PushProgressListener() {}

    private static PushProgressListener instance;

    /**
     * Creates the instance of {@link PushProgressListener} if it doesn't exist yet and returns it
     * @return the instance of {@link PushProgressListener}
     */
    public static PushProgressListener get() {
        if (instance == null) {
            instance = new PushProgressListener();
        }
        return instance;
    }

    @Override
    public void onSuccess(Object... t) {
        if(t[0] instanceof Project) {
            String message = "Pushing the " + ((Project)t[0]).getName() + " project is successful!";
            _consoleService.addMessage(message, MessageType.SUCCESS);
        }
    }

    @Override
    public void onError(Object... t) {
        if(t[0] instanceof Project) {
            String message = "Failed pushing the " + ((Project)t[0]).getName() + " project!";
            _consoleService.addMessage(message, MessageType.ERROR);
        }
    }

    @Override
    public void onStart(Object... t) {
        _consoleService.addMessage("Push projects is started...", MessageType.SIMPLE);
        _stateService.stateON(ApplicationState.PUSH);
    }

    @Override
    public void onFinish(Object... t) {
        _stateService.stateOFF(ApplicationState.PUSH);
        _consoleService.addMessage("Push projects is finished!", MessageType.SIMPLE);
    }
}
