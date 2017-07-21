package com.lgc.gitlabtool.git.listeners.stateListeners;

/**
* Allows to generate state changing application state and to send it to all listeners.
* Generator can sign the listeners for several events and for a specific event.
*
* Notifies the listeners about the application state change.
*
* @author Lyudmila Lyska
*/
public interface StateListenerGenerator {

    /**
     * Adds listener for tracking changing of the state
     *
     * @param state    the application state which the listener should to track.
     * @param listener the listener
     */
    void addStateListener(ApplicationState state, StateListener listener);

    /**
     * Remove listener for this the state.
     *
     * @param state    the application state which the listener should to track.
     * @param listener the listener which was signed to track this state.
     */
    void removeStateListener(ApplicationState state, StateListener listener);
}
