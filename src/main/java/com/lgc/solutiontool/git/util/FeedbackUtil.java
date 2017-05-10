package com.lgc.solutiontool.git.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The class responsible for sending messages to the user about the progress of the operation.
 *
 * @author Lyudmila Lyska
 */
public class FeedbackUtil {

    /**
     * Report an error during the operation.
     *
     * @param onError  method for tracking the unsuccessful operations.
     * @param progress a percentage of progress
     * @param message  error message
     */
    public static <T, U> void sendError(BiConsumer<T, U> onError, T progress, U message) {
        if (onError != null) {
            onError.accept(progress, message);
        }
    }

    /**
     * Report about successful operation.
     *
     * @param onSuccess method for tracking the successful operations.
     * @param progress  a percentage of progress
     * @param project   cloned project
     */
    public static <T, U> void sendSuccess(BiConsumer<T, U> onSuccess, T progress, U project) {
        if (onSuccess != null) {
            onSuccess.accept(progress, project);
        }
    }

    /**
     * Report about successful operation.
     *
     * @param onSuccess method for tracking the successful operations.
     * @param progress  a percentage of progress
     */
    public static <T> void sendSuccess(Consumer<T> onSuccess, T progress) {
        if (onSuccess != null) {
            onSuccess.accept(progress);
        }
    }

}
