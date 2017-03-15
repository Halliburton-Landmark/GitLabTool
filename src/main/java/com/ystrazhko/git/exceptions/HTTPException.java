package com.ystrazhko.git.exceptions;

/**
 *
 *
 * @author Lyska Lyudmila
 *
 */
public class HTTPException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public HTTPException() {
        super();
    }

    /**
     *
     *
     * @param message
     */
    public HTTPException(String message) {
        super(message);
    }

    /**
     *
     *
     * @param exception
     */
    public HTTPException(Throwable exception) {
        super(exception);
    }

    /**
     *
     *
     * @param message
     * @param exception
     */
    public HTTPException(String message, Throwable exception) {
        super(message, exception);
    }

    /**
     *
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public HTTPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
