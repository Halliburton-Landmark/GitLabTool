package com.lgc.gitlabtool.git.services;

/**
 * Marks class as a service
 *
 * @author Igor Khlaponin
 */
public interface Service {

    /**
     * Provides dispose actions
     */
    default void dispose() {}
}
