package com.lgc.gitlabtool.git.ui.javafx;

import java.util.List;
import java.util.stream.Collectors;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.listeners.stateListeners.StateListener;
import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.StateService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

/**
 * Modified HBox component that shows current application status
 * Status bar contains progress indicator, that will be shown if some operation runs and won't show otherwise
 * 
 * @author Igor Khlaponin
 *
 */
public class StatusBar extends HBox implements StateListener {

    private static final StateService _stateService = 
            (StateService) ServiceProvider.getInstance().getService(StateService.class.getName());

    private static final double STATUS_BAR_HEIGHT = 15.0;
    private static final int ELEMENTS_SPACING = 5;
    /** Default application state */
    private static final String READY_TO_USE_STATE = "Ready to use";

    @FXML
    private final ProgressIndicator _statusIndicator = new ProgressIndicator();
    @FXML
    private final Label _statusLabel = new Label();
    @FXML
    private final Label _currentStatus = new Label();

    /**
     * Constructor that creates the instance of this class
     */
    public StatusBar() {
        initUI();
        initListeners();
    }

    private void initUI() {
        this.setMaxHeight(STATUS_BAR_HEIGHT);
        this.setHeight(STATUS_BAR_HEIGHT);
        _statusLabel.setText("Application status: ");
        _currentStatus.setText(READY_TO_USE_STATE);
        _statusIndicator.setDisable(true);

        getChildren().addAll(_statusLabel, _currentStatus, _statusIndicator);
        setSpacing(ELEMENTS_SPACING);
    }

    private void initListeners() {
        _stateService.addStateListener(ApplicationState.CLONE, this);
        _stateService.addStateListener(ApplicationState.COMMIT, this);
        _stateService.addStateListener(ApplicationState.PUSH, this);
        _stateService.addStateListener(ApplicationState.PULL, this);
        _stateService.addStateListener(ApplicationState.CREATE_PROJECT, this);
        _stateService.addStateListener(ApplicationState.SWITCH_BRANCH, this);
        _stateService.addStateListener(ApplicationState.EDIT_POM, this);
        _stateService.addStateListener(ApplicationState.REVERT, this);
    }

    @Override
    public void handleEvent(ApplicationState changedState, boolean isActivate) {
        if (isActivate) {
            List<ApplicationState> activeStates = _stateService.getActiveStates();
            if (!activeStates.isEmpty()) {
                String status = activeStates.stream()
                                            .map(elem -> elem.toString())
                                            .distinct()
                                            .collect(Collectors.joining(", "));
                Platform.runLater(() -> _currentStatus.setText(status));
                _statusIndicator.setDisable(false);
            }
        } else {
            Platform.runLater(() -> _currentStatus.setText(READY_TO_USE_STATE));
            _statusIndicator.setDisable(true);
        }
    }
}
