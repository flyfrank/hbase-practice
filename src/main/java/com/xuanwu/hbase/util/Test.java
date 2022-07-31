/*
 * Copyright (c) 2022年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.xuanwu.hbase.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author huyaoke
 * @date 2022/7/28
 */
public class Test {
    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        long timestamp = Long.MAX_VALUE - time;
        System.out.println(Long.MAX_VALUE);
        System.out.println(time);
        String result = StringUtils.leftPad(String.valueOf(timestamp), String.valueOf(Long.MAX_VALUE).length());
        System.out.println(result);
    }
}
