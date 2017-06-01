package com.lgc.solutiontool.git.services;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public interface ProgressListener {

    void onSuccess(Object... t);

    void onError(Object... t);

    void onStart(Object... t);

    void onFinish(Object... t);
}
