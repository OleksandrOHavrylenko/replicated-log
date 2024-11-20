package com.distributed.secondary;

import com.distributed.commons.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LogRepository {

    private static final Logger log = LoggerFactory.getLogger(LogRepository.class);

    private final Map<Long, String> messageRepository = Collections.synchronizedMap(new LinkedHashMap<>());

    public void add(final LogItem item) {
        messageRepository.put(item.getId(),item.getMessage());
        log.info("Message saved to Secondary node memory {}-{}", item.getId(), item.getMessage());
    }

    public List<String> getAll() {
        return List.copyOf(messageRepository.values());
    }
}
