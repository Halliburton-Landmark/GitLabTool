package com.ystrazhko.git.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 *
 * @author Lyska Lyudmila
 *
 */
public class ExceptionProvider {
    /** **/
    private static ExceptionProvider _exceptionProvider;

    /** **/
    private final Map<String, String> _exceptions;

    static {
        _exceptionProvider = new ExceptionProvider();
    }

    private ExceptionProvider() {
        _exceptions = new HashMap<>();
        _exceptions.put("401", "You entered a incorrect data!");
        _exceptions.put("404", "Page not found");
    }

    /**
     *
     *
     * @return
     */
    public static ExceptionProvider getInstance() {
        return _exceptionProvider;
    }

    /**
     *
     *
     * @param exception
     */
    public void throwException(Throwable exception) {
        if (exception == null) {
            return;
        }

        String code = getCodeException(exception);
        if (code == null) {
            return;
        }
        String message =  getMessageException(code);
        if (message == null) {
            throw new HTTPException(exception);
        }
        throw new HTTPException(message, exception);
    }

    private String getCodeException(Throwable exception) {
        String message = exception.getMessage();
        // find code
        message = message.substring(message.indexOf("code: "), message.indexOf("for"));
        // delete all except digits
        return message.replaceAll("\\D", "");
    }

    private String getMessageException(String code) {
        for (Entry<String, String> exception : _exceptions.entrySet()) {
            if (exception.getKey().equals(code)) {
                return exception.getValue();
            }
        }
        return null;
    }
}
