package com.lgc.gitlabtool.git.services;

import com.lgc.gitlabtool.git.listeners.stateListeners.ApplicationState;
import com.lgc.gitlabtool.git.listeners.stateListeners.StateEventGenerator;

/**
 * State service stores data about all states our program.
 * We can switch states and ON/OFF their using this service.
 *
 * We can find out the certain state was activated or deactivated.
 * Also, we can subscribe to a event of changing application state,
 * which will be notifies all listeners
 * when a state'll be changing from ON to OFF or vice versa.
 *
 * @author Lyudmila Lyska
 */
public interface StateService extends StateEventGenerator {

    /**
     * Activates the state.
     *
     * Warning: The state should is activated and deactivated
     * an equal number of times that it will is off.
     *
     * @param state is the state which is stored in the ApplicationState enum.
     */
    void stateON(ApplicationState state);

    /**
     * Deactivates the state.
     *
     * Warning: The state should is activated and deactivated
     * an equal number of times that it will is off.
     *
     * @param state is the state which is stored in the ApplicationState enum.
     */
    void stateOFF(ApplicationState state);

    /**
     * Checks that the state is activated.
     *
     * @param state is the state which is stored in the ApplicationState enum.
     * @return <true> if state is activated or <false> if not.
     */
    boolean isActiveState(ApplicationState state);

    /**
     * Checks that all processes finished (for example: clone, pull, push etc.)
     *
     * @return <true> if any process doesn't finish,
     *         <false> if all processes finished.
     */
    boolean isBusy();
}
