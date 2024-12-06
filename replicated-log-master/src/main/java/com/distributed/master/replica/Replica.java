package com.distributed.master.replica;

import com.distributed.commons.LogItem;
import com.distributed.master.SecClient;
import com.distributed.master.heartbeat.ReplicaStatus;
import io.grpc.StatusRuntimeException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Replica {
    private static final Logger log = LoggerFactory.getLogger(Replica.class);

    @NotNull
    @NotBlank
    private String host;
    private int port;
    private SecClient secClient;
    private ReplicaStatus status = ReplicaStatus.UNHEALTHY;
    private AtomicInteger pingFail = new AtomicInteger(0);

    public Replica(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.secClient = new SecClient(host, port);
    }

    public void asyncSendMessage(@NotNull final LogItem item, final CountDownLatch replicationDone) {
        this.secClient.asyncReplicateLog(item, replicationDone);
    }

    public void ping() {
        try {
            this.secClient.syncPing(getPingTimeout());
            this.status = statusUp();
        } catch (StatusRuntimeException e) {
            log.warn("Ping failed with status: {}", e.getStatus());
            log.warn("Error while ping replica={}", this.host, e);
            pingFail.incrementAndGet();
            this.status = statusDown(pingFail);
        } catch (Exception e) {
            log.warn("Error while ping replica={}", this.host, e);
            pingFail.incrementAndGet();
            this.status = statusDown(pingFail);
        }
    }

    private ReplicaStatus statusUp() {
        pingFail.set(0);
        int current = this.status.ordinal();
        return ReplicaStatus.values()[Math.max(current - 1, 0)];
    }

    private ReplicaStatus statusDown(AtomicInteger pingFail) {
        if(pingFail.get() >= 3) {
            this.pingFail.set(0);
            int current = this.status.ordinal();
            return ReplicaStatus.values()[Math.min(current + 1, ReplicaStatus.values().length - 1)];
        }
        return this.status;
    }

    public ReplicaStatus getStatus() {
        return status;
    }

    private int getPingTimeout() {
        return getStatus().getPingTimeoutSec();
    }
}

