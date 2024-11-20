package com.distributed.master;

import com.distributed.commons.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LogRepository {

    private static final Logger log = LoggerFactory.getLogger(LogRepository.class);

    private final Map<Long, String> messageRepository = Collections.synchronizedMap(new LinkedHashMap<>());

    public void add(final LogItem item) {
        messageRepository.put(item.getId(), item.getMessage());
        log.info("Message saved to Master node memory {}-{}", item.getId(), item.getMessage());
    }

    public List<String> getAll() {
        return List.copyOf(messageRepository.values());
    }
}
