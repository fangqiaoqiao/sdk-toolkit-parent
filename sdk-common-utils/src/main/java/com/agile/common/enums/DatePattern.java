package com.agile.common.enums;

/**
 * 常用日期时间格式枚举
 */
public enum DatePattern {
    /**
     * 年-月-日 时:分:秒
     */
    DATETIME_PATTERN("yyyy-MM-dd HH:mm:ss"),
    /**
     * 年-月-日
     */
    DATE_PATTERN("yyyy-MM-dd"),
    /**
     * 时:分:秒
     */
    TIME_PATTERN("HH:mm:ss"),
    /**
     * 年月日时分秒（紧凑）
     */
    DATETIME_COMPACT("yyyyMMddHHmmss"),
    /**
     * 年月日（紧凑）
     */
    DATE_COMPACT("yyyyMMdd"),
    /**
     * 时分秒（紧凑）
     */
    TIME_COMPACT("HHmmss"),
    /**
     * 年-月-日 时:分
     */
    DATETIME_MINUTE("yyyy-MM-dd HH:mm"),
    /**
     * 年/月/日 时:分:秒
     */
    DATETIME_SLASH("yyyy/MM/dd HH:mm:ss"),
    /**
     * 年/月/日
     */
    DATE_SLASH("yyyy/MM/dd");

    private final String pattern;

    DatePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}