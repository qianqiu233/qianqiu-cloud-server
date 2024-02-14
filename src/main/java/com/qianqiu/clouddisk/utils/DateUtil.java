package com.qianqiu.clouddisk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    // 常量格式：年月日
    public static final String YMD = "yyyy-MM-dd";

    // 常量格式：小时分钟秒钟
    public static final String HMS = "HH:mm:ss";

    // 格式化日期
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    // 计算日期之间的差值（毫秒）
    public static long dateDifference(Date date1, Date date2) {
        return date2.getTime() - date1.getTime();
    }

    // 毫秒转格式化日期
    public static String convertMillisecondsToFormattedDate(long milliseconds, String pattern) {
        Date date = new Date(milliseconds);
        return format(date, pattern);
    }

    //字符串转日期
    public static Date parseStringToDate(String dateString, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            // Attempt to parse the date string
            return sdf.parse(dateString);
        } catch (ParseException e) {
            // If parsing fails, throw a more informative exception
            throw new IllegalArgumentException("解析日期字符串出现错误，可能不是一个正确的格式: " + dateString + " with pattern: " + pattern, e);
        }
    }

}
