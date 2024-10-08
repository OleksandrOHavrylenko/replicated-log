package com.distributed.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final AtomicLong counter = new AtomicLong();

    private final MessageService messageService;

    public MessageController(final MessageService messageService) {
        this.messageService = Objects.requireNonNull(messageService);
    }

    @GetMapping("/list")
    List<String> getMessages() {
        return messageService.list().stream()
                .map(Message::getMessage)
                .collect(Collectors.toList());
    }

    @PostMapping("/append")
    String append(@RequestBody Message message) {
        message.setId(counter.getAndIncrement());
        return messageService.append(message);
    }
}
