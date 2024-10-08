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
    private final SecClient secClient1;

    public MessageController(LogRepository logRepository) {
        this.logRepository = logRepository;
        secClient1 = new SecClient("secondary1", 9091);
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
        secClient1.replicateLog(message);
        log.info("Message replicated to secondaries.");
        return "OK " + message.getMessage();
    }
}
