package com.lgc.solutiontool.git.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 *
 * @author Lyudmila Lyska
 */
public class FeedbackUtil {

    /**
     *
     *
     * @param <U>
     * @param onError
     * @param progress
     * @param message
     */
    public static <T, U> void sendError(BiConsumer<T, U> onError, T progress, U message) {
        if (onError != null) {
            onError.accept(progress, message);
        }
    }

    /**
     *
     *
     * @param onSuccess
     * @param progress
     */
    public static <T> void sendSuccess(Consumer<T> onSuccess, T progress) {
        if (onSuccess != null) {
            onSuccess.accept(progress);
        }
    }

}
