package com.distributed.master;

import com.distributed.commons.LogItem;
import com.distributed.commons.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        LogItem item = new LogItem(IdGenerator.next(), message.getMessage());

        logRepository.add(item);

        String responseMessage = replicationService.replicateToAll(item);
        return responseMessage;
    }

    public List<String> list() {
        log.info("GET all messages: {}", logRepository.getAll());
        return logRepository.getAll().stream().
                map(LogItem::getMessage).
                collect(Collectors.toList());
    }
}
