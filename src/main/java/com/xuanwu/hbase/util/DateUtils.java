/*
 * Copyright (c) 2022年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.xuanwu.hbase.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author huyaoke
 * @date 2022/7/27
 */
public class DateUtils {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDateStr() {
        LocalDate localDate = LocalDate.now();
        return localDate.format(DATE_FORMATTER);
    }

    public static long getTime(String dataTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dataTime, DATE_TIME_FORMATTER);
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}
