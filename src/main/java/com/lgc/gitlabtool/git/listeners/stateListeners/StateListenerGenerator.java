package com.lgc.gitlabtool.git.listeners.stateListeners;

import java.util.Set;

/**
*
*
* @author Lyudmila Lyska
*/
public interface StateListenerGenerator {

    /**
     *
     * @param state
     * @param listener
     */
    void addStateListener(ApplicationState state, StateListener listener);

    /**
     *
     * @param state
     * @param listener
     */
    void removeStateListener(ApplicationState state, StateListener listener);

    /**
     *
     * @param state
     * @return
     */
    Set<StateListener> getListeners(ApplicationState state);
}
