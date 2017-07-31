package com.lgc.gitlabtool.git.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.listeners.stateListeners.StateListener;

public class StateServiceImpl implements StateService {
    private static final int ACTIVATE_STATE = 1;
    private static final int DEACTIVATE_STATE = -1;
    private static final int START_STATE = 0;

    private final Map<ApplicationState, Integer> _states;
    private final Map<ApplicationState, Set<StateListener>> _listeners;

    private static final Logger _logger = LogManager.getLogger(StateServiceImpl.class);

    public StateServiceImpl() {
        _listeners = new ConcurrentHashMap<>();
        _listeners.put(ApplicationState.CLONE, createSynchronizedSet());
        _listeners.put(ApplicationState.PULL, createSynchronizedSet());
        _listeners.put(ApplicationState.COMMIT, createSynchronizedSet());
        _listeners.put(ApplicationState.PUSH, createSynchronizedSet());
        _listeners.put(ApplicationState.CREATE_PROJECT, createSynchronizedSet());

        _states = new ConcurrentHashMap<>();
        _states.put(ApplicationState.CLONE, START_STATE);
        _states.put(ApplicationState.PULL, START_STATE);
        _states.put(ApplicationState.COMMIT, START_STATE);
        _states.put(ApplicationState.PUSH, START_STATE);
        _states.put(ApplicationState.CREATE_PROJECT, START_STATE);
    }

    @Override
    public void stateON(ApplicationState state) {
        setState(state, ACTIVATE_STATE);
        _logger.info("Activated " + state.getState());
    }

    @Override
    public void stateOFF(ApplicationState state) {
        setState(state, DEACTIVATE_STATE);
        _logger.info("Deactivate " + state.getState());
    }

    private void setState(ApplicationState state, int operation) {
        Integer value = _states.get(state);

        Integer newValue;
        if (value == null) {
            newValue = operation != DEACTIVATE_STATE ? operation : START_STATE;
        } else if (value < START_STATE) {
            // we have incorrect value in the map
            newValue = START_STATE;
        } else {
            newValue = value + operation;
        }
        _states.put(state, newValue);
        // if state was changed from ON to OFF and vice versa
        if (isActive(value) != isActive(newValue)) {
            notifyListenersByType(state);
        }
    }

    @Override
    public boolean isActiveState(ApplicationState state) {
        Integer value = _states.get(state);
        if (value < START_STATE) {
            // we have incorrect value in the map
            _states.put(state, START_STATE);
        }
        return isActive(value);
    }

    private boolean isActive(Integer value) {
        return value != null && value > START_STATE ? true : false;
    }

    @Override
    public void addStateListener(ApplicationState state, StateListener addListener) {
        Set<StateListener> listeners = _listeners.get(state);

        if (listeners == null) {
            Set<StateListener> newSet = createSynchronizedSet();
            newSet.add(addListener);
            _listeners.put(state, newSet);
        } else {
            listeners.add(addListener);
        }
    }

    @Override
    public void removeStateListener(ApplicationState state, StateListener removeListener) {
        Set<StateListener> listeners = _listeners.get(state);
        if (listeners != null && !listeners.isEmpty()) {
            listeners.remove(removeListener);
        }
    }

    private Set<StateListener> createSynchronizedSet() {
        return Collections.synchronizedSet(new HashSet<StateListener>());
    }

    private void notifyListenersByType(ApplicationState changedState) {
        _logger.info("Notifying listeners about changing of " + changedState.getState());
        final Set<StateListener> listeners = _listeners.get(changedState);
        if (listeners != null) {
            listeners.forEach(listener -> listener.handleEvent(changedState, isActiveState(changedState)));
        }
    }
}
