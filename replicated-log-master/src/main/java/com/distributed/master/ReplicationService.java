package com.distributed.master;

import com.distributed.commons.LogItem;
import com.distributed.master.exceptions.NoQuorumException;
import com.distributed.master.replica.Replica;
import com.distributed.master.replica.ReplicaRepository;
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

    private final ReplicaRepository replicaRepository;
    private final LogRepository logRepository;
    private final ExecutorService executor;

    public ReplicationService(final ReplicaRepository replicaRepository, final LogRepository logRepository) {
        this.replicaRepository = Objects.requireNonNull(replicaRepository);
        this.logRepository = Objects.requireNonNull(logRepository);
        this.executor = Executors.newFixedThreadPool(replicaRepository.getReplicasCount());
    }

    public String replicateToAll(final String message, final int writeConcern) {
        long availableNodes = replicaRepository.getAliveReplicasCount() + MASTER_NODE;
        long totalNodes = replicaRepository.getReplicasCount() + MASTER_NODE;
        if(availableNodes < (totalNodes + 1) / 2  ) {
            throw new NoQuorumException(availableNodes, totalNodes);
        }

        LogItem item = new LogItem(IdGenerator.next(), message);
        logRepository.add(item);

        CountDownLatch writeConcernLatch = new CountDownLatch(Math.min(writeConcern - MASTER_NODE, replicaRepository.getReplicasCount()));

        for (Replica replica : replicaRepository.getReplicas()) {
            executor.submit(() -> replica.asyncSendMessage(item, writeConcernLatch, (writeConcern - MASTER_NODE) == replicaRepository.getReplicasCount()));
        }

        log.info("replicateToAll executed");

        try {
            writeConcernLatch.await();
        } catch (InterruptedException e) {
            log.info("RuntimeException occurred while replication", e);
        }

        return String.format("ACK %s",item.getMessage());
    }
}
