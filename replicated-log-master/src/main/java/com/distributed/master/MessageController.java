package com.distributed.master;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class MessageController {
    private final AtomicLong counter = new AtomicLong();

    private final SecondaryClient secondaryClient;


    private final List<Message> messageRepository = new ArrayList<>();

    public MessageController(final SecondaryClient secondaryClient) {
        this.secondaryClient = secondaryClient;
    }

    @GetMapping("/list")
    List<String> getMessages() {
        System.out.println("GET Messages");
        return messageRepository.stream()
                .map(Message::getMessage)
                .collect(Collectors.toList());
//        return messageRepository;
    }

    @PostMapping("/append")
    String  addMessage(@RequestBody Message message) {
        message.setId(counter.getAndIncrement());
        System.out.println("POST Message: " + message.getMessage());
        messageRepository.add(message);
        secondaryClient.appendLog(message);
        System.out.println("Sent to sec");
        return "OK " + message.getMessage();
    }
}
