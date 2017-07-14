package com.lgc.gitlabtool.git.services;

/**
 * Empty listener which doesn't obtaining data on the process of performing the operation.
 *
 * @author Lyska Lyudmila
 */
public class EmptyProgressListener implements ProgressListener {

    private static final EmptyProgressListener _emptyListener = new EmptyProgressListener();;

    /**
     * Gets instance of empty listener
     *
     * @return empty listener
     */
    public static EmptyProgressListener get() {
        return _emptyListener;
    }

    private EmptyProgressListener() {}

    @Override
    public void onSuccess(Object... t) {
    }

    @Override
    public void onError(Object... t) {
    }

    @Override
    public void onStart(Object... t) {
    }

    @Override
    public void onFinish(Object... t) {
    }

}
