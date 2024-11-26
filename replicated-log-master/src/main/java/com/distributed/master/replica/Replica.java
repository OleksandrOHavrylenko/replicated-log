package com.distributed.master.replica;

import com.distributed.commons.LogItem;
import com.distributed.master.SecClient;
import com.distributed.master.heartbeat.ReplicaStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.concurrent.CountDownLatch;

public class Replica {
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
        this.secClient.syncPing();
    }
}

