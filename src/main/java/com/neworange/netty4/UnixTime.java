package com.neworange.netty4;

import java.util.Date;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/15 15:13
 * @description
 */
public class UnixTime {
    private final long value;

    public UnixTime() {
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }

    public UnixTime(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String toString() {
        return new Date((value() - 2208988800L) * 1000L).toString();
    }
}
