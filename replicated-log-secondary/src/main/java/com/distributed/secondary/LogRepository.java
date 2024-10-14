package com.distributed.secondary;

import com.distributed.commons.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class LogRepository {

    private static final Logger log = LoggerFactory.getLogger(LogRepository.class);

    private final List<LogItem> messageRepository = Collections.synchronizedList(new ArrayList<>());

    public void add(LogItem item) {
        messageRepository.add(item);
        log.info("Message saved to Secondary node memory {}-{}", item.getId(), item.getMessage());
    }

    public List<LogItem> getAll() {
        return messageRepository;
    }
}
