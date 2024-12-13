package com.distributed.secondary;

import com.distributed.commons.LogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class TempBuffer {
    private static final Logger log = LoggerFactory.getLogger(TempBuffer.class);

    private final Map<Long, LogItem> buffer = Collections.synchronizedMap(new HashMap<>());

    public void add(final LogItem item) {
        buffer.put(item.getId(), item);
        log.info("added item to buffer {}, buffer size = {}", item, buffer.size());
    }

    public boolean contains(final Long id) {
        return buffer.containsKey(id);
    }

    public LogItem getAndDelete(final Long id) {
        return buffer.remove(id);
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public void deleteAllLessThen(long id) {
        buffer.entrySet().removeIf(e -> e.getKey() < id);
        log.info("deleted all items less then id: {}, Buffer size = {}", id, buffer.size());
    }
}
