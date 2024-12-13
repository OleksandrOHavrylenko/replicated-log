package com.distributed.master;

import com.distributed.commons.LogItem;
import com.distributed.master.replica.Replica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RestoreService {
    private static final Logger log = LoggerFactory.getLogger(RestoreService.class);

    private final LogRepository logRepository;

    public RestoreService(final LogRepository logRepository) {
        this.logRepository = Objects.requireNonNull(logRepository);
    }

    public void restore(long lastId, Replica replica) {
        List<LogItem> allBiggerThen = logRepository.getAllBiggerThen(lastId);
        log.info("Plan to restore items with ids: {}", allBiggerThen.stream().map(LogItem::getId).toList());
        replica.restore(allBiggerThen);
    }

}
