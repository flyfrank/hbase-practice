package com.xuanwu.hbase.util;

/**
 * @author Yaoke.Hu
 * @date 2020-06-16 16:21-13
 * <p>
 * 雪花算法
 * 以twitter提供的算法为蓝本
 */
public class SnowflakeGenerator {
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private static final long SEQUENCE_BITS = 12L;

    private static final long WORKER_ID_LEFT_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 开始时间戳：2022-05-30 19:08:48
     */
    private final long twepoch = 1653908928000L;

    /**
     * 机器ID， 5个bit，取值 0~31
     */
    private final long workerId;

    /**
     * 机房ID，5个bit，取值 0~31
     */
    private final long dateCenterId;

    /**
     * 序列号，12个bit， 最多就是同一毫秒生成4096个
     */
    private long sequence = 0L;

    /**
     * 最近一次时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 系统时间被回拨，超过临界值时报错，当前1s, 单位ms
     */
    private static final long MAX_WAIT_TIME = 1000L;

    public SnowflakeGenerator(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("data center Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dateCenterId = dataCenterId;
    }

    public long nextId() {
        synchronized (SnowflakeGenerator.class) {
            long timestamp = timeGen();
            // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
            if (timestamp < lastTimestamp) {
                long gap = lastTimestamp - timestamp;
                if (gap > MAX_WAIT_TIME) {
                    throw new RuntimeException(
                        String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
                } else {
                    // 空转直到时间大于回拨前的时间
                    for (; ; ) {
                        if (timeGen() > lastTimestamp) {
                            break;
                        }
                    }
                }
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            return generateId(timestamp);
        }
    }

    private long generateId(long timestamp) {
        return ((timestamp - twepoch) << TIMESTAMP_LEFT_SHIFT)
            | (dateCenterId << DATA_CENTER_ID_LEFT_SHIFT)
            | (workerId << WORKER_ID_LEFT_SHIFT)
            | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static long getMaxWorkerId() {
        return MAX_WORKER_ID;
    }

    public static long getMaxDataCenterId() {
        return MAX_DATA_CENTER_ID;
    }
}
