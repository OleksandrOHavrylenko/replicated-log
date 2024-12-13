package com.distributed.master;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private final static AtomicLong counter = new AtomicLong(0L);

    public static Long next() {
        return counter.incrementAndGet();
    }

    public static long getLast() {
        return counter.get();
    }
}