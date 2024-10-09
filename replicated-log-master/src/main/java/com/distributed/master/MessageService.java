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

    private final ReplicationService replicationService;


    public MessageService(final LogRepository logRepository, final ReplicationService replicationService) {
        this.logRepository = Objects.requireNonNull(logRepository);
        this.replicationService = Objects.requireNonNull(replicationService);
    }

    public String append(Message message) {
        logRepository.add(message);
        String responseMessage = replicationService.replicateToAll(message);
        return responseMessage;
    }

    public List<Message> list() {
        log.info("GET all messages: {}", logRepository.getAll());
        return logRepository.getAll();
    }
}
