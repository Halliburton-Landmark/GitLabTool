package com.lgc.gitlabtool.git.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4jExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static Logger _logger = LogManager.getLogger(Log4jExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        _logger.error("Uncaught exception in thread: " + t.getName(), e);
    }

}
