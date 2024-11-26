package com.distributed.master.replica;

import com.distributed.master.heartbeat.ReplicaStatus;
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

    private final ReplicaRepository replicaRepository;
    private final ScheduledExecutorService executorService;

    public HeartBeatService(final ReplicaRepository replicaRepository) {
        this.replicaRepository = Objects.requireNonNull(replicaRepository);
        this.executorService = Executors.newScheduledThreadPool(replicaRepository.getReplicasCount() + 1);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        List<Replica> replicas = replicaRepository.getReplicas();
        for (Replica replica : replicas) {
            executorService.scheduleWithFixedDelay(replica::ping, 0, 5, TimeUnit.SECONDS);
        }

        Runnable logStatuses = () -> {
            List<ReplicaStatus> statuses = replicaRepository.getReplicas().stream().map(Replica::getStatus).collect(Collectors.toList());
            log.info("Replica statuses: {}", statuses);
        };
        executorService.scheduleWithFixedDelay(logStatuses, 0, 7, TimeUnit.SECONDS);

        log.info("Health service started");
    }

    @PreDestroy
    public void shutDown() {
        if(executorService != null){
            this.executorService.shutdown();
        }
    }
}
