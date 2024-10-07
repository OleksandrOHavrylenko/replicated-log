package com.distributed.secondary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class SecMessageController {

    private final List<Message> messageRepository = new ArrayList<>();

    @GetMapping("/list")
    List<String> getMessages() {
        System.out.println("GET Messages");
        return messageRepository.stream()
                .map(Message::getMessage)
                .collect(Collectors.toList());
//        return messageRepository;
    }
}
