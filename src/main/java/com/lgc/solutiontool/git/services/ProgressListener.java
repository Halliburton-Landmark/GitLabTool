package com.lgc.solutiontool.git.services;

/**
 * Listener for obtaining data on the process of performing the operation.
 *
 * @author Lyudmila Lyska
 */
public interface ProgressListener {

    /**
     * The method works out with a successful operation
     *
     * @param t - any number of objects necessary to obtain detailed information about the process of work
     */
    void onSuccess(Object... t);

    /**
     * The method works out with a failed operation
     *
     * @param t - any number of objects necessary to obtain detailed information about the process of work
     */
    void onError(Object... t);

    /**
     * The method works out with a starting operation
     *
     * @param t - any number of objects necessary to obtain detailed information about the process of work
     */
    void onStart(Object... t);

    /**
     * The method works out with a finished operation
     *
     * @param t - any number of objects necessary to obtain detailed information about the process of work
     */
    void onFinish(Object... t);
}
