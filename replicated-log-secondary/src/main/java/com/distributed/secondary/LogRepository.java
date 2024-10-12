package com.distributed.secondary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class LogRepository {

    private static final Logger log = LoggerFactory.getLogger(LogRepository.class);

    private final List<Message> messageRepository = Collections.synchronizedList(new ArrayList<>());

    public void add(Message message) {
        messageRepository.add(message);
        log.info("Message saved to Secondary node memory {}-{}", message.getId(), message.getMessage());
    }

    public List<Message> getAll() {
        return messageRepository;
    }
}
