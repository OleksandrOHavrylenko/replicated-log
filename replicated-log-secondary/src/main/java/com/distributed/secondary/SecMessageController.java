package com.distributed.secondary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class SecMessageController {

    private static final Logger log = LoggerFactory.getLogger(SecMessageController.class);

    private final LogRepository logRepository;

    public SecMessageController(final LogRepository logRepository) {
        this.logRepository = Objects.requireNonNull(logRepository);
    }

    @GetMapping("/list")
    List<String> getMessages() {
        List<String> allMessages = logRepository.getAll();
        log.info("GET all messages: {}", allMessages);
        return allMessages;
    }
}
