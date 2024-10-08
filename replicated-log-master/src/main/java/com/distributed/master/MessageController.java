package com.distributed.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final AtomicLong counter = new AtomicLong();

    private final LogRepository logRepository;
    private final SecondaryClient secondaryClient;

    public MessageController(final SecondaryClient secondaryClient, LogRepository logRepository) {
        this.secondaryClient = secondaryClient;
        this.logRepository = logRepository;
    }

    @GetMapping("/list")
    List<String> getMessages() {
        log.info("GET all messages.");
        return logRepository.getAll().stream()
                .map(Message::getMessage)
                .collect(Collectors.toList());
//        return messageRepository;
    }

    @PostMapping("/append")
    String append(@RequestBody Message message) {
        message.setId(counter.getAndIncrement());
        logRepository.add(message);
        secondaryClient.appendLog(message);
        log.info("Message replicated to secondaries.");
        return "OK " + message.getMessage();
    }
}
