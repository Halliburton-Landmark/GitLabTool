package com.lgc.gitlabtool.git.listeners.stateListeners;

/**
 * Listener for tracking changes of application state.
 *
 * @author Lyudmila Lyska
 */
public interface StateListener {

    /**
     * Processes event changing of state on which the listener was signed
     *
     * @param changedState the state which was changed
     * @param isActivate  isActivate  <true> if status is activated, <false> if status is deactivated.
     */
    void handleEvent(ApplicationState changedState, boolean isActivate);
}
