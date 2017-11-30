package com.lgc.gitlabtool.git.external;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides possibility to build new {@link ThreadFactory} based on different settings.
 *
 * @author Igor Khlaponin
 */
public class ThreadFactoryBuilder {

    private ThreadFactory _threadFactory;
    private String _namingPattern;
    private Boolean _isDaemon = false;
    private Integer _priority = Thread.NORM_PRIORITY;
    private Thread.UncaughtExceptionHandler _exceptionHandler;

    /**
     * Set new thread factory to the builder
     * @param threadFactory - instance of {@link ThreadFactory}
     * @return ThreadFactoryBuilder
     * @throws NullPointerException if {@code threadFactory} is equals to null
     */
    public ThreadFactoryBuilder setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("ThreadFactory cannot be null");
        }
        this._threadFactory = threadFactory;
        return this;
    }

    /**
     * Set the naming pattern for thread factory
     *
     * @param namingPattern - the naming pattern
     * @return ThreadFactoryBuilder
     * @throws NullPointerException if {@code namingPattern} is equals to null
     */
    public ThreadFactoryBuilder setNamingPattern(String namingPattern) {
        if (namingPattern == null) {
            throw new NullPointerException("Naming pattern cannot be null");
        }
        this._namingPattern = namingPattern;
        return this;
    }

    /**
     * Set the flag that shows if threads in {@link ThreadFactory} should be daemons
     * @param isDaemon {@code true} if threads should be daemons or {@code false} otherwise
     * @return ThreadFactoryBuilder
     */
    public ThreadFactoryBuilder setDaemon(Boolean isDaemon) {
        this._isDaemon = isDaemon;
        return this;
    }

    /**
     * Set the priority of created threads in {@link ThreadFactory}
     *
     * @param priority - the priority
     * @return ThreadFactoryBuilder
     * @throws IllegalArgumentException if set priority is lower than {@link Thread#MIN_PRIORITY}
     *         or higher than {@link Thread#MAX_PRIORITY}
     */
    public ThreadFactoryBuilder setPriority(Integer priority) {
        String messagePattern = "Thread priority %s must be";
        if (priority < Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException(String.format(messagePattern + " >= %s",
                    priority, Thread.MIN_PRIORITY));
        }
        if (priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(String.format(messagePattern + " <= %s",
                    priority, Thread.MAX_PRIORITY));
        }
        this._priority = priority;
        return this;
    }

    /**
     * Set UncaughtExceptionHandler for created threads in {@link ThreadFactory}
     *
     * @param exceptionHandler - the instance of {@link java.lang.Thread.UncaughtExceptionHandler}
     * @return ThreadFactoryBuilder
     * @throws NullPointerException if {@code exceptionHandler} is equals to null
     */
    public ThreadFactoryBuilder setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        if (exceptionHandler == null) {
            throw new NullPointerException("Exception handler cannot be null");
        }
        this._exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Builds a new {@link ThreadFactory} based on builder settings
     *
     * @return new instance of ThreadFactory with set parameters
     */
    public ThreadFactory build() {
        return build(this);
    }

    private static ThreadFactory build(ThreadFactoryBuilder builder) {
        final AtomicLong counter = new AtomicLong(0);
        final String namingPattern = builder._namingPattern;
        final Boolean isDaemon = builder._isDaemon;
        final Integer priority = builder._priority;
        final ThreadFactory factory = builder._threadFactory != null
                ? builder._threadFactory
                : Executors.defaultThreadFactory();
        final Thread.UncaughtExceptionHandler exceptionHandler = builder._exceptionHandler;

        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = factory.newThread(runnable);
                if (namingPattern != null) {
                    thread.setName(String.format(namingPattern, counter.getAndIncrement()));
                }
                thread.setDaemon(isDaemon);
                thread.setPriority(priority);
                if (exceptionHandler != null) {
                    thread.setUncaughtExceptionHandler(exceptionHandler);
                }
                return thread;
            }
        };
    }
}
