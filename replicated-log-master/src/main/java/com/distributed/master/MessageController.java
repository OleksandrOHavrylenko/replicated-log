package com.distributed.master;

import com.distributed.commons.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(final MessageService messageService) {
        this.messageService = Objects.requireNonNull(messageService);
    }

    @GetMapping("/list")
    List<String> getMessages() {
        return messageService.list();
    }

    @PostMapping("/append")
    String append(@RequestBody Message message) {
        return messageService.append(message);
    }
}
