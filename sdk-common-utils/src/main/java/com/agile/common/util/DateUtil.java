package com.agile.common.util;

import com.agile.common.enums.DatePattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期时间工具类，提供格式化和解析功能。
 * 推荐使用 {@link DatePattern} 枚举定义格式，避免魔法字符串。
 */
public final class DateUtil {

    private DateUtil() {

    }

    // -------------------------------------------------------------------------
    // 格式化当前时间
    // -------------------------------------------------------------------------

    /**
     * 获取当前时间按照指定格式字符串的表示
     *
     * @param pattern 格式字符串（如 "yyyy-MM-dd HH:mm:ss"）
     * @return 格式化后的当前时间字符串
     */
    public static String formatNow(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取当前时间按照指定格式枚举的表示
     *
     * @param pattern 日期格式枚举
     * @return 格式化后的当前时间字符串
     */
    public static String formatNow(DatePattern pattern) {
        return formatNow(pattern.getPattern());
    }

    // -------------------------------------------------------------------------
    // 格式化指定时间
    // -------------------------------------------------------------------------

    /**
     * 格式化指定时间
     *
     * @param dateTime 待格式化的时间
     * @param pattern  格式字符串
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化指定时间
     *
     * @param dateTime 待格式化的时间
     * @param pattern  日期格式枚举
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, DatePattern pattern) {
        return format(dateTime, pattern.getPattern());
    }

    // -------------------------------------------------------------------------
    // 解析字符串为 LocalDateTime
    // -------------------------------------------------------------------------

    /**
     * 将字符串解析为 LocalDateTime（字符串必须包含日期和时间）
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     格式字符串
     * @return LocalDateTime 对象
     * @throws DateTimeParseException 如果解析失败
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将字符串解析为 LocalDateTime
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     日期格式枚举
     * @return LocalDateTime 对象
     * @throws DateTimeParseException 如果解析失败
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, DatePattern pattern) {
        return parseDateTime(dateTimeStr, pattern.getPattern());
    }

    // -------------------------------------------------------------------------
    // 便捷获取常用格式的当前时间字符串
    // -------------------------------------------------------------------------

    /**
     * 获取当前日期时间字符串（格式：yyyy-MM-dd HH:mm:ss）
     */
    public static String nowDateTime() {
        return formatNow(DatePattern.DATETIME_PATTERN);
    }

    /**
     * 获取当前日期字符串（格式：yyyy-MM-dd）
     */
    public static String nowDate() {
        return formatNow(DatePattern.DATE_PATTERN);
    }

    /**
     * 获取当前时间字符串（格式：HH:mm:ss）
     */
    public static String nowTime() {
        return formatNow(DatePattern.TIME_PATTERN);
    }

    /**
     * 获取当前紧凑日期时间字符串（格式：yyyyMMddHHmmss）
     */
    public static String nowCompactDateTime() {
        return formatNow(DatePattern.DATETIME_COMPACT);
    }
}