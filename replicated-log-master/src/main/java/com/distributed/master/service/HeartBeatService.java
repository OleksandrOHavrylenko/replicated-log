package com.distributed.master.service;

import com.distributed.master.heartbeat.ReplicaStatus;
import com.distributed.master.replica.Replica;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HeartBeatService {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatService.class);

    private final ReplicaService replicaService;
    private final ScheduledExecutorService executorService;

    public HeartBeatService(final ReplicaService replicaService) {
        this.replicaService = Objects.requireNonNull(replicaService);
        this.executorService = Executors.newScheduledThreadPool(replicaService.getReplicasCount() + 1);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        List<Replica> replicas = replicaService.getReplicas();
        for (Replica replica : replicas) {
            executorService.scheduleWithFixedDelay(replica::ping, 0, 3, TimeUnit.SECONDS);
        }

        Runnable logStatuses = () -> {
            List<ReplicaStatus> statuses = replicaService.getReplicas().stream().map(Replica::getStatus).collect(Collectors.toList());
            log.info("Replicas statuses: {}", statuses);
        };
        executorService.scheduleWithFixedDelay(logStatuses, 0, 3, TimeUnit.SECONDS);

        log.info("Health service started");
    }

    @PreDestroy
    public void shutDown() {
        if(executorService != null){
            this.executorService.shutdown();
        }
    }
}
