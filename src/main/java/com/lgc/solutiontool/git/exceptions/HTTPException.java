package com.lgc.solutiontool.git.exceptions;

/**
 * Class stores data about HTTP error.
 *
 * @author Lyska Lyudmila
 *
 */
public class HTTPException extends RuntimeException {

    private static final long serialVersionUID = 5330689541238440285L;

    private static final String DEFAULT_MESSAGE = "An unknown error occurred!";

    /**
     * Constructor without parameters
     * Creates exception with a default message
     */
    public HTTPException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Creates exception with a message
     *
     * @param message for exception
     */
    public HTTPException(String message) {
        super(message);
    }

    /**
     * Creates exception on the basis of other exception with a default message.
     *
     * @param exception nested of exception
     */
    public HTTPException(Throwable exception) {
        super(DEFAULT_MESSAGE, exception);
    }

    /**
     * Creates exception on the basis of other exception and message
     *
     * @param message the detail message
     * @param exception nested of exception
     */
    public HTTPException(String message, Throwable exception) {
        super(message, exception);
    }

    /**
     * Creates exception with set parameter
     *
     * @param message the detail message
     * @param cause nested of exception
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public HTTPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
