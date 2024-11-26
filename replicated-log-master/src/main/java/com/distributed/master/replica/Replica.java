package com.distributed.master.replica;

import com.distributed.commons.LogItem;
import com.distributed.master.SecClient;
import com.distributed.master.heartbeat.ReplicaStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class Replica {
    private static final Logger log = LoggerFactory.getLogger(Replica.class);

    @NotNull
    @NotBlank
    private String host;
    private int port;
    private SecClient secClient;
    private ReplicaStatus status = ReplicaStatus.UNHEALTHY;

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
            this.secClient.syncPing();
            this.status = statusUp();
        } catch (Exception e) {
            log.warn("Error while ping replica={}", this.host, e);
            this.status = statusDown();
        }
    }

    private ReplicaStatus statusUp() {
        int current= this.status.ordinal();
        return ReplicaStatus.values()[Math.max(current - 1, 0)];
    }

    private ReplicaStatus statusDown() {
        int current= this.status.ordinal();
        return ReplicaStatus.values()[Math.min(current + 1, ReplicaStatus.values().length - 1)];
    }

    public ReplicaStatus getStatus() {
        return status;
    }
}

