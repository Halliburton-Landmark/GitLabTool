package com.lgc.gitlabtool.git.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The class responsible for performing null check before executing function
 *
 * @author Lyudmila Lyska
 * @author Yevhen Strazhko
 */
public class NullCheckUtil {

    /**
     * Executes BiConsumer if it is not null.
     *
     * @param biConsumer function to execute
     * @param t first param
     * @param u second param
     */
    public static <T, U> void acceptBiConsumer(BiConsumer<T, U> biConsumer, T t, U u) {
        if (biConsumer != null) {
            biConsumer.accept(t, u);
        }
    }

    /**
     * Executes consumer if it is not null
     *
     * @param consumer consumer to execute
     * @param arg consumer arg
     */
    public static <T> void acceptConsumer(Consumer<T> consumer, T arg) {
        if (consumer != null) {
            consumer.accept(arg);
        }
    }

}
