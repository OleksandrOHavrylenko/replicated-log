package com.distributed.master.replica;

import com.distributed.commons.LogItem;
import com.distributed.master.IdGenerator;
import com.distributed.master.RestoreService;
import com.distributed.master.SecClient;
import com.distributed.master.heartbeat.ReplicaStatus;
import io.grpc.StatusRuntimeException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Replica {
    private static final Logger log = LoggerFactory.getLogger(Replica.class);

    @NotNull
    @NotBlank
    private String host;
    private int port;
    private final SecClient secClient;
    private final RestoreService restoreService;
    private ReplicaStatus status = ReplicaStatus.UNHEALTHY;
    private AtomicInteger pingFail = new AtomicInteger(0);

    public Replica(final String host, final int port, final String name, final RestoreService restoreService) {
        this.host = host;
        this.port = port;
        this.secClient = new SecClient(host, port, name);
        this.restoreService = Objects.requireNonNull(restoreService);
    }

    public void asyncSendMessage(@NotNull final LogItem item, final CountDownLatch replicationDone, boolean waitForReady) {
        this.secClient.asyncReplicateLog(List.of(item), replicationDone, waitForReady);
    }

    public void restore(@NotNull final List<LogItem> items) {
        this.secClient.asyncReplicateLog(items, null, false);
    }

    private void restore(long lastId) {
        if (lastId < IdGenerator.getLast()) {
            restoreService.restore(lastId, this);
        }
    }

    public void ping() {
        try {
            long lastId = this.secClient.syncPing(getPingTimeout());
            this.status = statusUp();
            restore(lastId);
        } catch (StatusRuntimeException e) {
            log.warn("Ping failed with status: {}", e.getStatus());
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

