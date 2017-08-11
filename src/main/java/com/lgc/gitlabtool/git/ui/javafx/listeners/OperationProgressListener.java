package com.lgc.gitlabtool.git.ui.javafx.listeners;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.entities.MessageType;
import com.lgc.gitlabtool.git.entities.Project;
import com.lgc.gitlabtool.git.jgit.JGit;
import com.lgc.gitlabtool.git.jgit.JGitStatus;
import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.services.ProgressListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;
import com.lgc.gitlabtool.git.ui.javafx.ProgressDialog;
import com.lgc.gitlabtool.git.ui.javafx.StatusDialog;
import com.lgc.gitlabtool.git.util.NullCheckUtil;

import javafx.application.Platform;

/**
 * Realization of the {@link ProgressListener} to use with {@link ProgressDialog}
 * <p>
 * Should be used for different {@link JGit} operations: <code>pull, push, clone</code>, etc.
 * <p>
 * <b>Important!</b><br>
 * Realizations of methods have definite count of parameters (it is needed to use it with {@link ProgressDialog})<br>
 * Please see javadoc
 * 
 * @author Igor Khlaponin
 */
public class OperationProgressListener implements ProgressListener {

    private static final Logger _logger = LogManager.getLogger(OperationProgressListener.class);
    private final ProgressDialog _progressDialog;
    private Consumer<Object> _finishedAction;
    private final ApplicationState _applicationState;

    private final StateService _stateService = (StateService) ServiceProvider.getInstance()
            .getService(StateService.class.getName());

    public OperationProgressListener(ProgressDialog progressDialog, ApplicationState applicationState) {
        if (progressDialog == null || applicationState == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        _progressDialog = progressDialog;
        _applicationState = applicationState;
    }

    public OperationProgressListener(ProgressDialog progressDialog, ApplicationState applicationState, Consumer<Object> finishedAction) {
        this(progressDialog, applicationState);
        _finishedAction = finishedAction;
    }

    /**
     * Action that will be executed if operation executed successfully
     * <p>
     * <b>Should always contains 3 parameters in this realization (order is valuable):</b>
     * <li><code>progress</code> instance of <code>Double</code> - progress of the operation</li>
     * <li><code>project</code> instance of <code>Project</code> - project for the operation</li>
     * <li><code>status</code> instance of <code>JGitStatus</code> - status of the operation</li>
     */
    @Override
    public void onSuccess(Object... t) {
        if (t[0] instanceof Long) {
            double progress = (long) t[0] * 0.01;
            _progressDialog.updateProgressBar(progress);
        }
        if (t.length >= 3 && t[1] instanceof Project && t[2] instanceof JGitStatus) {
            String message = ((Project) t[1]).getName() + " : " + t[2];
            _progressDialog.addMessageToConcole(message, MessageType.SUCCESS);
            _logger.info(_applicationState + ": " + message);
        }
    }

    /**
     * Action that will be executed if operation has errors
     * <p>
     * <b>Should always contains 2 parameters in this realization (order is valuable):</b>
     * <li><code>progress</code> instance of <code>Double</code> - progress of the operation</li>
     * <li><code>message</code> instance of <code>String</code> - message that should be shown</li>
     */
    @Override
    public void onError(Object... t) {
        if (t[0] instanceof Long) {
            double progress = (long) t[0] * 0.01;
            _progressDialog.updateProgressBar(progress);
        }
        if (t.length >= 2 && t[1] instanceof String) {
            String message = (String) t[1];
            _progressDialog.addMessageToConcole(message, MessageType.ERROR);
            _logger.error(_applicationState + ": " + message);
        }
        if (t.length >= 3 && t[1] instanceof Project && t[2] instanceof JGitStatus) {
            String message = ((Project) t[1]).getName() + ": " + t[2];
            _progressDialog.addMessageToConcole(message, MessageType.ERROR);
            _logger.error(_applicationState + ": " + message);
        }
    }

    /**
     * Action that will be executed if operation is started
     * <p>
     * <b>Should always contains 1 parameter in this realization.</b>
     * It could be:
     * <li><code>project</code> instance of <code>Project</code> - a project for which operation performs</li>
     * or
     * <li><code>message</code> instance of <code>String</code> - start message</li>
     */
    @Override
    public void onStart(Object... t) {
        if (t[0] instanceof Project) {
            Project project = (Project) t[0];
            _progressDialog.updateProjectLabel(project.getName());
        } else if (t[0] instanceof String) {
            _progressDialog.addMessageToConcole((String) t[0], MessageType.SIMPLE);
        }
        _logger.info(_applicationState + ": started");
    }

    /**
     * Action that will be executed if operation is finished
     * <p>
     * <b>Should always contains 1 parameter in this realization:</b>
     * <li><code>message</code> instance of <code>String</code> - message that should be shown</li>
     */
    @Override
    public final void onFinish(Object... t) {
        try {
            doOnFinishJob(t);
        } finally {
            /* {@link ApplicationState} should be switched off after the finishing the operation*/
            _stateService.stateOFF(_applicationState);
        }
    }

    protected void doOnFinishJob(Object... t) {
        String onFinishMessage = t[0] instanceof String ? (String) t[0] : "Operation finished";
        _progressDialog.addMessageToConcole(onFinishMessage, MessageType.SIMPLE);
        _logger.info(_applicationState + ": finished");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _progressDialog.resetProgress();
                NullCheckUtil.acceptConsumer(_finishedAction, null);
                showStatusDialog(onFinishMessage);
            }
        });
    }

    protected void showStatusDialog(String message) {
        StatusDialog statusDialog = new StatusDialog(_applicationState.toString(), 
                _applicationState.toString()+ " info", message);
        statusDialog.showAndWait();
    }
}
