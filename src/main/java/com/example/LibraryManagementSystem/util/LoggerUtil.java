package com.example.LibraryManagementSystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    public static void info(String message) {
        logger.info("{} - {}", getCallingMethodName(), message);
    }

    public static void error(String message) {
        logger.error("{} - {}", getCallingMethodName(), message);
    }

    public static void debug(String message) {
        logger.debug("{} - {}", getCallingMethodName(), message);
    }

    private static String getCallingMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }
}
