package com.distributed.master.replica;

import com.distributed.commons.LogItem;
import com.distributed.master.IdGenerator;
import com.distributed.master.RestoreService;
import com.distributed.master.SecClient;
import com.distributed.master.heartbeat.ReplicaStatus;
import com.distributed.master.heartbeat.StatusHandler;
import io.grpc.StatusRuntimeException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class Replica {
    private static final Logger log = LoggerFactory.getLogger(Replica.class);

    @NotNull
    @NotBlank
    private String host;
    private int port;
    private final SecClient secClient;
    private final RestoreService restoreService;
    private final StatusHandler statusHandler = new StatusHandler();

    public Replica(final String host, final int port, final String name, final RestoreService restoreService) {
        this.host = host;
        this.port = port;
        this.secClient = new SecClient(host, port, name);
        this.restoreService = Objects.requireNonNull(restoreService);
    }

    public void asyncSendMessage(@NotNull final LogItem item, final CountDownLatch replicationDone, boolean waitForReady) {
        this.secClient.asyncReplicateLog(List.of(item), replicationDone, waitForReady, this.statusHandler.getStatus());
    }

    public void restore(@NotNull final List<LogItem> items) {
        this.secClient.asyncReplicateLog(items, null, false, this.statusHandler.getStatus());
    }

    private void checkForRestore(long lastId) {
        if (lastId < IdGenerator.getLast()) {
            restoreService.restore(lastId, this);
        }
    }

    public void ping() {
        try {
            long lastId = this.secClient.syncPing(getPingTimeout());
            statusHandler.statusUp();
            checkForRestore(lastId);
        } catch (StatusRuntimeException e) {
            log.warn("Ping failed with status: {}", e.getStatus());
            statusHandler.statusDown();;
        } catch (Exception e) {
            log.warn("Error while ping replica={}", this.host, e);
            statusHandler.statusDown();
        }
    }

    public ReplicaStatus getStatus() {
        return statusHandler.getStatus();
    }

    private int getPingTimeout() {
        return getStatus().getPingTimeoutSec();
    }
}

