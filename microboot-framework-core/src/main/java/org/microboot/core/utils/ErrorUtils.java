package org.microboot.core.utils;

/**
 * @author 胡鹏
 */
public class ErrorUtils {

    public static String getErrorMsg(Throwable e) {
        if (e.getCause() == null) {
            return e.getMessage();
        }
        return getErrorMsg(e.getCause());
    }

    public static Throwable getCause(Throwable e) {
        if (e.getCause() == null) {
            return e;
        }
        return getCause(e.getCause());
    }
}
