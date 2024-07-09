package com.example.reactorspring.service;

import com.example.reactorspring.dto.ChatMessage;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatService {
    private static final String CHANNEL = "chat";

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public ChatService(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Void> sendMessage(ChatMessage chatMessage) {
        return reactiveRedisTemplate.convertAndSend(CHANNEL, chatMessage.getUsername() + ": " + chatMessage.getMessage()).then();
    }

    public Flux<String> receiveMessages() {
        return reactiveRedisTemplate.listenToChannel(CHANNEL).map(message -> message.getMessage());
    }
}

