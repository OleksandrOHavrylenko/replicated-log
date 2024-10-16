package com.distributed.master;

import com.distributed.commons.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class ReplicationService {

    private static final Logger log = LoggerFactory.getLogger(ReplicationService.class);

    private final int replicasNum;
    private final SecClient secClient1;
    private final SecClient secClient2;
    private final ExecutorService executor;

    public ReplicationService(@Value("${client.sec1.host}") final String sec1Host, @Value("${client.sec1.port}") final int sec1Port,
                              @Value("${client.sec2.host}") final String sec2Host, @Value("${client.sec2.port}") final int sec2Port,
                              @Value("${client.replicasNum}") final int replicasNum) {
        this.replicasNum = replicasNum;
        this.secClient1 = new SecClient(sec1Host, sec1Port);
        this.secClient2 = new SecClient(sec2Host, sec2Port);
        this.executor = Executors.newFixedThreadPool(replicasNum);
    }

    public String asyncReplicateToAll(final LogItem item, final int writeConcern) {
        CountDownLatch doneLatch = new CountDownLatch(Math.min(writeConcern, this.replicasNum));

        asyncReplicateTo(secClient1, item, doneLatch);
        asyncReplicateTo(secClient2, item, doneLatch);
        log.info("replicateToAll executed");

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            log.info("RuntimeException occurred while replication", e);
        }

        log.info("Message replicated to all secondaries.");

        return String.format("ACK %s",item.getMessage());
    }

    public void asyncReplicateTo(SecClient secClient, LogItem item, CountDownLatch doneLatch) {
        executor.execute(() -> secClient.asyncReplicateLog(item, doneLatch));
    }
}
