package com.example.reactorspring.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> save(User user);
    Flux<User> findAll();
    Mono<User> findById(Long id);
    Mono<Void> deleteById(Long id);
}
