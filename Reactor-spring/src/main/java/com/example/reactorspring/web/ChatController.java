package com.example.reactorspring.web;

import com.example.reactorspring.dto.ChatMessage;
import com.example.reactorspring.service.ChatService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/message")
    public Mono<Void> postMessage(@RequestBody ChatMessage chatMessage) {
        return chatService.sendMessage(chatMessage);
    }

    @GetMapping("/messages")
    public Flux<ServerSentEvent<String>> streamMessages() {
        return chatService.receiveMessages()
                .map(message -> ServerSentEvent.builder(message).build());
    }
}
