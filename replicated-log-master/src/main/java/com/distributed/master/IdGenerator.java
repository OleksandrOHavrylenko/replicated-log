package com.distributed.master;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private final static AtomicLong counter = new AtomicLong();

    public static Long next() {
        return counter.getAndIncrement();
    }
}