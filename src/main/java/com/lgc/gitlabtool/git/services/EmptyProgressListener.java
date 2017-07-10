package com.lgc.gitlabtool.git.services;


public class EmptyProgressListener implements ProgressListener {

    private static final EmptyProgressListener _emptyListener = new EmptyProgressListener();;

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
