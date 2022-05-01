package org.microboot.core.utils;

import org.apache.logging.log4j.Logger;
import org.microboot.core.func.FuncV1;

/**
 * @author 胡鹏
 */
public class LoggerUtils {

    public static String error(Logger logger, Throwable e) {
        return printLog(e, msg -> logger.error(msg));
    }

    public static String warn(Logger logger, Throwable e) {
        return printLog(e, msg -> logger.warn(msg));
    }

    private static String printLog(Throwable e, FuncV1<String> func) {
        StringBuilder message = new StringBuilder(e.toString() + "\r\n");
        for (StackTraceElement traceElement : e.getStackTrace()) {
            message.append("\tat ").append(traceElement).append("\r\n");
        }
        try {
            func.func(message.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message.toString();
    }
}
