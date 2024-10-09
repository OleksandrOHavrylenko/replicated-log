package com.distributed.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final LogRepository logRepository;
    private final SecClient secClient1;
    private final SecClient secClient2;

    public MessageService(final LogRepository logRepository) {
        this.logRepository = Objects.requireNonNull(logRepository);
        this.secClient1 = new SecClient("secondary1", 9091);
        this.secClient2 = new SecClient("secondary2", 9091);
    }

    public String append(Message message) {
        logRepository.add(message);
        String response1 = secClient1.replicateLog(message);
        String response2 = secClient2.replicateLog(message);
        log.info("Response from sec1 {}: ", response1);
        log.info("Response from sec2 {}: ", response2);
        log.info("Message replicated to secondaries.");
        return "OK " + message.getMessage();
    }

    public List<Message> list() {
        log.info("GET all messages.");
        return logRepository.getAll();
    }
}
