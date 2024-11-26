package com.distributed.secondary;

import java.util.concurrent.atomic.AtomicLong;

public class IdChecker {
    private final static AtomicLong counter = new AtomicLong(0L);

    public static Long getLast() {
        return counter.get();
    }

    public static long getNext() {
        return counter.get() + 1L;
    }

    public static long incrementAndGet() {
        return counter.incrementAndGet();
    }
}
