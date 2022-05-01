package org.microboot.core.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

/**
 * @author 胡鹏
 */
public class ProcessUtil {

    private static final Logger logger = LogManager.getLogger(ProcessUtil.class);

    private interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        long GetProcessId(Long hProcess);
    }

    /**
     * 获取进程id
     *
     * @return
     */
    public static String getProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }


    /**
     * 获取进程id
     *
     * @param process
     * @return
     */
    public static String getProcessId(Process process) {
        long pid = -1;
        Field field;
        if (Platform.isWindows()) {
            try {
                field = process.getClass().getDeclaredField("handle");
                field.setAccessible(true);
                pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(process));
            } catch (Throwable e) {
                LoggerUtils.error(logger, e);
            }
        } else if (Platform.isLinux() || Platform.isAIX()) {
            try {
                Class<?> clazz = Class.forName("java.lang.UNIXProcess");
                field = clazz.getDeclaredField("pid");
                field.setAccessible(true);
                pid = (Long) field.get(process);
            } catch (Throwable e) {
                LoggerUtils.error(logger, e);
            }
        }
        return String.valueOf(pid);
    }

    /**
     * 杀死进程
     *
     * @param pid
     */
    public static void killProcess(String pid) {
        if (StringUtils.isBlank(pid) || StringUtils.equals(pid, "-1")) {
            return;
        }
        Process process = null;
        try {
            String command = "";
            if (Platform.isWindows()) {
                command = "cmd.exe /c taskkill /F /PID " + pid + " /T";
            } else if (Platform.isLinux() || Platform.isAIX()) {
                command = "kill -9 " + pid;
            }
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (Throwable e) {
            LoggerUtils.error(logger, e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
