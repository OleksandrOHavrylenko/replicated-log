package com.distributed.master.heartbeat;

import java.util.concurrent.atomic.AtomicInteger;

import static com.distributed.master.heartbeat.ReplicaStatus.*;

public class StatusHandler {
    private final ReplicaStatus[] statuses = {UNHEALTHY, SUSPECTED, HEALTHY};
    private final AtomicInteger currentStatus = new AtomicInteger(0);
    private final AtomicInteger pingFail = new AtomicInteger(0);

    public ReplicaStatus getStatus() {
        return statuses[currentStatus.get()];
    }

    public void statusUp() {
        pingFail.set(0);
        currentStatus.set(Math.min(currentStatus.get() + 1, statuses.length - 1));
    }

    public void statusDown() {
        pingFail.incrementAndGet();
        if (pingFail.get() >= 3) {
            this.pingFail.set(0);
            currentStatus.set(Math.max(currentStatus.get() - 1, 0));
        }
    }
}
