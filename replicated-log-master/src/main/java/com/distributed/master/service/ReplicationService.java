package com.distributed.master.service;

import com.distributed.commons.model.LogItem;
import com.distributed.master.repository.LogRepository;
import com.distributed.master.exceptions.NoQuorumException;
import com.distributed.master.replica.Replica;
import com.distributed.master.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ReplicationService {

    private static final Logger log = LoggerFactory.getLogger(ReplicationService.class);
    public static final int MASTER_NODE = 1;

    private final ReplicaService replicaService;
    private final LogRepository logRepository;
    private final ExecutorService executor;

    public ReplicationService(final ReplicaService replicaService, final LogRepository logRepository) {
        this.replicaService = Objects.requireNonNull(replicaService);
        this.logRepository = Objects.requireNonNull(logRepository);
        this.executor = Executors.newFixedThreadPool(replicaService.getReplicasCount());
    }

    public String replicateToAll(final String message, final int writeConcern) {
        checkQuorum();

        LogItem item = new LogItem(IdGenerator.next(), message);
        logRepository.add(item);

        CountDownLatch writeConcernLatch = new CountDownLatch(Math.min(writeConcern - MASTER_NODE, replicaService.getReplicasCount()));

        for (Replica replica : replicaService.getReplicas()) {
            executor.submit(() -> replica.asyncSendMessage(item, writeConcernLatch, (writeConcern - MASTER_NODE) == replicaService.getReplicasCount()));
        }

        log.info("replicateToAll executed");

        try {
            writeConcernLatch.await();
        } catch (InterruptedException e) {
            log.info("RuntimeException occurred while replication", e);
        }

        return String.format("ACK %s", item.getMessage());
    }

    private void checkQuorum() {
        long availableNodes = replicaService.getAliveReplicasCount() + MASTER_NODE;
        long totalNodes = replicaService.getReplicasCount() + MASTER_NODE;
        if (availableNodes < (totalNodes + 1) / 2) {
            throw new NoQuorumException(availableNodes, totalNodes);
        }
    }
}
