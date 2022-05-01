package org.microboot.core.utils;

import java.text.DecimalFormat;

/**
 * @author 胡鹏
 */
public class TimeUtils {

    private static final DecimalFormat decimalFormat = new DecimalFormat("00");

    /**
     * 格式化时间：HH:mm:ss
     *
     * @param start
     * @param end
     * @return
     */
    public static String getDiffTime(long start, long end) {
        long hh = 1000 * 60 * 60;
        long mm = 1000 * 60;
        long ss = 1000;
        long diff = end - start;
        // 计算差多少小时
        long hour = diff / hh;
        // 计算差多少分钟
        long minute = diff % hh / mm;
        // 计算差多少秒
        long seconds = diff % hh % mm / ss;
        return decimalFormat.format(hour) + ":" + decimalFormat.format(minute) + ":" + decimalFormat.format(seconds);
    }
}
