package com.example.reactorspring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MvcTestService {

    private final WebClient webClient;
    private final String mvcUrl = "http://127.0.0.1:8090";

    public Mono<Map> testMvcToWebClient(String msg) {
        String uriString = UriComponentsBuilder.fromHttpUrl(mvcUrl)
                .path("/mvc/%s".formatted(msg))
                .buildAndExpand()
                .toUriString();

        return webClient.get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(error -> Mono.just(new HashMap<String, String>()));
    }
}
