package com.ystrazhko.git.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Class for HTTP error handling.
 * Processes the passed exception and converts it to related HTTP exception
 * with readable message
 *
 * @author Lyska Lyudmila
 */
public class HTTPExceptionProvider {
    private static HTTPExceptionProvider _exceptionProvider;

    private final Map<String, String> _exceptions;

    static {
        _exceptionProvider = new HTTPExceptionProvider();
    }

    private HTTPExceptionProvider() {
        _exceptions = new HashMap<>();
        _exceptions.put("401", "Your user name or password is incorrect");
        _exceptions.put("404", "Page not found");
    }

    /**
     * Gets instance the HTTPExceptionProvider
     *
     * @return HTTPExceptionProvider.
     */
    public static HTTPExceptionProvider getInstance() {
        return _exceptionProvider;
    }

    /**
     * Processes the passed exception and gives the necessary message to the user.
     *
     * @param exception received exception
     */
    public void throwException(Throwable exception) {
        if (exception == null) {
            return;
        }

        String code = getCodeException(exception).orElseThrow(() -> new RuntimeException(exception));
        String message =  getMessageException(code).orElseThrow(() -> new HTTPException(exception));
        throw new HTTPException(message, exception);
    }

    private Optional<String> getCodeException(Throwable exception) {
        String message = exception.getMessage();

        int codeIndex = message.indexOf("code: ");

        if (codeIndex == -1) {
            return Optional.empty();
        }

        // find code
        message = message.substring(codeIndex, message.indexOf("for"));
        // delete all except digits
        return Optional.of(message.replaceAll("\\D", ""));
    }

    private Optional<String> getMessageException(String code) {
        for (Entry<String, String> exception : _exceptions.entrySet()) {
            if (exception.getKey().equals(code)) {
                return Optional.of(exception.getValue());
            }
        }
        return Optional.empty();
    }
}
