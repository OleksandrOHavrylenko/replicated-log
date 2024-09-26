package com.distributed.master;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MessageController {

    private final List<Message> messageRepository = new ArrayList<>();

    @GetMapping
    List<Message> getMessages() {
        System.out.println("GET Messages");
        return messageRepository;
    }

    @PostMapping
    Message addMessage(@RequestBody Message message) {
        System.out.println("POST Message: " + message);
        messageRepository.add(message);
        return message;
    }


}
