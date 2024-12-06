package com.distributed.master.heartbeat;

public enum ReplicaStatus {
    HEALTHY(3, 100),
    SUSPECTED(5, 500),
    UNHEALTHY(10, -1);

    private final int pingTimeoutSec;
    private final int retryTimeOutMillis;

    ReplicaStatus(int pingTimeoutSec, int retryTimeOutSec) {
        this.pingTimeoutSec = pingTimeoutSec;
        this.retryTimeOutMillis = retryTimeOutSec;
    }

    public int getPingTimeoutSec() {
        return pingTimeoutSec;
    }

    public int getRetryTimeOutMillis() {
        return retryTimeOutMillis;
    }
}
