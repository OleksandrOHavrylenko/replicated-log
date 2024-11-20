package com.distributed.master;

import com.distributed.commons.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ReplicationService {

    private static final Logger log = LoggerFactory.getLogger(ReplicationService.class);

    private final List<SecClient> secClients;
    private final ExecutorService executor;

    public ReplicationService(@Value("${client.sec1.host}") final String sec1Host, @Value("${client.sec1.port}") final int sec1Port,
                              @Value("${client.sec2.host}") final String sec2Host, @Value("${client.sec2.port}") final int sec2Port) {

        this.secClients = Arrays.asList(new SecClient(sec1Host, sec1Port), new SecClient(sec2Host, sec2Port));
        this.executor = Executors.newFixedThreadPool(secClients.size());
    }

    public String replicateToAll(final LogItem item, final int writeConcern) {
        CountDownLatch writeConcernLatch = new CountDownLatch(Math.min(writeConcern, secClients.size()));

        for (SecClient secClient : secClients) {
            executor.execute(() -> secClient.asyncReplicateLog(item, writeConcernLatch));
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
