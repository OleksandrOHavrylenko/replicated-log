package com.distributed.master;

import com.distributed.commons.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    public String replicateToAll(final LogItem item, final int writeConcern) {
        Future<String> futureSec1 = replicateTo(secClient1, item);
        Future<String> futureSec2 = replicateTo(secClient2, item);
        log.info("replicateToAll executed");

        String response1 = "";
        String response2 = "";
        try {
            response1 = futureSec1.get();
            response2 = futureSec2.get();
        } catch (InterruptedException e) {
            log.error("RuntimeException occurred while replication", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException occurred while replication", e);
        }

        log.info("Response from replica {} {}: ", secClient1.getHost(), response1);
        log.info("Response from replica {} {}: ", secClient2.getHost(), response2);
        log.info("Message replicated to all secondaries.");

        return String.format("ACK %s",item.getMessage());
    }


    public Future<String> replicateTo(SecClient secClient, LogItem item) {
        return executor.submit(() -> secClient.replicateLog(item));
    }

}
