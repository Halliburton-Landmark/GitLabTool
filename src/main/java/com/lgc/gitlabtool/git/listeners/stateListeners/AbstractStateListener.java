package com.lgc.gitlabtool.git.listeners.stateListeners;

/**
 * <h6>Abstract class that contains common logic for disposing.</h6>
 *
 * <p>Any state listener should dispose itself to stop listening to all events and
 * to prevent listener's leak.</p>
 * <p>Just call the dispose() method and StateService will do all the rest job for you.</p>
 *
 * @author Yevhen Strazhko
 */
public abstract class AbstractStateListener implements StateListener {
    private boolean _isDisposed = false;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }
}
