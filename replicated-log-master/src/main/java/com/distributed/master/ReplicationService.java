package com.distributed.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ReplicationService {

    private static final Logger log = LoggerFactory.getLogger(ReplicationService.class);

    private int secNumber = 2;
    private final SecClient secClient1;
    private final SecClient secClient2;
    private ExecutorService executor;

    public ReplicationService() {
        this.secClient1 = new SecClient("secondary1", 9091);
        this.secClient2 = new SecClient("secondary2", 9091);
        this.executor = Executors.newFixedThreadPool(secNumber);
    }

    public String replicateToAll(Message message) {
        Future<String> futureSec1 = replicateTo(secClient1, message);
        Future<String> futureSec2 = replicateTo(secClient2, message);
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

        log.info("Response from sec1 {}: ", response1);
        log.info("Response from sec2 {}: ", response2);
        log.info("Message replicated to all secondaries.");

        return "OK " + message.getMessage();
    }


    public Future<String> replicateTo(SecClient secClient, Message message) {
        return executor.submit(() -> secClient.replicateLog(message));
    }

}
