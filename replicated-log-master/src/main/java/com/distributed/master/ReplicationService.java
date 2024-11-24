package com.distributed.master;

import com.distributed.commons.LogItem;
import com.distributed.master.replica.Replica;
import com.distributed.master.replica.ReplicaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ReplicationService {

    private static final Logger log = LoggerFactory.getLogger(ReplicationService.class);

    private final ReplicaRepository replicaRepository;
    private final ExecutorService executor;

    public ReplicationService(final ReplicaRepository replicaRepository) {
        this.replicaRepository = replicaRepository;
        this.executor = Executors.newFixedThreadPool(replicaRepository.getReplicasCount());
    }

    public String replicateToAll(final LogItem item, final int writeConcern) {
        CountDownLatch writeConcernLatch = new CountDownLatch(Math.min(writeConcern, replicaRepository.getReplicasCount()));

        for (Replica replica : replicaRepository.getReplicas()) {
            executor.submit(() -> replica.asyncSendMessage(item, writeConcernLatch));
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
