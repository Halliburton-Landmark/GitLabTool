package com.lgc.gitlabtool.git.util;

import com.lgc.gitlabtool.git.services.ServiceProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class that contains actions that will be invoked before application shut downing
 *
 * @author Igor Khlaponin
 */
public class FinishUtil {

    private static final Logger _logger = LogManager.getLogger(FinishUtil.class);

    /**
     * Set of actions that will be invoked before application shut downing
     */
    public static void shutdown() {
        ServiceProvider.getInstance().stop();
        _logger.debug(System.lineSeparator() +
                "<<<< Exit application >>>>" + System.lineSeparator());
    }
}
