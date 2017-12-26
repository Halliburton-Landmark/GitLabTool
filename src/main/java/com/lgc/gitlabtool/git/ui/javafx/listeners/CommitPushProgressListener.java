package com.lgc.gitlabtool.git.ui.javafx.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;

public class CommitPushProgressListener implements ProgressListener {

    private static final Logger _logger = LogManager.getLogger(CommitPushProgressListener.class);

    private final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    private final ApplicationState _currentState;

    public CommitPushProgressListener(ApplicationState currentState) {
        _currentState = currentState;
        if (_currentState != null) {
            _stateService.stateON(_currentState);
        }
    }

    @Override
    public void onSuccess(Object... t) {
        if (t[0] instanceof Integer) {
            showProgress(t[0]);
        }
    }

    @Override
    public void onError(Object... t) {
        showProgress(t[0]);
    }

    private void showProgress(Object object) {
        if (object instanceof Integer) {
            _logger.info("Progress: " + object + "%");
        }
    }

    @Override
    public void onStart(Object... t) {
    }

    @Override
    public void onFinish(Object... t) {
        if (_currentState != null) {
            _stateService.stateOFF(_currentState);
        }
    }

}
